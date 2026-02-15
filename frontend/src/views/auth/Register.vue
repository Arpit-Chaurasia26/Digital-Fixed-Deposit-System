<template>
 <div class="auth-page">
   <Navbar />
   <div class="auth-container">
     <div class="auth-card card slide-in-up">
       <div class="auth-header">
         <h2>Create Account</h2>
         <p>Join Zeta FD for secure investments</p>
       </div>

       <!-- Step Indicator -->
       <div class="step-indicator">
         <div class="step" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
           <div class="step-circle">
             <span v-if="currentStep > 1">✓</span>
             <span v-else>1</span>
           </div>
           <span class="step-label">Email</span>
         </div>
         <div class="step-line" :class="{ active: currentStep > 1 }"></div>
         <div class="step" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
           <div class="step-circle">
             <span v-if="currentStep > 2">✓</span>
             <span v-else>2</span>
           </div>
           <span class="step-label">Verify</span>
         </div>
         <div class="step-line" :class="{ active: currentStep > 2 }"></div>
         <div class="step" :class="{ active: currentStep >= 3 }">
           <div class="step-circle">3</div>
           <span class="step-label">Details</span>
         </div>
       </div>

       <!-- Step 1: Email & Send OTP -->
       <form v-if="currentStep === 1" @submit.prevent="handleSendOtp" class="auth-form">
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

         <div v-if="otpError" class="alert alert-error">{{ otpError }}</div>

         <button
           type="submit"
           class="btn btn-primary btn-full"
           :disabled="sendingOtp || !formData.email || !!errors.email"
         >
           <span v-if="sendingOtp" class="spinner"></span>
           <span v-else>Send Verification Code</span>
         </button>
       </form>

       <!-- Step 2: Verify OTP -->
       <div v-if="currentStep === 2" class="auth-form">
         <div class="otp-sent-info">
           <div class="otp-icon">📧</div>
           <p>We've sent a 6-digit verification code to</p>
           <strong>{{ formData.email }}</strong>
         </div>

         <div class="otp-input-group">
           <input
             v-for="(_, index) in otpDigits"
             :key="index"
             :ref="(el) => setOtpRef(el as HTMLInputElement, index)"
             v-model="otpDigits[index]"
             type="text"
             maxlength="1"
             class="otp-input"
             inputmode="numeric"
             @input="onOtpInput(index)"
             @keydown="onOtpKeydown($event, index)"
             @paste="onOtpPaste($event)"
           />
         </div>
         <span v-if="errors.otp" class="form-error otp-error">{{ errors.otp }}</span>

         <div v-if="otpError" class="alert alert-error">{{ otpError }}</div>

         <button
           class="btn btn-primary btn-full"
           :disabled="verifyingOtp || otpCode.length !== 6"
           @click="handleVerifyOtp"
         >
           <span v-if="verifyingOtp" class="spinner"></span>
           <span v-else>Verify Code</span>
         </button>

         <div class="otp-actions">
           <button
             type="button"
             class="link-button"
             :disabled="resendCooldown > 0 || sendingOtp"
             @click="handleResendOtp"
           >
             <span v-if="resendCooldown > 0">Resend code in {{ resendCooldown }}s</span>
             <span v-else-if="sendingOtp">Sending...</span>
             <span v-else>Resend code</span>
           </button>
           <button type="button" class="link-button" @click="changeEmail">
             Change email
           </button>
         </div>
       </div>

       <!-- Step 3: Complete Registration -->
       <form v-if="currentStep === 3" @submit.prevent="handleRegister" class="auth-form">
         <div class="verified-email">
           <span class="verified-icon">✓</span>
           <span>{{ formData.email }}</span>
           <span class="verified-badge">Verified</span>
         </div>

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
           <div class="password-requirements">
             <span :class="{ met: formData.password.length >= 8 }">✓ 8+ characters</span>
             <span :class="{ met: /[a-z]/.test(formData.password) }">✓ Lowercase</span>
             <span :class="{ met: /[A-Z]/.test(formData.password) }">✓ Uppercase</span>
             <span :class="{ met: /[0-9]/.test(formData.password) }">✓ Number</span>
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
           <span v-else>Create Account</span>
         </button>
       </form>

       <div class="divider">
         <span>OR</span>
       </div>

       <button @click="handleGoogleSignup" class="btn btn-google btn-full" type="button">
         <svg class="google-icon" viewBox="0 0 24 24" width="20" height="20">
           <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
           <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
           <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
           <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
         </svg>
         Continue with Google
       </button>

       <div class="auth-footer">
         <p>Already have an account? <router-link to="/login">Login here</router-link></p>
       </div>
     </div>
   </div>
   <Footer />
 </div>
</template>


<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import * as validators from '@/utils/validators';
import { authService } from '@/services/authServices';


const store = useStore();
const router = useRouter();


const _components = { Navbar, Footer };
void _components;


const currentStep = ref(1);
const sendingOtp = ref(false);
const verifyingOtp = ref(false);
const otpError = ref('');
const resendCooldown = ref(0);
let cooldownTimer: ReturnType<typeof setInterval> | null = null;

const otpDigits = ref<string[]>(['', '', '', '', '', '']);
const otpRefs = ref<(HTMLInputElement | null)[]>([]);

const setOtpRef = (el: HTMLInputElement | null, index: number) => {
 otpRefs.value[index] = el;
};

const otpCode = computed(() => otpDigits.value.join(''));


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
 otp: '',
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

 if (formData.value.confirmPassword) {
   const confirmResult = validators.confirmPassword(formData.value.password)(formData.value.confirmPassword);
   errors.value.confirmPassword = typeof confirmResult === 'string' ? confirmResult : '';
 }
};


// OTP input handlers
const onOtpInput = (index: number) => {
 const val = otpDigits.value[index];
 if (val && !/^\d$/.test(val)) {
   otpDigits.value[index] = '';
   return;
 }
 if (val && index < 5) {
   otpRefs.value[index + 1]?.focus();
 }
 errors.value.otp = '';
};

const onOtpKeydown = (event: KeyboardEvent, index: number) => {
 if (event.key === 'Backspace' && !otpDigits.value[index] && index > 0) {
   otpRefs.value[index - 1]?.focus();
 }
};

const onOtpPaste = (event: ClipboardEvent) => {
 event.preventDefault();
 const paste = event.clipboardData?.getData('text')?.replace(/\D/g, '').slice(0, 6) || '';
 for (let i = 0; i < 6; i++) {
   otpDigits.value[i] = paste[i] || '';
 }
 const focusIdx = Math.min(paste.length, 5);
 otpRefs.value[focusIdx]?.focus();
};


const startCooldown = () => {
 resendCooldown.value = 60;
 if (cooldownTimer) clearInterval(cooldownTimer);
 cooldownTimer = setInterval(() => {
   resendCooldown.value--;
   if (resendCooldown.value <= 0 && cooldownTimer) {
     clearInterval(cooldownTimer);
     cooldownTimer = null;
   }
 }, 1000);
};

onUnmounted(() => {
 if (cooldownTimer) clearInterval(cooldownTimer);
});


const handleSendOtp = async () => {
 validateEmail();
 if (errors.value.email) return;

 sendingOtp.value = true;
 otpError.value = '';
 try {
   await authService.sendEmailOtp(formData.value.email);
   currentStep.value = 2;
   startCooldown();
 } catch (err: any) {
   otpError.value = err?.response?.data?.message || 'Failed to send verification code. Please try again.';
 } finally {
   sendingOtp.value = false;
 }
};


const handleVerifyOtp = async () => {
 if (otpCode.value.length !== 6) {
   errors.value.otp = 'Please enter the complete 6-digit code';
   return;
 }

 verifyingOtp.value = true;
 otpError.value = '';
 try {
   await authService.verifyEmailOtp(formData.value.email, otpCode.value);
   currentStep.value = 3;
 } catch (err: any) {
   const msg = err?.response?.data?.message || '';
   if (msg.includes('expired')) {
     otpError.value = 'Verification code has expired. Please request a new one.';
   } else if (msg.includes('Invalid')) {
     otpError.value = 'Invalid verification code. Please check and try again.';
   } else {
     otpError.value = msg || 'Verification failed. Please try again.';
   }
 } finally {
   verifyingOtp.value = false;
 }
};


const handleResendOtp = async () => {
 sendingOtp.value = true;
 otpError.value = '';
 otpDigits.value = ['', '', '', '', '', ''];
 try {
   await authService.sendEmailOtp(formData.value.email);
   startCooldown();
 } catch (err: any) {
   otpError.value = err?.response?.data?.message || 'Failed to resend code. Please try again.';
 } finally {
   sendingOtp.value = false;
 }
};


const changeEmail = () => {
 currentStep.value = 1;
 otpDigits.value = ['', '', '', '', '', ''];
 otpError.value = '';
 errors.value.otp = '';
 if (cooldownTimer) {
   clearInterval(cooldownTimer);
   cooldownTimer = null;
 }
 resendCooldown.value = 0;
};


const handleRegister = async () => {
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

const handleGoogleSignup = () => {
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
 max-width: 500px;
 padding: var(--spacing-2xl);
}


.auth-header {
 text-align: center;
 margin-bottom: var(--spacing-lg);

 h2 {
   color: var(--zeta-primary);
   margin-bottom: var(--spacing-sm);
 }

 p {
   color: var(--zeta-text-secondary);
 }
}


/* Step Indicator */
.step-indicator {
 display: flex;
 align-items: center;
 justify-content: center;
 margin-bottom: var(--spacing-2xl);
 gap: 0;
}

.step {
 display: flex;
 flex-direction: column;
 align-items: center;
 gap: var(--spacing-xs);

 .step-circle {
   width: 36px;
   height: 36px;
   border-radius: 50%;
   display: flex;
   align-items: center;
   justify-content: center;
   font-size: var(--font-size-sm);
   font-weight: 700;
   border: 2px solid var(--zeta-divider);
   color: var(--zeta-text-secondary);
   background: var(--zeta-surface);
   transition: all 0.3s ease;
 }

 .step-label {
   font-size: var(--font-size-xs);
   color: var(--zeta-text-secondary);
   font-weight: 500;
   transition: color 0.3s ease;
 }

 &.active .step-circle {
   border-color: var(--zeta-primary);
   color: white;
   background: var(--zeta-primary);
   box-shadow: 0 0 0 4px rgba(14, 116, 144, 0.15);
 }

 &.active .step-label {
   color: var(--zeta-primary);
   font-weight: 600;
 }

 &.completed .step-circle {
   border-color: #16a34a;
   color: white;
   background: #16a34a;
   box-shadow: 0 0 0 4px rgba(22, 163, 74, 0.15);
 }

 &.completed .step-label {
   color: #16a34a;
 }
}

.step-line {
 width: 48px;
 height: 2px;
 background: var(--zeta-divider);
 margin: 0 var(--spacing-sm);
 margin-bottom: 20px;
 transition: background 0.3s ease;

 &.active {
   background: #16a34a;
 }
}


/* OTP Section */
.otp-sent-info {
 text-align: center;
 margin-bottom: var(--spacing-xl);

 .otp-icon {
   font-size: 48px;
   margin-bottom: var(--spacing-md);
 }

 p {
   color: var(--zeta-text-secondary);
   margin-bottom: var(--spacing-xs);
 }

 strong {
   color: var(--zeta-primary);
   font-size: var(--font-size-md);
 }
}

.otp-input-group {
 display: flex;
 justify-content: center;
 gap: var(--spacing-sm);
 margin-bottom: var(--spacing-lg);
}

.otp-input {
 width: 48px;
 height: 56px;
 text-align: center;
 font-size: 24px;
 font-weight: 700;
 border: 2px solid var(--zeta-divider);
 border-radius: var(--radius-md);
 background: var(--zeta-surface);
 color: var(--zeta-text);
 outline: none;
 transition: all 0.2s ease;
 caret-color: var(--zeta-primary);

 &:focus {
   border-color: var(--zeta-primary);
   box-shadow: 0 0 0 3px rgba(14, 116, 144, 0.15);
 }
}

.otp-error {
 text-align: center;
 display: block;
 margin-bottom: var(--spacing-md);
}

.otp-actions {
 display: flex;
 justify-content: space-between;
 align-items: center;
 margin-top: var(--spacing-lg);
}


/* Verified Email Badge */
.verified-email {
 display: flex;
 align-items: center;
 gap: var(--spacing-sm);
 padding: var(--spacing-md);
 background: rgba(22, 163, 74, 0.08);
 border: 1px solid rgba(22, 163, 74, 0.25);
 border-radius: var(--radius-md);
 margin-bottom: var(--spacing-xl);
 font-size: var(--font-size-sm);

 .verified-icon {
   width: 24px;
   height: 24px;
   border-radius: 50%;
   background: #16a34a;
   color: white;
   display: flex;
   align-items: center;
   justify-content: center;
   font-size: 14px;
   font-weight: 700;
   flex-shrink: 0;
 }

 span:nth-child(2) {
   color: var(--zeta-text);
   font-weight: 500;
   overflow: hidden;
   text-overflow: ellipsis;
 }

 .verified-badge {
   margin-left: auto;
   background: #16a34a;
   color: white;
   padding: 2px 10px;
   border-radius: 999px;
   font-size: var(--font-size-xs);
   font-weight: 700;
   text-transform: uppercase;
   letter-spacing: 0.3px;
   flex-shrink: 0;
 }
}


/* Password Requirements */
.password-requirements {
 display: flex;
 flex-wrap: wrap;
 gap: var(--spacing-sm) var(--spacing-md);
 margin-top: var(--spacing-sm);

 span {
   font-size: var(--font-size-xs);
   color: var(--zeta-text-secondary);
   opacity: 0.6;
   transition: all 0.2s ease;

   &.met {
     color: #16a34a;
     opacity: 1;
   }
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


.link-button {
 background: none;
 border: none;
 color: var(--zeta-primary);
 font-weight: 600;
 cursor: pointer;
 padding: 0;
 font-size: var(--font-size-sm);

 &:hover:not(:disabled) {
   text-decoration: underline;
 }

 &:disabled {
   opacity: 0.5;
   cursor: not-allowed;
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



