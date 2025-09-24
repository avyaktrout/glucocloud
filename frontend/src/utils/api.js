import axios from 'axios';

// Base API configuration
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: async (email, password) => {
    const response = await api.post('/api/auth/login', { email, password });
    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
  },

  register: async (userData) => {
    const response = await api.post('/api/auth/register', userData);
    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    window.location.href = '/login';
  },

  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('authToken');
  },
};

// Glucose API
export const glucoseAPI = {
  getReadings: async (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;
    const response = await api.get('/api/glucose', { params });
    return response.data;
  },

  createReading: async (reading) => {
    const response = await api.post('/api/glucose', reading);
    return response.data;
  },

  updateReading: async (id, reading) => {
    const response = await api.put(`/api/glucose/${id}`, reading);
    return response.data;
  },

  deleteReading: async (id) => {
    await api.delete(`/api/glucose/${id}`);
  },
};

// Meals API
export const mealsAPI = {
  getMeals: async (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;
    const response = await api.get('/api/meals', { params });
    return response.data;
  },

  createMeal: async (meal) => {
    const response = await api.post('/api/meals', meal);
    return response.data;
  },

  updateMeal: async (id, meal) => {
    const response = await api.put(`/api/meals/${id}`, meal);
    return response.data;
  },

  deleteMeal: async (id) => {
    await api.delete(`/api/meals/${id}`);
  },
};

// Medications API
export const medicationsAPI = {
  getMedications: async (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;
    const response = await api.get('/api/medications', { params });
    return response.data;
  },

  createMedication: async (medication) => {
    const response = await api.post('/api/medications', medication);
    return response.data;
  },

  updateMedication: async (id, medication) => {
    const response = await api.put(`/api/medications/${id}`, medication);
    return response.data;
  },

  deleteMedication: async (id) => {
    await api.delete(`/api/medications/${id}`);
  },

  getMedicationNames: async () => {
    const response = await api.get('/api/medications/names');
    return response.data;
  },
};

// Analytics API
export const analyticsAPI = {
  getGlucoseSummary: async (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;
    const response = await api.get('/api/analytics/glucose/summary', { params });
    return response.data;
  },

  getGlucoseFlags: async (days = 30) => {
    const response = await api.get('/api/analytics/glucose/flags', { params: { days } });
    return response.data;
  },

  getMealSummary: async (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;
    const response = await api.get('/api/analytics/meals/summary', { params });
    return response.data;
  },

  getMedicationSummary: async (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;
    const response = await api.get('/api/analytics/medications/summary', { params });
    return response.data;
  },

  getMealGlucoseCorrelations: async (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;
    const response = await api.get('/api/analytics/meal-glucose-correlations', { params });
    return response.data;
  },

  getDashboard: async (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;
    const response = await api.get('/api/analytics/dashboard', { params });
    return response.data;
  },
};

// Export API
export const exportAPI = {
  exportGlucose: async (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;
    const response = await api.get('/api/export/glucose', {
      params,
      responseType: 'blob'
    });
    return response.data;
  },

  exportMeals: async (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;
    const response = await api.get('/api/export/meals', {
      params,
      responseType: 'blob'
    });
    return response.data;
  },

  exportMedications: async (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;
    const response = await api.get('/api/export/medications', {
      params,
      responseType: 'blob'
    });
    return response.data;
  },

  exportComprehensiveReport: async (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;
    const response = await api.get('/api/export/comprehensive-report', {
      params,
      responseType: 'blob'
    });
    return response.data;
  },
};

export default api;