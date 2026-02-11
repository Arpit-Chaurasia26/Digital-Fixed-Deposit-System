<template>
  <div class="page">
    <Navbar />
    <div class="container page-content admin-layout">
      <AdminSidebar />
      <div class="admin-content">
      <div class="page-header card">
        <div>
          <h1>User Portfolio Lookup</h1>
          <p>Quickly review a user's FD portfolio summary.</p>
        </div>
      </div>

      <div class="card panel">
        <div class="panel-header">
          <h3>Search</h3>
          <div class="days-select">
            <div class="days-row">
              <label>User ID</label>
              <button class="btn btn-secondary" @click="loadPortfolio" :disabled="loading">Load</button>
            </div>
            <input v-model.number="userId" type="number" class="form-control" min="1" />
          </div>
        </div>
        <div v-if="error" class="alert alert-error">{{ error }}</div>
        <div v-if="loading" class="alert alert-info">Loading portfolio...</div>
        <div v-else-if="portfolio" class="portfolio-grid">
          <div class="stat-card">
            <h4>Total FDs</h4>
            <p>{{ portfolio.totalFDs }}</p>
          </div>
          <div class="stat-card">
            <h4>Active FDs</h4>
            <p>{{ portfolio.activeFDs }}</p>
          </div>
          <div class="stat-card">
            <h4>Matured FDs</h4>
            <p>{{ portfolio.maturedFDs }}</p>
          </div>
          <div class="stat-card">
            <h4>Total Principal</h4>
            <p>{{ formatCurrency(portfolio.totalPrincipal) }}</p>
          </div>
          <div class="stat-card">
            <h4>Interest Accrued</h4>
            <p>{{ formatCurrency(portfolio.totalInterestAccrued) }}</p>
          </div>
          <div class="stat-card">
            <h4>Next Maturity</h4>
            <p>{{ portfolio.nextMaturityDate ? formatDate(portfolio.nextMaturityDate) : '—' }}</p>
          </div>
        </div>
        <p v-else class="empty">Enter a valid user ID to view portfolio.</p>
      </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import AdminSidebar from '@/components/admin/AdminSidebar.vue';
import { fdService } from '@/services/fdService';
import { FDPortfolio } from '@/types';
import { formatCurrency, formatDate, getErrorMessage } from '@/utils/helpers';

const userId = ref<number | null>(null);
const portfolio = ref<FDPortfolio | null>(null);
const loading = ref(false);
const error = ref('');

const loadPortfolio = async () => {
  if (!userId.value) {
    error.value = 'Please enter a valid user ID.';
    return;
  }
  loading.value = true;
  error.value = '';
  try {
    portfolio.value = await fdService.getAdminUserPortfolio(userId.value);
  } catch (err) {
    portfolio.value = null;
    error.value = getErrorMessage(err, 'Unable to load user portfolio');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.page-header { padding: var(--spacing-2xl); background: var(--zeta-gradient-hero); color: white; border-radius: var(--radius-xl); margin-bottom: var(--spacing-2xl); }
.panel { padding: var(--spacing-xl); border-radius: var(--radius-xl); box-shadow: var(--shadow-md); }
.panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--spacing-md); }
.days-select { display: grid; gap: var(--spacing-sm); }
.days-row { display: flex; align-items: center; justify-content: space-between; gap: var(--spacing-md); }
.portfolio-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: var(--spacing-lg); }
.stat-card { background: var(--zeta-background); padding: var(--spacing-md); border-radius: var(--radius-md); border: 1px solid var(--zeta-divider); }
.stat-card h4 { margin-bottom: var(--spacing-xs); color: var(--zeta-text-secondary); font-size: var(--font-size-sm); }
.stat-card p { margin: 0; font-weight: 700; color: var(--zeta-primary); }
.empty { color: var(--zeta-text-secondary); }
</style>
