import ollama
from typing import Dict, Optional
from config import settings
import json
import base64


class LLMService:
    """Service for analyzing documents using Qwen3-VL vision model via Ollama"""
    
    def __init__(self):
        self.model = settings.ollama_model
        self.base_url = settings.ollama_base_url
        
        # Initialize Ollama client with custom base URL
        self.client = ollama.Client(host=self.base_url)
    
    def analyze_document_with_vision(self, image_bytes: bytes) -> Dict[str, Optional[str]]:
        """
        Analyze document image directly using vision model
        
        Args:
            image_bytes: Image file content as bytes
            
        Returns:
            Dict containing:
                - tarih: Date in dd/mm/yyyy format
                - firma: Company/vendor name
                - ucret: Amount with currency
                - vergi_miktari: Tax amount with currency
                - raw_response: Raw model response
                
        Raises:
            Exception: If LLM analysis fails
        """
        
        # Convert image to base64
        image_base64 = base64.b64encode(image_bytes).decode('utf-8')
        
        prompt = """Sen bir muhasebe belgesi analiz asistanısın. Sana verilen görsel bir fatura, fiş veya muhasebe belgesidir.

Görseli analiz et ve şu formatta yanıt ver:

Tarih: [dd/mm/yyyy formatında tarih veya "bulunamadı"]
Firma: [firma/mağaza adı veya "bulunamadı"]
Ücret: [toplam tutar ve para birimi veya "bulunamadı"]
Vergi Miktarı: [KDV/vergi tutarı ve para birimi veya "bulunamadı"]
h
Sadece bu 4 satırı yaz, başka açıklama ekleme."""

        try:
            # Call Ollama API with vision support
            response = self.client.chat(
                model=self.model,
                messages=[
                    {
                        'role': 'user',
                        'content': prompt,
                        'images': [image_base64]
                    }
                ],
                options={
                    'temperature': 0.1,
                    'top_p': 0.9,
                }
            )
            
            # Extract response content
            content = response['message']['content'].strip()
            
            # Parse the simple text format
            result = {
                'tarih': None,
                'firma': None,
                'ucret': None,
                'vergi_miktari': None,
                'raw_response': content
            }
            
            lines = content.split('\n')
            for line in lines:
                line = line.strip()
                if line.startswith('Tarih:'):
                    value = line.replace('Tarih:', '').strip()
                    if value and value.lower() != 'bulunamadı':
                        result['tarih'] = value
                elif line.startswith('Firma:'):
                    value = line.replace('Firma:', '').strip()
                    if value and value.lower() != 'bulunamadı':
                        result['firma'] = value
                elif line.startswith('Ücret:') or line.startswith('Ucret:'):
                    value = line.replace('Ücret:', '').replace('Ucret:', '').strip()
                    if value and value.lower() != 'bulunamadı':
                        result['ucret'] = value
                elif line.startswith('Vergi Miktarı:') or line.startswith('Vergi Miktari:'):
                    value = line.replace('Vergi Miktarı:', '').replace('Vergi Miktari:', '').strip()
                    if value and value.lower() != 'bulunamadı':
                        result['vergi_miktari'] = value
            
            return result
            
        except Exception as e:
            raise Exception(f"LLM analysis failed: {str(e)}")
    
    def chat_with_assistant(self, question: str, conversation_history: list = None) -> str:
        """
        Chat with KobERP business assistant for SME-related questions
        
        Args:
            question: User's question about their business
            conversation_history: Optional list of previous messages for context
            
        Returns:
            str: AI assistant's response
            
        Raises:
            Exception: If chat fails
        """
        
        system_prompt = """Sen KobERP Asistanı'sın, küçük ve orta ölçekli işletmelere (KOBİ) yardımcı olan uzman bir iş danışmanısın.

Görevin:
- İşletme yönetimi, finans, muhasebe, stok yönetimi, satış stratejileri hakkında pratik öneriler sunmak
- Türkiye'deki KOBİ'lere özel tavsiyelerde bulunmak
- Net, anlaşılır ve uygulanabilir çözümler önermek
- İşletme sahiplerinin günlük sorunlarına hızlı yanıtlar vermek

Uzmanlık alanların:
- Stok yönetimi ve envanter optimizasyonu
- Nakit akışı ve finansal planlama
- Satış ve pazarlama stratejileri
- Müşteri ilişkileri yönetimi (CRM)
- Operasyonel verimlilik
- Dijital dönüşüm ve teknoloji kullanımı
- Vergi ve muhasebe konularında temel bilgiler
- İş geliştirme ve büyüme stratejileri

Önemli kurallar:
- Sadece KOBİ'lerle ve işletme yönetimiyle ilgili sorulara yanıt ver
- Eğer soru işletme yönetimiyle alakalı değilse, kibarca konuyla ilgili sorular sormasını iste
- Türkçe ve samimi bir dil kullan
- Kısa ve öz yanıtlar ver, gerekirse madde madde açıkla
- Gereksiz teknik jargon kullanma, anlaşılır ol"""

        try:
            # Build messages list
            messages = [
                {
                    'role': 'system',
                    'content': system_prompt
                }
            ]
            
            # Add conversation history if provided
            if conversation_history:
                messages.extend(conversation_history)
            
            # Add current question
            messages.append({
                'role': 'user',
                'content': question
            })
            
            # Use text-only model for chat
            # For vision models, extract base name and use qwen3:4b for chat
            if '-vl' in self.model:
                # Vision model (qwen3-vl:2b) -> use qwen3:4b for chat
                chat_model = 'qwen3:4b'
            else:
                # Already a text model, use it as is
                chat_model = self.model
            
            response = self.client.chat(
                model=chat_model,
                messages=messages,
                options={
                    'temperature': 0.7,
                    'top_p': 0.9,
                    'max_tokens': 500
                }
            )
            
            return response['message']['content'].strip()
            
        except Exception as e:
            raise Exception(f"Chat failed: {str(e)}")
    
    def check_model_availability(self) -> bool:
        """
        Check if the configured Qwen model is available
        
        Returns:
            bool: True if model is available, False otherwise
        """
        try:
            models = self.client.list()
            available_models = [model['name'] for model in models.get('models', [])]
            return self.model in available_models
        except Exception:
            return False


# Singleton instance
llm_service = LLMService()
