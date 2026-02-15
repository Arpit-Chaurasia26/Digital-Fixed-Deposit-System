<template>
  <div class="oauth-callback-page">
    <div class="callback-container">
      <div class="callback-card">
        <div v-if="processing" class="processing">
          <div class="spinner-large"></div>
          <h2>Authenticating with Google...</h2>
          <p>Please wait while we complete your login</p>
        </div>
        
        <div v-else-if="error" class="error-state">
          <div class="error-icon">⚠️</div>
          <h2>Authentication Failed</h2>
          <p>{{ error }}</p>
          <button @click="goToLogin" class="btn btn-primary">
            Back to Login
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';

const router = useRouter();
const store = useStore();

const processing = ref(true);
const error = ref('');

onMounted(async () => {
  try {
    // The backend has already set authentication cookies
    // Just fetch the user profile to verify authentication
    await store.dispatch('auth/fetchProfile');

    // Get user and redirect based on role
    const user = store.getters['auth/user'];
    
    if (!user) {
      error.value = 'Authentication failed. Please try again.';
      processing.value = false;
      return;
    }
    
    if (user.role === 'ADMIN') {
      router.push('/admin/dashboard');
    } else {
      router.push('/user/dashboard');
    }
  } catch (err: any) {
    console.error('OAuth callback error:', err);
    error.value = err?.response?.data?.message || 'Failed to complete authentication. Please try again.';
    processing.value = false;
  }
});

const goToLogin = () => {
  router.push('/login');
};
</script>

<style scoped lang="scss">
.oauth-callback-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--zeta-gradient-hero);
  padding: var(--spacing-lg);
}

.callback-container {
  width: 100%;
  max-width: 500px;
}

.callback-card {
  background: white;
  border-radius: var(--border-radius-xl);
  padding: var(--spacing-3xl);
  text-align: center;
  box-shadow: var(--shadow-xl);
}

.processing {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-lg);

  h2 {
    color: var(--color-text-primary);
    font-size: var(--font-size-2xl);
    margin: 0;
  }

  p {
    color: var(--color-text-secondary);
    margin: 0;
  }
}

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-lg);

  .error-icon {
    font-size: 4rem;
  }

  h2 {
    color: var(--color-danger);
    font-size: var(--font-size-2xl);
    margin: 0;
  }

  p {
    color: var(--color-text-secondary);
    margin: 0;
  }
}

.spinner-large {
  width: 60px;
  height: 60px;
  border: 4px solid var(--color-surface);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.btn {
  margin-top: var(--spacing-md);
  padding: var(--spacing-sm) var(--spacing-xl);
  font-size: var(--font-size-base);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  border: none;
  transition: all 0.2s;

  &.btn-primary {
    background: var(--color-primary);
    color: white;

    &:hover {
      background: var(--color-primary-dark);
      transform: translateY(-2px);
      box-shadow: var(--shadow-md);
    }
  }
}
</style>
