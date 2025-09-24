import React, { useState, useEffect } from 'react';
import { medicationsAPI } from '../utils/api';

const Medications = () => {
  const [medications, setMedications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingMedication, setEditingMedication] = useState(null);

  const [formData, setFormData] = useState({
    name: '',
    medicationType: 'INSULIN_RAPID',
    dosage: '',
    unit: 'UNITS',
    takenAt: '',
    effectivenessRating: '',
    sideEffects: '',
    note: '',
  });

  const medicationTypes = [
    { value: 'INSULIN_RAPID', label: 'Rapid-Acting Insulin' },
    { value: 'INSULIN_LONG', label: 'Long-Acting Insulin' },
    { value: 'INSULIN_INTERMEDIATE', label: 'Intermediate-Acting Insulin' },
    { value: 'METFORMIN', label: 'Metformin' },
    { value: 'SULFONYLUREA', label: 'Sulfonylurea' },
    { value: 'DPP4_INHIBITOR', label: 'DPP-4 Inhibitor' },
    { value: 'GLP1_AGONIST', label: 'GLP-1 Agonist' },
    { value: 'SGLT2_INHIBITOR', label: 'SGLT-2 Inhibitor' },
    { value: 'OTHER', label: 'Other' },
  ];

  const units = [
    { value: 'UNITS', label: 'Units' },
    { value: 'MG', label: 'mg' },
    { value: 'ML', label: 'mL' },
    { value: 'TABLETS', label: 'Tablets' },
  ];

  const effectivenessOptions = [
    { value: 1, label: '1 - Not Effective' },
    { value: 2, label: '2 - Slightly Effective' },
    { value: 3, label: '3 - Moderately Effective' },
    { value: 4, label: '4 - Very Effective' },
    { value: 5, label: '5 - Extremely Effective' },
  ];

  useEffect(() => {
    fetchMedications();
  }, []);

  const fetchMedications = async () => {
    try {
      setLoading(true);
      const data = await medicationsAPI.getMedications();
      setMedications(data);
    } catch (err) {
      setError('Failed to load medications');
      console.error('Medications error:', err);
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
      const medicationData = {
        ...formData,
        dosage: parseFloat(formData.dosage),
        effectivenessRating: formData.effectivenessRating ? parseInt(formData.effectivenessRating) : null,
        takenAt: formData.takenAt || new Date().toISOString().slice(0, 16),
      };

      if (editingMedication) {
        await medicationsAPI.updateMedication(editingMedication.id, medicationData);
        setSuccess('Medication updated successfully!');
      } else {
        await medicationsAPI.createMedication(medicationData);
        setSuccess('Medication added successfully!');
      }

      resetForm();
      fetchMedications();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save medication');
    }
  };

  const handleEdit = (medication) => {
    setEditingMedication(medication);
    setFormData({
      name: medication.name,
      medicationType: medication.medicationType || 'INSULIN_RAPID',
      dosage: medication.dosage.toString(),
      unit: medication.unit || 'UNITS',
      takenAt: new Date(medication.takenAt).toISOString().slice(0, 16),
      effectivenessRating: medication.effectivenessRating ? medication.effectivenessRating.toString() : '',
      sideEffects: medication.sideEffects || '',
      note: medication.note || '',
    });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this medication entry?')) {
      try {
        await medicationsAPI.deleteMedication(id);
        setSuccess('Medication deleted successfully!');
        fetchMedications();
      } catch (err) {
        setError('Failed to delete medication');
      }
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      medicationType: 'INSULIN_RAPID',
      dosage: '',
      unit: 'UNITS',
      takenAt: '',
      effectivenessRating: '',
      sideEffects: '',
      note: '',
    });
    setEditingMedication(null);
    setShowForm(false);
  };

  const getMedicationIcon = (type) => {
    if (type.includes('INSULIN')) return 'fas fa-syringe';
    return 'fas fa-pills';
  };

  const getMedicationColor = (type) => {
    if (type.includes('INSULIN')) return 'text-blue-600';
    if (type === 'METFORMIN') return 'text-green-600';
    if (type.includes('GLP1')) return 'text-purple-600';
    return 'text-gray-600';
  };

  const getEffectivenessColor = (rating) => {
    if (rating >= 4) return 'text-green-600';
    if (rating >= 3) return 'text-yellow-600';
    if (rating >= 2) return 'text-orange-600';
    return 'text-red-600';
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
          <h1 className="text-3xl font-bold text-gray-900">Medications</h1>
          <p className="text-gray-600 mt-1">
            Track your diabetes medications and dosages
          </p>
        </div>
        <button
          onClick={() => setShowForm(!showForm)}
          className="btn-primary"
        >
          <i className="fas fa-plus mr-2"></i>
          Add Medication
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
            {editingMedication ? 'Edit Medication' : 'Add New Medication'}
          </h3>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="name" className="form-label">
                  Medication Name *
                </label>
                <input
                  id="name"
                  name="name"
                  type="text"
                  required
                  className="form-input"
                  placeholder="e.g., Humalog, Metformin"
                  value={formData.name}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="medicationType" className="form-label">
                  Medication Type
                </label>
                <select
                  id="medicationType"
                  name="medicationType"
                  className="form-input"
                  value={formData.medicationType}
                  onChange={handleChange}
                >
                  {medicationTypes.map((type) => (
                    <option key={type.value} value={type.value}>
                      {type.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label htmlFor="dosage" className="form-label">
                  Dosage *
                </label>
                <input
                  id="dosage"
                  name="dosage"
                  type="number"
                  min="0"
                  step="0.1"
                  required
                  className="form-input"
                  placeholder="5.0"
                  value={formData.dosage}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="unit" className="form-label">
                  Unit
                </label>
                <select
                  id="unit"
                  name="unit"
                  className="form-input"
                  value={formData.unit}
                  onChange={handleChange}
                >
                  {units.map((unit) => (
                    <option key={unit.value} value={unit.value}>
                      {unit.label}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label htmlFor="effectivenessRating" className="form-label">
                  Effectiveness Rating
                </label>
                <select
                  id="effectivenessRating"
                  name="effectivenessRating"
                  className="form-input"
                  value={formData.effectivenessRating}
                  onChange={handleChange}
                >
                  <option value="">Select rating</option>
                  {effectivenessOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
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
              <label htmlFor="sideEffects" className="form-label">
                Side Effects
              </label>
              <input
                id="sideEffects"
                name="sideEffects"
                type="text"
                className="form-input"
                placeholder="Any side effects experienced"
                value={formData.sideEffects}
                onChange={handleChange}
              />
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
                placeholder="Any additional notes about this medication..."
                value={formData.note}
                onChange={handleChange}
              />
            </div>

            <div className="flex space-x-3">
              <button type="submit" className="btn-primary">
                <i className="fas fa-save mr-2"></i>
                {editingMedication ? 'Update Medication' : 'Save Medication'}
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

      {/* Medications List */}
      <div className="bg-white rounded-lg shadow-sm border">
        <div className="p-6 border-b border-gray-200">
          <h3 className="text-lg font-semibold text-gray-900">Your Medications</h3>
          <p className="text-sm text-gray-600 mt-1">
            {medications.length} total medication entries
          </p>
        </div>

        {loading ? (
          <div className="flex justify-center items-center py-12">
            <div className="loading-spinner"></div>
            <span className="ml-2 text-gray-600">Loading medications...</span>
          </div>
        ) : medications.length > 0 ? (
          <div className="divide-y divide-gray-200">
            {medications.map((medication) => (
              <div key={medication.id} className="p-6">
                <div className="flex items-center justify-between">
                  <div className="flex-1">
                    <div className="flex items-center space-x-4 mb-2">
                      <i className={`${getMedicationIcon(medication.medicationType)} text-xl ${getMedicationColor(medication.medicationType)}`}></i>
                      <h4 className="text-lg font-semibold text-gray-900">
                        {medication.name}
                      </h4>
                      <span className="px-2 py-1 bg-purple-100 text-purple-800 text-xs rounded-full">
                        {medication.medicationType.replace('_', ' ')}
                      </span>
                    </div>

                    <div className="flex items-center space-x-6 text-sm mb-2">
                      <span className="font-semibold text-gray-900">
                        <i className="fas fa-prescription-bottle mr-1"></i>
                        {medication.dosage} {medication.unit}
                      </span>
                      {medication.effectivenessRating && (
                        <span className={`font-medium ${getEffectivenessColor(medication.effectivenessRating)}`}>
                          <i className="fas fa-star mr-1"></i>
                          {medication.effectivenessRating}/5 effectiveness
                        </span>
                      )}
                    </div>

                    <div className="text-sm text-gray-500 mb-2">
                      <i className="fas fa-clock mr-1"></i>
                      {formatDateTime(medication.takenAt)}
                    </div>

                    {medication.sideEffects && (
                      <div className="text-sm text-orange-700 mb-2">
                        <i className="fas fa-exclamation-triangle mr-1"></i>
                        Side effects: {medication.sideEffects}
                      </div>
                    )}

                    {medication.note && (
                      <p className="text-sm text-gray-700 italic">
                        "{medication.note}"
                      </p>
                    )}
                  </div>

                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => handleEdit(medication)}
                      className="text-blue-600 hover:text-blue-800 p-2"
                      title="Edit medication"
                    >
                      <i className="fas fa-edit"></i>
                    </button>
                    <button
                      onClick={() => handleDelete(medication.id)}
                      className="text-red-600 hover:text-red-800 p-2"
                      title="Delete medication"
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
            <i className="fas fa-pills text-4xl text-gray-300 mb-4"></i>
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              No medications logged yet
            </h3>
            <p className="text-gray-600 mb-4">
              Start tracking your medications to monitor dosages and effectiveness
            </p>
            <button
              onClick={() => setShowForm(true)}
              className="btn-primary"
            >
              <i className="fas fa-plus mr-2"></i>
              Add First Medication
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Medications;