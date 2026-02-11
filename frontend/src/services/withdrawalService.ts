import apiClient from './axios';
import { WithdrawalPreview, WithdrawalReceipt } from '@/types';


export const withdrawalService = {
 // Get withdrawal preview (break preview)
 async getBreakPreview(fdId: number): Promise<WithdrawalPreview> {
   const response = await apiClient.get(`/fd/${fdId}/break-preview`);
   return response.data;
 },


 // Confirm withdrawal (break FD)
 async confirmBreak(fdId: number): Promise<WithdrawalReceipt> {
   const response = await apiClient.post(`/fd/${fdId}/break`);
   return response.data;
 },


 // Check withdrawal eligibility
 async checkEligibility(fdId: number): Promise<boolean> {
   const response = await apiClient.get(`/fd/${fdId}/withdrawal-eligibility`);
   return response.data;
 },
};



