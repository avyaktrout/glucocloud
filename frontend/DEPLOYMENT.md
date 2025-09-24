# GlucoCloud Frontend Deployment Guide

## Deployment Options

### 1. Netlify (Recommended)

Netlify offers free hosting with automatic builds from Git repositories.

#### Steps:

1. **Prepare your repository:**
   ```bash
   # Make sure your frontend code is in a Git repository
   git init
   git add .
   git commit -m "Initial commit"

   # Push to GitHub, GitLab, or Bitbucket
   git remote add origin <your-repo-url>
   git push -u origin main
   ```

2. **Deploy to Netlify:**
   - Go to [netlify.com](https://netlify.com)
   - Sign up/Login with your Git provider
   - Click "New site from Git"
   - Choose your repository
   - Set build settings:
     - **Build command:** `npm run build`
     - **Publish directory:** `build`
     - **Base directory:** `frontend` (if your frontend is in a subdirectory)

3. **Environment Variables:**
   - In Netlify dashboard, go to Site settings → Environment variables
   - Add:
     ```
     REACT_APP_API_URL=https://your-backend-url.onrender.com
     ```

4. **Configure redirects:**
   Create `frontend/public/_redirects` file:
   ```
   /*    /index.html   200
   ```

### 2. Vercel

Vercel is another excellent free hosting option.

#### Steps:

1. **Install Vercel CLI:**
   ```bash
   npm i -g vercel
   ```

2. **Deploy:**
   ```bash
   cd frontend
   vercel
   ```

3. **Configure:**
   - Set build command: `npm run build`
   - Set output directory: `build`
   - Add environment variable: `REACT_APP_API_URL`

### 3. GitHub Pages

Free hosting directly from your GitHub repository.

#### Steps:

1. **Install gh-pages:**
   ```bash
   cd frontend
   npm install --save-dev gh-pages
   ```

2. **Update package.json:**
   ```json
   {
     "homepage": "https://yourusername.github.io/glucocloud",
     "scripts": {
       "predeploy": "npm run build",
       "deploy": "gh-pages -d build"
     }
   }
   ```

3. **Deploy:**
   ```bash
   npm run deploy
   ```

## Build Optimization

### 1. Environment Configuration

Create `.env.production` file:
```
REACT_APP_API_URL=https://your-production-backend-url.com
GENERATE_SOURCEMAP=false
```

### 2. Build for Production

```bash
cd frontend
npm run build
```

### 3. Test Production Build Locally

```bash
npx serve -s build
```

## Performance Optimizations

### 1. Code Splitting
Already implemented with React.lazy() in routing.

### 2. Bundle Analysis
```bash
npm install --save-dev webpack-bundle-analyzer
npm run build
npx webpack-bundle-analyzer build/static/js/*.js
```

### 3. PWA Features
The app is already PWA-ready with service worker support.

## Security Considerations

1. **Environment Variables:**
   - Never commit `.env` files to Git
   - Use `REACT_APP_` prefix for public variables
   - Keep sensitive data on the backend

2. **HTTPS:**
   - All deployment platforms provide HTTPS by default
   - Ensure backend APIs also use HTTPS

3. **Content Security Policy:**
   Add to `public/index.html`:
   ```html
   <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:;">
   ```

## Monitoring and Analytics

### 1. Google Analytics (Optional)
Add to `public/index.html`:
```html
<!-- Google Analytics -->
<script async src="https://www.googletagmanager.com/gtag/js?id=GA_TRACKING_ID"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());
  gtag('config', 'GA_TRACKING_ID');
</script>
```

### 2. Error Monitoring
Consider integrating Sentry for error tracking:
```bash
npm install @sentry/react
```

## Domain Configuration

### Custom Domain Setup:

1. **Purchase domain** from provider like Namecheap, GoDaddy, etc.

2. **Configure DNS:**
   - For Netlify: Add CNAME record pointing to `yoursite.netlify.app`
   - For Vercel: Add CNAME record pointing to `cname.vercel-dns.com`

3. **Update deployment platform:**
   - Add custom domain in platform settings
   - Enable HTTPS (usually automatic)

## Troubleshooting

### Common Issues:

1. **Build Fails:**
   - Check Node.js version (use LTS)
   - Clear node_modules and reinstall: `rm -rf node_modules package-lock.json && npm install`

2. **API Connection Issues:**
   - Verify REACT_APP_API_URL is correct
   - Check CORS settings on backend
   - Ensure backend is deployed and accessible

3. **Routing Issues:**
   - Make sure `_redirects` file is in `public` folder for Netlify
   - For Apache, use `.htaccess` file

4. **Performance Issues:**
   - Enable gzip compression
   - Optimize images
   - Use React.memo for expensive components

## Maintenance

### Regular Updates:
```bash
# Update dependencies
npm update

# Security audit
npm audit
npm audit fix

# Check for outdated packages
npx npm-check-updates
```

### Backup Strategy:
- Git repository serves as primary backup
- Export user data regularly via CSV export feature
- Keep deployment configurations documented

---

## Quick Deploy Commands

**Netlify:**
```bash
# One-time setup
npm install -g netlify-cli
netlify login

# Deploy
cd frontend
npm run build
netlify deploy --prod --dir=build
```

**Vercel:**
```bash
# One-time setup
npm install -g vercel
vercel login

# Deploy
cd frontend
vercel --prod
```

Your GlucoCloud frontend is now ready for production deployment! 🚀