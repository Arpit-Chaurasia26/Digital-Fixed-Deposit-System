<template>
  <div class="page">
    <Navbar />
    <div class="container page-content admin-layout">
      <AdminSidebar />
      <div class="admin-content">
        <router-link to="/admin/fd-management/all" class="back-link">← Back to All FDs</router-link>

        <div v-if="loading" class="alert alert-info">Loading fixed deposit details...</div>
        <div v-else-if="error" class="alert alert-error">{{ error }}</div>

        <div v-else-if="fd" class="card fd-detail-card">
          <div class="fd-header">
            <h1>FD Details #{{ fd.id }}</h1>
            <span :class="['status-badge', `status-${fd.status.toLowerCase()}`]">{{ fd.status }}</span>
          </div>

          <div class="details-grid">
            <div class="detail-item">
              <label>User ID</label>
              <strong>{{ fd.userId }}</strong>
            </div>
            <div class="detail-item">
              <label>Amount</label>
              <strong>{{ formatCurrency(fd.amount) }}</strong>
            </div>
            <div class="detail-item">
              <label>Interest Rate</label>
              <strong>{{ fd.interestRate }}% p.a.</strong>
            </div>
            <div class="detail-item">
              <label>Tenure</label>
              <strong>{{ fd.tenureMonths }} months</strong>
            </div>
            <div class="detail-item">
              <label>Start Date</label>
              <strong>{{ formatDate(fd.startDate) }}</strong>
            </div>
            <div class="detail-item">
              <label>Maturity Date</label>
              <strong>{{ formatDate(fd.maturityDate) }}</strong>
            </div>
            <div class="detail-item">
              <label>Accrued Interest</label>
              <strong>{{ formatCurrency(fd.accruedInterest || 0) }}</strong>
            </div>
            <div class="detail-item">
              <label>Current Value</label>
              <strong>{{ formatCurrency((fd.amount || 0) + (fd.accruedInterest || 0)) }}</strong>
            </div>
            <div class="detail-item">
              <label>Scheme</label>
              <div class="scheme-row">
                <strong>{{ formatSchemeName(fd.interestScheme) }}</strong>
                <span :class="['frequency-chip', `frequency-${getFrequencyFromScheme(fd.interestScheme).toLowerCase()}`]">
                  {{ formatFrequencyLabel(getFrequencyFromScheme(fd.interestScheme)) }}
                </span>
              </div>
            </div>
            <div class="detail-item">
              <label>Created At</label>
              <strong>{{ formatDate(fd.createdAt) }}</strong>
            </div>
          </div>

          <div class="actions-row">
            <router-link :to="`/admin/fd-management/interest-timeline?fdId=${fd.id}`" class="btn btn-primary">
              View Interest Timeline
            </router-link>
            <router-link :to="`/admin/fd-management/update-status`" class="btn btn-outline">
              Update Status
            </router-link>
          </div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import AdminSidebar from '@/components/admin/AdminSidebar.vue';
import { fdService } from '@/services/fdService';
import { FixedDeposit } from '@/types';
import { formatCurrency, formatDate, getErrorMessage } from '@/utils/helpers';

const route = useRoute();
const fd = ref<FixedDeposit | null>(null);
const loading = ref(false);
const error = ref('');

const formatSchemeName = (name: string) => name.replace(/_/g, ' ').replace(/\b\w/g, (l) => l.toUpperCase());
const getFrequencyFromScheme = (scheme: string) => {
  const frequencyByScheme: Record<string, 'MONTHLY' | 'QUARTERLY' | 'YEARLY'> = {
    STANDARD_6_MONTHS: 'MONTHLY',
    STANDARD_12_MONTHS: 'MONTHLY',
    SENIOR_CITIZEN_12_MONTHS: 'MONTHLY',
    TAX_SAVER_5_YEARS: 'YEARLY',
  };

  return frequencyByScheme[scheme] ?? 'MONTHLY';
};

const formatFrequencyLabel = (frequency: 'MONTHLY' | 'QUARTERLY' | 'YEARLY') => {
  if (frequency === 'YEARLY') return 'Yearly';
  if (frequency === 'QUARTERLY') return 'Quarterly';
  return 'Monthly';
};

const loadFD = async () => {
  const fdId = Number(route.params.id);
  if (!Number.isFinite(fdId) || fdId <= 0) {
    error.value = 'Invalid FD ID.';
    return;
  }

  loading.value = true;
  error.value = '';
  try {
    fd.value = await fdService.getAdminFDById(fdId);
  } catch (err) {
    fd.value = null;
    error.value = getErrorMessage(err, 'Unable to load fixed deposit details');
  } finally {
    loading.value = false;
  }
};

onMounted(loadFD);
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.back-link { display: inline-block; margin-bottom: var(--spacing-lg); color: var(--zeta-primary); }
.fd-detail-card {
  padding: var(--spacing-2xl);
  border-radius: var(--radius-xl);
  border: 1px solid var(--zeta-border);
  background: var(--zeta-surface);
  box-shadow: var(--shadow-md);
}
.fd-header { display: flex; justify-content: space-between; align-items: center; gap: var(--spacing-md); }
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
}
.detail-item label {
  display: block;
  color: var(--zeta-text-secondary);
  margin-bottom: var(--spacing-xs);
}
.scheme-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-sm);
}
.actions-row { display: flex; gap: var(--spacing-sm); flex-wrap: wrap; }

.frequency-chip {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: var(--font-size-xs);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.3px;
  color: #fff;
}

.frequency-monthly {
  background: linear-gradient(135deg, #06b6d4, #22d3ee);
}

.frequency-quarterly {
  background: linear-gradient(135deg, #8b5cf6, #a78bfa);
}

.frequency-yearly {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: var(--font-size-xs);
  font-weight: 700;
  text-transform: uppercase;
  color: white;
}
.status-active { background: linear-gradient(135deg, #16a34a, #22c55e); }
.status-matured { background: linear-gradient(135deg, #3b82f6, #60a5fa); }
.status-broken,
.status-closed { background: linear-gradient(135deg, #ef4444, #f87171); }
.status-pending { background: linear-gradient(135deg, #f59e0b, #fbbf24); }

@media (max-width: 768px) {
  .details-grid { grid-template-columns: 1fr; }
}
</style>
