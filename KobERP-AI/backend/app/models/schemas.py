from pydantic import BaseModel, Field
from typing import Optional


class DocumentAnalysisResponse(BaseModel):
    """Response model for document analysis"""
    
    tarih: Optional[str] = Field(
        None,
        description="İşlem tarihi (dd/mm/yyyy formatında)",
        example="15/11/2024"
    )
    firma: Optional[str] = Field(
        None,
        description="İşlemin yapıldığı firma/mağaza adı",
        example="Migros"
    )
    ucret: Optional[str] = Field(
        None,
        description="Toplam ödenen tutar (para birimi ile)",
        example="125.50 TL"
    )
    vergi_miktari: Optional[str] = Field(
        None,
        description="KDV/vergi tutarı (para birimi ile)",
        example="22.59 TL"
    )
    raw_response: Optional[str] = Field(
        None,
        description="Ham model yanıtı",
        example="Tarih: 15/11/2024\nFirma: Migros\nÜcret: 125.50 TL\nVergi Miktarı: 22.59 TL"
    )
    
    class Config:
        json_schema_extra = {
            "example": {
                "tarih": "15/11/2024",
                "firma": "Migros",
                "ucret": "125.50 TL",
                "vergi_miktari": "22.59 TL",
                "raw_response": "Tarih: 15/11/2024\nFirma: Migros\nÜcret: 125.50 TL\nVergi Miktarı: 22.59 TL"
            }
        }


class ErrorResponse(BaseModel):
    """Error response model"""
    
    detail: str = Field(
        ...,
        description="Hata mesajı",
        example="Authentication failed"
    )


class ChatRequest(BaseModel):
    """Request model for chatbot"""
    
    question: str = Field(
        ...,
        description="Kullanıcının işletmesiyle ilgili sorusu",
        example="Stoğumu nasıl daha iyi yönetebilirim?",
        min_length=1,
        max_length=1000
    )
    conversation_history: Optional[list] = Field(
        None,
        description="Önceki konuşma geçmişi (opsiyonel)",
        example=[]
    )


class ChatResponse(BaseModel):
    """Response model for chatbot"""
    
    answer: str = Field(
        ...,
        description="KobERP Asistanı'nın yanıtı",
        example="Stok yönetimini geliştirmek için şunları öneriyorum:\n1. Düzenli sayım yapın\n2. Stok takip yazılımı kullanın\n3. Minimum-maksimum stok seviyeleri belirleyin"
    )
    
    class Config:
        json_schema_extra = {
            "example": {
                "answer": "Stok yönetimini geliştirmek için şunları öneriyorum:\n1. Düzenli sayım yapın\n2. Stok takip yazılımı kullanın\n3. Minimum-maksimum stok seviyeleri belirleyin"
            }
        }


class HealthResponse(BaseModel):
    """Health check response model"""
    
    status: str = Field(
        ...,
        description="Servis durumu",
        example="healthy"
    )
    version: str = Field(
        ...,
        description="API versiyonu",
        example="1.0.0"
    )
    ollama_status: str = Field(
        ...,
        description="Ollama model durumu",
        example="available"
    )
