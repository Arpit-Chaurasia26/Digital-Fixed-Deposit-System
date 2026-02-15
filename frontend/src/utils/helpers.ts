// Format currency
export const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 2,
  }).format(amount);
};

// Format date
export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  }).format(date);
};

// Format date with time
export const formatDateTime = (dateString: string): string => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
};

// Calculate days between dates
export const daysBetween = (date1: string, date2: string): number => {
  const d1 = new Date(date1);
  const d2 = new Date(date2);
  const diffTime = Math.abs(d2.getTime() - d1.getTime());
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};

// Get days until maturity
export const daysUntilMaturity = (maturityDate: string): number => {
  return daysBetween(new Date().toISOString(), maturityDate);
};

// Validate email
export const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// Validate phone
export const isValidPhone = (phone: string): boolean => {
  const phoneRegex = /^[6-9]\d{9}$/;
  return phoneRegex.test(phone);
};

// Truncate text
export const truncate = (text: string, length: number): string => {
  if (text.length <= length) return text;
  return text.substring(0, length) + '...';
};

// Get status color
export const getStatusColor = (status: string): string => {
  const colors: { [key: string]: string } = {
    ACTIVE: '#4CAF50',
    MATURED: '#2196F3',
    BROKEN: '#f44336',
    PENDING: '#FF9800',
    OPEN: '#FF9800',
    IN_PROGRESS: '#2196F3',
    RESOLVED: '#4CAF50',
    CLOSED: '#757575',
  };
  return colors[status] || '#757575';
};

// Calculate percentage
export const calculatePercentage = (value: number, total: number): number => {
  if (total === 0) return 0;
  return (value / total) * 100;
};

// Format percentage
export const formatPercentage = (value: number): string => {
  return `${value.toFixed(2)}%`;
};

// Debounce function
export const debounce = <T extends (...args: any[]) => any>(
  func: T,
  wait: number
): ((...args: Parameters<T>) => void) => {
  let timeout: ReturnType<typeof setTimeout> | null = null;
  return (...args: Parameters<T>) => {
    if (timeout) clearTimeout(timeout);
    timeout = setTimeout(() => func(...args), wait);
  };
};

// Copy to clipboard
export const copyToClipboard = async (text: string): Promise<boolean> => {
  try {
    await navigator.clipboard.writeText(text);
    return true;
  } catch (err) {
    console.error('Failed to copy:', err);
    return false;
  }
};

// Friendly error message helper
export const getErrorMessage = (error: any, fallback: string): string => {
  const status = error?.response?.status;
  const serverMessage = error?.response?.data?.message || error?.message;

  if (status === 400) return serverMessage || `${fallback}. Please check your input and try again.`;
  if (status === 401) return 'You are not signed in. Please log in and try again.';
  if (status === 403) return 'You do not have permission to perform this action.';
  if (status === 404) return serverMessage || 'Requested resource was not found.';
  if (status === 409) return serverMessage || 'This request conflicts with existing data.';
  if (status >= 500) return 'We hit a server error. Please try again in a moment.';

  return serverMessage || fallback;
};
