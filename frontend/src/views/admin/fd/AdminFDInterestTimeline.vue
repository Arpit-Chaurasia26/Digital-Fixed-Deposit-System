<template>
  <div class="page">
    <Navbar />
    <div class="container page-content admin-layout">
      <AdminSidebar />
      <div class="admin-content">
      <div class="page-header card">
        <div>
          <h1>FD Interest Timeline</h1>
          <p>Analyze accrual trends with clear timeline breakdowns.</p>
        </div>
      </div>

      <div class="card panel">
        <div class="panel-header">
          <h3>Lookup</h3>
          <div class="days-select">
            <label>FD ID</label>
            <input v-model.number="fdId" type="number" class="form-control" min="1" />
            <label>Interval</label>
            <select v-model="interval" class="form-control">
              <option value="MONTHLY">Monthly</option>
              <option value="QUARTERLY">Quarterly</option>
              <option value="YEARLY">Yearly</option>
            </select>
            <button class="btn btn-secondary" @click="loadTimeline" :disabled="loading">Load</button>
          </div>
        </div>

        <div v-if="error" class="alert alert-error">{{ error }}</div>
        <div v-if="loading" class="alert alert-info">Loading timeline...</div>

        <div v-else-if="timeline" class="timeline-section">
          <div class="timeline-header">
            <div><strong>Principal:</strong> {{ formatCurrency(timeline.principal) }}</div>
            <div><strong>Interest Rate:</strong> {{ timeline.interestRate }}%</div>
            <div><strong>Interval:</strong> {{ timeline.interval }}</div>
          </div>
          <div class="timeline-grid">
            <div v-for="point in timeline.timeline" :key="point.period" class="timeline-item">
              <span>{{ point.period }}</span>
              <strong>{{ formatCurrency(point.accruedInterest) }}</strong>
            </div>
          </div>
        </div>

        <p v-else class="empty">Enter an FD ID to view timeline.</p>
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
import { FDInterestTimeline } from '@/types';
import { formatCurrency, getErrorMessage } from '@/utils/helpers';

const fdId = ref<number | null>(null);
const interval = ref('MONTHLY');
const timeline = ref<FDInterestTimeline | null>(null);
const loading = ref(false);
const error = ref('');

const loadTimeline = async () => {
  if (!fdId.value) {
    error.value = 'Please enter a valid FD ID.';
    return;
  }
  loading.value = true;
  error.value = '';
  try {
    timeline.value = await fdService.getAdminInterestTimeline(fdId.value, interval.value);
  } catch (err) {
    timeline.value = null;
    error.value = getErrorMessage(err, 'Unable to load interest timeline');
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
.days-select { display: flex; gap: var(--spacing-sm); align-items: center; flex-wrap: wrap; }
.timeline-section { display: grid; gap: var(--spacing-md); }
.timeline-header { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: var(--spacing-md); }
.timeline-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: var(--spacing-md); }
.timeline-item { display: flex; justify-content: space-between; align-items: center; padding: var(--spacing-sm) var(--spacing-md); background: var(--zeta-background); border-radius: var(--radius-md); border: 1px solid var(--zeta-divider); }
.empty { color: var(--zeta-text-secondary); }
</style>
