<template>
  <div>
    <!-- <Navbar /> -->
     <div class="logo">
      <img src="/fd-logo.svg" alt="Zeta FD logo" class="logo-mark" />
      <span class="logo-classic" aria-hidden="true">Ζ</span>
          <span class="logo-text">Zeta FD</span>
        </div>
    <div class="container" style="padding: var(--spacing-3xl) 0;">
      <h1>404 - Page Not Found</h1>
      <p>The page you're looking for doesn't exist.</p>
      <router-link to="/" class="btn btn-primary">Go Home</router-link>
    </div>
    <!-- <Footer /> -->
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
  .logo {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    background: var(--zeta-gradient-primary);
  }

  .logo-mark {
    width: 34px;
    height: 34px;
    display: block;
  }

  .logo-classic {
    font-size: 24px;
    font-weight: 800;
    color: #67e8f9;
    line-height: 1;
  }

  .logo-text {
    font-size: 20px;
    font-weight: 600;
    color: white;
  }
</style>
