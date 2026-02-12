<template>
  <div class="page">
    <Navbar />
    <div class="container page-content admin-layout">
      <AdminSidebar />
      <div class="admin-content">
        <div class="page-header card">
          <div>
            <h1>All Fixed Deposits</h1>
            <p>Chronological view of all deposits across the platform.</p>
          </div>
        </div>

        <div class="card panel">
          <div class="panel-header">
            <h3>Sort Order</h3>
            <div class="order-actions">
              <div class="order-select">
                <select v-model="order" class="form-control" @change="loadAllFDs">
                  <option value="desc">Newest first</option>
                  <option value="asc">Oldest first</option>
                </select>
              </div>
              <button class="btn btn-secondary" @click="loadAllFDs">Refresh</button>
            </div>
          </div>

          <div v-if="error" class="alert alert-error">{{ error }}</div>
          <div v-if="loading" class="alert alert-info">Loading fixed deposits...</div>

          <div v-else-if="fds.length" class="fd-grid">
            <div v-for="fd in fds" :key="fd.id" class="fd-card card">
              <div class="fd-header">
                <h3>FD #{{ fd.id }}</h3>
                <span :class="['status-badge', `status-${fd.status.toLowerCase()}`]">{{ fd.status }}</span>
              </div>
              <p><strong>User:</strong> {{ fd.userId }}</p>
              <p><strong>Amount:</strong> {{ formatCurrency(fd.amount) }}</p>
              <p><strong>Created:</strong> {{ formatDate(fd.createdAt) }}</p>
              <p><strong>Maturity:</strong> {{ formatDate(fd.maturityDate) }}</p>
              <p><strong>Interest Rate:</strong> {{ fd.interestRate }}%</p>
            </div>
          </div>
          <p v-else class="empty">No fixed deposits found.</p>
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
import { FixedDeposit } from '@/types';
import { formatCurrency, formatDate, getErrorMessage } from '@/utils/helpers';

const order = ref<'asc' | 'desc'>('desc');
const fds = ref<FixedDeposit[]>([]);
const loading = ref(false);
const error = ref('');

const loadAllFDs = async () => {
  loading.value = true;
  error.value = '';
  try {
    fds.value = await fdService.getAdminAllFDs(order.value);
  } catch (err) {
    fds.value = [];
    error.value = getErrorMessage(err, 'Unable to load fixed deposits');
  } finally {
    loading.value = false;
  }
};

loadAllFDs();
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.page-header { padding: var(--spacing-2xl); background: var(--zeta-gradient-hero); color: white; border-radius: var(--radius-xl); margin-bottom: var(--spacing-2xl); }
.panel { padding: var(--spacing-xl); border-radius: var(--radius-xl); box-shadow: var(--shadow-md); }
.panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--spacing-md); }
.order-actions { display: flex; align-items: flex-end; gap: var(--spacing-sm); }
.order-select { display: grid; gap: 4px; }
.fd-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: var(--spacing-lg); }
.fd-card {
  padding: var(--spacing-xl);
  border-radius: var(--radius-xl);
  position: relative;
  overflow: hidden;
  background: var(--zeta-surface);
  border: 1px solid rgba(79, 70, 229, 0.15);
  box-shadow: var(--shadow-md);
}
.fd-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(99, 102, 241, 0.18), transparent 55%);
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
  box-shadow: 0 0 20px rgba(79, 70, 229, 0.45);
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
