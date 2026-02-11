<template>
 <div class="auth-page">
   <Navbar />
   <div class="auth-container">
     <div class="auth-card card slide-in-up">
       <div class="auth-header">
         <h2>Create Account</h2>
         <p>Join Zeta FD for secure investments</p>
       </div>


       <form @submit.prevent="handleRegister" class="auth-form">
         <div class="form-group">
           <label class="form-label">Full Name</label>
           <input
             v-model="formData.name"
             type="text"
             class="form-control"
             placeholder="Enter your full name"
             required
           />
           <span v-if="errors.name" class="form-error">{{ errors.name }}</span>
         </div>


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
               placeholder="Minimum 8 characters"
               required
               @blur="validatePassword"
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
           <div class="password-strength">
             <div class="password-strength-bar" :style="{ width: passwordStrength + '%' }"></div>
           </div>
         </div>


         <div class="form-group">
           <label class="form-label">Confirm Password</label>
           <input
             v-model="formData.confirmPassword"
             :type="showPassword ? 'text' : 'password'"
             class="form-control"
             placeholder="Re-enter password"
             required
           />
           <span v-if="errors.confirmPassword" class="form-error">{{ errors.confirmPassword }}</span>
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
           <span v-else>Register</span>
         </button>
       </form>


       <div class="auth-footer">
         <p>Already have an account? <router-link to="/login">Login here</router-link></p>
       </div>
     </div>
   </div>
   <Footer />
 </div>
</template>


<script setup lang="ts">
import { ref, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import * as validators from '@/utils/validators';


const store = useStore();
const router = useRouter();


const _components = { Navbar, Footer };
void _components;


const formData = ref({
 name: '',
 email: '',
 password: '',
 confirmPassword: '',
});


const errors = ref({
 name: '',
 email: '',
 password: '',
 confirmPassword: '',
});


const showPassword = ref(false);
const loading = computed(() => store.getters['auth/loading']);
const error = computed(() => store.getters['auth/error']);


const passwordStrength = computed(() => {
 const pass = formData.value.password;
 let strength = 0;
 if (pass.length >= 8) strength += 25;
 if (/[a-z]/.test(pass)) strength += 25;
 if (/[A-Z]/.test(pass)) strength += 25;
 if (/[0-9]/.test(pass)) strength += 25;
 return strength;
});


const isFormValid = computed(() => {
 return (
   formData.value.name &&
   formData.value.email &&
   formData.value.password &&
   formData.value.confirmPassword &&
   !errors.value.name &&
   !errors.value.email &&
   !errors.value.password &&
   !errors.value.confirmPassword
 );
});


const validateEmail = () => {
 const result = validators.email(formData.value.email);
 errors.value.email = typeof result === 'string' ? result : '';
};


const validatePassword = () => {
 const result = validators.password(formData.value.password);
 errors.value.password = typeof result === 'string' ? result : '';
  // Also validate confirm password if it's already filled
 if (formData.value.confirmPassword) {
   const confirmResult = validators.confirmPassword(formData.value.password)(formData.value.confirmPassword);
   errors.value.confirmPassword = typeof confirmResult === 'string' ? confirmResult : '';
 }
};


const handleRegister = async () => {
 // Validate all fields
 validateEmail();
 validatePassword();
  const confirmResult = validators.confirmPassword(formData.value.password)(formData.value.confirmPassword);
 errors.value.confirmPassword = typeof confirmResult === 'string' ? confirmResult : '';


 if (!isFormValid.value) return;


 try {
   await store.dispatch('auth/register', {
     name: formData.value.name,
     email: formData.value.email,
     password: formData.value.password,
   });


   // Get user role and redirect accordingly
   const user = store.getters['auth/user'];
   if (user?.role === 'ADMIN') {
     router.push('/admin/dashboard');
   } else {
     router.push('/user/dashboard');
   }
 } catch (err) {
   console.error('Registration failed:', err);
 }
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
 max-width: 500px;
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


 .password-strength {
   height: 4px;
   background: var(--zeta-divider);
   border-radius: var(--radius-full);
   margin-top: var(--spacing-sm);
   overflow: hidden;


   .password-strength-bar {
     height: 100%;
     background: var(--zeta-gradient-secondary);
     transition: width var(--transition-base);
   }
 }
}


.btn-full {
 width: 100%;
 display: flex;
 align-items: center;
 justify-content: center;
 gap: var(--spacing-sm);
 margin-top: var(--spacing-lg);
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


 a {
   color: var(--zeta-primary);
   font-weight: 600;


   &:hover {
     text-decoration: underline;
   }
 }
}
</style>



