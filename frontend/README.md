# GlucoCloud Frontend

A modern React dashboard for diabetes management and glucose tracking.

## 🚀 Features

- **User Authentication** - Secure login/register with JWT tokens
- **Glucose Tracking** - Log and monitor blood glucose readings with visual status indicators
- **Meal Logging** - Track meals with carbohydrate counting and nutrition information
- **Medication Management** - Log medications with dosages and effectiveness ratings
- **Advanced Analytics** - Interactive charts and visualizations using Chart.js
- **Data Export** - Professional CSV exports for healthcare providers
- **Responsive Design** - Works seamlessly on desktop, tablet, and mobile devices
- **Real-time Insights** - Personalized health scoring and recommendations

## 🛠️ Technology Stack

- **Frontend Framework:** React 18
- **Styling:** Tailwind CSS
- **Charts:** Chart.js & React-Chart.js-2
- **HTTP Client:** Axios
- **Routing:** React Router v6
- **Icons:** Font Awesome
- **Build Tool:** Create React App

## 📦 Installation

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd glucocloud/frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Set up environment variables:**
   ```bash
   cp .env.example .env
   ```
   Edit `.env` and update the API URL:
   ```
   REACT_APP_API_URL=http://localhost:8080
   ```

4. **Start the development server:**
   ```bash
   npm start
   ```

The app will open at [http://localhost:3000](http://localhost:3000)

## 🏗️ Build for Production

```bash
npm run build
```

This creates an optimized production build in the `build` folder.

## 📱 Application Structure

```
src/
├── components/
│   ├── auth/
│   │   ├── Login.js          # Login form component
│   │   └── Register.js       # Registration form component
│   └── layout/
│       └── Navbar.js         # Navigation bar component
├── pages/
│   ├── Dashboard.js          # Main dashboard with health metrics
│   ├── Glucose.js           # Glucose readings CRUD interface
│   ├── Meals.js             # Meal logging interface
│   ├── Medications.js       # Medication tracking interface
│   ├── Analytics.js         # Data visualization and charts
│   └── Export.js            # CSV export functionality
├── utils/
│   └── api.js               # API client and HTTP utilities
├── App.js                   # Main app component with routing
└── index.js                 # Application entry point
```

## 🔧 API Integration

The frontend communicates with the Spring Boot backend via RESTful APIs:

- **Authentication:** JWT-based login/register
- **Glucose Readings:** Full CRUD operations
- **Meals:** Meal logging with nutrition tracking
- **Medications:** Medication management with effectiveness ratings
- **Analytics:** Health scoring and insights
- **Export:** CSV data export functionality

## 📊 Charts and Visualizations

The analytics page features multiple chart types:

- **Glucose Trends:** Time-series line chart with color-coded status indicators
- **Daily Averages:** Bar chart showing daily glucose averages
- **Time in Range:** Distribution chart showing glucose range percentages
- **Carbs vs Glucose:** Correlation scatter plot for meal impact analysis

## 🎨 UI/UX Features

- **Modern Design:** Clean, healthcare-focused interface
- **Responsive Layout:** Mobile-first design with Tailwind CSS
- **Status Indicators:** Color-coded glucose status (Normal, High, Low, Critical)
- **Interactive Forms:** Real-time validation and user feedback
- **Loading States:** Smooth loading animations and skeleton screens
- **Alert System:** Success/error notifications for user actions

## 🔐 Security Features

- **JWT Authentication:** Secure token-based authentication
- **Protected Routes:** Route guards for authenticated pages
- **Input Validation:** Client-side form validation
- **XSS Protection:** Secure handling of user input
- **HTTPS Ready:** Production deployment with SSL support

## 🚀 Deployment

See [DEPLOYMENT.md](./DEPLOYMENT.md) for detailed deployment instructions for:

- **Netlify** (Recommended)
- **Vercel**
- **GitHub Pages**

### Quick Deploy Commands:

**Netlify:**
```bash
npm run build
netlify deploy --prod --dir=build
```

**Vercel:**
```bash
vercel --prod
```

## 🔧 Development

### Available Scripts:

- `npm start` - Start development server
- `npm test` - Run test suite
- `npm run build` - Build for production
- `npm run eject` - Eject from Create React App (⚠️ irreversible)

### Code Style:

- Use functional components with hooks
- Follow React best practices
- Use Tailwind CSS for styling
- Implement proper error handling
- Use async/await for API calls

## 🧪 Testing

```bash
npm test
```

Run tests in watch mode for development.

## 📈 Performance Optimizations

- **Code Splitting:** Lazy loading of route components
- **Image Optimization:** Responsive images with proper sizing
- **Bundle Analysis:** Webpack bundle analyzer integration
- **Caching:** Browser caching for static assets
- **Minification:** Production build optimization

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:

1. Check the [Issues](../../issues) page
2. Review the [DEPLOYMENT.md](./DEPLOYMENT.md) guide
3. Refer to the backend [README](../README.md) for API documentation

## 🎯 Demo Credentials

For testing purposes, you can use:
- **Email:** demo@glucocloud.com
- **Password:** demo123

---

Built with ❤️ for better diabetes management