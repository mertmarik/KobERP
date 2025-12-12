import pytesseract
from PIL import Image
import io
from typing import Optional
from config import settings


class OCRService:
    """Service for extracting text from images using Tesseract OCR"""
    
    def __init__(self):
        # Set Tesseract command path if specified in config
        if settings.tesseract_cmd:
            pytesseract.pytesseract.tesseract_cmd = settings.tesseract_cmd
    
    def extract_text_from_image(self, image_bytes: bytes, lang: str = 'tur+eng') -> str:
        """
        Extract text from image bytes using Tesseract OCR
        
        Args:
            image_bytes: Image file content as bytes
            lang: Language(s) for OCR (default: Turkish + English)
            
        Returns:
            str: Extracted text from the image
            
        Raises:
            Exception: If OCR processing fails
        """
        try:
            # Convert bytes to PIL Image
            image = Image.open(io.BytesIO(image_bytes))
            
            # Convert to RGB if necessary (RGBA, P, etc.)
            if image.mode not in ('RGB', 'L'):
                image = image.convert('RGB')
            
            # Perform OCR with Turkish and English language support
            # Configuration options:
            # --psm 3: Fully automatic page segmentation (default)
            # --oem 3: Default OCR Engine Mode
            custom_config = r'--oem 3 --psm 3'
            
            text = pytesseract.image_to_string(
                image,
                lang=lang,
                config=custom_config
            )
            
            # Clean up extracted text
            text = text.strip()
            
            if not text:
                raise Exception("No text could be extracted from the image")
            
            return text
            
        except Exception as e:
            raise Exception(f"OCR processing failed: {str(e)}")
    
    def is_valid_image(self, image_bytes: bytes) -> bool:
        """
        Check if the provided bytes represent a valid image
        
        Args:
            image_bytes: Image file content as bytes
            
        Returns:
            bool: True if valid image, False otherwise
        """
        try:
            image = Image.open(io.BytesIO(image_bytes))
            image.verify()
            return True
        except Exception:
            return False


# Singleton instance
ocr_service = OCRService()
