import apiClient from './axios';
import { WithdrawalPreview, WithdrawalReceipt, EligibilityMessage, WIthdrawalHistory } from '@/types';


export const withdrawalService = {
 // Get withdrawal preview (break preview)
 async getBreakPreview(fdId: number, amount:number): Promise<WithdrawalPreview> {
   const response = await apiClient.get(`/fd/${fdId}/break-preview?amount=${amount}`);
   return response.data;
 },


 // Confirm withdrawal (break FD)
 async confirmBreak(fdId: number, amount:number): Promise<WithdrawalReceipt> {
   const response = await apiClient.post(`/fd/${fdId}/break?amount=${amount}`);
   return response.data;
 },


 // Check withdrawal eligibility
 async checkEligibility(fdId: number): Promise<EligibilityMessage> {
   const response = await apiClient.get(`/fd/${fdId}/withdrawal-eligibility`);
   return response.data;
 },

 //Get Trnsaction History
  async getTransactionHistory(userId:number):Promise<WIthdrawalHistory>{
    const response = await apiClient.get(`/fd/${userId}/withdrawals`);
    console.log(response.data);
    return response.data;
  }

};



