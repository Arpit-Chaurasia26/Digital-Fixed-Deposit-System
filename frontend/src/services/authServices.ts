import apiClient from './axios';
import { LoginRequest, RegisterRequest, UserProfile, UpdateProfileRequest, ChangePasswordRequest } from '@/types';


export const authService = {
 // Register new user
 async register(data: RegisterRequest): Promise<void> {
   const response = await apiClient.post('/auth/register', data);
   return response.data;
 },


 // Login user
 async login(data: LoginRequest): Promise<void> {
   const response = await apiClient.post('/auth/login', data);
   return response.data;
 },


 // Get user profile
 async getProfile(): Promise<UserProfile> {
   const response = await apiClient.get('/user/profile');
   return response.data;
 },


 // Update user profile
 async updateProfile(data: UpdateProfileRequest): Promise<UserProfile> {
   const response = await apiClient.put('/user/profile', data);
   return response.data;
 },


 // Change password
 async changePassword(data: ChangePasswordRequest): Promise<string> {
   const response = await apiClient.put('/user/profile/password', data);
   return response.data;
 },


 // Logout user
 async logout(): Promise<void> {
   const response = await apiClient.post('/auth/logout');
   return response.data;
 },


 // Refresh token
 async refreshToken(): Promise<void> {
   const response = await apiClient.post('/auth/refresh');
   return response.data;
 },


 // Send OTP for email verification (registration flow)
 async sendEmailOtp(email: string): Promise<void> {
   await apiClient.post(`/auth/email/send-otp?email=${encodeURIComponent(email)}`);
 },


 // Verify email OTP (registration flow)
 async verifyEmailOtp(email: string, otp: string): Promise<void> {
   await apiClient.post(`/auth/email/verify-otp?email=${encodeURIComponent(email)}&otp=${encodeURIComponent(otp)}`);
 },


 // Send OTP for password reset
 async sendPasswordOtp(email: string): Promise<void> {
   await apiClient.post(`/auth/password/send-otp?email=${encodeURIComponent(email)}`);
 },


 // Reset password with OTP
 async resetPassword(email: string, otp: string, newPassword: string): Promise<void> {
   await apiClient.post(
     `/auth/password/reset?email=${encodeURIComponent(email)}&otp=${encodeURIComponent(otp)}&newPassword=${encodeURIComponent(newPassword)}`
   );
 },
};



