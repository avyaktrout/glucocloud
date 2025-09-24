import React, { useState, useEffect } from 'react';
import { glucoseAPI } from '../utils/api';

const Glucose = () => {
  const [readings, setReadings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingReading, setEditingReading] = useState(null);

  const [formData, setFormData] = useState({
    readingValue: '',
    takenAt: '',
    readingType: 'RANDOM',
    note: '',
  });

  const readingTypes = [
    { value: 'FASTING', label: 'Fasting' },
    { value: 'BEFORE_MEAL', label: 'Before Meal' },
    { value: 'AFTER_MEAL', label: 'After Meal' },
    { value: 'BEDTIME', label: 'Bedtime' },
    { value: 'RANDOM', label: 'Random' },
    { value: 'OTHER', label: 'Other' },
  ];

  useEffect(() => {
    fetchReadings();
  }, []);

  const fetchReadings = async () => {
    try {
      setLoading(true);
      const data = await glucoseAPI.getReadings();
      setReadings(data);
    } catch (err) {
      setError('Failed to load glucose readings');
      console.error('Glucose error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      const readingData = {
        ...formData,
        readingValue: parseFloat(formData.readingValue),
        takenAt: formData.takenAt || new Date().toISOString().slice(0, 16),
      };

      if (editingReading) {
        await glucoseAPI.updateReading(editingReading.id, readingData);
        setSuccess('Reading updated successfully!');
      } else {
        await glucoseAPI.createReading(readingData);
        setSuccess('Reading added successfully!');
      }

      resetForm();
      fetchReadings();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save reading');
    }
  };

  const handleEdit = (reading) => {
    setEditingReading(reading);
    setFormData({
      readingValue: reading.readingValue.toString(),
      takenAt: new Date(reading.takenAt).toISOString().slice(0, 16),
      readingType: reading.readingType || 'RANDOM',
      note: reading.note || '',
    });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this reading?')) {
      try {
        await glucoseAPI.deleteReading(id);
        setSuccess('Reading deleted successfully!');
        fetchReadings();
      } catch (err) {
        setError('Failed to delete reading');
      }
    }
  };

  const resetForm = () => {
    setFormData({
      readingValue: '',
      takenAt: '',
      readingType: 'RANDOM',
      note: '',
    });
    setEditingReading(null);
    setShowForm(false);
  };

  const getStatusColor = (reading) => {
    if (reading.status === 'CRITICALLY_HIGH' || reading.status === 'CRITICALLY_LOW') {
      return 'bg-red-100 border-red-500';
    }
    if (reading.status === 'HIGH') return 'bg-yellow-100 border-yellow-500';
    if (reading.status === 'LOW') return 'bg-blue-100 border-blue-500';
    return 'bg-green-100 border-green-500';
  };

  const formatDateTime = (dateTime) => {
    return new Date(dateTime).toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
    });
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Glucose Readings</h1>
          <p className="text-gray-600 mt-1">
            Track and monitor your blood glucose levels
          </p>
        </div>
        <button
          onClick={() => setShowForm(!showForm)}
          className="btn-primary"
        >
          <i className="fas fa-plus mr-2"></i>
          Add Reading
        </button>
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

      {/* Add/Edit Form */}
      {showForm && (
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            {editingReading ? 'Edit Reading' : 'Add New Reading'}
          </h3>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="readingValue" className="form-label">
                  Glucose Reading (mg/dL) *
                </label>
                <input
                  id="readingValue"
                  name="readingValue"
                  type="number"
                  min="20"
                  max="999"
                  step="0.1"
                  required
                  className="form-input"
                  placeholder="125.5"
                  value={formData.readingValue}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="readingType" className="form-label">
                  Reading Type
                </label>
                <select
                  id="readingType"
                  name="readingType"
                  className="form-input"
                  value={formData.readingType}
                  onChange={handleChange}
                >
                  {readingTypes.map((type) => (
                    <option key={type.value} value={type.value}>
                      {type.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div>
              <label htmlFor="takenAt" className="form-label">
                Date & Time
              </label>
              <input
                id="takenAt"
                name="takenAt"
                type="datetime-local"
                className="form-input"
                value={formData.takenAt}
                onChange={handleChange}
              />
              <p className="text-xs text-gray-500 mt-1">
                Leave empty to use current time
              </p>
            </div>

            <div>
              <label htmlFor="note" className="form-label">
                Notes (Optional)
              </label>
              <textarea
                id="note"
                name="note"
                rows="3"
                className="form-input"
                placeholder="Any additional notes about this reading..."
                value={formData.note}
                onChange={handleChange}
              />
            </div>

            <div className="flex space-x-3">
              <button type="submit" className="btn-primary">
                <i className="fas fa-save mr-2"></i>
                {editingReading ? 'Update Reading' : 'Save Reading'}
              </button>
              <button
                type="button"
                onClick={resetForm}
                className="btn-secondary"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Readings List */}
      <div className="bg-white rounded-lg shadow-sm border">
        <div className="p-6 border-b border-gray-200">
          <h3 className="text-lg font-semibold text-gray-900">Your Readings</h3>
          <p className="text-sm text-gray-600 mt-1">
            {readings.length} total readings
          </p>
        </div>

        {loading ? (
          <div className="flex justify-center items-center py-12">
            <div className="loading-spinner"></div>
            <span className="ml-2 text-gray-600">Loading readings...</span>
          </div>
        ) : readings.length > 0 ? (
          <div className="divide-y divide-gray-200">
            {readings.map((reading) => (
              <div
                key={reading.id}
                className={`p-6 border-l-4 ${getStatusColor(reading)}`}
              >
                <div className="flex items-center justify-between">
                  <div className="flex-1">
                    <div className="flex items-center space-x-4">
                      <span className="text-2xl font-bold text-gray-900">
                        {reading.readingValue} mg/dL
                      </span>
                      <span className={`px-3 py-1 rounded-full text-sm font-medium ${
                        reading.status === 'NORMAL' ? 'bg-green-100 text-green-800' :
                        reading.status === 'HIGH' ? 'bg-yellow-100 text-yellow-800' :
                        reading.status === 'LOW' ? 'bg-blue-100 text-blue-800' :
                        'bg-red-100 text-red-800'
                      }`}>
                        {reading.status}
                      </span>
                      <span className={`px-2 py-1 rounded text-xs ${
                        reading.inRange ? 'bg-green-500 text-white' : 'bg-gray-500 text-white'
                      }`}>
                        {reading.inRange ? 'In Range' : 'Out of Range'}
                      </span>
                    </div>

                    <div className="mt-2 flex items-center space-x-4 text-sm text-gray-600">
                      <span>
                        <i className="fas fa-clock mr-1"></i>
                        {formatDateTime(reading.takenAt)}
                      </span>
                      {reading.readingType && (
                        <span>
                          <i className="fas fa-tag mr-1"></i>
                          {reading.readingType.replace('_', ' ')}
                        </span>
                      )}
                    </div>

                    {reading.note && (
                      <p className="mt-2 text-sm text-gray-700 italic">
                        "{reading.note}"
                      </p>
                    )}
                  </div>

                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => handleEdit(reading)}
                      className="text-blue-600 hover:text-blue-800 p-2"
                      title="Edit reading"
                    >
                      <i className="fas fa-edit"></i>
                    </button>
                    <button
                      onClick={() => handleDelete(reading.id)}
                      className="text-red-600 hover:text-red-800 p-2"
                      title="Delete reading"
                    >
                      <i className="fas fa-trash"></i>
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <i className="fas fa-heartbeat text-4xl text-gray-300 mb-4"></i>
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              No glucose readings yet
            </h3>
            <p className="text-gray-600 mb-4">
              Start tracking your blood glucose to see trends and insights
            </p>
            <button
              onClick={() => setShowForm(true)}
              className="btn-primary"
            >
              <i className="fas fa-plus mr-2"></i>
              Add First Reading
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Glucose;