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
</style>



