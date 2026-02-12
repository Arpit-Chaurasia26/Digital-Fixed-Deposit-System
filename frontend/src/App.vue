<script setup lang="ts">
import { watch } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import { useTheme } from '@/composables/useTheme';

const store = useStore();
const route = useRoute();
const { initTheme } = useTheme();

// Auth is handled by the router guard — no duplicate call needed here.

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
