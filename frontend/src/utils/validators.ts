// Validation rules
export const required = (value: any): boolean | string => {
 return !!value || 'This field is required';
};


export const email = (value: string): boolean | string => {
 const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
 return pattern.test(value) || 'Invalid email address';
};


export const minLength = (min: number) => (value: string): boolean | string => {
 return value.length >= min || `Minimum ${min} characters required`;
};


export const maxLength = (max: number) => (value: string): boolean | string => {
 return value.length <= max || `Maximum ${max} characters allowed`;
};


export const minValue = (min: number) => (value: number): boolean | string => {
 return value >= min || `Minimum value is ${min}`;
};


export const maxValue = (max: number) => (value: number): boolean | string => {
 return value <= max || `Maximum value is ${max}`;
};


export const phone = (value: string): boolean | string => {
 const pattern = /^[6-9]\d{9}$/;
 return pattern.test(value) || 'Invalid phone number (must be 10 digits starting with 6-9)';
};


export const password = (value: string): boolean | string => {
 if (value.length < 8) return 'Password must be at least 8 characters';
 if (!/[A-Z]/.test(value)) return 'Password must contain at least one uppercase letter';
 if (!/[a-z]/.test(value)) return 'Password must contain at least one lowercase letter';
 if (!/[0-9]/.test(value)) return 'Password must contain at least one number';
 return true;
};


export const confirmPassword = (password: string) => (value: string): boolean | string => {
 return value === password || 'Passwords do not match';
};


export const numeric = (value: any): boolean | string => {
 return !isNaN(value) || 'Must be a number';
};


export const positiveNumber = (value: number): boolean | string => {
 return value > 0 || 'Must be a positive number';
};


export const integer = (value: any): boolean | string => {
 return Number.isInteger(Number(value)) || 'Must be an integer';
};



