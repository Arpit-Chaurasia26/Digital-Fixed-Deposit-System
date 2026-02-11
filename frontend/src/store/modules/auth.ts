import { authService } from '@/services/authService';
import { getErrorMessage } from '@/utils/helpers';
import { LoginRequest, RegisterRequest, UserProfile, UpdateProfileRequest, ChangePasswordRequest } from '@/types';


export interface AuthState {
 user: UserProfile | null;
 isAuthenticated: boolean;
 loading: boolean;
 error: string | null;
}


type Commit = (type: string, payload?: unknown) => void;
type ActionCtx = { commit: Commit };


const buildAuthErrorMessage = (error: any, action: 'login' | 'register'): string => {
 const status = error?.response?.status;
 const serverMessage = error?.response?.data?.message;


 if (action === 'login') {
   if (status === 401) return 'Invalid email or password. Please try again.';
   if (status === 403) return 'Your account does not have access. Contact support if this is unexpected.';
 }


 if (action === 'register') {
   if (status === 409) return 'An account with this email already exists. Please sign in instead.';
 }


 return getErrorMessage(error, action === 'login' ? 'Unable to sign in' : 'Unable to create account');
};


const authModule = {
 namespaced: true,


 state: (): AuthState => ({
   user: null,
   isAuthenticated: false,
   loading: false,
   error: null,
 }),


 getters: {
   isAuthenticated: (state: AuthState) => state.isAuthenticated,
   user: (state: AuthState) => state.user,
   isAdmin: (state: AuthState) => state.user?.role === 'ADMIN',
   isUser: (state: AuthState) => state.user?.role === 'USER',
   userId: (state: AuthState) => state.user?.id,
   loading: (state: AuthState) => state.loading,
   error: (state: AuthState) => state.error,
 },


 mutations: {
   SET_USER(state: AuthState, user: UserProfile | null) {
     state.user = user;
     state.isAuthenticated = !!user;
   },
   SET_LOADING(state: AuthState, loading: boolean) {
     state.loading = loading;
   },
   SET_ERROR(state: AuthState, error: string | null) {
     state.error = error;
   },
   CLEAR_ERROR(state: AuthState) {
     state.error = null;
   },
 },


 actions: {
   async register({ commit }: ActionCtx, data: RegisterRequest) {
     commit('SET_LOADING', true);
     commit('CLEAR_ERROR');
     try {
       await authService.register(data);
       // After registration, fetch profile
       const user = await authService.getProfile();
       commit('SET_USER', user);
       return { success: true };
     } catch (error: any) {
       commit('SET_ERROR', buildAuthErrorMessage(error, 'register'));
       throw error;
     } finally {
       commit('SET_LOADING', false);
     }
   },


   async login({ commit }: ActionCtx, data: LoginRequest) {
     commit('SET_LOADING', true);
     commit('CLEAR_ERROR');
     try {
       await authService.login(data);
       // After login, fetch profile
       const user = await authService.getProfile();
       commit('SET_USER', user);
       return { success: true };
     } catch (error: any) {
       commit('SET_ERROR', buildAuthErrorMessage(error, 'login'));
       throw error;
     } finally {
       commit('SET_LOADING', false);
     }
   },


   async fetchProfile({ commit }: ActionCtx) {
     commit('SET_LOADING', true);
     commit('CLEAR_ERROR');
     try {
       const user = await authService.getProfile();
       commit('SET_USER', user);
     } catch (error: any) {
       commit('SET_USER', null);
       throw error;
     } finally {
       commit('SET_LOADING', false);
     }
   },


   async logout({ commit }: ActionCtx) {
     commit('SET_LOADING', true);
     try {
       await authService.logout();
       commit('SET_USER', null);
     } catch (error) {
       // Even if logout fails, clear local state
       commit('SET_USER', null);
     } finally {
       commit('SET_LOADING', false);
     }
   },


   async updateProfile({ commit }: ActionCtx, data: UpdateProfileRequest) {
     commit('SET_LOADING', true);
     commit('CLEAR_ERROR');
     try {
       const user = await authService.updateProfile(data);
       commit('SET_USER', user);
       return user;
     } catch (error: any) {
       commit('SET_ERROR', getErrorMessage(error, 'Unable to update profile'));
       throw error;
     } finally {
       commit('SET_LOADING', false);
     }
   },


   async changePassword({ commit }: ActionCtx, data: ChangePasswordRequest) {
     commit('SET_LOADING', true);
     commit('CLEAR_ERROR');
     try {
       return await authService.changePassword(data);
     } catch (error: any) {
       commit('SET_ERROR', getErrorMessage(error, 'Unable to update password'));
       throw error;
     } finally {
       commit('SET_LOADING', false);
     }
   },


   clearError({ commit }: ActionCtx) {
     commit('CLEAR_ERROR');
   },
 },
};


export default authModule;



