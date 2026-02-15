<template>
  <div class="page">
    <Navbar />
    <div class="container page-content user-layout">
      <UserSidebar />
      <div class="user-content">
        <div class="page-header card">
          <div>
            <h1>Maturing Fixed Deposits</h1>
            <p>See your deposits that will mature soon.</p>
          </div>
        </div>

        <div class="card panel">
          <div class="panel-header">
            <h3>Filters</h3>
            <div class="days-select">
              <div class="days-row">
                <label>Days</label>
                <button class="btn btn-secondary" @click="loadMaturing">Load</button>
              </div>
              <input v-model.number="days" type="number" class="form-control" min="1" />
            </div>
          </div>

          <div v-if="loading" class="alert alert-info">Loading maturing FDs...</div>
          <div v-else-if="maturingFDs.length" class="fd-grid">
            <div v-for="fd in maturingFDs" :key="fd.fdId" class="fd-card card">
              <div class="fd-header">
                <h3>FD #{{ fd.fdId }}</h3>
                <span :class="['status-badge', `status-${fd.status.toLowerCase()}`]">{{ fd.status }}</span>
              </div>
              <p><strong>Amount:</strong> {{ formatCurrency(fd.amount) }}</p>
              <p><strong>Maturity:</strong> {{ formatDate(fd.maturityDate) }}</p>
              <p><strong>Days Remaining:</strong> {{ fd.daysRemaining }}</p>
              <router-link :to="`/user/fd/${fd.fdId}`" class="btn btn-outline btn-sm mt-2">View Details</router-link>
            </div>
          </div>
          <div v-else class="empty">No maturing FDs found for the selected range.</div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useStore } from 'vuex';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { formatCurrency, formatDate } from '@/utils/helpers';

const store = useStore();
const userId = computed(() => store.getters['auth/userId']);
const maturingFDs = computed(() => store.getters['fd/maturingFDs']);
const loading = computed(() => store.getters['fd/loading']);
const days = ref(30);

const loadMaturing = async () => {
  if (!userId.value) return;
  await store.dispatch('fd/fetchMaturingFDs', { userId: userId.value, days: days.value });
};

loadMaturing();
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.user-content { min-width: 0; }
.page-header { padding: var(--spacing-2xl); background: var(--zeta-gradient-hero); color: white; border-radius: var(--radius-xl); margin-bottom: var(--spacing-2xl); }
.panel { padding: var(--spacing-xl); border-radius: var(--radius-xl); box-shadow: var(--shadow-md); }
.panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--spacing-md); }
.days-select { display: grid; gap: var(--spacing-sm); }
.days-row { display: flex; align-items: center; justify-content: space-between; gap: var(--spacing-md); }
.fd-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: var(--spacing-lg); }
.fd-card { padding: var(--spacing-xl); border-radius: var(--radius-xl); position: relative; overflow: hidden; background: var(--zeta-surface); border: 1px solid rgba(6, 182, 212, 0.18); box-shadow: var(--shadow-md); }
.fd-card::after { content: ''; position: absolute; inset: 0; background: radial-gradient(circle at top right, rgba(6, 182, 212, 0.22), transparent 55%); pointer-events: none; }
.fd-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--spacing-md); position: relative; z-index: 1; }
.fd-card p { margin: var(--spacing-xs) 0; color: var(--zeta-text-secondary); position: relative; z-index: 1; }
.status-badge { display: inline-flex; align-items: center; padding: 6px 12px; border-radius: 999px; font-size: var(--font-size-xs); font-weight: 700; text-transform: uppercase; letter-spacing: 0.4px; position: relative; z-index: 1; color: white; }
.status-active { background: linear-gradient(135deg, #16a34a, #22c55e); box-shadow: 0 0 20px rgba(34, 197, 94, 0.55); }
.status-matured { background: linear-gradient(135deg, #3b82f6, #60a5fa); box-shadow: 0 0 20px rgba(59, 130, 246, 0.55); }
.status-broken, .status-closed { background: linear-gradient(135deg, #ef4444, #f87171); box-shadow: 0 0 20px rgba(239, 68, 68, 0.55); }
.empty { color: var(--zeta-text-secondary); }
</style>
