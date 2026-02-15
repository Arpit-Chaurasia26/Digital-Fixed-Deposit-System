<template>
 <div class="auth-page">
   <Navbar />
   <div class="auth-container">
     <div class="auth-card card slide-in-up">
       <div class="auth-header">
         <h2>Welcome Back</h2>
         <p>Login to access your FD account</p>
       </div>


       <form @submit.prevent="handleLogin" class="auth-form">
         <div class="form-group">
           <label class="form-label">Email Address</label>
           <input
             v-model="formData.email"
             type="email"
             class="form-control"
             placeholder="Enter your email"
             required
             @blur="validateEmail"
           />
           <span v-if="errors.email" class="form-error">{{ errors.email }}</span>
         </div>


         <div class="form-group">
           <label class="form-label">Password</label>
           <div class="password-input">
             <input
               v-model="formData.password"
               :type="showPassword ? 'text' : 'password'"
               class="form-control"
               placeholder="Enter your password"
               required
             />
             <button
               type="button"
               class="password-toggle"
               @click="showPassword = !showPassword"
               aria-label="Toggle password visibility"
             >
               {{ showPassword ? '👁️' : '👁️‍🗨️' }}
             </button>
           </div>
           <span v-if="errors.password" class="form-error">{{ errors.password }}</span>
         </div>


         <div class="form-options">
           <label class="checkbox-label">
             <input type="checkbox" v-model="rememberMe" />
             <span>Remember me</span>
           </label>
           <router-link to="/forgot-password" class="forgot-password">Forgot Password?</router-link>
         </div>


         <div v-if="error" class="alert alert-error">
           {{ error }}
         </div>


         <button
           type="submit"
           class="btn btn-primary btn-full"
           :disabled="loading || !isFormValid"
         >
           <span v-if="loading" class="spinner"></span>
           <span v-else>Login</span>
         </button>
       </form>

       <div class="divider">
         <span>OR</span>
       </div>

       <button @click="handleGoogleLogin" class="btn btn-google btn-full" type="button">
         <svg class="google-icon" viewBox="0 0 24 24" width="20" height="20">
           <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
           <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
           <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
           <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
         </svg>
         Continue with Google
       </button>


       <div class="auth-footer">
         <p>
           Don't have an account?
           <button type="button" class="link-button" @click="goToRegister">Register here</button>
         </p>
       </div>
     </div>
   </div>
   <Footer />
 </div>
</template>


<script setup lang="ts">
import { ref, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter, useRoute } from 'vue-router';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import * as validators from '@/utils/validators';


const store = useStore();
const router = useRouter();
const route = useRoute();


const formData = ref({
 email: '',
 password: '',
});


const errors = ref({
 email: '',
 password: '',
});


const showPassword = ref(false);
const rememberMe = ref(false);
const loading = computed(() => store.getters['auth/loading']);
const error = computed(() => store.getters['auth/error']);


const isFormValid = computed(() => {
 return (
   formData.value.email &&
   formData.value.password &&
   !errors.value.email &&
   !errors.value.password
 );
});


const validateEmail = () => {
 const result = validators.email(formData.value.email);
 errors.value.email = typeof result === 'string' ? result : '';
};


const handleLogin = async () => {
 // Validate fields
 validateEmail();
  const passwordResult = validators.required(formData.value.password);
 errors.value.password = typeof passwordResult === 'string' ? passwordResult : '';


 if (!isFormValid.value) return;


 try {
   await store.dispatch('auth/login', {
     email: formData.value.email,
     password: formData.value.password,
   });


   // Get redirect path or default based on role
   const redirect = route.query.redirect as string;
   const user = store.getters['auth/user'];
  
   if (redirect) {
     router.push(redirect);
   } else if (user?.role === 'ADMIN') {
     router.push('/admin/dashboard');
   } else {
     router.push('/user/dashboard');
   }
 } catch (err) {
   console.error('Login failed:', err);
 }
};


const goToRegister = () => {
 router.push('/register');
};

const handleGoogleLogin = () => {
  // Redirect to backend OAuth endpoint
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
  window.location.href = `${apiBaseUrl}/oauth2/authorization/google`;
};
</script>


<style scoped lang="scss">
.auth-page {
 min-height: 100vh;
 display: flex;
 flex-direction: column;
}


.auth-container {
 flex: 1;
 display: flex;
 align-items: center;
 justify-content: center;
 padding: var(--spacing-xl) var(--spacing-lg);
 background: var(--zeta-gradient-hero);
}


.auth-card {
 width: 100%;
 max-width: 450px;
 padding: var(--spacing-2xl);
}


.auth-header {
 text-align: center;
 margin-bottom: var(--spacing-xl);


 h2 {
   color: var(--zeta-primary);
   margin-bottom: var(--spacing-sm);
 }


 p {
   color: var(--zeta-text-secondary);
 }
}


.auth-form {
 .form-group {
   margin-bottom: var(--spacing-lg);
 }


 .password-input {
   position: relative;


   .password-toggle {
     position: absolute;
     right: var(--spacing-sm);
     top: 50%;
     transform: translateY(-50%);
     background: none;
     border: none;
     cursor: pointer;
     font-size: var(--font-size-lg);
     padding: var(--spacing-sm);
   }
 }
}


.form-options {
 display: flex;
 justify-content: space-between;
 align-items: center;
 margin-bottom: var(--spacing-lg);


 .checkbox-label {
   display: flex;
   align-items: center;
   gap: var(--spacing-sm);
   cursor: pointer;
   color: var(--zeta-text-secondary);


   input[type="checkbox"] {
     cursor: pointer;
   }
 }


 .forgot-password {
   color: var(--zeta-primary);
   font-size: var(--font-size-sm);


   &:hover {
     text-decoration: underline;
   }
 }
}


.btn-full {
 width: 100%;
 display: flex;
 align-items: center;
 justify-content: center;
 gap: var(--spacing-sm);
}


.alert {
 padding: var(--spacing-md);
 border-radius: var(--radius-md);
 margin-bottom: var(--spacing-lg);
}


.alert-error {
 background: var(--zeta-error-light);
 color: var(--zeta-error-dark);
}


.auth-footer {
 text-align: center;
 margin-top: var(--spacing-lg);
 padding-top: var(--spacing-lg);
 border-top: 1px solid var(--zeta-divider);


 .link-button {
   background: none;
   border: none;
   color: var(--zeta-primary);
   font-weight: 600;
   cursor: pointer;
   padding: 0;
   margin-left: 4px;


   &:hover {
     text-decoration: underline;
   }
 }
}

.divider {
  display: flex;
  align-items: center;
  text-align: center;
  margin: var(--spacing-xl) 0;
  color: var(--zeta-text-secondary);

  &::before,
  &::after {
    content: '';
    flex: 1;
    border-bottom: 1px solid var(--zeta-divider);
  }

  span {
    padding: 0 var(--spacing-md);
    font-size: var(--font-size-sm);
    font-weight: 500;
  }
}

.btn-google {
  background: var(--zeta-background-paper);
  color: var(--zeta-text-primary);
  border: 1px solid var(--zeta-divider);
  font-weight: 500;
  transition: all 0.3s ease;

  &:hover {
    background: var(--zeta-surface);
    border-color: var(--zeta-text-secondary);
    box-shadow: var(--shadow-md);
  }

  .google-icon {
    margin-right: var(--spacing-sm);
  }
}
</style>



