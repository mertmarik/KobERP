from fastapi import APIRouter, UploadFile, File, Depends, HTTPException, Form
from middleware.auth import verify_token
from services.llm_service import llm_service
from models.schemas import DocumentAnalysisResponse, ErrorResponse
from typing import Dict, Optional
from PIL import Image
import io
import logging
import httpx

router = APIRouter()
logger = logging.getLogger(__name__)

# Allowed file extensions and MIME types
ALLOWED_EXTENSIONS = {'.jpg', '.jpeg', '.png', '.gif', '.bmp', '.tiff', '.tif'}
ALLOWED_MIME_TYPES = {
    'image/jpeg',
    'image/png',
    'image/gif',
    'image/bmp',
    'image/tiff'
}


@router.post(
    "/analyze",
    response_model=DocumentAnalysisResponse,
    responses={
        200: {
            "description": "Belge başarıyla analiz edildi",
            "model": DocumentAnalysisResponse
        },
        400: {
            "description": "Geçersiz dosya formatı veya içerik",
            "model": ErrorResponse
        },
        401: {
            "description": "Yetkisiz erişim - geçersiz veya eksik token",
            "model": ErrorResponse
        },
        500: {
            "description": "Sunucu hatası",
            "model": ErrorResponse
        }
    },
    summary="Muhasebe Belgesi Analizi",
    description="""
    Fatura, fiş veya muhasebe belgelerini analiz eder.
    
    **İki Yöntem Desteklenir:**
    1. **Dosya Yükleme**: `file` parametresi ile dosya yükleyin
    2. **URL'den İndirme**: `image_url` parametresi ile presigned URL gönderin
    
    **Nasıl Çalışır:**
    1. Belge Qwen3-VL vision modeline gönderilir
    2. Model görseli analiz ederek şu bilgileri çıkarır:
        - Tarih (dd/mm/yyyy formatında)
        - Firma adı
        - Toplam ücret (para birimi ile)
        - Vergi miktarı (para birimi ile)
    
    **Not:** Eğer belgede bir bilgi yoksa, ilgili alan null olarak dönecektir.
    
    **Desteklenen Formatlar:** JPG, PNG, GIF, BMP, TIFF
    
    **Yetkilendirme:** Bearer token (Auth0) gereklidir.
    """
)
async def analyze_document(
    token_payload: Dict = Depends(verify_token),
    file: Optional[UploadFile] = File(None, description="Analiz edilecek belge dosyası"),
    image_url: Optional[str] = Form(None, description="Belge görselinin URL'si (presigned URL)")
) -> DocumentAnalysisResponse:
    """
    Analyze accounting document from file upload or URL
    
    This endpoint:
    1. Accepts either a file upload OR an image URL
    2. Validates and downloads/reads the image
    3. Sends image directly to Qwen3-VL vision model
    4. Returns structured financial information
    
    Requires valid Auth0 Bearer token.
    """
    
    try:
        file_contents = None
        
        # Check that exactly one method is provided
        if file is None and image_url is None:
            raise HTTPException(
                status_code=400,
                detail="Lütfen bir dosya yükleyin veya image_url parametresi gönderin"
            )
        
        if file is not None and image_url is not None:
            raise HTTPException(
                status_code=400,
                detail="Hem dosya hem URL gönderilemez, birini seçin"
            )
        
        # Method 1: File Upload
        if file is not None:
            # Validate file type by extension
            file_extension = None
            if file.filename:
                file_extension = '.' + file.filename.rsplit('.', 1)[-1].lower()
                if file_extension not in ALLOWED_EXTENSIONS:
                    raise HTTPException(
                        status_code=400,
                        detail=f"Geçersiz dosya formatı. Desteklenen formatlar: {', '.join(ALLOWED_EXTENSIONS)}"
                    )
            
            # Validate content type
            if file.content_type not in ALLOWED_MIME_TYPES:
                raise HTTPException(
                    status_code=400,
                    detail=f"Geçersiz MIME tipi. Desteklenen tipler: {', '.join(ALLOWED_MIME_TYPES)}"
                )
            
            # Read file contents
            file_contents = await file.read()
            logger.info(f"Processing uploaded file: {file.filename} for user: {token_payload.get('sub', 'unknown')}")
        
        # Method 2: URL Download
        elif image_url is not None:
            try:
                logger.info(f"Downloading image from URL for user: {token_payload.get('sub', 'unknown')}")
                logger.info(f"Original URL: {image_url}")
                
                # Use URL as-is since we're running in Docker with proper networking
                download_url = image_url
                
                logger.info(f"Downloading from: {download_url}")
                
                async with httpx.AsyncClient(timeout=30.0, verify=False) as client:
                    response = await client.get(download_url)
                    response.raise_for_status()
                    file_contents = response.content
                    
                    # Validate content type from response
                    content_type = response.headers.get('content-type', '')
                    if not any(ct in content_type.lower() for ct in ['image/jpeg', 'image/png', 'image/gif', 'image/bmp', 'image/tiff']):
                        raise HTTPException(
                            status_code=400,
                            detail=f"URL geçersiz bir görsel döndürdü: {content_type}"
                        )
                
                logger.info(f"Successfully downloaded image, size: {len(file_contents)} bytes")
                
            except httpx.HTTPError as e:
                logger.error(f"Failed to download image from URL: {str(e)}")
                raise HTTPException(
                    status_code=400,
                    detail=f"URL'den görsel indirilemedi: {str(e)}"
                )
            except Exception as e:
                logger.error(f"Unexpected error downloading image: {str(e)}")
                raise HTTPException(
                    status_code=500,
                    detail=f"Görsel indirme hatası: {str(e)}"
                )
        
        # Validate file size (max 10MB)
        max_size = 10 * 1024 * 1024  # 10MB
        if len(file_contents) > max_size:
            raise HTTPException(
                status_code=400,
                detail="Dosya boyutu çok büyük (maksimum 10MB)"
            )
        
        # Check if valid image
        try:
            image = Image.open(io.BytesIO(file_contents))
            image.verify()
        except Exception:
            raise HTTPException(
                status_code=400,
                detail="Geçersiz görsel dosyası"
            )
        
        # Analyze with Vision LLM
        try:
            analysis_result = llm_service.analyze_document_with_vision(file_contents)
            logger.info("Vision LLM analysis successful")
        except Exception as e:
            logger.error(f"Vision LLM analysis failed: {str(e)}")
            raise HTTPException(
                status_code=500,
                detail=f"Belge analizi başarısız: {str(e)}"
            )
        
        # Return structured response
        return DocumentAnalysisResponse(**analysis_result)
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Unexpected error: {str(e)}")
        raise HTTPException(
            status_code=500,
            detail=f"Beklenmeyen bir hata oluştu: {str(e)}"
        )
