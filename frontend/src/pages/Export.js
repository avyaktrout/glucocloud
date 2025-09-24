import React, { useState } from 'react';
import { exportAPI } from '../utils/api';

const Export = () => {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  const handleExport = async (exportType) => {
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      let blob;
      let filename;

      switch (exportType) {
        case 'glucose':
          blob = await exportAPI.exportGlucose();
          filename = 'glucose_readings.csv';
          break;
        case 'meals':
          blob = await exportAPI.exportMeals();
          filename = 'meals.csv';
          break;
        case 'medications':
          blob = await exportAPI.exportMedications();
          filename = 'medications.csv';
          break;
        case 'comprehensive':
          blob = await exportAPI.exportComprehensiveReport();
          filename = 'health_report.csv';
          break;
        default:
          throw new Error('Invalid export type');
      }

      // Create download link
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);

      setSuccess(`${filename} downloaded successfully!`);
    } catch (err) {
      console.error('Export error details:', {
        message: err.message,
        response: err.response,
        status: err.response?.status,
        statusText: err.response?.statusText,
        data: err.response?.data,
        exportType
      });

      let errorMessage = 'Failed to export data. Please try again.';
      if (err.response?.status === 401) {
        errorMessage = 'Authentication failed. Please log in again.';
      } else if (err.response?.status === 500) {
        errorMessage = 'Server error occurred during export. Please try again later.';
      } else if (err.response?.data?.message) {
        errorMessage = `Export failed: ${err.response.data.message}`;
      }

      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const exportOptions = [
    {
      type: 'glucose',
      title: 'Glucose Readings',
      description: 'Export all your glucose readings with timestamps and notes',
      icon: 'fas fa-heartbeat',
      color: 'text-red-600',
      bgColor: 'bg-red-50',
    },
    {
      type: 'meals',
      title: 'Meals Data',
      description: 'Export meal logs with carb counts and nutrition information',
      icon: 'fas fa-utensils',
      color: 'text-green-600',
      bgColor: 'bg-green-50',
    },
    {
      type: 'medications',
      title: 'Medications',
      description: 'Export medication logs with dosages and effectiveness ratings',
      icon: 'fas fa-pills',
      color: 'text-blue-600',
      bgColor: 'bg-blue-50',
    },
    {
      type: 'comprehensive',
      title: 'Complete Health Report',
      description: 'Export comprehensive report with analytics and insights',
      icon: 'fas fa-file-medical',
      color: 'text-purple-600',
      bgColor: 'bg-purple-50',
    },
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Export Data</h1>
        <p className="text-gray-600 mt-1">
          Download your health data for sharing with healthcare providers
        </p>
      </div>

      {/* Alerts */}
      {success && (
        <div className="alert alert-success">
          <i className="fas fa-check-circle mr-2"></i>
          {success}
        </div>
      )}

      {error && (
        <div className="alert alert-error">
          <i className="fas fa-exclamation-circle mr-2"></i>
          {error}
        </div>
      )}

      {/* Export Options */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {exportOptions.map((option) => (
          <div
            key={option.type}
            className={`bg-white rounded-lg shadow-sm border p-6 ${option.bgColor} border-l-4`}
          >
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <div className="flex items-center space-x-3 mb-3">
                  <i className={`${option.icon} text-2xl ${option.color}`}></i>
                  <h3 className="text-lg font-semibold text-gray-900">
                    {option.title}
                  </h3>
                </div>
                <p className="text-gray-600 text-sm mb-4">
                  {option.description}
                </p>
                <button
                  onClick={() => handleExport(option.type)}
                  disabled={loading}
                  className="btn-primary flex items-center"
                >
                  {loading ? (
                    <>
                      <div className="loading-spinner mr-2"></div>
                      Exporting...
                    </>
                  ) : (
                    <>
                      <i className="fas fa-download mr-2"></i>
                      Export CSV
                    </>
                  )}
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Export Information */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          <i className="fas fa-info-circle text-blue-600 mr-2"></i>
          Export Information
        </h3>

        <div className="space-y-4 text-sm text-gray-600">
          <div className="flex items-start space-x-3">
            <i className="fas fa-file-csv text-green-600 mt-1"></i>
            <div>
              <p className="font-medium text-gray-800">CSV Format</p>
              <p>All exports are in CSV format, compatible with Excel, Google Sheets, and other spreadsheet applications.</p>
            </div>
          </div>

          <div className="flex items-start space-x-3">
            <i className="fas fa-user-md text-blue-600 mt-1"></i>
            <div>
              <p className="font-medium text-gray-800">Healthcare Provider Ready</p>
              <p>Exported data includes professional formatting and medical terminology for easy sharing with your healthcare team.</p>
            </div>
          </div>

          <div className="flex items-start space-x-3">
            <i className="fas fa-shield-alt text-purple-600 mt-1"></i>
            <div>
              <p className="font-medium text-gray-800">Privacy & Security</p>
              <p>Exports are generated on-demand and downloaded directly to your device. No data is stored on external servers.</p>
            </div>
          </div>

          <div className="flex items-start space-x-3">
            <i className="fas fa-calendar-alt text-orange-600 mt-1"></i>
            <div>
              <p className="font-medium text-gray-800">Date Range</p>
              <p>Current exports include all your historical data. Custom date ranges will be available in future updates.</p>
            </div>
          </div>
        </div>
      </div>

      {/* Usage Tips */}
      <div className="bg-blue-50 rounded-lg border border-blue-200 p-6">
        <h3 className="text-lg font-semibold text-blue-900 mb-4">
          <i className="fas fa-lightbulb text-blue-600 mr-2"></i>
          Tips for Healthcare Appointments
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
          <div className="space-y-2">
            <p className="font-medium text-blue-900">📋 Before Your Appointment:</p>
            <ul className="space-y-1 text-blue-800 ml-4">
              <li>• Export comprehensive health report</li>
              <li>• Print or email to your provider</li>
              <li>• Review patterns and trends</li>
            </ul>
          </div>

          <div className="space-y-2">
            <p className="font-medium text-blue-900">💡 Best Practices:</p>
            <ul className="space-y-1 text-blue-800 ml-4">
              <li>• Export data 24-48 hours before visits</li>
              <li>• Include at least 2-4 weeks of data</li>
              <li>• Highlight any concerning patterns</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Export;