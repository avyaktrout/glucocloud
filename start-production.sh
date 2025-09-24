#!/bin/bash
# Production startup script for GlucoCloud

echo "🚀 Starting GlucoCloud in Production Mode..."

# Check Java version
java -version

# Check if required environment variables are set
if [ -z "$DATABASE_URL" ]; then
    echo "❌ ERROR: DATABASE_URL environment variable is not set"
    exit 1
fi

if [ -z "$JWT_SECRET" ]; then
    echo "❌ ERROR: JWT_SECRET environment variable is not set"
    exit 1
fi

echo "✅ Environment variables configured"

# Set production profile
export SPRING_PROFILES_ACTIVE=production

# Set server port (default to 8080 if not set by platform)
export SERVER_PORT=${PORT:-8080}

echo "🔧 Configuration:"
echo "  - Profile: production"
echo "  - Port: $SERVER_PORT"
echo "  - Database: Configured"
echo "  - JWT: Configured"

# Start the application
echo "🎯 Starting GlucoCloud API..."
java -Dserver.port=$SERVER_PORT \
     -Xms256m -Xmx512m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -Dspring.profiles.active=production \
     -jar target/glucocloud-api-0.0.1-SNAPSHOT.jar

echo "🏁 GlucoCloud stopped"