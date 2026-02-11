<script setup lang="ts">
import { onMounted, watch } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';

const store = useStore();
const route = useRoute();

onMounted(async () => {
  // Try to fetch user profile on app mount (silent authentication)
  try {
    await store.dispatch('auth/fetchProfile');
  } catch (error) {
    // User not authenticated, continue
  }
});

const syncShell = () => {
  const isAdminRoute = route.path.startsWith('/admin');
  const isUserRoute = route.path.startsWith('/user');
  document.body.classList.toggle('admin-shell', isAdminRoute);
  document.body.classList.toggle('user-shell', !isAdminRoute && isUserRoute);
};

watch(() => route.path, syncShell, { immediate: true });
</script>

<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>
