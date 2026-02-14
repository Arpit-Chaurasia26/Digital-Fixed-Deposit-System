import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import {
  formatCurrency,
  formatDate,
  formatDateTime,
  daysBetween,
  daysUntilMaturity,
  isValidEmail,
  isValidPhone,
  truncate,
  getStatusColor,
  calculatePercentage,
  formatPercentage,
  debounce,
  copyToClipboard,
  getErrorMessage,
} from '@/utils/helpers';

describe('helpers', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('formats currency', () => {
    expect(formatCurrency(1234)).toContain('₹');
  });

  it('formats date and datetime', () => {
    expect(formatDate('2025-01-02')).toContain('2025');
    expect(formatDateTime('2025-01-02T10:30:00Z')).toContain('2025');
  });

  it('calculates day differences', () => {
    expect(daysBetween('2025-01-01', '2025-01-11')).toBe(10);
  });

  it('calculates days until maturity', () => {
    vi.setSystemTime(new Date('2025-01-01T00:00:00Z'));
    expect(daysUntilMaturity('2025-01-11T00:00:00Z')).toBe(10);
  });

  it('validates email and phone', () => {
    expect(isValidEmail('a@b.com')).toBe(true);
    expect(isValidEmail('bad-email')).toBe(false);
    expect(isValidPhone('9876543210')).toBe(true);
    expect(isValidPhone('12345')).toBe(false);
  });

  it('truncates text', () => {
    expect(truncate('abcdef', 3)).toBe('abc...');
    expect(truncate('abc', 5)).toBe('abc');
  });

  it('returns status color and percentage helpers', () => {
    expect(getStatusColor('ACTIVE')).toBe('#4CAF50');
    expect(getStatusColor('UNKNOWN')).toBe('#757575');
    expect(calculatePercentage(25, 100)).toBe(25);
    expect(calculatePercentage(1, 0)).toBe(0);
    expect(formatPercentage(12.3456)).toBe('12.35%');
  });

  it('debounces function calls', () => {
    const fn = vi.fn();
    const d = debounce(fn, 200);

    d('a');
    d('b');
    vi.advanceTimersByTime(199);
    expect(fn).not.toHaveBeenCalled();

    vi.advanceTimersByTime(1);
    expect(fn).toHaveBeenCalledTimes(1);
    expect(fn).toHaveBeenCalledWith('b');
  });

  it('copies to clipboard', async () => {
    const writeText = vi.fn().mockResolvedValue(undefined);
    Object.defineProperty(navigator, 'clipboard', {
      value: { writeText },
      configurable: true,
    });

    await expect(copyToClipboard('abc')).resolves.toBe(true);
    expect(writeText).toHaveBeenCalledWith('abc');
  });

  it('builds user-friendly error messages', () => {
    expect(getErrorMessage({ response: { status: 401 } }, 'Fallback')).toContain('log in');
    expect(getErrorMessage({ response: { status: 404, data: { message: 'Not found from API' } } }, 'Fallback')).toBe('Not found from API');
    expect(getErrorMessage({ message: 'Raw error' }, 'Fallback')).toBe('Raw error');
  });
});
