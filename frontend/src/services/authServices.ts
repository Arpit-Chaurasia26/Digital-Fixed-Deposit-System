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
};



