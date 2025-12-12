from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from api.analyze import router as analyze_router
from api.chat import router as chat_router
from config import settings
from services.llm_service import llm_service
from models.schemas import HealthResponse
import logging
import time

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Create FastAPI app with OpenAPI configuration
app = FastAPI(
    title=settings.app_name,
    version=settings.app_version,
    description="""
    🤖 **KobERP AI Backend API**
    
    Bu API, muhasebe belgelerini analiz eder ve KOBİ'lere özel iş danışmanlığı sağlar.
    
    ## Özellikler
    
    * 🔐 **Auth0 ile Güvenli Kimlik Doğrulama**: Bearer token ile korunan endpoint'ler
    * 📄 **Belge Analizi**: Vision AI ile görsel belgelerden veri çıkarma
    * 💬 **KobERP Asistanı**: İşletme yönetimi için yapay zeka danışmanı
    * 🧠 **AI ile Analiz**: Qwen3 LLM modeli ile akıllı analiz
    * 📊 **Yapılandırılmış Çıktı**: Tarih, firma, ücret ve vergi bilgilerini düzenli formatta döndürme
    
    ## Kullanım
    
    1. Auth0'dan geçerli bir Bearer token alın
    2. `/api/v1/analyze` endpoint'ine belge yükleyin veya `/api/v1/chat` ile sohbet edin
    3. Yapılandırılmış analiz sonucu veya asistan yanıtını alın
    
    ## Desteklenen Belgeler
    
    - Faturalar
    - Satış fişleri
    - Makbuzlar
    - Diğer muhasebe belgeleri
    
    ## Desteklenen Dosya Formatları
    
    JPG, JPEG, PNG, GIF, BMP, TIFF
    """,
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_url="/openapi.json",
    contact={
        "name": "KobERP AI Team",
        "email": "support@koberp.com",
    },
    license_info={
        "name": "MIT",
    },
)

# CORS Configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Üretimde bunu değiştirin
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Request timing middleware
@app.middleware("http")
async def add_process_time_header(request: Request, call_next):
    """Add processing time to response headers"""
    start_time = time.time()
    response = await call_next(request)
    process_time = time.time() - start_time
    response.headers["X-Process-Time"] = str(process_time)
    return response


# Global exception handler
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    """Handle uncaught exceptions"""
    logger.error(f"Unhandled exception: {str(exc)}", exc_info=True)
    return JSONResponse(
        status_code=500,
        content={"detail": "İç sunucu hatası oluştu"}
    )


# Include routers
app.include_router(
    analyze_router,
    prefix="/api/v1",
    tags=["Belge Analizi"]
)

app.include_router(
    chat_router,
    prefix="/api/v1",
    tags=["KobERP Asistanı"]
)


@app.on_event("startup")
async def startup_event():
    """Run on application startup"""
    logger.info(f"Starting {settings.app_name} v{settings.app_version}")
    logger.info(f"Debug mode: {settings.debug}")
    logger.info(f"Auth0 domain: {settings.auth0_domain}")
    logger.info(f"Ollama model: {settings.ollama_model}")
    
    # Check Ollama availability
    if llm_service.check_model_availability():
        logger.info("✅ Ollama model is available")
    else:
        logger.warning("⚠️ Ollama model is not available. Please ensure Ollama is running and the model is pulled.")


@app.on_event("shutdown")
async def shutdown_event():
    """Run on application shutdown"""
    logger.info(f"Shutting down {settings.app_name}")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8004,
        reload=settings.debug
    )
