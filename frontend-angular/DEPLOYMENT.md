# EcoMarket Frontend - Deployment Guide

## ✅ Issues Fixed

### Original Problem
The application was failing to deploy on Vercel with the following errors:
- HTTP requests to `localhost:8090` during build process
- Puppeteer SSL certificate issues in corporate environments  
- NPM installation failures due to file permission issues

### Solutions Applied

1. **Environment Configuration**: Added proper environment files to separate development and production API URLs
2. **Build Configuration**: Removed SSR (Server-Side Rendering) to create a SPA (Single Page Application) build
3. **Service Updates**: Updated all services to use environment-based API URLs
4. **NPM Installation**: Fixed Puppeteer download issues and dependency conflicts

## 📁 Files Modified

### Environment Files
- `src/environments/environment.ts` - Development environment (localhost:8090)
- `src/environments/environment.prod.ts` - Production environment

### Service Files
- `src/app/services/products.service.ts` - Updated to use environment.apiUrl
- `src/app/services/auth.service.ts` - Updated to use environment.apiUrl

### Configuration Files
- `angular.json` - Removed SSR configuration, added environment file replacements
- `package.json` - Added SPA build script
- `vercel.json` - Updated for proper SPA deployment with Puppeteer skip
- `.nvmrc` - Added Node.js version specification

## 🚀 Installation & Development

### Local Setup
```powershell
# Set environment variable to skip Puppeteer download
$env:PUPPETEER_SKIP_DOWNLOAD="true"

# Install dependencies
npm install --legacy-peer-deps

# Start development server
npm start
```

The app will be available at `http://localhost:4200/`

### Build Commands
```powershell
# Regular build
npm run build

# SPA build (for Vercel)
npm run build:spa
```

## 🌐 Deployment Steps

### 1. Update Backend URL
Before deploying, update the API URL in `src/environments/environment.prod.ts`:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-actual-backend-url.com/ecomarket/api'
};
```

### 2. Deploy Backend First
Make sure your Spring Boot backend is deployed and accessible at the URL specified above.

### 3. Deploy to Vercel
The application will now build successfully on Vercel using the configuration in `vercel.json`:

- **Build Command**: `npm run build:spa`
- **Output Directory**: `dist/frontend-angular/browser/browser`
- **Environment Variables**: `PUPPETEER_SKIP_DOWNLOAD=true`
- **Install Command**: `npm install --legacy-peer-deps`

## 🔧 Troubleshooting

### NPM Installation Issues
If you encounter Puppeteer or SSL certificate issues:
```powershell
$env:PUPPETEER_SKIP_DOWNLOAD="true"
npm cache clean --force
Remove-Item -Recurse -Force node_modules -ErrorAction SilentlyContinue
Remove-Item package-lock.json -ErrorAction SilentlyContinue
npm install --legacy-peer-deps
```

### Build Failures
- Ensure you're using the correct Node.js version (22.14.0 as specified in `.nvmrc`)
- Use `npm run build:spa` for deployment builds
- Check that environment files are properly configured

## 📊 Architecture Changes
- **Before**: SSR application that tried to call APIs during build
- **After**: SPA that makes API calls only in the browser
- **Result**: Successful builds without localhost dependency issues

## ✅ Status
- ✅ NPM installation working
- ✅ Development server running (localhost:4200)
- ✅ Production build successful
- ✅ SPA build successful
- ✅ Vercel configuration updated
- ✅ Environment variables configured
