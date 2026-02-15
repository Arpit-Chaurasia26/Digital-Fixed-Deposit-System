import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import router from '@/router';


// Create axios instance
const apiClient: AxiosInstance = axios.create({
 baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
 withCredentials: true, // Important: Send cookies with requests
 headers: {
   'Content-Type': 'application/json',
 },
});


// Request interceptor
apiClient.interceptors.request.use(
 (config: InternalAxiosRequestConfig) => {
   return config;
 },
 (error: AxiosError) => {
   return Promise.reject(error);
 }
);


// Response interceptor
apiClient.interceptors.response.use(
 (response: AxiosResponse) => {
   return response;
 },
 async (error: AxiosError) => {
   const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
   const requestUrl = originalRequest?.url || '';

   // Handle 401 Unauthorized - try to refresh token
   // Skip refresh for public auth endpoints (login, register, OTP, password reset)
   const isAuthEndpoint = requestUrl.includes('/auth/');
   if (error.response?.status === 401 && !originalRequest._retry && !isAuthEndpoint) {
     originalRequest._retry = true;


     try {
       // Try to refresh the token
       await axios.post(
         `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/auth/refresh`,
         {},
         { withCredentials: true }
       );


       // Retry the original request
       return apiClient(originalRequest);
     } catch (refreshError) {
       // Refresh failed - let route guards handle navigation
       return Promise.reject(refreshError);
     }
   }


   // Handle 403 Forbidden
   if (error.response?.status === 403) {
     console.error('Access forbidden');
     router.push('/');
   }


   return Promise.reject(error);
 }
);


export default apiClient;



