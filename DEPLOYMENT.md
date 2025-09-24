# GlucoCloud Production Deployment Guide

Deploy GlucoCloud to production for **FREE** using Render.com! This guide will get your diabetes management platform live and accessible worldwide.

## 🚀 Free Deployment Options

### Option 1: Render.com (Recommended - 100% Free)

**Why Render.com?**
- ✅ **Completely Free**: No credit card required
- ✅ **Auto-Deploy**: GitHub integration with automatic deploys
- ✅ **PostgreSQL**: Free PostgreSQL database included
- ✅ **SSL**: Automatic HTTPS certificates
- ✅ **Global CDN**: Fast worldwide access
- ✅ **Zero Downtime**: Seamless deployments

### Option 2: Railway, Fly.io, or Heroku alternatives
- Similar free tiers available
- Follow similar steps with platform-specific configurations

## 📋 Prerequisites

1. **GitHub Account**: To host your code
2. **Render.com Account**: Sign up at https://render.com (free)
3. **Local Git**: Have your GlucoCloud code ready

## 🔧 Step-by-Step Deployment

### Step 1: Prepare Your Repository

1. **Initialize Git** (if not already done):
   ```bash
   git init
   git add .
   git commit -m "Initial GlucoCloud commit"
   ```

2. **Create GitHub Repository**:
   - Go to GitHub and create a new repository
   - Push your code:
   ```bash
   git remote add origin https://github.com/YOURUSERNAME/glucocloud.git
   git push -u origin main
   ```

### Step 2: Deploy on Render.com

1. **Sign up at Render.com** (free account)

2. **Create a New Web Service**:
   - Click "New +" → "Web Service"
   - Connect your GitHub repository
   - Select your GlucoCloud repository

3. **Configure Build Settings**:
   ```
   Name: glucocloud-api
   Environment: Java
   Build Command: ./mvnw clean package -DskipTests
   Start Command: java -Dserver.port=$PORT -jar target/glucocloud-api-0.0.1-SNAPSHOT.jar
   ```

4. **Set Environment Variables**:
   ```
   SPRING_PROFILES_ACTIVE=production
   JWT_SECRET=your-secure-jwt-secret-here-make-it-very-long-and-random
   ```

### Step 3: Create PostgreSQL Database

1. **In Render Dashboard**:
   - Click "New +" → "PostgreSQL"
   - Name: `glucocloud-db`
   - Region: Same as your web service
   - Plan: Free

2. **Get Database URL**:
   - Copy the "External Database URL"
   - Add to your web service environment variables:
   ```
   DATABASE_URL=postgresql://username:password@hostname:port/database
   ```

### Step 4: Deploy!

1. **Trigger Deployment**:
   - Render will automatically build and deploy
   - Monitor the deploy logs
   - First deploy takes ~5-10 minutes

2. **Verify Health**:
   - Your app will be available at: `https://glucocloud-api.onrender.com`
   - Check health: `https://glucocloud-api.onrender.com/actuator/health`

## 🧪 Test Your Live Deployment

Once deployed, test your live API:

```bash
# Register a user
curl -X POST https://glucocloud-api.onrender.com/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'

# Login and get token
curl -X POST https://glucocloud-api.onrender.com/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# Test with JWT token
curl -X GET "https://glucocloud-api.onrender.com/analytics/dashboard" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Export data
curl -X GET "https://glucocloud-api.onrender.com/export/comprehensive-report" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output health_report.csv
```

## 🔧 Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Activates production profile | `production` |
| `DATABASE_URL` | PostgreSQL connection string | `postgresql://user:pass@host:5432/db` |
| `JWT_SECRET` | Secret key for JWT signing | `your-super-secret-key-64-chars-min` |
| `PORT` | Server port (auto-set by Render) | `10000` |

## 📊 Monitoring & Maintenance

### Health Checks
- **Health Endpoint**: `/actuator/health`
- **Render Monitoring**: Built-in uptime monitoring
- **Database Health**: Automatic PostgreSQL monitoring

### Logs
- View logs in Render dashboard
- Real-time log streaming available
- Error tracking and alerts

### Updates
- **Automatic Deploys**: Push to GitHub → Auto-deploy
- **Zero Downtime**: Render handles rolling deployments
- **Rollback**: Easy rollback to previous versions

## 💰 Cost Breakdown (FREE!)

| Service | Cost | Limits |
|---------|------|---------|
| **Render Web Service** | $0/month | 750 hours/month (always-on) |
| **PostgreSQL Database** | $0/month | 1GB storage, 100 connections |
| **SSL Certificate** | $0/month | Automatic HTTPS |
| **Domain** | $0/month | `.onrender.com` subdomain |
| **Total** | **$0/month** | Perfect for diabetes management |

## 🌐 Custom Domain (Optional)

1. **Purchase Domain**: From any registrar
2. **Add to Render**: In your service settings
3. **Update DNS**: Point to Render's servers
4. **SSL**: Automatic certificate generation

## 🔒 Security Best Practices

1. **JWT Secret**: Use a strong, random secret (64+ characters)
2. **Database**: Use strong passwords (auto-generated by Render)
3. **CORS**: Configure allowed origins for frontend
4. **Rate Limiting**: Consider adding for production
5. **Environment Variables**: Never commit secrets to Git

## 🆘 Troubleshooting

### Common Issues

1. **Build Fails**:
   - Check Java version (requires Java 17)
   - Verify Maven wrapper permissions: `chmod +x mvnw`

2. **Database Connection Fails**:
   - Verify DATABASE_URL format
   - Check PostgreSQL service is running

3. **JWT Errors**:
   - Ensure JWT_SECRET is set and long enough
   - Check environment variable is properly set

4. **App Won't Start**:
   - Check logs in Render dashboard
   - Verify start command is correct
   - Ensure port binding: `java -Dserver.port=$PORT`

### Getting Help

- **Render Docs**: https://render.com/docs
- **Community**: Render Community Forum
- **Support**: Render support for technical issues

## 🎉 Success!

Once deployed, you'll have:

✅ **Live API**: Accessible worldwide 24/7
✅ **Secure Database**: PostgreSQL with automatic backups
✅ **HTTPS**: Automatic SSL certificates
✅ **Monitoring**: Health checks and logging
✅ **Auto-Deploy**: Push code → Instant deployment

**Your GlucoCloud API is now production-ready for real diabetes management!** 🩸📊

Share your API URL with users, integrate with frontends, or use it for personal diabetes tracking - completely free!

---

**Example Live URL**: `https://glucocloud-api.onrender.com`
**Documentation**: Available at your deployed URL + `/export/formats`