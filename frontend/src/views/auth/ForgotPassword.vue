<template>
  <div class="auth-page">
    <Navbar />
    <div class="auth-container">
      <div class="auth-card card slide-in-up">
        <div class="auth-header">
          <h2>Reset Password</h2>
          <p>Recover access to your Zeta FD account</p>
        </div>

        <!-- Step Indicator -->
        <div v-if="currentStep < 4" class="step-indicator">
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
            <span class="step-label">Reset</span>
          </div>
        </div>

        <!-- Step 1: Enter Email -->
        <div v-if="currentStep === 1">
          <div class="step-description">
            <p>Enter your registered email and we'll send you a verification code.</p>
          </div>

          <form @submit.prevent="handleSendOtp" class="auth-form">
            <div class="form-group">
              <label class="form-label">Email Address</label>
              <input
                v-model="email"
                type="email"
                class="form-control"
                placeholder="Enter your registered email"
                required
                @blur="validateEmail"
              />
              <span v-if="errors.email" class="form-error">{{ errors.email }}</span>
            </div>

            <div v-if="apiError" class="alert alert-error">{{ apiError }}</div>

            <button
              type="submit"
              class="btn btn-primary btn-full"
              :disabled="sendingOtp || !email || !!errors.email"
            >
              <span v-if="sendingOtp" class="spinner"></span>
              <span v-else>Send Reset Code</span>
            </button>
          </form>

          <div class="auth-footer">
            <p>Remember your password? <router-link to="/login">Back to Login</router-link></p>
          </div>
        </div>

        <!-- Step 2: Enter OTP -->
        <div v-if="currentStep === 2">
          <div class="otp-sent-info">
            <div class="otp-icon">📧</div>
            <p>We've sent a 6-digit verification code to</p>
            <strong>{{ email }}</strong>
          </div>

          <div class="auth-form">
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

            <div v-if="apiError" class="alert alert-error">{{ apiError }}</div>

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

          <div class="auth-footer">
            <p><router-link to="/login">Back to Login</router-link></p>
          </div>
        </div>

        <!-- Step 3: New Password -->
        <div v-if="currentStep === 3">
          <form @submit.prevent="handleResetPassword" class="auth-form">
            <div class="verified-email">
              <span class="verified-icon">✓</span>
              <span>{{ email }}</span>
              <span class="verified-badge">Verified</span>
            </div>

            <div class="form-group">
              <label class="form-label">New Password</label>
              <div class="password-input">
                <input
                  v-model="newPassword"
                  :type="showPassword ? 'text' : 'password'"
                  class="form-control"
                  placeholder="Minimum 8 characters"
                  required
                  @input="validatePasswords"
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
                <span :class="{ met: newPassword.length >= 8 }">✓ 8+ characters</span>
                <span :class="{ met: /[a-z]/.test(newPassword) }">✓ Lowercase</span>
                <span :class="{ met: /[A-Z]/.test(newPassword) }">✓ Uppercase</span>
                <span :class="{ met: /[0-9]/.test(newPassword) }">✓ Number</span>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Confirm New Password</label>
              <input
                v-model="confirmPassword"
                :type="showPassword ? 'text' : 'password'"
                class="form-control"
                placeholder="Re-enter new password"
                required
                @input="validatePasswords"
              />
              <span v-if="errors.confirmPassword" class="form-error">{{ errors.confirmPassword }}</span>
            </div>

            <div v-if="apiError" class="alert alert-error">{{ apiError }}</div>

            <button
              type="submit"
              class="btn btn-primary btn-full"
              :disabled="resetting || !isPasswordFormValid"
            >
              <span v-if="resetting" class="spinner"></span>
              <span v-else>Reset Password</span>
            </button>
          </form>
        </div>

        <!-- Step 4: Success -->
        <div v-if="currentStep === 4">
          <div class="success-state">
            <div class="success-icon-wrapper">
              <div class="success-icon">✓</div>
            </div>
            <h2>Password Reset Successful!</h2>
            <p>Your password has been changed successfully. You can now login with your new password.</p>
            <router-link to="/login" class="btn btn-primary btn-full">
              Go to Login
            </router-link>
          </div>
        </div>


      </div>
    </div>
    <Footer />
  </div>
</template>


<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue';
import { authService } from '@/services/authServices';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import * as validators from '@/utils/validators';


const currentStep = ref(1);
const email = ref('');
const newPassword = ref('');
const confirmPassword = ref('');
const showPassword = ref(false);
const verifiedOtp = ref('');

const sendingOtp = ref(false);
const verifyingOtp = ref(false);
const resetting = ref(false);
const apiError = ref('');
const resendCooldown = ref(0);
let cooldownTimer: ReturnType<typeof setInterval> | null = null;

const otpDigits = ref<string[]>(['', '', '', '', '', '']);
const otpRefs = ref<(HTMLInputElement | null)[]>([]);

const setOtpRef = (el: HTMLInputElement | null, index: number) => {
  otpRefs.value[index] = el;
};

const otpCode = computed(() => otpDigits.value.join(''));

const errors = ref({
  email: '',
  otp: '',
  password: '',
  confirmPassword: '',
});


const passwordStrength = computed(() => {
  const pass = newPassword.value;
  let strength = 0;
  if (pass.length >= 8) strength += 25;
  if (/[a-z]/.test(pass)) strength += 25;
  if (/[A-Z]/.test(pass)) strength += 25;
  if (/[0-9]/.test(pass)) strength += 25;
  return strength;
});


const isPasswordFormValid = computed(() => {
  return (
    newPassword.value &&
    confirmPassword.value &&
    newPassword.value === confirmPassword.value &&
    passwordStrength.value === 100 &&
    !errors.value.password &&
    !errors.value.confirmPassword
  );
});


const validateEmail = () => {
  const result = validators.email(email.value);
  errors.value.email = typeof result === 'string' ? result : '';
};


const validatePasswords = () => {
  const passResult = validators.password(newPassword.value);
  errors.value.password = typeof passResult === 'string' ? passResult : '';

  if (confirmPassword.value && newPassword.value !== confirmPassword.value) {
    errors.value.confirmPassword = 'Passwords do not match';
  } else {
    errors.value.confirmPassword = '';
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
  apiError.value = '';
  try {
    await authService.sendPasswordOtp(email.value);
    currentStep.value = 2;
    startCooldown();
  } catch (err: any) {
    const status = err?.response?.status;
    const msg = err?.response?.data?.message || '';
    if (status === 429 || msg.includes('Too many attempts')) {
      apiError.value = msg || 'Too many attempts. Please try again later.';
    } else if (msg.includes('No account found')) {
      apiError.value = 'No account found with this email address. Please check your email and try again.';
    } else if (msg.includes('Unable to send')) {
      apiError.value = 'Unable to send verification code right now. Please try again later.';
    } else {
      apiError.value = 'Something went wrong. Please try again later.';
    }
  } finally {
    sendingOtp.value = false;
  }
};


const handleVerifyOtp = () => {
  if (otpCode.value.length !== 6) {
    errors.value.otp = 'Please enter the complete 6-digit code';
    return;
  }
  // Store the OTP for the reset call, move to password step
  verifiedOtp.value = otpCode.value;
  apiError.value = '';
  currentStep.value = 3;
};


const handleResendOtp = async () => {
  sendingOtp.value = true;
  apiError.value = '';
  otpDigits.value = ['', '', '', '', '', ''];
  try {
    await authService.sendPasswordOtp(email.value);
    startCooldown();
  } catch (err: any) {
    const status = err?.response?.status;
    const msg = err?.response?.data?.message || '';
    if (status === 429 || msg.includes('Too many attempts')) {
      apiError.value = msg || 'Too many attempts. Please try again later.';
    } else {
      apiError.value = 'Failed to resend code. Please try again.';
    }
  } finally {
    sendingOtp.value = false;
  }
};


const changeEmail = () => {
  currentStep.value = 1;
  otpDigits.value = ['', '', '', '', '', ''];
  apiError.value = '';
  errors.value.otp = '';
  if (cooldownTimer) {
    clearInterval(cooldownTimer);
    cooldownTimer = null;
  }
  resendCooldown.value = 0;
};


const handleResetPassword = async () => {
  validatePasswords();
  if (!isPasswordFormValid.value) return;

  resetting.value = true;
  apiError.value = '';
  try {
    await authService.resetPassword(email.value, verifiedOtp.value, newPassword.value);
    currentStep.value = 4;
  } catch (err: any) {
    const msg = err?.response?.data?.message || '';
    const status = err?.response?.status;
    if (status === 429 || msg.includes('Too many attempts')) {
      apiError.value = msg || 'Too many failed attempts. Please try again later.';
    } else if (msg.includes('expired') || msg.includes('OTP expired')) {
      apiError.value = 'Verification code has expired. Please request a new one.';
      currentStep.value = 2;
      otpDigits.value = ['', '', '', '', '', ''];
      verifiedOtp.value = '';
    } else if (msg.includes('Invalid OTP')) {
      apiError.value = 'Invalid verification code. Please re-enter your code.';
      currentStep.value = 2;
      otpDigits.value = ['', '', '', '', '', ''];
      verifiedOtp.value = '';
    } else if (msg.includes('User not found') || msg.includes('not found')) {
      apiError.value = 'No account found with this email address.';
      currentStep.value = 1;
    } else if (msg.includes('blocked')) {
      apiError.value = msg || 'Password reset temporarily blocked. Please try again later.';
    } else {
      apiError.value = 'Password reset failed. Please try again.';
    }
  } finally {
    resetting.value = false;
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
  max-width: 480px;
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

.step-description {
  text-align: center;
  margin-bottom: var(--spacing-lg);

  p {
    color: var(--zeta-text-secondary);
    margin: 0;
  }
}

/* OTP sent info */
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


/* OTP Inputs */
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


/* Password */
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


/* Success State */
.success-state {
  text-align: center;
  padding: var(--spacing-xl) 0;

  .success-icon-wrapper {
    margin-bottom: var(--spacing-xl);
  }

  .success-icon {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: linear-gradient(135deg, #16a34a, #22c55e);
    color: white;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-size: 36px;
    font-weight: 700;
    box-shadow: 0 8px 32px rgba(22, 163, 74, 0.3);
    animation: successPulse 2s ease-in-out infinite;
  }

  h2 {
    color: var(--zeta-primary);
    margin-bottom: var(--spacing-md);
  }

  p {
    color: var(--zeta-text-secondary);
    margin-bottom: var(--spacing-xl);
    line-height: 1.6;
  }
}

@keyframes successPulse {
  0%, 100% { box-shadow: 0 8px 32px rgba(22, 163, 74, 0.3); }
  50% { box-shadow: 0 8px 48px rgba(22, 163, 74, 0.5); }
}


/* Shared */
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
  text-decoration: none;
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
