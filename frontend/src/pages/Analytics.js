import React, { useState, useEffect } from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
  TimeScale,
} from 'chart.js';
import { Line, Bar } from 'react-chartjs-2';
import { analyticsAPI, glucoseAPI, mealsAPI } from '../utils/api';
import 'chartjs-adapter-date-fns';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
  TimeScale
);

const Analytics = () => {
  const [analytics, setAnalytics] = useState(null);
  const [glucoseData, setGlucoseData] = useState([]);
  const [mealsData, setMealsData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [timeRange, setTimeRange] = useState('7'); // days

  useEffect(() => {
    fetchAnalyticsData();
  }, [timeRange]);

  const fetchAnalyticsData = async () => {
    try {
      setLoading(true);
      setError('');

      const [analyticsResponse, glucoseResponse, mealsResponse] = await Promise.all([
        analyticsAPI.getDashboard(),
        glucoseAPI.getReadings(),
        mealsAPI.getMeals()
      ]);

      setAnalytics(analyticsResponse);

      // Filter data by time range
      const cutoffDate = new Date();
      cutoffDate.setDate(cutoffDate.getDate() - parseInt(timeRange));

      const filteredGlucose = glucoseResponse.filter(reading =>
        new Date(reading.takenAt) >= cutoffDate
      );
      const filteredMeals = mealsResponse.filter(meal =>
        new Date(meal.consumedAt) >= cutoffDate
      );

      setGlucoseData(filteredGlucose);
      setMealsData(filteredMeals);
    } catch (err) {
      setError('Failed to load analytics data');
      console.error('Analytics error:', err);
    } finally {
      setLoading(false);
    }
  };

  // Glucose trend chart data
  const glucoseTrendData = {
    labels: glucoseData.map(reading => new Date(reading.takenAt)),
    datasets: [
      {
        label: 'Glucose Reading (mg/dL)',
        data: glucoseData.map(reading => ({
          x: new Date(reading.takenAt),
          y: reading.readingValue
        })),
        borderColor: 'rgb(59, 130, 246)',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        fill: true,
        tension: 0.4,
        pointBackgroundColor: glucoseData.map(reading => {
          if (reading.status === 'HIGH' || reading.status === 'CRITICALLY_HIGH') return 'rgb(239, 68, 68)';
          if (reading.status === 'LOW' || reading.status === 'CRITICALLY_LOW') return 'rgb(234, 179, 8)';
          return 'rgb(34, 197, 94)';
        }),
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: 6,
      }
    ]
  };

  // Daily averages chart
  const dailyAverages = {};
  glucoseData.forEach(reading => {
    const date = new Date(reading.takenAt).toDateString();
    if (!dailyAverages[date]) {
      dailyAverages[date] = { sum: 0, count: 0 };
    }
    dailyAverages[date].sum += reading.readingValue;
    dailyAverages[date].count += 1;
  });

  const dailyAverageData = {
    labels: Object.keys(dailyAverages).map(date => new Date(date)),
    datasets: [
      {
        label: 'Daily Average (mg/dL)',
        data: Object.values(dailyAverages).map(day => day.sum / day.count),
        backgroundColor: 'rgba(34, 197, 94, 0.6)',
        borderColor: 'rgb(34, 197, 94)',
        borderWidth: 1,
      }
    ]
  };

  // Carbs vs Glucose correlation
  const carbsVsGlucose = mealsData.map(meal => {
    // Find glucose readings 1-3 hours after meal
    const mealTime = new Date(meal.consumedAt);
    const postMealReadings = glucoseData.filter(reading => {
      const readingTime = new Date(reading.takenAt);
      const timeDiff = (readingTime - mealTime) / (1000 * 60 * 60); // hours
      return timeDiff >= 1 && timeDiff <= 3;
    });

    if (postMealReadings.length > 0) {
      const avgGlucose = postMealReadings.reduce((sum, r) => sum + r.readingValue, 0) / postMealReadings.length;
      return {
        x: meal.carbsGrams,
        y: avgGlucose,
        label: meal.name
      };
    }
    return null;
  }).filter(Boolean);

  const carbsCorrelationData = {
    datasets: [
      {
        label: 'Carbs vs Post-Meal Glucose',
        data: carbsVsGlucose,
        backgroundColor: 'rgba(147, 51, 234, 0.6)',
        borderColor: 'rgb(147, 51, 234)',
        pointRadius: 8,
        pointHoverRadius: 10,
      }
    ]
  };

  // Time in range distribution
  const timeInRangeData = analytics ? {
    labels: ['In Range', 'High', 'Low', 'Critical'],
    datasets: [
      {
        label: 'Time in Range (%)',
        data: [
          analytics.timeInRange?.normal || 0,
          analytics.timeInRange?.high || 0,
          analytics.timeInRange?.low || 0,
          analytics.timeInRange?.critical || 0
        ],
        backgroundColor: [
          'rgba(34, 197, 94, 0.8)',
          'rgba(234, 179, 8, 0.8)',
          'rgba(59, 130, 246, 0.8)',
          'rgba(239, 68, 68, 0.8)'
        ],
        borderColor: [
          'rgb(34, 197, 94)',
          'rgb(234, 179, 8)',
          'rgb(59, 130, 246)',
          'rgb(239, 68, 68)'
        ],
        borderWidth: 2,
      }
    ]
  } : null;

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
      },
    },
    scales: {
      x: {
        type: 'time',
        time: {
          displayFormats: {
            day: 'MMM dd',
            hour: 'HH:mm'
          }
        }
      },
      y: {
        beginAtZero: false,
        grid: {
          color: 'rgba(0, 0, 0, 0.1)',
        }
      }
    }
  };

  const barChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        grid: {
          color: 'rgba(0, 0, 0, 0.1)',
        }
      }
    }
  };

  const scatterOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
      },
      tooltip: {
        callbacks: {
          label: function(context) {
            const point = context.raw;
            return `${point.label}: ${point.x}g carbs → ${point.y.toFixed(1)} mg/dL`;
          }
        }
      }
    },
    scales: {
      x: {
        title: {
          display: true,
          text: 'Carbohydrates (g)'
        }
      },
      y: {
        title: {
          display: true,
          text: 'Post-Meal Glucose (mg/dL)'
        }
      }
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-96">
        <div className="loading-spinner"></div>
        <span className="ml-2 text-gray-600">Loading analytics...</span>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Analytics</h1>
          <p className="text-gray-600 mt-1">Advanced insights and visualizations</p>
        </div>
        <div className="flex items-center space-x-3">
          <label className="text-sm font-medium text-gray-700">Time Range:</label>
          <select
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value)}
            className="form-input text-sm w-auto"
          >
            <option value="7">Last 7 days</option>
            <option value="14">Last 14 days</option>
            <option value="30">Last 30 days</option>
            <option value="90">Last 90 days</option>
          </select>
        </div>
      </div>

      {error && (
        <div className="alert alert-error">
          <i className="fas fa-exclamation-circle mr-2"></i>
          {error}
        </div>
      )}

      {/* Key Metrics */}
      {analytics && (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Health Score</p>
                <p className="text-2xl font-bold text-blue-600">{analytics.healthScore}/100</p>
              </div>
              <i className="fas fa-heart text-3xl text-blue-600"></i>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Avg Glucose</p>
                <p className="text-2xl font-bold text-green-600">{analytics.averageGlucose?.toFixed(1)} mg/dL</p>
              </div>
              <i className="fas fa-heartbeat text-3xl text-green-600"></i>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Time in Range</p>
                <p className="text-2xl font-bold text-purple-600">{analytics.timeInRange?.normal?.toFixed(1)}%</p>
              </div>
              <i className="fas fa-target text-3xl text-purple-600"></i>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Total Readings</p>
                <p className="text-2xl font-bold text-orange-600">{glucoseData.length}</p>
              </div>
              <i className="fas fa-chart-line text-3xl text-orange-600"></i>
            </div>
          </div>
        </div>
      )}

      {/* Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Glucose Trend Chart */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            <i className="fas fa-chart-line text-blue-600 mr-2"></i>
            Glucose Trends
          </h3>
          <div className="h-80">
            {glucoseData.length > 0 ? (
              <Line data={glucoseTrendData} options={chartOptions} />
            ) : (
              <div className="flex items-center justify-center h-full text-gray-500">
                No glucose data available
              </div>
            )}
          </div>
        </div>

        {/* Daily Averages */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            <i className="fas fa-chart-bar text-green-600 mr-2"></i>
            Daily Averages
          </h3>
          <div className="h-80">
            {Object.keys(dailyAverages).length > 0 ? (
              <Bar data={dailyAverageData} options={barChartOptions} />
            ) : (
              <div className="flex items-center justify-center h-full text-gray-500">
                No daily average data available
              </div>
            )}
          </div>
        </div>

        {/* Time in Range */}
        {timeInRangeData && (
          <div className="bg-white rounded-lg shadow-sm border p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">
              <i className="fas fa-pie-chart text-purple-600 mr-2"></i>
              Time in Range Distribution
            </h3>
            <div className="h-80">
              <Bar data={timeInRangeData} options={barChartOptions} />
            </div>
          </div>
        )}

        {/* Carbs vs Glucose Correlation */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            <i className="fas fa-project-diagram text-orange-600 mr-2"></i>
            Carbs vs Post-Meal Glucose
          </h3>
          <div className="h-80">
            {carbsVsGlucose.length > 0 ? (
              <Line data={carbsCorrelationData} options={scatterOptions} />
            ) : (
              <div className="flex items-center justify-center h-full text-gray-500">
                No meal correlation data available
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Insights */}
      {analytics?.insights && analytics.insights.length > 0 && (
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            <i className="fas fa-lightbulb text-yellow-600 mr-2"></i>
            Personalized Insights
          </h3>
          <div className="space-y-3">
            {analytics.insights.map((insight, index) => (
              <div key={index} className="flex items-start space-x-3 p-3 bg-blue-50 rounded-lg">
                <i className="fas fa-info-circle text-blue-600 mt-1"></i>
                <p className="text-blue-800">{insight}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* No Data State */}
      {glucoseData.length === 0 && mealsData.length === 0 && (
        <div className="bg-white rounded-lg shadow-sm border p-12 text-center">
          <i className="fas fa-chart-line text-6xl text-gray-300 mb-4"></i>
          <h3 className="text-xl font-semibold text-gray-700 mb-2">No Data Available</h3>
          <p className="text-gray-600 mb-6">
            Start logging glucose readings and meals to see detailed analytics and insights.
          </p>
          <div className="space-x-4">
            <button
              onClick={() => window.location.href = '/glucose'}
              className="btn-primary"
            >
              <i className="fas fa-heartbeat mr-2"></i>
              Add Glucose Reading
            </button>
            <button
              onClick={() => window.location.href = '/meals'}
              className="btn-secondary"
            >
              <i className="fas fa-utensils mr-2"></i>
              Log Meal
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Analytics;