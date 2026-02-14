<template>
  <div class="page">
    <Navbar />
    <div class="container page-content admin-layout">
      <AdminSidebar />
      <div class="admin-content">
      <div class="admin-hero card">
        <div>
          <h1>Admin Dashboard</h1>
          <p>Manage fixed deposits, support tickets, and platform operations.</p>
        </div>
        <div class="hero-chip">Admin Console</div>
      </div>

      <div class="dashboard-grid">
        <router-link to="/admin/fd-management" class="card dashboard-card">
          <div class="card-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 10h18" />
              <path d="M4 10l8-5 8 5" />
              <path d="M5 10v8M9 10v8M15 10v8M19 10v8" />
              <path d="M3 18h18" />
            </svg>
          </div>
          <div>
            <h3>FD Management</h3>
            <p>View and manage all FDs</p>
          </div>
        </router-link>
        <router-link to="/admin/support" class="card dashboard-card">
          <div class="card-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M4 14a4 4 0 1 0 0-4" />
              <path d="M20 10a4 4 0 1 0 0 4" />
              <path d="M9 12h6" />
            </svg>
          </div>
          <div>
            <h3>Support Tickets</h3>
            <p>Respond and resolve user tickets</p>
          </div>
        </router-link>
        <a href="#financial-summary" class="card dashboard-card">
          <div class="card-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M4 19h16" />
              <path d="M6 16V8" />
              <path d="M12 16V5" />
              <path d="M18 16v-6" />
            </svg>
          </div>
          <div>
            <h3>Financial Summary</h3>
            <p>View current year analytics</p>
          </div>
        </a>
      </div>

      <div id="financial-summary" class="financial-summary card">
        <div class="section-header">
          <h2>Financial Summary</h2>
          <div class="year-select">
            <label>Year</label>
            <select v-model.number="selectedYear" class="form-control" @change="loadSummary">
              <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
            </select>
          </div>
        </div>

        <div v-if="loadingSummary" class="empty-summary">Loading summary...</div>
        <div v-else-if="summary" class="summary-grid">
          <div>
            <strong>Total FDs Created:</strong>
            {{ summary.totalFDsCreated }}
          </div>
          <div>
            <strong>Total Principal Deposited:</strong>
            {{ formatCurrency(summary.totalPrincipalDeposited) }}
          </div>
          <div>
            <strong>Total Interest Accrued:</strong>
            {{ formatCurrency(summary.totalInterestAccruedTillDate) }}
          </div>
        </div>
        <div v-else-if="summaryError" class="empty-summary">{{ summaryError }}</div>
        <div v-else class="empty-summary">No summary available yet.</div>
      </div>

      <div class="quick-actions">
        <router-link to="/admin/fd-management" class="btn btn-primary">Go to FD Management</router-link>
        <router-link to="/admin/support" class="btn btn-secondary">Go to Support Tickets</router-link>
      </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import AdminSidebar from '@/components/admin/AdminSidebar.vue';
import { fdService } from '@/services/fdService';
import { AdminFinancialYearSummary } from '@/types';
import { formatCurrency } from '@/utils/helpers';

const summary = ref<AdminFinancialYearSummary | null>(null);
const summaryError = ref('');
const loadingSummary = ref(false);
const currentYear = new Date().getFullYear();
const selectedYear = ref(currentYear);
const yearOptions = Array.from({ length: 6 }, (_, i) => currentYear - i);

const loadSummary = async () => {
  loadingSummary.value = true;
  summaryError.value = '';
  try {
    summary.value = await fdService.getAdminFinancialYearSummary(selectedYear.value);
  } catch (error: any) {
    const status = error?.response?.status;
    const message = error?.response?.data?.message || error?.message;
    summaryError.value = `Failed to load summary${status ? ` (HTTP ${status})` : ''}${message ? `: ${message}` : ''}`;
    summary.value = null;
  } finally {
    loadingSummary.value = false;
  }
};

onMounted(loadSummary);
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.admin-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-xl);
  padding: var(--spacing-2xl);
  background: var(--zeta-gradient-hero);
  color: white;
  border-radius: var(--radius-xl);
  margin-bottom: var(--spacing-2xl);
  box-shadow: var(--shadow-md);
}

.admin-hero h1 { margin: 0 0 var(--spacing-sm) 0; font-size: var(--font-size-3xl); }
.admin-hero p { margin: 0; color: rgba(255, 255, 255, 0.9); }
.hero-chip { background: rgba(255, 255, 255, 0.15); border: 1px solid rgba(255, 255, 255, 0.3); padding: var(--spacing-sm) var(--spacing-lg); border-radius: 999px; font-weight: 600; }

.dashboard-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: var(--spacing-xl); margin-bottom: var(--spacing-xl); }
.dashboard-card {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
  padding: var(--spacing-xl);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  text-decoration: none;
  color: inherit;
  transition: transform var(--transition-base), box-shadow var(--transition-base);
}
.dashboard-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-lg); }
.dashboard-card .card-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: rgba(99, 102, 241, 0.12);
  color: var(--zeta-primary);
}
.dashboard-card .card-icon svg { width: 24px; height: 24px; }
.financial-summary { padding: var(--spacing-xl); border-radius: var(--radius-xl); box-shadow: var(--shadow-md); }
.summary-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: var(--spacing-md); margin-top: var(--spacing-lg); }
.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: var(--spacing-md); }
.section-subtitle { color: var(--zeta-text-secondary); }
.empty-summary { color: var(--zeta-text-secondary); padding: var(--spacing-md); }

.year-select { display: flex; align-items: center; gap: var(--spacing-sm); }
.year-select label { color: var(--zeta-text-secondary); font-size: var(--font-size-sm); }
.year-select .form-control { min-width: 120px; }

.quick-actions { display: flex; gap: var(--spacing-md); flex-wrap: wrap; margin-top: var(--spacing-lg); }
</style>
