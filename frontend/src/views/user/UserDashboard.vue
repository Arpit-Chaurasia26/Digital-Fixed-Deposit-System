<template>
  <div class="page">
    <Navbar />
    <div class="container page-content user-layout">
      <UserSidebar />
      <div class="user-content">
      <div class="dashboard-hero card">
        <div>
          <h1>Welcome Back</h1>
          <p>Track your investments and manage all your fixed deposits in one place.</p>
        </div>
        <div class="hero-chip">Secure • Fast • Trusted</div>
      </div>
      
      <!-- Portfolio Summary -->
      <div v-if="portfolio" class="portfolio-summary">
        <div class="section-header">
          <h2>Portfolio Overview</h2>
          <span class="section-badge">Live</span>
        </div>
        <div class="stats-grid">
          <div class="stat-card card">
            <h3>Total Invested</h3>
            <p class="stat-value">{{ formatCurrency(portfolio.totalPrincipal) }}</p>
            <span class="stat-caption">Principal invested</span>
          </div>
          <div class="stat-card card">
            <h3>Current Value</h3>
            <p class="stat-value">{{ formatCurrency((portfolio.totalPrincipal || 0) + (portfolio.totalInterestAccrued || 0)) }}</p>
            <span class="stat-caption">Including interest</span>
          </div>
          <div class="stat-card card">
            <h3>Total FDs</h3>
            <p class="stat-value">{{ portfolio.totalFDs }}</p>
            <span class="stat-caption">Active investments</span>
          </div>
          <div class="stat-card card">
            <h3>Average Return</h3>
            <p class="stat-value">{{ formatCurrency(portfolio.totalInterestAccrued) }}</p>
            <span class="stat-caption">Accrued interest</span>
          </div>
        </div>
      </div>

      <!-- Financial Year Summary -->
      <div v-if="financialYearSummary" class="financial-summary card">
        <div class="section-header">
          <h2>Financial Year Summary</h2>
          <div class="year-select">
            <label>Year</label>
            <select v-model.number="selectedYear" class="form-control" @change="loadFinancialSummary">
              <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
            </select>
          </div>
        </div>
        <div class="summary-grid">
          <div class="summary-card">
            <label>Total Interest Accrued</label>
            <strong>{{ formatCurrency(financialYearSummary.totalInterestAccruedTillDate) }}</strong>
          </div>
          <div class="summary-card">
            <label>Total Principal</label>
            <strong>{{ formatCurrency(financialYearSummary.totalPrincipalDeposited) }}</strong>
          </div>
          <div class="summary-card">
            <label>Total FDs Created</label>
            <strong>{{ financialYearSummary.totalFDsCreated }}</strong>
          </div>
        </div>
      </div>

      <!-- Maturing FDs -->
      <div v-if="maturingFDs && maturingFDs.length > 0" class="maturing-fds">
        <div class="section-header">
          <h2>FDs Maturing Soon</h2>
          <span class="section-subtitle">Next {{ daysFilter }} days</span>
        </div>
        <div class="fds-grid">
          <div v-for="fd in maturingFDs" :key="fd.fdId" class="fd-card card">
            <h3>FD #{{ fd.fdId }}</h3>
            <p><strong>Amount:</strong> {{ formatCurrency(fd.amount) }}</p>
            <p><strong>Maturity Date:</strong> {{ formatDate(fd.maturityDate) }}</p>
            <span class="status-badge status-warning">Maturing in {{ fd.daysRemaining }} days</span>
            <router-link :to="`/user/fd/${fd.fdId}`" class="btn btn-primary btn-sm mt-2">View Details</router-link>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="quick-actions card">
        <div class="section-header">
          <h2>Quick Actions</h2>
          <span class="section-subtitle">Go to frequently used pages</span>
        </div>
        <div class="actions-grid">
          <router-link to="/user/book-fd" class="action-card">
            <span class="icon">📊</span>
            <div>
              <h3>Book New FD</h3>
              <p>Start a new investment</p>
            </div>
          </router-link>
          <router-link to="/user/fd-list" class="action-card">
            <span class="icon">📋</span>
            <div>
              <h3>View All FDs</h3>
              <p>Track all deposits</p>
            </div>
          </router-link>
          <router-link to="/user/portfolio" class="action-card">
            <span class="icon">💼</span>
            <div>
              <h3>My Portfolio</h3>
              <p>Performance overview</p>
            </div>
          </router-link>
          <router-link to="/user/support" class="action-card">
            <span class="icon">🎫</span>
            <div>
              <h3>Support Tickets</h3>
              <p>Get help and updates</p>
            </div>
          </router-link>
        </div>
      </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue';
import { useStore } from 'vuex';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { formatCurrency, formatDate } from '@/utils/helpers';

const store = useStore();
const userId = computed(() => store.getters['auth/userId']);
const portfolio = computed(() => store.getters['fd/portfolio']);
const financialYearSummary = computed(() => store.getters['fd/financialSummary']);
const maturingFDs = computed(() => store.getters['fd/maturingFDs']);
const daysFilter = ref(30);
const currentYear = new Date().getFullYear();
const selectedYear = ref(currentYear);
const yearOptions = Array.from({ length: 6 }, (_, i) => currentYear - i);

const loadFinancialSummary = async () => {
  if (!userId.value) return;
  await store.dispatch('fd/fetchFinancialSummary', { userId: userId.value, year: selectedYear.value });
};

onMounted(async () => {
  if (userId.value) {
    await Promise.all([
      store.dispatch('fd/fetchPortfolio', userId.value),
      loadFinancialSummary(),
      store.dispatch('fd/fetchMaturingFDs', { userId: userId.value, days: daysFilter.value })
    ]);
  }
});
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.user-content { min-width: 0; }

.dashboard-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-xl);
  padding: var(--spacing-2xl);
  border-radius: var(--radius-xl);
  background: var(--zeta-gradient-hero);
  color: white;
  margin-bottom: var(--spacing-2xl);
  box-shadow: 0 24px 55px rgba(15, 23, 42, 0.18);
  position: relative;
  overflow: hidden;
}

.dashboard-hero::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(255, 255, 255, 0.25), transparent 60%);
  pointer-events: none;
}

.dashboard-hero h1 {
  margin: 0 0 var(--spacing-sm) 0;
  font-size: var(--font-size-3xl);
}

.dashboard-hero p {
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
}

.hero-chip {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.4);
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: 999px;
  font-weight: 700;
  letter-spacing: 0.3px;
  box-shadow: 0 14px 30px rgba(15, 23, 42, 0.15);
  position: relative;
  z-index: 1;
}

.portfolio-summary, .financial-summary, .maturing-fds, .quick-actions {
  margin-bottom: var(--spacing-2xl);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-md);
}

.section-badge {
  background: linear-gradient(135deg, rgba(6, 182, 212, 0.2), rgba(14, 116, 144, 0.2));
  color: var(--zeta-accent);
  padding: 6px 14px;
  border-radius: 999px;
  font-size: var(--font-size-xs);
  font-weight: 700;
  letter-spacing: 0.4px;
  border: 1px solid rgba(6, 182, 212, 0.35);
  box-shadow: 0 0 18px rgba(6, 182, 212, 0.35);
  text-transform: uppercase;
}

.section-subtitle {
  color: var(--zeta-text-secondary);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: var(--spacing-lg);
  margin-top: var(--spacing-lg);
}

.stat-card {
  padding: var(--spacing-xl);
  text-align: center;
  border-radius: var(--radius-xl);
  border: 1px solid var(--zeta-border);
  background: var(--zeta-surface);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
  h3 { font-size: 0.875rem; color: var(--zeta-text-secondary); margin-bottom: var(--spacing-sm); }
  .stat-value {
    font-size: clamp(1.25rem, 2.4vw, 1.8rem);
    font-weight: 700;
    color: var(--zeta-primary);
    line-height: 1.2;
    word-break: break-word;
  }
  .stat-caption { color: var(--zeta-text-secondary); font-size: var(--font-size-sm); }
}

.stat-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(14, 116, 144, 0.12), transparent 60%);
  pointer-events: none;
}

.financial-summary {
  padding: var(--spacing-xl);
  border-radius: var(--radius-xl);
  border: 1px solid var(--zeta-border);
  background: var(--zeta-surface);
  box-shadow: var(--shadow-md);
  .summary-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: var(--spacing-md);
    margin-top: var(--spacing-lg);
  }
  .year-select { display: flex; align-items: center; gap: var(--spacing-sm); }
  .year-select label { color: var(--zeta-text-secondary); font-size: var(--font-size-sm); }
  .year-select .form-control { min-width: 120px; }
}

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

.fds-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--spacing-lg);
  margin-top: var(--spacing-lg);
}

.fd-card {
  padding: var(--spacing-xl);
  border-radius: var(--radius-xl);
  border: 1px solid var(--zeta-border);
  background: var(--zeta-surface);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
}

.fd-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(245, 158, 11, 0.18), transparent 60%);
  pointer-events: none;
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
  box-shadow: 0 0 18px rgba(245, 158, 11, 0.55);
}

.status-warning {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
}

.quick-actions {
  padding: var(--spacing-xl);
  .actions-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: var(--spacing-lg);
    margin-top: var(--spacing-lg);
  }
  .action-card {
    display: flex;
    gap: var(--spacing-md);
    align-items: center;
    padding: var(--spacing-lg);
    border-radius: var(--radius-lg);
    background: var(--zeta-surface);
    box-shadow: var(--shadow-sm);
    text-decoration: none;
    color: inherit;
    transition: transform var(--transition-base), box-shadow var(--transition-base);
    .icon { font-size: 2rem; }
    h3 { margin: 0; font-size: var(--font-size-lg); }
    p { margin: 0; color: var(--zeta-text-secondary); }
    &:hover { transform: translateY(-4px); box-shadow: var(--shadow-md); }
  }
}
</style>
