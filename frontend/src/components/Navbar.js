import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { authAPI } from '../utils/api';

const Navbar = () => {
  const location = useLocation();
  const user = authAPI.getCurrentUser();

  const isActiveLink = (path) => {
    return location.pathname === path;
  };

  const handleLogout = () => {
    authAPI.logout();
  };

  const navLinks = [
    { path: '/dashboard', name: 'Dashboard', icon: 'fas fa-tachometer-alt' },
    { path: '/glucose', name: 'Glucose', icon: 'fas fa-heartbeat' },
    { path: '/meals', name: 'Meals', icon: 'fas fa-utensils' },
    { path: '/medications', name: 'Medications', icon: 'fas fa-pills' },
    { path: '/analytics', name: 'Analytics', icon: 'fas fa-chart-line' },
    { path: '/export', name: 'Export', icon: 'fas fa-download' },
  ];

  return (
    <nav className="bg-white shadow-lg border-b">
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center py-4">
          {/* Logo */}
          <div className="flex items-center space-x-2">
            <i className="fas fa-heartbeat text-2xl text-blue-600"></i>
            <span className="text-2xl font-bold text-gray-800">GlucoCloud</span>
            <span className="text-sm text-gray-500 bg-blue-100 px-2 py-1 rounded">Beta</span>
          </div>

          {/* Navigation Links */}
          <div className="hidden md:flex space-x-1">
            {navLinks.map((link) => (
              <Link
                key={link.path}
                to={link.path}
                className={`flex items-center space-x-2 px-4 py-2 rounded-md transition-colors ${
                  isActiveLink(link.path)
                    ? 'bg-blue-100 text-blue-700 font-medium'
                    : 'text-gray-600 hover:text-gray-800 hover:bg-gray-100'
                }`}
              >
                <i className={link.icon}></i>
                <span>{link.name}</span>
              </Link>
            ))}
          </div>

          {/* User Menu */}
          <div className="flex items-center space-x-4">
            <div className="hidden md:flex items-center space-x-2 text-gray-600">
              <i className="fas fa-user-circle text-lg"></i>
              <span className="text-sm">
                {user?.firstName || 'User'} {user?.lastName || ''}
              </span>
            </div>
            <button
              onClick={handleLogout}
              className="flex items-center space-x-2 text-gray-600 hover:text-red-600 transition-colors"
              title="Logout"
            >
              <i className="fas fa-sign-out-alt"></i>
              <span className="hidden md:inline">Logout</span>
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        <div className="md:hidden pb-4">
          <div className="flex flex-wrap gap-2">
            {navLinks.map((link) => (
              <Link
                key={link.path}
                to={link.path}
                className={`flex items-center space-x-2 px-3 py-2 text-sm rounded-md transition-colors ${
                  isActiveLink(link.path)
                    ? 'bg-blue-100 text-blue-700 font-medium'
                    : 'text-gray-600 hover:text-gray-800 hover:bg-gray-100'
                }`}
              >
                <i className={link.icon}></i>
                <span>{link.name}</span>
              </Link>
            ))}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;