import { describe, it, expect, vi, beforeEach } from 'vitest';

vi.mock('@/services/axios', () => ({
  default: {
    put: vi.fn(),
    get: vi.fn(),
  },
}));

import apiClient from '@/services/axios';
import { adminService } from '@/services/adminService';

describe('adminService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('updateFDStatus calls correct endpoint', async () => {
    (apiClient.put as any).mockResolvedValue({ data: undefined });

    await adminService.updateFDStatus(12, 'ACTIVE' as any);

    expect(apiClient.put).toHaveBeenCalledWith('/admin/fd/12/status', 'ACTIVE', {
      headers: { 'Content-Type': 'application/json' },
    });
  });

  it('getMaturingFDs uses default days', async () => {
    (apiClient.get as any).mockResolvedValue({ data: [] });

    await adminService.getMaturingFDs();

    expect(apiClient.get).toHaveBeenCalledWith('/admin/fd/maturing?days=7');
  });

  it('getFinancialYearSummary builds URL with year', async () => {
    (apiClient.get as any).mockResolvedValue({ data: {} });

    await adminService.getFinancialYearSummary(2026);

    expect(apiClient.get).toHaveBeenCalledWith('/admin/fd/summary/financial-year?year=2026');
  });

  it('getUserPortfolio calls user portfolio endpoint', async () => {
    (apiClient.get as any).mockResolvedValue({ data: {} });

    await adminService.getUserPortfolio(5);

    expect(apiClient.get).toHaveBeenCalledWith('/admin/fd/user/5/portfolio');
  });

  it('getInterestTimeline supports interval query', async () => {
    (apiClient.get as any).mockResolvedValue({ data: {} });

    await adminService.getInterestTimeline(9, 'MONTHLY');

    expect(apiClient.get).toHaveBeenCalledWith('/admin/fd/9/interest/timeline?interval=MONTHLY');
  });
});
