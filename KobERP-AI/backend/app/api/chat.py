from fastapi import APIRouter, Depends, HTTPException
from typing import Dict
from middleware.auth import verify_token
from services.llm_service import llm_service
from models.schemas import ChatRequest, ChatResponse, ErrorResponse
import logging

router = APIRouter()
logger = logging.getLogger(__name__)


@router.post(
    "/chat",
    response_model=ChatResponse,
    responses={
        401: {"model": ErrorResponse, "description": "Unauthorized"},
        500: {"model": ErrorResponse, "description": "Internal Server Error"}
    },
    summary="KobERP İşletme Asistanı ile Sohbet",
    description="""
    KOBİ'lere özel iş danışmanlığı chatbot endpoint'i.
    
    Bu endpoint işletme yönetimi, stok, finans, satış stratejileri gibi konularda
    KOBİ'lere yardımcı olan yapay zeka asistanıyla iletişim kurar.
    
    **Örnek Sorular:**
    - Stoğumu nasıl daha iyi yönetirim?
    - Nakit akışımı nasıl iyileştirebilirim?
    - Müşteri memnuniyetini artırmak için ne yapmalıyım?
    - Satışlarımı nasıl artırabilirim?
    - İşletmemin dijitalleşmesi için nereden başlamalıyım?
    """
)
async def chat_with_assistant(
    request: ChatRequest,
    token: Dict = Depends(verify_token)
) -> ChatResponse:
    """
    KobERP Asistanı ile sohbet et
    
    Args:
        request: Kullanıcı sorusu ve opsiyonel konuşma geçmişi
        token: Auth0 JWT token (otomatik doğrulama)
        
    Returns:
        ChatResponse: Asistanın yanıtı
        
    Raises:
        HTTPException: Hata durumunda
    """
    
    try:
        logger.info(f"Chat request received from user: {token.get('sub', 'unknown')}")
        logger.debug(f"Question: {request.question[:100]}...")  # Log first 100 chars
        
        # Call LLM service
        answer = llm_service.chat_with_assistant(
            question=request.question,
            conversation_history=request.conversation_history
        )
        
        logger.info("Chat response generated successfully")
        
        return ChatResponse(answer=answer)
        
    except Exception as e:
        logger.error(f"Chat error: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=500,
            detail=f"Chat işlemi başarısız oldu: {str(e)}"
        )
