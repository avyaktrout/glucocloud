# GlucoCloud

A complete full-stack diabetes health tracker with Spring Boot backend and React frontend. Track blood glucose, meals, and medications with analytics and data export capabilities.

## 🚀 Free Deployment Strategy

- **Local Development**: H2 Database + Local Storage
- **Production**: Render.com (Free Tier) + PostgreSQL + Cloudinary
- **Zero AWS costs** using free alternatives

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3 + Java 17
- **Frontend**: React 18 + Bootstrap 5
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

### Phase 6 (Days 11-12) ✅
- [x] Complete React frontend dashboard
- [x] User authentication and login interface
- [x] Glucose, meal, and medication management UI
- [x] Analytics dashboard with visualizations
- [x] Data export functionality with UI
- [x] Responsive design and navigation

### Phase 7 (Future) - Advanced Features
- [ ] Real-time notifications and reminders
- [ ] Advanced AI predictions and insights
- [ ] Mobile app considerations
- [ ] Healthcare provider portal

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Node.js 16+
- npm 8+

### Run Full Application Locally
```bash
# Clone and navigate
cd GlucoCloud

# Terminal 1 - Run Backend
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8082"

# Terminal 2 - Run Frontend (in new terminal)
cd frontend
npm install
PORT=3001 npm start

# Access Application: http://localhost:3001
# Demo login: demo@glucocloud.com / demo123

# Access H2 Console: http://localhost:8082/h2-console
# JDBC URL: jdbc:h2:mem:glucocloud
# Username: sa / Password: password
```

### Test Authentication

**Register a user:**
```bash
curl -X POST http://localhost:8082/api/auth/register \
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
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Get current user (with JWT token):**
```bash
curl -X GET http://localhost:8082/api/auth/me \
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
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | User login |
| GET | `/api/auth/me` | Get current user info |

### Glucose Endpoints ✅

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/glucose` | Create glucose reading |
| GET | `/api/glucose` | List readings (with optional date filters) |
| GET | `/api/glucose/{id}` | Get specific reading |
| PUT | `/api/glucose/{id}` | Update reading |
| DELETE | `/api/glucose/{id}` | Delete reading |

### Meal Endpoints ✅

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/meals` | Create meal entry |
| GET | `/api/meals` | List meals (with optional date filters) |
| GET | `/api/meals/{id}` | Get specific meal |
| PUT | `/api/meals/{id}` | Update meal |
| DELETE | `/api/meals/{id}` | Delete meal |

### Medication Endpoints ✅

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/medications` | Create medication entry |
| GET | `/api/medications` | List medications (with optional date filters) |
| GET | `/api/medications/{id}` | Get specific medication |
| PUT | `/api/medications/{id}` | Update medication |
| DELETE | `/api/medications/{id}` | Delete medication |
| GET | `/api/medications/names` | Get unique medication names |

### Analytics Endpoints ✅

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/analytics/glucose/summary` | Detailed glucose statistics |
| GET | `/api/analytics/glucose/flags` | Pattern detection and alerts |
| GET | `/api/analytics/meals/summary` | Meal nutrition analysis |
| GET | `/api/analytics/medications/summary` | Medication adherence analysis |
| GET | `/api/analytics/meal-glucose-correlations` | Meal impact on glucose |
| GET | `/api/analytics/dashboard` | Comprehensive health dashboard |

### Data Export Endpoints ✅

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/export/glucose` | Export glucose readings to CSV |
| GET | `/api/export/meals` | Export meals data to CSV |
| GET | `/api/export/medications` | Export medications data to CSV |
| GET | `/api/export/comprehensive-report` | Export complete health report |
| GET | `/api/export/all-data` | Export all data (comprehensive report) |
| GET | `/api/export/formats` | Get available export formats info |

## 🧪 Test with Demo Data

The app includes sample data for testing:

**Demo User Login:**
```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo@glucocloud.com",
    "password": "demo123"
  }'
```

**Add a glucose reading:**
```bash
curl -X POST http://localhost:8082/api/glucose \
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
curl -X GET "http://localhost:8082/api/analytics/glucose/summary" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Add a meal:**
```bash
curl -X POST http://localhost:8082/api/meals \
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
curl -X POST http://localhost:8082/api/medications \
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
curl -X GET "http://localhost:8082/api/analytics/dashboard" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Get meal-glucose correlations:**
```bash
curl -X GET "http://localhost:8082/api/analytics/meal-glucose-correlations" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Export glucose data to CSV:**
```bash
curl -X GET "http://localhost:8082/api/export/glucose" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output glucose_readings.csv
```

**Export comprehensive health report:**
```bash
curl -X GET "http://localhost:8082/api/export/comprehensive-report" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output health_report.csv
```

**Export with date range:**
```bash
curl -X GET "http://localhost:8082/api/export/meals?from=2024-01-01T00:00:00&to=2024-01-31T23:59:59" \
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

**Complete Full-Stack Application! 🎉**

GlucoCloud is now a **complete full-stack diabetes management application** with:
- ✅ **Working Spring Boot Backend** (port 8082)
- ✅ **Complete React Frontend** (port 3001)
- ✅ **User Authentication & Dashboard**
- ✅ **Glucose, Meal & Medication Tracking**
- ✅ **Advanced Analytics & Data Export**
- ✅ **Ready for Production Deployment**

**🚀 Access your complete application at: http://localhost:3001**