<template>
  <div class="page">
    <Navbar />
    <div class="container page-content user-layout">
      <UserSidebar />
      <div class="user-content">
      <div class="page-header card">
        <div>
          <h1>My Portfolio</h1>
          <p>See the performance of all your investments</p>
        </div>
      </div>
      <div class="portfolio-layout">
        <div v-if="portfolio" class="portfolio-grid">
          <div class="card stat-card"><h3>Total Invested</h3><p>{{ formatCurrency(portfolio.totalPrincipal) }}</p><span>Principal</span></div>
          <div class="card stat-card"><h3>Current Value</h3><p>{{ formatCurrency((portfolio.totalPrincipal || 0) + (portfolio.totalInterestAccrued || 0)) }}</p><span>With interest</span></div>
          <div class="card stat-card"><h3>Total FDs</h3><p>{{ portfolio.totalFDs }}</p><span>Active & closed</span></div>
          <div class="card stat-card"><h3>Accrued Interest</h3><p>{{ formatCurrency(portfolio.totalInterestAccrued) }}</p><span>Till date</span></div>
        </div>

        <div class="portfolio-right card">
          <div class="section-header">
            <h2>All Fixed Deposits</h2>
            <router-link to="/user/book-fd" class="btn btn-primary">Book New FD</router-link>
          </div>
          <div v-if="fds && fds.length" class="fd-grid">
            <div v-for="fd in fds" :key="fd.id" class="fd-card card">
              <div class="fd-header">
                <h3>FD #{{ fd.id }}</h3>
                <span :class="['status-badge', `status-${getStatusClass(fd.status)}`]">{{ fd.status }}</span>
              </div>
              <p><strong>Amount:</strong> {{ formatCurrency(fd.amount) }}</p>
              <p><strong>Interest Rate:</strong> {{ fd.interestRate }}%</p>
              <p><strong>Tenure:</strong> {{ fd.tenureMonths }} months</p>
              <p><strong>Maturity Date:</strong> {{ formatDate(fd.maturityDate) }}</p>
              <p><strong>Accrued Interest:</strong> {{ formatCurrency(fd.accruedInterest || 0) }}</p>
              <router-link :to="`/user/fd/${fd.id}`" class="btn btn-outline btn-sm mt-2">View Details</router-link>
            </div>
          </div>
          <div v-else class="empty-state">
            <p>No fixed deposits found yet.</p>
            <router-link to="/user/book-fd" class="btn btn-primary">Book Your First FD</router-link>
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
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { formatCurrency, formatDate } from '@/utils/helpers';

const store = useStore();
const userId = computed(() => store.getters['auth/userId']);
const portfolio = computed(() => store.getters['fd/portfolio']);
const fds = computed(() => store.getters['fd/allFDs']);

const getStatusClass = (status: string) => {
  const classes: { [key: string]: string } = {
    ACTIVE: 'success',
    MATURED: 'info',
    BROKEN: 'error',
  };
  return classes[status] || 'info';
};

onMounted(async () => {
  if (userId.value) {
    await Promise.all([
      store.dispatch('fd/fetchPortfolio', userId.value),
      store.dispatch('fd/fetchUserFDs', { userId: userId.value }),
    ]);
  }
});
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
.page-header p { margin: 0; color: rgba(255, 255, 255, 0.9); }
.portfolio-layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--spacing-2xl);
}
.portfolio-right {
  padding: var(--spacing-xl);
  border-radius: var(--radius-xl);
  border: 1px solid var(--zeta-border);
  background: var(--zeta-surface);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
}

.portfolio-right::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.14), transparent 60%);
  pointer-events: none;
}
.portfolio-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: var(--spacing-lg);
}
.stat-card {
  padding: var(--spacing-xl);
  border-radius: var(--radius-xl);
  border: 1px solid var(--zeta-border);
  background: var(--zeta-surface);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
}

.stat-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(14, 116, 144, 0.12), transparent 60%);
  pointer-events: none;
}
.stat-card p { font-size: var(--font-size-2xl); font-weight: 700; color: var(--zeta-primary); margin: var(--spacing-sm) 0; }
.stat-card span { color: var(--zeta-text-secondary); font-size: var(--font-size-sm); }
.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: var(--spacing-lg); }
.fd-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--spacing-lg);
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
  background: radial-gradient(circle at top right, rgba(99, 102, 241, 0.14), transparent 60%);
  pointer-events: none;
}
.fd-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: var(--spacing-sm); }
.empty-state { text-align: center; padding: var(--spacing-3xl); }

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

@media (max-width: 1024px) {
  .portfolio-layout { grid-template-columns: 1fr; }
  .fd-grid { grid-template-columns: 1fr; }
}
</style>
