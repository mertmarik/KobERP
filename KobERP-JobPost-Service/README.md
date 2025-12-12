# KobERP Job Post Service

Spring Boot tabanlı İş İlanı Yönetim Servisi

## Özellikler

### İş İlanı Yönetimi
- ✅ İş ilanı oluşturma
- ✅ İş ilanı güncelleme
- ✅ İş ilanı silme
- ✅ İş ilanı durumu değiştirme (Açık/Beklemede/Kapalı)
- ✅ İş ilanlarını listeleme

### Başvuru Yönetimi
- ✅ Başvuru oluşturma
- ✅ Başvuruları görüntüleme
- ✅ Başvuru kabul etme
- ✅ Başvuru reddetme
- ✅ Başvuru durumu güncelleme

## Teknolojiler

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

## Kurulum

### Gereksinimler
- JDK 17 veya üzeri
- Maven 3.6+
- PostgreSQL 12+

### Veritabanı Yapılandırması

`src/main/resources/application.properties` dosyasını düzenleyin:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/koberp
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Projeyi Çalıştırma

```bash
# Bağımlılıkları yükle ve projeyi derle
mvn clean install

# Uygulamayı başlat
mvn spring-boot:run
```

Uygulama varsayılan olarak `http://localhost:8080` adresinde çalışacaktır.

## API Endpoints

### İş İlanı Endpoints

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/api/job-posts` | Yeni iş ilanı oluşturur |
| GET | `/api/job-posts` | Tüm iş ilanlarını getirir |
| GET | `/api/job-posts/{id}` | ID'ye göre iş ilanı getirir |
| GET | `/api/job-posts/owner/{ownerId}` | Sahibine göre iş ilanlarını getirir |
| GET | `/api/job-posts/status/{status}` | Duruma göre iş ilanlarını getirir |
| PUT | `/api/job-posts/{id}` | İş ilanını günceller |
| PATCH | `/api/job-posts/{id}/status` | İş ilanı durumunu günceller |
| DELETE | `/api/job-posts/{id}` | İş ilanını siler |

### Başvuru Endpoints

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/api/applications` | Yeni başvuru oluşturur |
| GET | `/api/applications` | Tüm başvuruları getirir |
| GET | `/api/applications/{id}` | ID'ye göre başvuru getirir |
| GET | `/api/applications/job-post/{jobPostingId}` | İş ilanına göre başvuruları getirir |
| GET | `/api/applications/status/{status}` | Duruma göre başvuruları getirir |
| PATCH | `/api/applications/{id}/accept` | Başvuruyu kabul eder |
| PATCH | `/api/applications/{id}/reject` | Başvuruyu reddeder |
| PATCH | `/api/applications/{id}/status` | Başvuru durumunu günceller |
| DELETE | `/api/applications/{id}` | Başvuruyu siler |

## İş İlanı Durumları

- `OPEN` - Açık
- `PENDING` - Beklemede
- `CLOSED` - Kapalı

## Başvuru Durumları

- `PENDING` - Bekliyor
- `ACCEPTED` - Kabul Edildi
- `REJECTED` - Reddedildi

## Örnek Kullanım

### İş İlanı Oluşturma

```bash
curl -X POST http://localhost:8080/api/job-posts \
  -H "Content-Type: application/json" \
  -d '{
    "ownerId": "user123",
    "postTitle": "Software Developer",
    "department": "IT",
    "location": "Istanbul",
    "salary": "15000-20000 TL",
    "jobType": "Full-time",
    "publishDate": "2025-11-30",
    "lastApplicationDate": "2025-12-30",
    "jobDefinition": "Java geliştirici aranıyor",
    "requiredSkills": "Java, Spring Boot, PostgreSQL",
    "postStatus": "OPEN"
  }'
```

### Başvuru Kabul Etme

```bash
curl -X PATCH http://localhost:8080/api/applications/1/accept \
  -H "Content-Type: application/json" \
  -d '{
    "reviewedBy": 1,
    "statusNotes": "Mülakat için davet edilecek"
  }'
```

### İş İlanı Durumu Değiştirme

```bash
curl -X PATCH http://localhost:8080/api/job-posts/{id}/status \
  -H "Content-Type: application/json" \
  -d '{
    "postStatus": "CLOSED"
  }'
```

## Lisans

MIT License
