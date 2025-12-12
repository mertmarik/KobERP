# KobERP AI Backend

🤖 Yapay zeka destekli muhasebe belgesi analiz API'si

## Özellikler

- ✅ **Auth0 ile Güvenli Kimlik Doğrulama**: Bearer token ile korunan endpoint'ler
- 📄 **OCR ile Belge Okuma**: Tesseract OCR kullanarak görsel belgelerden metin çıkarma
- 🧠 **AI ile Analiz**: Qwen3 LLM modeli ile akıllı belge analizi
- 📊 **Yapılandırılmış Çıktı**: Tarih, firma, ücret ve vergi bilgilerini düzenli formatta döndürme
- 📚 **Swagger/OpenAPI Dokümantasyonu**: Etkileşimli API dokümantasyonu

## Teknolojiler

- **FastAPI**: Modern, hızlı Python web framework
- **Tesseract OCR**: Görüntüden metin çıkarma
- **Ollama + Qwen3**: Yerel LLM ile belge analizi
- **Auth0**: JWT token ile kimlik doğrulama
- **Pydantic**: Veri validasyonu ve şema tanımları

## Gereksinimler

### Yazılım Gereksinimleri

- Python 3.9+
- Tesseract OCR
- Ollama

### Tesseract Kurulumu

#### Windows
```powershell
# Chocolatey ile
choco install tesseract

# veya manuel olarak
# https://github.com/UB-Mannheim/tesseract/wiki adresinden indirin
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install tesseract-ocr tesseract-ocr-tur
```

#### macOS
```bash
brew install tesseract tesseract-lang
```

### Ollama Kurulumu ve Model İndirme

1. Ollama'yı yükleyin: https://ollama.ai/download
2. Qwen3 modelini indirin:

```powershell
ollama pull qwen2.5:3b
```

## Kurulum

### 1. Repoyu Klonlayın

```powershell
git clone <repo-url>
cd KobERP-AI/backend
```

### 2. Virtual Environment Oluşturun

```powershell
python -m venv venv
.\venv\Scripts\Activate.ps1
```

### 3. Bağımlılıkları Yükleyin

```powershell
pip install -r requirements.txt
```

### 4. Ortam Değişkenlerini Ayarlayın

`.env.example` dosyasını `.env` olarak kopyalayın:

```powershell
Copy-Item .env.example .env
```

`.env` dosyasını düzenleyin ve Auth0 bilgilerinizi ekleyin:

```env
# Auth0 Configuration
AUTH0_DOMAIN=your-domain.auth0.com
AUTH0_API_AUDIENCE=your-api-audience
AUTH0_ISSUER=https://your-domain.auth0.com/
AUTH0_ALGORITHMS=RS256

# Ollama Configuration
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=qwen2.5:3b

# Application Configuration
APP_NAME=KobERP AI Backend
APP_VERSION=1.0.0
DEBUG=False
```

### 5. Auth0 Yapılandırması

1. Auth0 hesabı oluşturun: https://auth0.com
2. Yeni bir API oluşturun
3. API Audience ve Domain bilgilerini `.env` dosyasına ekleyin
4. Frontend uygulamanızı Auth0'a kaydedin

## Çalıştırma

### Geliştirme Modu

```powershell
cd app
python main.py
```

veya uvicorn ile:

```powershell
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### Üretim Modu

```powershell
uvicorn app.main:app --host 0.0.0.0 --port 8000 --workers 4
```

API şu adreste çalışacaktır: http://localhost:8000

## API Dokümantasyonu

API çalıştıktan sonra aşağıdaki adresleri ziyaret edebilirsiniz:

- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc
- **OpenAPI JSON**: http://localhost:8000/openapi.json

## API Kullanımı

### Endpoint: POST /api/v1/analyze

Muhasebe belgelerini analiz eder.

#### Request

```bash
curl -X POST "http://localhost:8000/api/v1/analyze" \
  -H "Authorization: Bearer YOUR_AUTH0_TOKEN" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@receipt.jpg"
```

#### Response

```json
{
  "tarih": "15/11/2024",
  "firma": "Migros",
  "ucret": "125.50 TL",
  "vergi_miktari": "22.59 TL"
}
```

### Sağlık Kontrolü: GET /health

```bash
curl http://localhost:8000/health
```

Response:
```json
{
  "status": "healthy",
  "version": "1.0.0",
  "ollama_status": "available"
}
```

## Frontend Entegrasyonu

Frontend'den istek yaparken Auth0 token'ı Bearer şeklinde gönderin:

```javascript
const response = await fetch('http://localhost:8000/api/v1/analyze', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${accessToken}`
  },
  body: formData
});
```

## Proje Yapısı

```
backend/
├── app/
│   ├── __init__.py
│   ├── main.py              # FastAPI uygulaması ve yapılandırma
│   ├── config.py            # Ayarlar ve ortam değişkenleri
│   ├── api/
│   │   ├── __init__.py
│   │   └── analyze.py       # Belge analizi endpoint'i
│   ├── middleware/
│   │   ├── __init__.py
│   │   └── auth.py          # Auth0 JWT doğrulama
│   ├── models/
│   │   ├── __init__.py
│   │   └── schemas.py       # Pydantic modelleri
│   └── services/
│       ├── __init__.py
│       ├── ocr_service.py   # Tesseract OCR servisi
│       └── llm_service.py   # Qwen3 LLM servisi
├── .env                     # Ortam değişkenleri (git'te yok)
├── .env.example             # Ortam değişkenleri şablonu
├── requirements.txt         # Python bağımlılıkları
└── README.md               # Bu dosya
```

## Desteklenen Dosya Formatları

- JPG/JPEG
- PNG
- GIF
- BMP
- TIFF

Maksimum dosya boyutu: 10MB

## Hata Kodları

- `200`: Başarılı analiz
- `400`: Geçersiz dosya formatı veya içerik
- `401`: Yetkisiz erişim (geçersiz veya eksik token)
- `500`: Sunucu hatası (OCR veya LLM hatası)

## Geliştirme Notları

### Tesseract Dil Desteği

Varsayılan olarak Türkçe + İngilizce dil desteği kullanılır. Farklı diller eklemek için:

```bash
# Linux
sudo apt-get install tesseract-ocr-<lang>

# Windows (Tesseract kurulumu sırasında dil paketlerini seçin)
```

### LLM Modeli Değiştirme

Farklı bir Ollama modeli kullanmak için `.env` dosyasında `OLLAMA_MODEL` değerini değiştirin:

```env
OLLAMA_MODEL=llama2:7b
```

Model indirme:
```bash
ollama pull llama2:7b
```

## Sorun Giderme

### Tesseract Bulunamadı Hatası

Windows'ta Tesseract yolunu `.env` dosyasında belirtin:

```env
TESSERACT_CMD=C:\\Program Files\\Tesseract-OCR\\tesseract.exe
```

### Ollama Bağlantı Hatası

1. Ollama'nın çalıştığından emin olun:
```bash
ollama list
```

2. Model indirildiğinden emin olun:
```bash
ollama pull qwen2.5:3b
```

### Auth0 Token Hatası

- Token'ın süresinin dolmadığından emin olun
- `AUTH0_DOMAIN`, `AUTH0_API_AUDIENCE` ve `AUTH0_ISSUER` değerlerinin doğru olduğundan emin olun
- Token'ın `Bearer ` prefix'i ile gönderildiğinden emin olun

## Lisans

MIT

## Katkıda Bulunma

Pull request'ler memnuniyetle karşılanır. Büyük değişiklikler için lütfen önce bir issue açın.

## İletişim

Sorularınız için: support@koberp.com
