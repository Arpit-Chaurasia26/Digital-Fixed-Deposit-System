<template>
  <nav class="navbar">
    <div class="container navbar-container">
      <router-link :to="homeRoute" class="navbar-brand">
        <div class="logo">
          <img src="/fd-logo.svg" alt="Zeta FD logo" class="logo-mark" />
          <span class="logo-text">Zeta FD</span>
        </div>
      </router-link>

      <div :class="['navbar-menu', { active: mobileMenuOpen }]">
        <template v-if="!isAuthenticated">
          <router-link to="/" class="navbar-link" @click="closeMobileMenu">Home</router-link>
          <router-link to="/about" class="navbar-link" @click="closeMobileMenu">About</router-link>
          <router-link to="/contact" class="navbar-link" @click="closeMobileMenu">Contact</router-link>
          <router-link to="/register" class="navbar-link" @click="closeMobileMenu">Register</router-link>
          <router-link to="/login" class="btn btn-primary navbar-btn" @click="closeMobileMenu">Login</router-link>
        </template>

        <template v-else-if="isAdmin">
          <router-link to="/admin/dashboard" class="navbar-link" @click="closeMobileMenu">Dashboard</router-link>
          <router-link to="/admin/fd-management" class="navbar-link" @click="closeMobileMenu">FD Management</router-link>
          <router-link to="/admin/support" class="navbar-link" @click="closeMobileMenu">Support</router-link>
          <div class="navbar-user">
            <router-link to="/admin/profile" class="navbar-link navbar-username" @click="closeMobileMenu">
              {{ user?.name }}
            </router-link>
            <button @click="handleLogout" class="btn btn-outline navbar-btn">Logout</button>
          </div>
        </template>

        <template v-else>
          <router-link to="/" class="navbar-link" @click="closeMobileMenu">Home</router-link>
          <router-link to="/user/dashboard" class="navbar-link" @click="closeMobileMenu">Dashboard</router-link>
          <router-link to="/about" class="navbar-link" @click="closeMobileMenu">About Us</router-link>
          <router-link to="/contact" class="navbar-link" @click="closeMobileMenu">Contact</router-link>
          <div class="navbar-user">
            <router-link to="/user/profile" class="navbar-link navbar-username" @click="closeMobileMenu">
              {{ user?.name }}
            </router-link>
            <button @click="handleLogout" class="btn btn-outline navbar-btn">Logout</button>
          </div>
        </template>
      </div>

      <div class="navbar-actions">
        <button class="theme-toggle" @click="toggleTheme" :aria-label="theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'" :title="theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'">
          <span class="theme-icon">
            <svg v-if="theme === 'dark'" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
          </span>
        </button>

        <button class="navbar-toggle" @click="toggleMobileMenu" aria-label="Toggle menu">
          <span></span>
          <span></span>
          <span></span>
        </button>
      </div>
    </div>
  </nav>
  <div class="navbar-spacer" aria-hidden="true"></div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import { useTheme } from '@/composables/useTheme';

const store = useStore();
const router = useRouter();
const mobileMenuOpen = ref(false);
const { theme, toggleTheme } = useTheme();

const isAuthenticated = computed(() => store.getters['auth/isAuthenticated']);
const isAdmin = computed(() => store.getters['auth/isAdmin']);
const user = computed(() => store.getters['auth/user']);
const homeRoute = computed(() => '/');

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value;
};

const closeMobileMenu = () => {
  mobileMenuOpen.value = false;
};

const handleLogout = async () => {
  try {
    await store.dispatch('auth/logout');
    router.push('/');
    closeMobileMenu();
  } catch (error) {
    console.error('Logout failed:', error);
  }
};
</script>

<style scoped lang="scss">
.navbar {
  background: linear-gradient(90deg, #171a52 0%, #20276a 45%, #0e6b86 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: var(--shadow-md);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  width: 100%;
  z-index: var(--z-sticky);
  padding: var(--spacing-md) 0;
}

.navbar-spacer {
  height: 76px;

  @media (max-width: 768px) {
    height: 70px;
  }
}

.navbar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
}

.navbar-brand {
  text-decoration: none;

  .logo {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .logo-mark {
    width: 40px;
    height: 40px;
    display: block;
    filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.3));
    flex-shrink: 0;
  }

  .logo-text {
    font-size: 22px;
    font-weight: 700;
    background: linear-gradient(135deg, #ffffff 0%, #67e8f9 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    letter-spacing: 0.5px;
  }

  @media (max-width: 768px) {
    .logo-mark {
      width: 32px;
      height: 32px;
    }

    .logo-text {
      font-size: 18px;
    }
  }
}

.navbar-menu {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  flex: 1;
  justify-content: center;

  @media (max-width: 768px) {
    position: fixed;
    top: 70px;
    left: -100%;
    width: 100%;
    height: calc(100vh - 70px);
    background: var(--zeta-primary-dark);
    flex-direction: column;
    padding: var(--spacing-xl);
    transition: left var(--transition-base);
    overflow-y: auto;

    &.active {
      left: 0;
    }
  }
}

.navbar-link {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);

  &:hover {
    color: white;
    background: rgba(255, 255, 255, 0.1);
  }
}

.navbar-user {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);

  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.navbar-username {
  color: white;
  font-weight: 500;
  text-decoration: none;
}

.navbar-btn {
  @media (max-width: 768px) {
    width: 100%;
  }
}

.btn.btn-primary {
  background: var(--zeta-gradient-accent);
}

.btn.btn-outline {
  border-color: white;
  color: white;

  &:hover {
    background: white;
    color: var(--zeta-primary);
  }
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.navbar-toggle {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  width: 30px;
  height: 24px;
  position: relative;

  span {
    display: block;
    width: 100%;
    height: 3px;
    background: white;
    border-radius: 2px;
    transition: all var(--transition-base);
    position: absolute;
    left: 0;

    &:nth-child(1) {
      top: 0;
    }

    &:nth-child(2) {
      top: 50%;
      transform: translateY(-50%);
    }

    &:nth-child(3) {
      bottom: 0;
    }
  }

  @media (max-width: 768px) {
    display: block;
  }
}
</style>
