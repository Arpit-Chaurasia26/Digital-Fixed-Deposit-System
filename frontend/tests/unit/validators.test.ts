import { describe, it, expect } from 'vitest';
import {
  required,
  email,
  minLength,
  maxLength,
  minValue,
  maxValue,
  phone,
  password,
  confirmPassword,
  numeric,
  positiveNumber,
  integer,
} from '@/utils/validators';

describe('validators', () => {
  it('validates required', () => {
    expect(required('x')).toBe(true);
    expect(required('')).toBe('This field is required');
  });

  it('validates email and phone', () => {
    expect(email('ok@test.com')).toBe(true);
    expect(email('bad')).toBe('Invalid email address');
    expect(phone('9876543210')).toBe(true);
    expect(phone('123')).toContain('Invalid phone number');
  });

  it('validates min/max length', () => {
    expect(minLength(3)('abcd')).toBe(true);
    expect(minLength(3)('ab')).toContain('Minimum 3');
    expect(maxLength(3)('ab')).toBe(true);
    expect(maxLength(3)('abcd')).toContain('Maximum 3');
  });

  it('validates min/max values', () => {
    expect(minValue(10)(11)).toBe(true);
    expect(minValue(10)(9)).toContain('Minimum value is 10');
    expect(maxValue(10)(9)).toBe(true);
    expect(maxValue(10)(11)).toContain('Maximum value is 10');
  });

  it('validates password and confirmation', () => {
    expect(password('Aa123456')).toBe(true);
    expect(password('short')).toContain('at least 8');
    expect(confirmPassword('secret')('secret')).toBe(true);
    expect(confirmPassword('secret')('other')).toBe('Passwords do not match');
  });

  it('validates numeric, positive and integer', () => {
    expect(numeric('12')).toBe(true);
    expect(numeric('abc')).toBe('Must be a number');
    expect(positiveNumber(1)).toBe(true);
    expect(positiveNumber(0)).toBe('Must be a positive number');
    expect(integer('12')).toBe(true);
    expect(integer('12.5')).toBe('Must be an integer');
  });
});
