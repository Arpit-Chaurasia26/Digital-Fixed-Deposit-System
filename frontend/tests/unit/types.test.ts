import { describe, it, expect } from 'vitest';
import { FDStatus, InterestScheme } from '@/types';

describe('types constants', () => {
  it('exposes FDStatus constants', () => {
    expect(FDStatus.ACTIVE).toBe('ACTIVE');
    expect(FDStatus.MATURED).toBe('MATURED');
    expect(FDStatus.BROKEN).toBe('BROKEN');
  });

  it('exposes InterestScheme constants', () => {
    expect(InterestScheme.STANDARD_6_MONTHS).toBe('STANDARD_6_MONTHS');
    expect(InterestScheme.STANDARD_12_MONTHS).toBe('STANDARD_12_MONTHS');
  });
});
