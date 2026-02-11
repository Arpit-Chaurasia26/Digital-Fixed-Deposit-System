import apiClient from './axios';
import {
  FDFinancialYearSummary,
  FDPortfolio,
  FDInterestTimeline,
  FDMaturityResponse,
  FDStatus,
} from '@/types';

export const adminService = {
  // Update FD status
  async updateFDStatus(fdId: number, status: FDStatus): Promise<void> {
    const response = await apiClient.put(`/admin/fd/${fdId}/status`, status, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    return response.data;
  },

  // Get FDs maturing within N days (all users)
  async getMaturingFDs(days: number = 7): Promise<FDMaturityResponse[]> {
    const response = await apiClient.get(`/admin/fd/maturing?days=${days}`);
    return response.data;
  },

  // Get financial year summary (all users)
  async getFinancialYearSummary(year?: number): Promise<FDFinancialYearSummary> {
    const url = year
      ? `/admin/fd/summary/financial-year?year=${year}`
      : `/admin/fd/summary/financial-year`;
    const response = await apiClient.get(url);
    return response.data;
  },

  // Get specific user's portfolio
  async getUserPortfolio(userId: number): Promise<FDPortfolio> {
    const response = await apiClient.get(`/admin/fd/user/${userId}/portfolio`);
    return response.data;
  },

  // Get interest timeline for specific FD
  async getInterestTimeline(fdId: number, interval?: string): Promise<FDInterestTimeline> {
    const url = interval
      ? `/admin/fd/${fdId}/interest/timeline?interval=${interval}`
      : `/admin/fd/${fdId}/interest/timeline`;
    const response = await apiClient.get(url);
    return response.data;
  },
};
