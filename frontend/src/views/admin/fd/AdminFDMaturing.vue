<template>
  <div class="page">
    <Navbar />
    <div class="container page-content admin-layout">
      <AdminSidebar />
      <div class="admin-content">
      <div class="page-header card">
        <div>
          <h1>Maturing Fixed Deposits</h1>
          <p>Track deposits that will mature soon and act proactively.</p>
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
        <div v-if="error" class="alert alert-error">{{ error }}</div>
        <div v-if="loading" class="alert alert-info">Loading maturing FDs...</div>
        <div v-else-if="maturing.length" class="fd-grid">
          <div v-for="fd in maturing" :key="fd.fdId" class="fd-card card">
            <div class="fd-header">
              <h3>FD #{{ fd.fdId }}</h3>
              <span :class="['status-badge', `status-${fd.status.toLowerCase()}`]">{{ fd.status }}</span>
            </div>
            <p><strong>Amount:</strong> {{ formatCurrency(fd.amount) }}</p>
            <p><strong>Maturity:</strong> {{ formatDate(fd.maturityDate) }}</p>
            <p><strong>Days Remaining:</strong> {{ fd.daysRemaining }}</p>
          </div>
        </div>
        <p v-else class="empty">No maturing FDs found for the selected range.</p>
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
import { FDMaturityResponse } from '@/types';
import { formatCurrency, formatDate, getErrorMessage } from '@/utils/helpers';

const days = ref(7);
const maturing = ref<FDMaturityResponse[]>([]);
const loading = ref(false);
const error = ref('');

const loadMaturing = async () => {
  loading.value = true;
  error.value = '';
  try {
    maturing.value = await fdService.getAdminMaturingFDs(days.value || 7);
  } catch (err) {
    maturing.value = [];
    error.value = getErrorMessage(err, 'Unable to load maturing deposits');
  } finally {
    loading.value = false;
  }
};

loadMaturing();
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.page-header { padding: var(--spacing-2xl); background: var(--zeta-gradient-hero); color: white; border-radius: var(--radius-xl); margin-bottom: var(--spacing-2xl); }
.panel { padding: var(--spacing-xl); border-radius: var(--radius-xl); box-shadow: var(--shadow-md); }
.panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--spacing-md); }
.days-select { display: grid; gap: var(--spacing-sm); }
.days-row { display: flex; align-items: center; justify-content: space-between; gap: var(--spacing-sm); }
.fd-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: var(--spacing-lg); }
.fd-card {
  padding: var(--spacing-xl);
  border-radius: var(--radius-xl);
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.96));
  border: 1px solid rgba(6, 182, 212, 0.18);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
}
.fd-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(6, 182, 212, 0.22), transparent 55%);
  pointer-events: none;
}
.fd-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--spacing-md); position: relative; z-index: 1; }
.fd-card p { margin: var(--spacing-xs) 0; color: var(--zeta-text-secondary); position: relative; z-index: 1; }
.fd-card strong { color: var(--zeta-text-primary); }

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: var(--font-size-xs);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  position: relative;
  z-index: 1;
  color: white;
}

.status-active {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  box-shadow: 0 0 20px rgba(34, 197, 94, 0.55);
}

.status-matured {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  box-shadow: 0 0 20px rgba(59, 130, 246, 0.55);
}

.status-broken,
.status-closed {
  background: linear-gradient(135deg, #ef4444, #f87171);
  box-shadow: 0 0 20px rgba(239, 68, 68, 0.55);
}
.empty { color: var(--zeta-text-secondary); }
</style>
