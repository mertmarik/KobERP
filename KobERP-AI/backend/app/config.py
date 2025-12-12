from pydantic_settings import BaseSettings
from typing import Optional
import os
from pathlib import Path


class Settings(BaseSettings):
    """Application settings and configuration"""
    
    # Auth0 Configuration
    auth0_domain: str
    auth0_api_audience: str
    auth0_client_id: str
    auth0_client_secret: str
    
    # Ollama Configuration
    ollama_base_url: str = "http://localhost:11434"
    ollama_model: str = "qwen3-vl:4b"
    
    # Application Configuration
    app_name: str = "KobERP AI Backend"
    app_version: str = "1.0.0"
    debug: bool = False
    
    # Tesseract Configuration
    tesseract_cmd: Optional[str] = None
    
    class Config:
        # Look for .env in parent directory (backend folder)
        env_file = str(Path(__file__).parent.parent / ".env")
        case_sensitive = False


settings = Settings()
