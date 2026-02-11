<template>
  <div>
    <Navbar />
    <div class="container" style="padding: var(--spacing-3xl) 0;">
      <h1>404 - Page Not Found</h1>
      <p>The page you're looking for doesn't exist.</p>
      <router-link to="/" class="btn btn-primary">Go Home</router-link>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
</script>
