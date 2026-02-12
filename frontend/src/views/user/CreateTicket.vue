<template>
  <div class="page">
    <Navbar />
    <div class="container page-content user-layout">
      <UserSidebar />
      <div class="user-content">
      <div class="page-header card">
        <div>
          <h1>Create Support Ticket</h1>
          <p>Let us know how we can help</p>
        </div>
      </div>
      <form @submit.prevent="handleSubmit" class="card form-card">
        <div class="form-group">
          <label class="form-label">Subject</label>
          <input v-model="form.subject" type="text" class="form-control" required />
        </div>
        <div class="form-group">
          <label class="form-label">Description</label>
          <textarea v-model="form.description" class="form-control" rows="5" required></textarea>
        </div>
        <div class="form-group">
          <label class="form-label">Related FD (optional)</label>
          <select v-model.number="form.fdId" class="form-control">
            <option :value="undefined">Select FD</option>
            <option v-for="fd in fds" :key="fd.id" :value="fd.id">
              FD #{{ fd.id }} • {{ formatCurrency(fd.amount) }} • {{ fd.status }}
            </option>
          </select>
          <small class="form-help" v-if="!fds.length">No FDs found for your account.</small>
        </div>
        <div v-if="errorMessage" class="alert alert-error">
          {{ errorMessage }}
        </div>
        <button type="submit" class="btn btn-primary" :disabled="loading">Create Ticket</button>
      </form>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { formatCurrency, getErrorMessage } from '@/utils/helpers';

const store = useStore();
const router = useRouter();
const loading = computed(() => store.getters['support/loading']);
const errorMessage = ref('');
const userId = computed(() => store.getters['auth/userId']);
const fds = computed(() => store.getters['fd/allFDs']);

const form = ref({ subject: '', description: '', fdId: undefined });

onMounted(async () => {
  if (userId.value) {
    await store.dispatch('fd/fetchUserFDs', { userId: userId.value });
  }
});

const handleSubmit = async () => {
  errorMessage.value = '';
  try {
    await store.dispatch('support/createTicket', form.value);
    router.push({ path: '/user/support', query: { created: '1' } });
  } catch (error) {
    errorMessage.value = getErrorMessage(error, 'Unable to create your ticket');
  }
};
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.user-content { min-width: 0; }
.page-header {
  margin-bottom: var(--spacing-2xl);
  padding: var(--spacing-2xl);
  background: var(--zeta-gradient-hero);
  color: white;
  border-radius: var(--radius-xl);
}
.form-card {
  padding: var(--spacing-2xl);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
}
</style>
