<script setup lang="ts">
import { watch } from 'vue';
import { useRoute } from 'vue-router';
import ConfirmNotificationBox from '@/components/common/ConfirmNotificationBox.vue';

const route = useRoute();

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
    <ConfirmNotificationBox />
  </div>
</template>

<style>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>
