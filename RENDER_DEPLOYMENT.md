# Render Deployment Configuration for EcoMarket (Fr2. **Frontend is Already Configured:**
   The Angular frontend is already configured to use relative paths (`/ecomarket/api`)
   since nginx will proxy API requests to the Spring Boot backend.

3. **Test the Deployment:**
   - Your complete application will be available at: `https://ecomarket-springboot.onrender.com`
   - Frontend: `https://ecomarket-springboot.onrender.com` (served by nginx)
   - API endpoints: `https://ecomarket-springboot.onrender.com/ecomarket/api/products` (proxied to Spring Boot)kend)

## Service Configuration

### Basic Settings
- **Service Name**: EcoMarket-springboot
- **Language**: Docker
- **Branch**: main
- **Region**: Oregon (US West)
- **Root Directory**: (leave empty)
- **Dockerfile Path**: `./Dockerfile` ⚠️ **IMPORTANT: Just `./Dockerfile` (moved to root)**

### Instance Type
- **For Testing**: Free ($0/month) - 512 MB RAM, 0.1 CPU
- **For Production**: Starter ($9/month) - 512 MB RAM, 0.5 CPU
- **Recommended**: Standard ($25/month) - 2 GB RAM, 1 CPU (for better performance with both services)

### Environment Variables
Add these environment variables in Render:

```
DATABASE_URL=jdbc:postgresql://your-postgres-host:5432/ecomarket_db
DB_USERNAME=your-db-username
DB_PASSWORD=your-db-password
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=your-very-long-secret-key-here-at-least-256-bits-long-should-be-random
CORS_ALLOWED_ORIGINS=https://your-frontend-vercel-app.vercel.app
PORT=8080
JAVA_OPTS=-Xmx400m
```

## Database Setup (Required!)

1. **Create PostgreSQL Database on Render:**
   - Go to Dashboard → New → PostgreSQL
   - Name: `ecomarket-database`
   - Plan: Free or Starter
   - Region: Same as your web service (Oregon US West)

2. **Get Database Connection Details:**
   After creating the database, copy:
   - External Database URL
   - Username
   - Password
   - Database name

3. **Update Environment Variables:**
   Use the database connection details in the environment variables above.

## Post-Deployment Steps

1. **Update Frontend Environment:**
   Update your Angular frontend's `environment.prod.ts`:
   ```typescript
   export const environment = {
     production: true,
     apiUrl: 'https://ecomarket-springboot.onrender.com/ecomarket/api'
   };
   ```

2. **Test the Deployment:**
   - Your backend will be available at: `https://ecomarket-springboot.onrender.com`
   - API endpoints: `https://ecomarket-springboot.onrender.com/ecomarket/api/products`

3. **Database Initialization:**
   The app should automatically create tables on first run using JPA/Hibernate.

## Architecture

This deployment uses a single Docker container that runs:
1. **Angular Frontend**: Served by nginx on port 80
2. **Spring Boot Backend**: Running on port 8080
3. **nginx Reverse Proxy**: Routes `/ecomarket/api/*` to Spring Boot, everything else to Angular

Benefits:
- ✅ Single service deployment (simpler and cheaper)
- ✅ No CORS issues (same origin)
- ✅ Automatic routing between frontend and backend
- ✅ Production-ready nginx serving static files

## Important Notes

- **Free Tier Limitations**: Services sleep after 15 minutes of inactivity
- **Build Time**: First build takes 10-15 minutes (builds both Angular and Spring Boot)
- **Memory Usage**: Recommended minimum 1 GB RAM for both services
- **Health Checks**: Render automatically monitors your service health
- **Logs**: Available in the Render dashboard for debugging

## Security Recommendations

1. Use strong, random JWT_SECRET (at least 256 bits)
2. Limit CORS_ALLOWED_ORIGINS to your actual frontend domains
3. Use environment-specific database credentials
4. Enable HTTPS (automatic with Render)

## Troubleshooting

If deployment fails:
1. Check build logs in Render dashboard
2. Verify Dockerfile path is correct: `./backend/Dockerfile`  
3. Ensure all environment variables are set
4. Check database connectivity
