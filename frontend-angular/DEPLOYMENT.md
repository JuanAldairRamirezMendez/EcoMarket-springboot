# EcoMarket Frontend - Vercel Deployment Guide

## Issue Fixed
The application was failing to deploy on Vercel because it was trying to make HTTP requests to `localhost:8090` during the build process. This has been resolved by:

1. **Environment Configuration**: Added proper environment files to separate development and production API URLs
2. **Build Configuration**: Removed SSR (Server-Side Rendering) to create a SPA (Single Page Application) build
3. **Service Updates**: Updated all services to use environment-based API URLs

## Files Modified

### Environment Files
- `src/environments/environment.ts` - Development environment
- `src/environments/environment.prod.ts` - Production environment

### Service Files
- `src/app/services/products.service.ts` - Updated to use environment.apiUrl
- `src/app/services/auth.service.ts` - Updated to use environment.apiUrl

### Configuration Files
- `angular.json` - Removed SSR configuration, added environment file replacements
- `package.json` - Added SPA build script
- `vercel.json` - Updated for proper SPA deployment

## Deployment Steps

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
The application will now build successfully on Vercel using the SPA configuration.

## Local Development
For local development, the app will continue to use `http://localhost:8090/ecomarket/api` as specified in `environment.ts`.

## Architecture Changes
- **Before**: SSR application that tried to call APIs during build
- **After**: SPA that makes API calls only in the browser

This ensures the build process completes successfully while maintaining all functionality.
