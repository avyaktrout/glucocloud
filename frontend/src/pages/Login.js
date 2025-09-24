import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authAPI } from '../utils/api';

const Login = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await authAPI.login(formData.email, formData.password);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleDemoLogin = async () => {
    setLoading(true);
    setError('');

    try {
      await authAPI.login('demo@glucocloud.com', 'demo123');
      navigate('/dashboard');
    } catch (err) {
      setError('Demo login failed. Please try manual login.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        {/* Header */}
        <div className="text-center">
          <div className="flex justify-center items-center space-x-2 mb-4">
            <i className="fas fa-heartbeat text-4xl text-blue-600"></i>
            <h1 className="text-3xl font-bold text-gray-900">GlucoCloud</h1>
          </div>
          <h2 className="text-xl text-gray-600">
            Professional Diabetes Management
          </h2>
          <p className="mt-2 text-sm text-gray-500">
            Sign in to access your health dashboard
          </p>
        </div>

        {/* Login Form */}
        <div className="bg-white rounded-xl shadow-lg p-8">
          {error && (
            <div className="alert alert-error mb-6">
              <i className="fas fa-exclamation-circle mr-2"></i>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="email" className="form-label">
                Email Address
              </label>
              <input
                id="email"
                name="email"
                type="email"
                autoComplete="email"
                required
                className="form-input"
                placeholder="Enter your email"
                value={formData.email}
                onChange={handleChange}
              />
            </div>

            <div>
              <label htmlFor="password" className="form-label">
                Password
              </label>
              <input
                id="password"
                name="password"
                type="password"
                autoComplete="current-password"
                required
                className="form-input"
                placeholder="Enter your password"
                value={formData.password}
                onChange={handleChange}
              />
            </div>

            <div>
              <button
                type="submit"
                disabled={loading}
                className="w-full btn-primary flex justify-center items-center"
              >
                {loading ? (
                  <>
                    <div className="loading-spinner mr-2"></div>
                    Signing in...
                  </>
                ) : (
                  <>
                    <i className="fas fa-sign-in-alt mr-2"></i>
                    Sign In
                  </>
                )}
              </button>
            </div>
          </form>

          {/* Demo Login */}
          <div className="mt-6 pt-6 border-t border-gray-200">
            <button
              onClick={handleDemoLogin}
              disabled={loading}
              className="w-full btn-secondary flex justify-center items-center"
            >
              <i className="fas fa-eye mr-2"></i>
              Try Demo Account
            </button>
            <p className="mt-2 text-xs text-gray-500 text-center">
              Explore with pre-loaded sample data
            </p>
          </div>

          {/* Register Link */}
          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              Don't have an account?{' '}
              <Link
                to="/register"
                className="font-medium text-blue-600 hover:text-blue-500 transition-colors"
              >
                Sign up here
              </Link>
            </p>
          </div>
        </div>

        {/* Features */}
        <div className="bg-white rounded-xl shadow-lg p-6 mt-8">
          <h3 className="text-lg font-semibold text-gray-800 mb-4 text-center">
            Why Choose GlucoCloud?
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div className="flex items-start space-x-2">
              <i className="fas fa-chart-line text-blue-600 mt-1"></i>
              <span className="text-gray-600">Advanced Analytics</span>
            </div>
            <div className="flex items-start space-x-2">
              <i className="fas fa-download text-blue-600 mt-1"></i>
              <span className="text-gray-600">Data Export</span>
            </div>
            <div className="flex items-start space-x-2">
              <i className="fas fa-shield-alt text-blue-600 mt-1"></i>
              <span className="text-gray-600">Secure & Private</span>
            </div>
            <div className="flex items-start space-x-2">
              <i className="fas fa-mobile-alt text-blue-600 mt-1"></i>
              <span className="text-gray-600">Mobile Friendly</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;