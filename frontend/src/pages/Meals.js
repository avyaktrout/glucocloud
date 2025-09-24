import React, { useState, useEffect } from 'react';
import { mealsAPI } from '../utils/api';

const Meals = () => {
  const [meals, setMeals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingMeal, setEditingMeal] = useState(null);

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    carbsGrams: '',
    calories: '',
    protein: '',
    fat: '',
    mealType: 'BREAKFAST',
    consumedAt: '',
    note: '',
  });

  const mealTypes = [
    { value: 'BREAKFAST', label: 'Breakfast' },
    { value: 'LUNCH', label: 'Lunch' },
    { value: 'DINNER', label: 'Dinner' },
    { value: 'SNACK', label: 'Snack' },
    { value: 'OTHER', label: 'Other' },
  ];

  useEffect(() => {
    fetchMeals();
  }, []);

  const fetchMeals = async () => {
    try {
      setLoading(true);
      const data = await mealsAPI.getMeals();
      setMeals(data);
    } catch (err) {
      setError('Failed to load meals');
      console.error('Meals error:', err);
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
      const mealData = {
        ...formData,
        carbsGrams: parseFloat(formData.carbsGrams),
        calories: formData.calories ? parseFloat(formData.calories) : null,
        protein: formData.protein ? parseFloat(formData.protein) : null,
        fat: formData.fat ? parseFloat(formData.fat) : null,
        consumedAt: formData.consumedAt || new Date().toISOString().slice(0, 16),
      };

      if (editingMeal) {
        await mealsAPI.updateMeal(editingMeal.id, mealData);
        setSuccess('Meal updated successfully!');
      } else {
        await mealsAPI.createMeal(mealData);
        setSuccess('Meal added successfully!');
      }

      resetForm();
      fetchMeals();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save meal');
    }
  };

  const handleEdit = (meal) => {
    setEditingMeal(meal);
    setFormData({
      name: meal.name,
      description: meal.description || '',
      carbsGrams: meal.carbsGrams.toString(),
      calories: meal.calories ? meal.calories.toString() : '',
      protein: meal.protein ? meal.protein.toString() : '',
      fat: meal.fat ? meal.fat.toString() : '',
      mealType: meal.mealType || 'BREAKFAST',
      consumedAt: new Date(meal.consumedAt).toISOString().slice(0, 16),
      note: meal.note || '',
    });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this meal?')) {
      try {
        await mealsAPI.deleteMeal(id);
        setSuccess('Meal deleted successfully!');
        fetchMeals();
      } catch (err) {
        setError('Failed to delete meal');
      }
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      description: '',
      carbsGrams: '',
      calories: '',
      protein: '',
      fat: '',
      mealType: 'BREAKFAST',
      consumedAt: '',
      note: '',
    });
    setEditingMeal(null);
    setShowForm(false);
  };

  const getCarbsColor = (carbs) => {
    if (carbs <= 15) return 'text-green-600';
    if (carbs <= 30) return 'text-yellow-600';
    if (carbs <= 45) return 'text-orange-600';
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
          <h1 className="text-3xl font-bold text-gray-900">Meals</h1>
          <p className="text-gray-600 mt-1">
            Track your meals and carbohydrate intake
          </p>
        </div>
        <button
          onClick={() => setShowForm(!showForm)}
          className="btn-primary"
        >
          <i className="fas fa-plus mr-2"></i>
          Add Meal
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
            {editingMeal ? 'Edit Meal' : 'Add New Meal'}
          </h3>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="name" className="form-label">
                  Meal Name *
                </label>
                <input
                  id="name"
                  name="name"
                  type="text"
                  required
                  className="form-input"
                  placeholder="e.g., Oatmeal with berries"
                  value={formData.name}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="mealType" className="form-label">
                  Meal Type
                </label>
                <select
                  id="mealType"
                  name="mealType"
                  className="form-input"
                  value={formData.mealType}
                  onChange={handleChange}
                >
                  {mealTypes.map((type) => (
                    <option key={type.value} value={type.value}>
                      {type.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div>
              <label htmlFor="description" className="form-label">
                Description
              </label>
              <input
                id="description"
                name="description"
                type="text"
                className="form-input"
                placeholder="Brief description of the meal"
                value={formData.description}
                onChange={handleChange}
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div>
                <label htmlFor="carbsGrams" className="form-label">
                  Carbs (g) *
                </label>
                <input
                  id="carbsGrams"
                  name="carbsGrams"
                  type="number"
                  min="0"
                  step="0.1"
                  required
                  className="form-input"
                  placeholder="15.0"
                  value={formData.carbsGrams}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="calories" className="form-label">
                  Calories
                </label>
                <input
                  id="calories"
                  name="calories"
                  type="number"
                  min="0"
                  className="form-input"
                  placeholder="250"
                  value={formData.calories}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="protein" className="form-label">
                  Protein (g)
                </label>
                <input
                  id="protein"
                  name="protein"
                  type="number"
                  min="0"
                  step="0.1"
                  className="form-input"
                  placeholder="12.5"
                  value={formData.protein}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="fat" className="form-label">
                  Fat (g)
                </label>
                <input
                  id="fat"
                  name="fat"
                  type="number"
                  min="0"
                  step="0.1"
                  className="form-input"
                  placeholder="8.0"
                  value={formData.fat}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div>
              <label htmlFor="consumedAt" className="form-label">
                Date & Time
              </label>
              <input
                id="consumedAt"
                name="consumedAt"
                type="datetime-local"
                className="form-input"
                value={formData.consumedAt}
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
                placeholder="Any additional notes about this meal..."
                value={formData.note}
                onChange={handleChange}
              />
            </div>

            <div className="flex space-x-3">
              <button type="submit" className="btn-primary">
                <i className="fas fa-save mr-2"></i>
                {editingMeal ? 'Update Meal' : 'Save Meal'}
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

      {/* Meals List */}
      <div className="bg-white rounded-lg shadow-sm border">
        <div className="p-6 border-b border-gray-200">
          <h3 className="text-lg font-semibold text-gray-900">Your Meals</h3>
          <p className="text-sm text-gray-600 mt-1">
            {meals.length} total meals logged
          </p>
        </div>

        {loading ? (
          <div className="flex justify-center items-center py-12">
            <div className="loading-spinner"></div>
            <span className="ml-2 text-gray-600">Loading meals...</span>
          </div>
        ) : meals.length > 0 ? (
          <div className="divide-y divide-gray-200">
            {meals.map((meal) => (
              <div key={meal.id} className="p-6">
                <div className="flex items-center justify-between">
                  <div className="flex-1">
                    <div className="flex items-center space-x-4 mb-2">
                      <h4 className="text-lg font-semibold text-gray-900">
                        {meal.name}
                      </h4>
                      <span className="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded-full">
                        {meal.mealType.replace('_', ' ')}
                      </span>
                    </div>

                    {meal.description && (
                      <p className="text-gray-600 text-sm mb-2">
                        {meal.description}
                      </p>
                    )}

                    <div className="flex items-center space-x-6 text-sm">
                      <span className={`font-semibold ${getCarbsColor(meal.carbsGrams)}`}>
                        <i className="fas fa-cookie-bite mr-1"></i>
                        {meal.carbsGrams}g carbs
                      </span>
                      {meal.calories && (
                        <span className="text-gray-600">
                          <i className="fas fa-fire mr-1"></i>
                          {meal.calories} cal
                        </span>
                      )}
                      {meal.protein && (
                        <span className="text-gray-600">
                          <i className="fas fa-dumbbell mr-1"></i>
                          {meal.protein}g protein
                        </span>
                      )}
                      {meal.fat && (
                        <span className="text-gray-600">
                          <i className="fas fa-tint mr-1"></i>
                          {meal.fat}g fat
                        </span>
                      )}
                    </div>

                    <div className="mt-2 text-sm text-gray-500">
                      <i className="fas fa-clock mr-1"></i>
                      {formatDateTime(meal.consumedAt)}
                    </div>

                    {meal.note && (
                      <p className="mt-2 text-sm text-gray-700 italic">
                        "{meal.note}"
                      </p>
                    )}
                  </div>

                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => handleEdit(meal)}
                      className="text-blue-600 hover:text-blue-800 p-2"
                      title="Edit meal"
                    >
                      <i className="fas fa-edit"></i>
                    </button>
                    <button
                      onClick={() => handleDelete(meal.id)}
                      className="text-red-600 hover:text-red-800 p-2"
                      title="Delete meal"
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
            <i className="fas fa-utensils text-4xl text-gray-300 mb-4"></i>
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              No meals logged yet
            </h3>
            <p className="text-gray-600 mb-4">
              Start tracking your meals to monitor carb intake and nutrition
            </p>
            <button
              onClick={() => setShowForm(true)}
              className="btn-primary"
            >
              <i className="fas fa-plus mr-2"></i>
              Add First Meal
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Meals;