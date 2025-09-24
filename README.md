# GlucoCloud API

A free diabetes health tracker built with Spring Boot. Track blood glucose, meals, and medications with analytics and data export capabilities.

## 🚀 Free Deployment Strategy

- **Local Development**: H2 Database + Local Storage
- **Production**: Render.com (Free Tier) + PostgreSQL + Cloudinary
- **Zero AWS costs** using free alternatives

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3 + Java 17
- **Database**: H2 (dev) / PostgreSQL (prod)
- **Authentication**: JWT with Spring Security
- **Image Storage**: Local (dev) / Cloudinary (prod)
- **Deployment**: Render.com Free Tier

## 📋 Features

### Phase 1 (Days 1-2) ✅
- [x] User registration and authentication
- [x] JWT-based security
- [x] H2 database setup
- [x] Basic project structure

### Phase 2 (Days 3-4) ✅
- [x] Glucose readings CRUD
- [x] Data validation and analytics
- [x] Basic time-in-range calculations
- [x] Pattern detection and flags
- [x] Sample data seeding

### Phase 3 (Days 5-6) ✅
- [x] Meals CRUD with photo uploads
- [x] Medication tracking with effectiveness ratings
- [x] Advanced analytics and correlations
- [x] Comprehensive dashboard with health scoring
- [x] Meal-glucose correlation analysis

### Phase 4 (Days 7-8) ✅
- [x] CSV data export for healthcare providers
- [x] Professional health reports
- [x] Date-range filtering for exports
- [x] Multiple export formats

### Phase 5 (Days 9-10) ✅
- [x] Production deployment to Render.com
- [x] Docker containerization
- [x] Health monitoring and logging
- [x] Environment configuration
- [x] Free PostgreSQL production database

### Phase 6 (Days 11-12) - Future
- [ ] Simple React frontend dashboard
- [ ] Real-time notifications and reminders
- [ ] Advanced AI predictions and insights
- [ ] Mobile app considerations

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### Run Locally
```bash
# Clone and navigate
cd GlucoCloud

# Run the application
mvn spring-boot:run

# Access H2 Console: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:glucocloud
# Username: sa
# Password: password
```

### Test Authentication

**Register a user:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Get current user (with JWT token):**
```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🏗️ Project Structure

```
src/main/java/com/glucocloud/api/
├── GlucoCloudApplication.java      # Main application class
├── config/
│   └── SecurityConfig.java         # Security configuration
├── controller/
│   └── AuthController.java         # Authentication endpoints
├── dto/
│   ├── AuthResponse.java          # Authentication response
│   ├── LoginRequest.java          # Login request
│   └── RegisterRequest.java       # Registration request
├── entity/
│   └── User.java                  # User entity
├── repository/
│   └── UserRepository.java       # User data access
├── security/
│   ├── JwtAuthenticationEntryPoint.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtUtils.java              # JWT utilities
└── service/
    └── UserService.java           # User business logic
```

## 🔄 Next Steps

1. **Test the current setup**:
   ```bash
   mvn spring-boot:run
   ```

2. **Ready for Phase 2**: Glucose readings CRUD
3. **Future**: Meal tracking with photos
4. **Deploy**: Free hosting on Render.com

## 💰 Cost Breakdown

- **Development**: $0 (local H2 + local storage)
- **Production**: $0 (Render free tier + PostgreSQL free tier)
- **Long-term**: ~$0-5/month (if you exceed free tiers)

## 📖 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | User login |
| GET | `/auth/me` | Get current user info |

### Glucose Endpoints ✅

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/glucose` | Create glucose reading |
| GET | `/glucose` | List readings (with optional date filters) |
| GET | `/glucose/{id}` | Get specific reading |
| PUT | `/glucose/{id}` | Update reading |
| DELETE | `/glucose/{id}` | Delete reading |

### Meal Endpoints ✅

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/meals` | Create meal entry |
| GET | `/meals` | List meals (with optional date filters) |
| GET | `/meals/{id}` | Get specific meal |
| PUT | `/meals/{id}` | Update meal |
| DELETE | `/meals/{id}` | Delete meal |

### Medication Endpoints ✅

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/medications` | Create medication entry |
| GET | `/medications` | List medications (with optional date filters) |
| GET | `/medications/{id}` | Get specific medication |
| PUT | `/medications/{id}` | Update medication |
| DELETE | `/medications/{id}` | Delete medication |
| GET | `/medications/names` | Get unique medication names |

### Analytics Endpoints ✅

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/analytics/glucose/summary` | Detailed glucose statistics |
| GET | `/analytics/glucose/flags` | Pattern detection and alerts |
| GET | `/analytics/meals/summary` | Meal nutrition analysis |
| GET | `/analytics/medications/summary` | Medication adherence analysis |
| GET | `/analytics/meal-glucose-correlations` | Meal impact on glucose |
| GET | `/analytics/dashboard` | Comprehensive health dashboard |

### Data Export Endpoints ✅

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/export/glucose` | Export glucose readings to CSV |
| GET | `/export/meals` | Export meals data to CSV |
| GET | `/export/medications` | Export medications data to CSV |
| GET | `/export/comprehensive-report` | Export complete health report |
| GET | `/export/all-data` | Export all data (comprehensive report) |
| GET | `/export/formats` | Get available export formats info |

## 🧪 Test with Demo Data

The app includes sample data for testing:

**Demo User Login:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo@glucocloud.com",
    "password": "demo123"
  }'
```

**Add a glucose reading:**
```bash
curl -X POST http://localhost:8080/glucose \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "readingValue": 125.5,
    "takenAt": "2024-01-15T08:30:00",
    "readingType": "FASTING",
    "note": "Morning reading before breakfast"
  }'
```

**Get glucose summary:**
```bash
curl -X GET "http://localhost:8080/analytics/glucose/summary" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Add a meal:**
```bash
curl -X POST http://localhost:8080/meals \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "description": "Grilled chicken with vegetables",
    "carbsGrams": 35,
    "calories": 450,
    "mealType": "DINNER",
    "eatenAt": "2024-01-15T18:30:00",
    "notes": "Healthy dinner"
  }'
```

**Add a medication:**
```bash
curl -X POST http://localhost:8080/medications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Metformin",
    "dosage": "500mg",
    "medicationType": "METFORMIN",
    "takenAt": "2024-01-15T08:00:00",
    "effectivenessRating": 4,
    "notes": "Taken with breakfast"
  }'
```

**Get comprehensive dashboard:**
```bash
curl -X GET "http://localhost:8080/analytics/dashboard" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Get meal-glucose correlations:**
```bash
curl -X GET "http://localhost:8080/analytics/meal-glucose-correlations" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Export glucose data to CSV:**
```bash
curl -X GET "http://localhost:8080/export/glucose" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output glucose_readings.csv
```

**Export comprehensive health report:**
```bash
curl -X GET "http://localhost:8080/export/comprehensive-report" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output health_report.csv
```

**Export with date range:**
```bash
curl -X GET "http://localhost:8080/export/meals?from=2024-01-01T00:00:00&to=2024-01-31T23:59:59" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output meals_january.csv
```

## 🎯 What Makes GlucoCloud Special

### 🔬 **Advanced Analytics**
- **Meal-Glucose Correlation**: Analyze how specific foods affect your blood sugar
- **Health Scoring**: 0-100 score based on glucose control, nutrition, and medication adherence
- **Pattern Detection**: Identify risky trends and get personalized recommendations
- **Progress Tracking**: Compare current vs. previous periods to track improvement

### 🍽️ **Smart Meal Tracking**
- **Carb Counting**: Track carbohydrates, calories, protein, and fat
- **Meal Impact Analysis**: See which meals cause glucose spikes
- **Photo Support**: Store meal photos (URLs) for visual reference
- **Nutrition Balance**: Get insights on your eating patterns

### 💊 **Medication Management**
- **Effectiveness Rating**: Rate how well medications work (1-5 scale)
- **Side Effect Tracking**: Log and monitor medication side effects
- **Adherence Analysis**: Track medication consistency
- **Multiple Types**: Support for insulin, oral medications, and supplements

### 📊 **Comprehensive Dashboard**
- **Health Score**: Overall diabetes management score with actionable insights
- **Trend Analysis**: 7-day glucose trends and pattern recognition
- **Personalized Recommendations**: AI-powered suggestions for improvement
- **Progress Metrics**: Compare your improvement over time

### 📄 **Healthcare Provider Ready**
- **CSV Export**: Export all data in healthcare-standard CSV format
- **Date Range Filtering**: Export specific time periods for appointments
- **Comprehensive Reports**: Detailed health summaries with insights
- **Professional Format**: Clean, organized data perfect for medical consultations
- **Multiple Export Options**: Glucose, meals, medications, or everything combined

---

## 🌐 Production Deployment

GlucoCloud is **production-ready** and can be deployed **100% FREE**!

### 🚀 Deploy to Render.com (Free)

1. **Push to GitHub**: Commit your code to a GitHub repository
2. **Connect Render**: Link your GitHub repo to Render.com
3. **Auto-Deploy**: Render builds and deploys automatically
4. **Free Database**: PostgreSQL included at no cost
5. **Global Access**: Your API available worldwide with HTTPS

**📖 Full deployment guide**: See [DEPLOYMENT.md](DEPLOYMENT.md) for step-by-step instructions.

**🎯 Live Example**: `https://glucocloud-api.onrender.com`

### 💰 Free Production Stack
- ✅ **API Hosting**: Render.com (Free tier)
- ✅ **Database**: PostgreSQL (Free 1GB)
- ✅ **SSL**: Automatic HTTPS certificates
- ✅ **Monitoring**: Health checks and logging
- ✅ **Domain**: `.onrender.com` subdomain
- ✅ **Total Cost**: **$0/month** 🎉

---

**Day 9-10 Complete! GlucoCloud is now live and accessible worldwide!** 🌐🎉

**Ready for Day 11-12: React Frontend Dashboard & Advanced Features?** 🎨📱