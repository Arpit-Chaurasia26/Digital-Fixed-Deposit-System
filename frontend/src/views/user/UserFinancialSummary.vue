<template>
  <div class="page">
    <Navbar />
    <div class="container page-content user-layout">
      <UserSidebar />
      <div class="user-content">
        <div class="page-header card">
          <div>
            <h1>Financial Year Summary</h1>
            <p>Review your fixed deposit performance by year.</p>
          </div>
          <span class="status-badge status-info">FY {{ selectedYear }}</span>
        </div>

        <div class="card panel">
          <div class="panel-header">
            <h3>Year</h3>
            <div class="days-select">
              <div class="days-row">
                <label>Year</label>
                <button class="btn btn-secondary" @click="loadSummary">Load</button>
              </div>
              <select v-model.number="selectedYear" class="form-control" @change="loadSummary">
                <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
              </select>
            </div>
          </div>

          <div v-if="loading" class="alert alert-info">Loading summary...</div>
          <div v-else-if="summary" class="summary-grid">
            <div class="summary-card">
              <label>Total Interest Accrued</label>
              <strong>{{ formatCurrency(summary.totalInterestAccruedTillDate) }}</strong>
            </div>
            <div class="summary-card">
              <label>Total Principal</label>
              <strong>{{ formatCurrency(summary.totalPrincipalDeposited) }}</strong>
            </div>
            <div class="summary-card">
              <label>Total FDs Created</label>
              <strong>{{ summary.totalFDsCreated }}</strong>
            </div>
          </div>
          <div v-else class="empty">No summary available for the selected year.</div>
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
import { formatCurrency } from '@/utils/helpers';

const store = useStore();
const userId = computed(() => store.getters['auth/userId']);
const summary = computed(() => store.getters['fd/financialSummary']);
const loading = computed(() => store.getters['fd/loading']);
const currentYear = new Date().getFullYear();
const selectedYear = ref(currentYear);
const yearOptions = Array.from({ length: 6 }, (_, i) => currentYear - i);

const loadSummary = async () => {
  if (!userId.value) return;
  await store.dispatch('fd/fetchFinancialSummary', { userId: userId.value, year: selectedYear.value });
};

loadSummary();
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.user-content { min-width: 0; }
.page-header {
  padding: var(--spacing-2xl);
  background: var(--zeta-gradient-hero);
  color: white;
  border-radius: var(--radius-xl);
  margin-bottom: var(--spacing-2xl);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-lg);
  box-shadow: var(--shadow-lg);
  position: relative;
  overflow: hidden;
}

.page-header::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(255, 255, 255, 0.25), transparent 60%);
  pointer-events: none;
}

.panel {
  padding: var(--spacing-xl);
  border-radius: var(--radius-xl);
  border: 1px solid var(--zeta-border);
  background: var(--zeta-surface);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
}

.panel::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.12), transparent 60%);
  pointer-events: none;
}
.panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--spacing-md); }
.days-select { display: grid; gap: var(--spacing-sm); }
.days-row { display: flex; align-items: center; justify-content: space-between; gap: var(--spacing-md); }
.summary-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: var(--spacing-md); margin-top: var(--spacing-lg); }
.summary-card {
  padding: var(--spacing-lg);
  border-radius: var(--radius-lg);
  border: 1px solid var(--zeta-border);
  background: var(--zeta-background-hover);
  box-shadow: var(--shadow-sm);
}

.summary-card label {
  display: block;
  color: var(--zeta-text-secondary);
  font-size: var(--font-size-sm);
  margin-bottom: var(--spacing-xs);
}

.summary-card strong {
  font-size: var(--font-size-xl);
  color: var(--zeta-primary);
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

.status-info {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  box-shadow: 0 0 18px rgba(59, 130, 246, 0.55);
}
.empty { color: var(--zeta-text-secondary); }
</style>
