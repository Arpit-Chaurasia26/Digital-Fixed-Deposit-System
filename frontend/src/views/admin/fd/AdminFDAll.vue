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

          <div class="search-row">
            <input
              v-model.trim="searchFdId"
              type="text"
              class="form-control"
              inputmode="numeric"
              placeholder="Search by FD ID"
            />
            <input
              v-model.trim="searchUserId"
              type="text"
              class="form-control"
              inputmode="numeric"
              placeholder="Search by User ID"
            />
            <button class="btn btn-outline" @click="clearSearch">Clear Search</button>
          </div>

          <div v-if="error" class="alert alert-error">{{ error }}</div>
          <div v-if="loading" class="alert alert-info">Loading fixed deposits...</div>

          <div v-else-if="filteredFDs.length" class="fd-grid">
            <div v-for="fd in filteredFDs" :key="fd.id" class="fd-card card">
              <div class="fd-header">
                <h3>FD #{{ fd.id }}</h3>
                <span :class="['status-badge', `status-${fd.status.toLowerCase()}`]">{{ fd.status }}</span>
              </div>
              <p><strong>User:</strong> {{ fd.userId }}</p>
              <p><strong>Amount:</strong> {{ formatCurrency(fd.amount) }}</p>
              <p><strong>Created:</strong> {{ formatDate(fd.createdAt) }}</p>
              <p><strong>Maturity:</strong> {{ formatDate(fd.maturityDate) }}</p>
              <p><strong>Interest Rate:</strong> {{ fd.interestRate }}%</p>
              <div class="fd-actions">
                <router-link :to="`/admin/fd-management/fd/${fd.id}`" class="btn btn-outline">View Details</router-link>
              </div>
            </div>
          </div>
          <p v-else class="empty">No fixed deposits found for the current search.</p>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
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
const searchFdId = ref('');
const searchUserId = ref('');

const filteredFDs = computed(() => {
  const fdIdQuery = searchFdId.value.trim();
  const userIdQuery = searchUserId.value.trim();

  return fds.value.filter((fd) => {
    const matchesFdId = fdIdQuery ? String(fd.id).includes(fdIdQuery) : true;
    const matchesUserId = userIdQuery ? String(fd.userId).includes(userIdQuery) : true;
    return matchesFdId && matchesUserId;
  });
});

const clearSearch = () => {
  searchFdId.value = '';
  searchUserId.value = '';
};

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
.search-row { display: grid; grid-template-columns: 1fr 1fr auto; gap: var(--spacing-md); margin-bottom: var(--spacing-lg); }
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
.fd-actions { margin-top: var(--spacing-md); position: relative; z-index: 1; }

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

@media (max-width: 900px) {
  .search-row { grid-template-columns: 1fr; }
}
</style>
