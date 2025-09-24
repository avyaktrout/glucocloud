import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { analyticsAPI, glucoseAPI } from '../utils/api';

const Dashboard = () => {
  const [dashboardData, setDashboardData] = useState(null);
  const [recentReadings, setRecentReadings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [dashboard, readings] = await Promise.all([
        analyticsAPI.getDashboard(),
        glucoseAPI.getReadings()
      ]);

      setDashboardData(dashboard);
      setRecentReadings(readings.slice(0, 5)); // Get latest 5 readings
    } catch (err) {
      setError('Failed to load dashboard data');
      console.error('Dashboard error:', err);
    } finally {
      setLoading(false);
    }
  };

  const getHealthScoreColor = (score) => {
    if (score >= 90) return 'health-score-excellent';
    if (score >= 80) return 'health-score-good';
    if (score >= 70) return 'health-score-fair';
    return 'health-score-poor';
  };

  const getGlucoseStatusColor = (reading) => {
    if (reading.status === 'CRITICALLY_HIGH' || reading.status === 'CRITICALLY_LOW') {
      return 'glucose-critical';
    }
    if (reading.status === 'HIGH') return 'glucose-high';
    if (reading.status === 'LOW') return 'glucose-low';
    return 'glucose-normal';
  };

  const formatDateTime = (dateTime) => {
    return new Date(dateTime).toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
    });
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="loading-spinner"></div>
        <span className="ml-2 text-gray-600">Loading dashboard...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-error">
        <i className="fas fa-exclamation-triangle mr-2"></i>
        {error}
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600 mt-1">
            Welcome back! Here's your diabetes management overview.
          </p>
        </div>
        <div className="flex space-x-3">
          <Link to="/glucose" className="btn-primary">
            <i className="fas fa-plus mr-2"></i>
            Add Reading
          </Link>
        </div>
      </div>

      {/* Health Score */}
      {dashboardData && (
        <div className={`metric-card ${getHealthScoreColor(dashboardData.healthScore)}`}>
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-lg font-semibold mb-2">Health Score</h3>
              <div className="flex items-center space-x-4">
                <span className="text-4xl font-bold">
                  {dashboardData.healthScore}/100
                </span>
                <div>
                  <p className="text-sm font-medium">
                    {dashboardData.healthScoreDescription}
                  </p>
                </div>
              </div>
            </div>
            <i className="fas fa-heart text-4xl opacity-20"></i>
          </div>
        </div>
      )}

      {/* Quick Stats */}
      {dashboardData?.glucoseSummary && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div className="metric-card">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Time in Range</p>
                <p className="text-2xl font-bold text-green-600">
                  {dashboardData.glucoseSummary.timeInRangePercentage.toFixed(1)}%
                </p>
                <p className="text-xs text-gray-500">Target: >70%</p>
              </div>
              <i className="fas fa-target text-2xl text-green-600"></i>
            </div>
          </div>

          <div className="metric-card">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Avg Glucose</p>
                <p className="text-2xl font-bold text-blue-600">
                  {dashboardData.glucoseSummary.averageReading?.toFixed(0)} mg/dL
                </p>
                <p className="text-xs text-gray-500">Last 30 days</p>
              </div>
              <i className="fas fa-chart-line text-2xl text-blue-600"></i>
            </div>
          </div>

          <div className="metric-card">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Total Readings</p>
                <p className="text-2xl font-bold text-purple-600">
                  {dashboardData.glucoseSummary.totalReadings}
                </p>
                <p className="text-xs text-gray-500">This month</p>
              </div>
              <i className="fas fa-heartbeat text-2xl text-purple-600"></i>
            </div>
          </div>

          <div className="metric-card">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Trend</p>
                <p className={`text-2xl font-bold ${
                  dashboardData.glucoseSummary.trend === 'IMPROVING' ? 'text-green-600' :
                  dashboardData.glucoseSummary.trend === 'WORSENING' ? 'text-red-600' :
                  'text-gray-600'
                }`}>
                  {dashboardData.glucoseSummary.trend}
                </p>
                <p className="text-xs text-gray-500">7-day trend</p>
              </div>
              <i className={`fas text-2xl ${
                dashboardData.glucoseSummary.trend === 'IMPROVING' ? 'fa-arrow-up text-green-600' :
                dashboardData.glucoseSummary.trend === 'WORSENING' ? 'fa-arrow-down text-red-600' :
                'fa-minus text-gray-600'
              }`}></i>
            </div>
          </div>
        </div>
      )}

      {/* Recent Readings & Insights */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Readings */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-900">Recent Readings</h3>
            <Link
              to="/glucose"
              className="text-blue-600 hover:text-blue-700 text-sm font-medium"
            >
              View All
            </Link>
          </div>

          {recentReadings.length > 0 ? (
            <div className="space-y-3">
              {recentReadings.map((reading) => (
                <div
                  key={reading.id}
                  className={`glucose-card ${getGlucoseStatusColor(reading)} p-4`}
                >
                  <div className="flex items-center justify-between">
                    <div>
                      <div className="flex items-center space-x-3">
                        <span className="text-xl font-bold">
                          {reading.readingValue} mg/dL
                        </span>
                        <span className={`text-sm px-2 py-1 rounded text-white ${
                          reading.status === 'NORMAL' ? 'bg-green-500' :
                          reading.status === 'HIGH' ? 'bg-yellow-500' :
                          reading.status === 'LOW' ? 'bg-blue-500' :
                          'bg-red-500'
                        }`}>
                          {reading.status}
                        </span>
                      </div>
                      <p className="text-sm text-gray-600 mt-1">
                        {reading.readingType} • {formatDateTime(reading.takenAt)}
                      </p>
                    </div>
                    <div className="text-right">
                      <i className={`fas text-xl ${
                        reading.inRange ? 'fa-check-circle text-green-500' : 'fa-exclamation-circle text-yellow-500'
                      }`}></i>
                    </div>
                  </div>
                  {reading.note && (
                    <p className="text-sm text-gray-600 mt-2 italic">
                      "{reading.note}"
                    </p>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8 text-gray-500">
              <i className="fas fa-heartbeat text-4xl mb-4"></i>
              <p>No glucose readings yet</p>
              <Link to="/glucose" className="btn-primary mt-4 inline-flex items-center">
                <i className="fas fa-plus mr-2"></i>
                Add First Reading
              </Link>
            </div>
          )}
        </div>

        {/* Key Insights */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Key Insights</h3>

          {dashboardData?.insights && Object.keys(dashboardData.insights).length > 0 ? (
            <div className="space-y-4">
              {Object.entries(dashboardData.insights).map(([key, value]) => (
                <div key={key} className="flex items-start space-x-3">
                  <i className="fas fa-lightbulb text-yellow-500 mt-1"></i>
                  <div>
                    <p className="text-sm text-gray-800">{value}</p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8 text-gray-500">
              <i className="fas fa-chart-line text-4xl mb-4"></i>
              <p>Insights will appear as you add more data</p>
            </div>
          )}

          {dashboardData?.recommendations && Object.keys(dashboardData.recommendations).length > 0 && (
            <div className="mt-6 pt-6 border-t border-gray-200">
              <h4 className="font-medium text-gray-900 mb-3">Recommendations</h4>
              <div className="space-y-3">
                {Object.entries(dashboardData.recommendations).map(([key, value]) => (
                  <div key={key} className="flex items-start space-x-3">
                    <i className="fas fa-arrow-right text-blue-500 mt-1 text-sm"></i>
                    <p className="text-sm text-gray-700">{value}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <Link
            to="/glucose"
            className="flex flex-col items-center p-4 border rounded-lg hover:bg-gray-50 transition-colors"
          >
            <i className="fas fa-heartbeat text-2xl text-red-500 mb-2"></i>
            <span className="text-sm font-medium">Log Glucose</span>
          </Link>

          <Link
            to="/meals"
            className="flex flex-col items-center p-4 border rounded-lg hover:bg-gray-50 transition-colors"
          >
            <i className="fas fa-utensils text-2xl text-green-500 mb-2"></i>
            <span className="text-sm font-medium">Log Meal</span>
          </Link>

          <Link
            to="/medications"
            className="flex flex-col items-center p-4 border rounded-lg hover:bg-gray-50 transition-colors"
          >
            <i className="fas fa-pills text-2xl text-blue-500 mb-2"></i>
            <span className="text-sm font-medium">Log Medication</span>
          </Link>

          <Link
            to="/export"
            className="flex flex-col items-center p-4 border rounded-lg hover:bg-gray-50 transition-colors"
          >
            <i className="fas fa-download text-2xl text-purple-500 mb-2"></i>
            <span className="text-sm font-medium">Export Data</span>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;