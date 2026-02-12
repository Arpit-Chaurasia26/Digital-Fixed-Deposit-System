<template>
  <div class="page">
    <Navbar />
    <div class="container page-content user-layout">
      <UserSidebar />
      <div class="user-content">
      <router-link to="/user/fd-list" class="back-link">← Back to FD List</router-link>
      
      <div v-if="loading" class="spinner"></div>
      
      <div v-else-if="fd" class="fd-detail-container">
        <div class="fd-detail-card card">
          <h1>FD Details #{{ fd.id }}</h1>
          <span :class="['status-badge', `status-${getStatusClass(fd.status)}`]">{{ fd.status }}</span>
          
          <div class="details-grid">
            <div class="detail-item">
              <label>Amount:</label>
              <strong>{{ formatCurrency(fd.amount) }}</strong>
            </div>
            <div class="detail-item">
              <label>Interest Rate:</label>
              <strong>{{ fd.interestRate }}% p.a.</strong>
            </div>
            <div class="detail-item">
              <label>Tenure:</label>
              <strong>{{ fd.tenureMonths }} months</strong>
            </div>
            <div class="detail-item">
              <label>Start Date:</label>
              <strong>{{ formatDate(fd.startDate) }}</strong>
            </div>
            <div class="detail-item">
              <label>Maturity Date:</label>
              <strong>{{ formatDate(fd.maturityDate) }}</strong>
            </div>
            <div class="detail-item">
              <label>Accrued Interest:</label>
              <strong>{{ formatCurrency(fd.accruedInterest || 0) }}</strong>
            </div>
            <div class="detail-item">
              <label>Current Value:</label>
              <strong>{{ formatCurrency((fd.amount || 0) + (fd.accruedInterest || 0)) }}</strong>
            </div>
            <div class="detail-item">
              <label>Scheme:</label>
              <strong>{{ formatSchemeName(fd.interestScheme) }}</strong>
            </div>
          </div>

          <div class="live-interest">
            <div>
              <h3>Live Interest Snapshot</h3>
              <p>Refresh to see the latest accrued interest from the server.</p>
            </div>
            <button class="btn btn-secondary" @click="refreshInterest" :disabled="loading">
              Refresh Interest
            </button>
          </div>
          <div v-if="accruedInterest" class="interest-card">
            <div>
              <strong>Status:</strong>
              <span :class="['status-badge', `status-${getStatusClass(accruedInterest.status)}`]">
                {{ accruedInterest.status }}
              </span>
            </div>
            <div><strong>Accrued:</strong> {{ formatCurrency(accruedInterest.accruedInterest) }}</div>
            <div><strong>Interest Rate:</strong> {{ accruedInterest.interestRate }}%</div>
          </div>
          <div v-if="interestError" class="alert alert-error">{{ interestError }}</div>

          <div class="actions-section">
            <router-link :to="`/user/fd/${fd.id}/interest`" class="btn btn-primary">View Interest Timeline</router-link>
            <router-link v-if="fd.status === 'ACTIVE'" :to="`/user/fd/${fd.id}/break`" class="btn btn-danger">Break FD</router-link>
          </div>
        </div>
      </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { formatCurrency, formatDate, getErrorMessage } from '@/utils/helpers';

const store = useStore();
const route = useRoute();

const userId = computed(() => store.getters['auth/userId']);
const fd = computed(() => store.getters['fd/currentFD']);
const loading = computed(() => store.getters['fd/loading']);
const accruedInterest = computed(() => store.getters['fd/accruedInterest']);
const interestError = computed(() => store.getters['fd/error']);

const getStatusClass = (status: string) => {
  const classes: { [key: string]: string } = {
    ACTIVE: 'success',
    MATURED: 'info',
    BROKEN: 'error',
  };
  return classes[status] || 'info';
};

const formatSchemeName = (name: string) => {
  return name.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
};

onMounted(async () => {
  const fdId = parseInt(route.params.id as string);
  if (userId.value && fdId) {
    await store.dispatch('fd/fetchFDById', { userId: userId.value, fdId });
    await store.dispatch('fd/fetchAccruedInterest', fdId);
  }
});

const refreshInterest = async () => {
  const fdId = parseInt(route.params.id as string);
  if (!fdId) return;
  try {
    await store.dispatch('fd/fetchAccruedInterest', fdId);
  } catch (err) {
    store.commit('fd/SET_ERROR', getErrorMessage(err, 'Unable to refresh interest'));
  }
};
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.user-content { min-width: 0; }
.back-link { display: inline-block; margin-bottom: var(--spacing-lg); color: var(--zeta-primary); }
.fd-detail-card {
  padding: var(--spacing-2xl);
  max-width: 900px;
  margin: 0 auto;
  border-radius: var(--radius-xl);
  border: 1px solid var(--zeta-border);
  background: var(--zeta-surface);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
}

.fd-detail-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.14), transparent 60%);
  pointer-events: none;
}
.details-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-lg);
  margin: var(--spacing-xl) 0;
}

.detail-item {
  padding: var(--spacing-md);
  border-radius: var(--radius-lg);
  background: var(--zeta-background-hover);
  border: 1px solid var(--zeta-border);
  box-shadow: var(--shadow-sm);
}
.detail-item { label { display: block; color: var(--zeta-text-secondary); margin-bottom: var(--spacing-xs); } }
.actions-section { display: flex; gap: var(--spacing-md); margin-top: var(--spacing-xl); }
.live-interest {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--spacing-xl);
  padding: var(--spacing-lg);
  border-radius: var(--radius-lg);
  background: var(--zeta-background-hover);
  border: 1px solid var(--zeta-border);
}

.interest-card {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-md);
  margin-top: var(--spacing-md);
  padding: var(--spacing-lg);
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, rgba(14, 116, 144, 0.08), rgba(34, 197, 94, 0.08));
  border: 1px solid var(--zeta-border);
  box-shadow: var(--shadow-md);
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border-radius: 999px;
  font-size: var(--font-size-xs);
  font-weight: 700;
  letter-spacing: 0.4px;
  text-transform: uppercase;
  color: white;
}

.status-success {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  box-shadow: 0 0 18px rgba(34, 197, 94, 0.55);
}

.status-info {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  box-shadow: 0 0 18px rgba(59, 130, 246, 0.55);
}

.status-error {
  background: linear-gradient(135deg, #ef4444, #f87171);
  box-shadow: 0 0 18px rgba(239, 68, 68, 0.55);
}
@media (max-width: 768px) { .details-grid { grid-template-columns: 1fr; } }
</style>
