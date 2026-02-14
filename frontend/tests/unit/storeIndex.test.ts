import { describe, it, expect } from 'vitest';
import store from '@/store';

describe('store index', () => {
  it('registers all root modules', () => {
    expect(store.hasModule('auth')).toBe(true);
    expect(store.hasModule('fd')).toBe(true);
    expect(store.hasModule('support')).toBe(true);
  });

  it('exposes expected root state keys', () => {
    expect(Object.keys(store.state).sort()).toEqual(['auth', 'fd', 'support']);
  });
});
