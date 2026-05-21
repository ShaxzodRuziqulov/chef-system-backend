# Google Cloud Run ga Deploy Qilish

## Talab qilinadigan vositalar
- [Google Cloud CLI (gcloud)](https://cloud.google.com/sdk/docs/install)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

---

## 1-qadam: Google Cloud loyiha sozlash

```bash
# Google ga kirish
gcloud auth login

# Loyiha ID ni belgilash (o'zingiznikini yozing)
gcloud config set project YOUR_PROJECT_ID

# Kerakli API larni yoqish
gcloud services enable run.googleapis.com sqladmin.googleapis.com artifactregistry.googleapis.com
```

---

## 2-qadam: Cloud SQL (PostgreSQL) yaratish

```bash
# PostgreSQL instance yaratish (bu bir necha daqiqa vaqt oladi)
gcloud sql instances create oshpaz-db \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-central1

# Database yaratish
gcloud sql databases create chef --instance=oshpaz-db

# Foydalanuvchi paroli o'rnatish
gcloud sql users set-password postgres \
  --instance=oshpaz-db \
  --password=YOUR_DB_PASSWORD

# Instance connection name ni olish (keyingi qadamda kerak bo'ladi)
gcloud sql instances describe oshpaz-db --format="value(connectionName)"
```

---

## 3-qadam: Artifact Registry yaratish (Docker image saqlash uchun)

```bash
gcloud artifacts repositories create oshpaz-repo \
  --repository-format=docker \
  --location=us-central1
```

---

## 4-qadam: Docker image build va push qilish

```bash
# Docker ni gcloud bilan autentifikatsiya qilish
gcloud auth configure-docker us-central1-docker.pkg.dev

# Image build qilish
docker build -t us-central1-docker.pkg.dev/YOUR_PROJECT_ID/oshpaz-repo/oshpaz-backend:latest .

# Image ni push qilish
docker push us-central1-docker.pkg.dev/YOUR_PROJECT_ID/oshpaz-repo/oshpaz-backend:latest
```

---

## 5-qadam: Cloud Run ga deploy qilish

```bash
gcloud run deploy oshpaz-backend \
  --image=us-central1-docker.pkg.dev/YOUR_PROJECT_ID/oshpaz-repo/oshpaz-backend:latest \
  --region=us-central1 \
  --platform=managed \
  --allow-unauthenticated \
  --port=8090 \
  --memory=512Mi \
  --cpu=1 \
  --add-cloudsql-instances=YOUR_PROJECT_ID:us-central1:oshpaz-db \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
  --set-env-vars="CLOUD_SQL_INSTANCE=YOUR_PROJECT_ID:us-central1:oshpaz-db" \
  --set-env-vars="DB_NAME=chef" \
  --set-env-vars="DB_USER=postgres" \
  --set-env-vars="DB_PASS=YOUR_DB_PASSWORD" \
  --set-env-vars="SECRET_KEY=YOUR_JWT_SECRET_KEY" \
  --set-env-vars="ALLOWED_ORIGINS=*"
```

---

## 6-qadam: Tekshirish

Deploy tugagach URL beriladi. Swagger UI orqali tekshirish:
```
https://YOUR_CLOUD_RUN_URL/swagger-ui/index.html
```

---

## Muhim eslatmalar

| Parametr | Nima | Qayerdan olish |
|----------|------|---------------|
| `YOUR_PROJECT_ID` | GCP loyiha ID | [console.cloud.google.com](https://console.cloud.google.com) |
| `YOUR_DB_PASSWORD` | PostgreSQL paroli | 2-qadamda o'rnatilgan |
| `YOUR_JWT_SECRET_KEY` | JWT token uchun maxfiy kalit | Yangi tasodifiy string yarating |

### JWT secret key yaratish:
```bash
openssl rand -hex 32
```

### Narx (taxminiy):
- Cloud Run: 2M so'rov/oy bepul, undan keyin arzon
- Cloud SQL db-f1-micro: ~$7-10/oy
