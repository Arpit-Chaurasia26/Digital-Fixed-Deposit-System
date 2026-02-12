import apiClient from './axios';
import {
  BookFDRequest,
  FixedDeposit,
  FDMaturityResponse,
  FDFinancialYearSummary,
  FDPortfolio,
  FDInterestTimeline,
  FDInterestResponse,
  FDStatus,
  SchemeResponse,
  AdminFinancialYearSummary,
} from '@/types';

export const fdService = {
  // Get all available FD schemes
  async getAllSchemes(): Promise<SchemeResponse[]> {
    const response = await apiClient.get('/fd/schemes');
    return response.data?.data ?? response.data;
  },
  
 // Book a new Fixed Deposit
 async bookFD(data: BookFDRequest): Promise<FixedDeposit> {
   const response = await apiClient.post('/fd/book', data);
   return response.data;
 },

 // Get user's FDs with filters
 async getUserFDs(
   userId: number,
   filters?: {
     status?: FDStatus;
     minAmount?: number;
     maxAmount?: number;
   }
 ): Promise<FixedDeposit[]> {
   const params = new URLSearchParams();
   if (filters?.status) params.append('status', filters.status);
   if (filters?.minAmount) params.append('minAmount', filters.minAmount.toString());
   if (filters?.maxAmount) params.append('maxAmount', filters.maxAmount.toString());


   const response = await apiClient.get(`/fd/user/${userId}?${params.toString()}`);
   return response.data;
 },


 // Get specific FD by ID
 async getFDById(userId: number, fdId: number): Promise<FixedDeposit> {
   const response = await apiClient.get(`/fd/user/${userId}/${fdId}`);
   return response.data;
 },

  // Get FDs maturing within N days
  async getMaturingFDs(userId: number, days: number = 7): Promise<FDMaturityResponse[]> {
    const response = await apiClient.get(`/fd/user/${userId}/maturing?days=${days}`);
    return response.data;
  },

  // Get financial year summary
  async getFinancialYearSummary(userId: number, year?: number): Promise<FDFinancialYearSummary> {
    const url = year
      ? `/fd/user/${userId}/summary/financial-year?year=${year}`
      : `/fd/user/${userId}/summary/financial-year`;
    const response = await apiClient.get(url);
    return response.data;
  },

  // Get admin financial year summary
  async getAdminFinancialYearSummary(year?: number): Promise<AdminFinancialYearSummary> {
    const url = year
      ? `/admin/fd/summary/financial-year?year=${year}`
      : `/admin/fd/summary/financial-year`;
    const response = await apiClient.get(url);
    return response.data;
  },

  // Get all maturing FDs for admin
  async getAdminMaturingFDs(days: number = 7): Promise<FDMaturityResponse[]> {
    const response = await apiClient.get(`/admin/fd/maturing?days=${days}`);
    return response.data;
  },

  // Update FD status (admin)
  async updateFDStatus(fdId: number, status: FDStatus): Promise<FixedDeposit> {
    const response = await apiClient.put(`/admin/fd/${fdId}/status`, { status });
    return response.data;
  },

  // Get admin FDs by financial year
  async getAdminFDsByYear(year: number): Promise<FixedDeposit[]> {
    const response = await apiClient.get(`/admin/fd/yearly?year=${year}`);
    return response.data;
  },

  // Get all admin FDs in chronological order
  async getAdminAllFDs(order: 'asc' | 'desc' = 'desc'): Promise<FixedDeposit[]> {
    const response = await apiClient.get(`/admin/fd/all?order=${order}`);
    return response.data;
  },

  // Get user FD portfolio as admin
  async getAdminUserPortfolio(userId: number): Promise<FDPortfolio> {
    const response = await apiClient.get(`/admin/fd/user/${userId}/portfolio`);
    return response.data;
  },

  // Get interest timeline for specific FD as admin
  async getAdminInterestTimeline(fdId: number, interval?: string): Promise<FDInterestTimeline> {
    const url = interval
      ? `/admin/fd/${fdId}/interest/timeline?interval=${interval}`
      : `/admin/fd/${fdId}/interest/timeline`;
    const response = await apiClient.get(url);
    return response.data;
  },

  // Get user FD portfolio
  async getUserPortfolio(userId: number): Promise<FDPortfolio> {
    const response = await apiClient.get(`/fd/user/${userId}/portfolio`);
    return response.data;
  },

  // Get interest timeline for specific FD
  async getInterestTimeline(fdId: number, interval?: string): Promise<FDInterestTimeline> {
    const url = interval
      ? `/fd/${fdId}/interest/timeline?interval=${interval}`
      : `/fd/${fdId}/interest/timeline`;
    const response = await apiClient.get(url);
    return response.data;
  },

  // Get current accrued interest
  async getAccruedInterest(fdId: number): Promise<FDInterestResponse> {
    const response = await apiClient.get(`/fd/${fdId}/interest`);
    return response.data;
  },
  

};
