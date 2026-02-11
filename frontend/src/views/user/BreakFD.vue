<template>
  <div class="page">
    <Navbar />
    <div class="container page-content user-layout">
      <UserSidebar />
      <div class="user-content">
      <div class="page-header card">
        <div>
          <h1>Break Fixed Deposit</h1>
          <p>Review the payout summary before confirming the break.</p>
        </div>
        <span :class="['status-badge', penaltyApplied ? 'status-warning' : 'status-success']">
          {{ penaltyApplied ? 'Penalty Applied' : 'No Penalty' }}
        </span>
      </div>

      <div v-if="breakPreview" class="break-card card">
        <div class="break-header">
          <h3>Break Preview</h3>
          <span class="rate-chip">Rate {{ formatRate(interestRate) }}%</span>
        </div>
        <div class="break-grid">
          <div class="break-item">
            <label>Principal</label>
            <strong>{{ formatCurrency(principalAmount) }}</strong>
          </div>
          <div class="break-item">
            <label>Accumulated Interest</label>
            <strong>{{ formatCurrency(accumulatedInterest) }}</strong>
          </div>
          <div class="break-item">
            <label>Penalty</label>
            <strong>{{ formatRate(penaltyRate) }}%</strong>
          </div>
          <div class="break-item">
            <label>Interest After Penalty</label>
            <strong>{{ formatCurrency(netInterest) }}</strong>
          </div>
          <div class="break-item highlight">
            <label>Total Payout</label>
            <strong>{{ formatCurrency(totalPayout) }}</strong>
          </div>
        </div>
        <button @click="confirmBreak" class="btn btn-danger">Confirm Break</button>
      </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { withdrawalService } from '@/services/withdrawalService';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { formatCurrency } from '@/utils/helpers';

const route = useRoute();
const router = useRouter();
const breakPreview = ref<any>(null);

const principalAmount = computed(() =>
  Number(breakPreview.value?.principleAmount ?? breakPreview.value?.principalAmount ?? 0)
);
const accumulatedInterest = computed(() =>
  Number(breakPreview.value?.accumulatedInterestAmount ?? breakPreview.value?.normalInterest ?? 0)
);
const penaltyRate = computed(() => Number(breakPreview.value?.penalty ?? breakPreview.value?.penaltyRate ?? 0));
const interestRate = computed(() => Number(breakPreview.value?.interestRate ?? breakPreview.value?.originalInterestRate ?? 0));
const netInterest = computed(() =>
  Number(breakPreview.value?.netInterestAmount ?? breakPreview.value?.interestAfterPenalty ?? 0)
);
const totalPayout = computed(() =>
  Number(breakPreview.value?.totalPayout ?? principalAmount.value + netInterest.value)
);
const penaltyApplied = computed(() => penaltyRate.value > 0);

const formatRate = (value: number) => (Number.isFinite(value) ? value.toFixed(2) : '0.00');

onMounted(async () => {
  const fdId = parseInt(route.params.id as string);
  if (fdId) {
    breakPreview.value = await withdrawalService.getBreakPreview(fdId);
  }
});

const confirmBreak = async () => {
  if (confirm('Are you sure you want to break this FD?')) {
    const fdId = parseInt(route.params.id as string);
    await withdrawalService.confirmBreak(fdId);
    alert('FD broken successfully');
    router.push('/user/fd-list');
  }
};
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.user-content { min-width: 0; }
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-lg);
  padding: var(--spacing-2xl);
  margin-bottom: var(--spacing-2xl);
  background: var(--zeta-gradient-hero);
  color: white;
  border-radius: var(--radius-xl);
  box-shadow: 0 24px 55px rgba(15, 23, 42, 0.18);
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

.break-card {
  padding: var(--spacing-2xl);
  border-radius: var(--radius-xl);
  border: 1px solid rgba(148, 163, 184, 0.2);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.96));
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
  position: relative;
  overflow: hidden;
}

.break-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(239, 68, 68, 0.12), transparent 60%);
  pointer-events: none;
}

.break-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.rate-chip {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  color: white;
  padding: 6px 14px;
  border-radius: 999px;
  font-weight: 700;
  letter-spacing: 0.4px;
  text-transform: uppercase;
  box-shadow: 0 0 18px rgba(59, 130, 246, 0.55);
}

.break-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.break-item {
  padding: var(--spacing-md);
  border-radius: var(--radius-lg);
  border: 1px solid rgba(148, 163, 184, 0.2);
  background: rgba(255, 255, 255, 0.7);
}

.break-item label { display: block; color: var(--zeta-text-secondary); margin-bottom: var(--spacing-xs); }

.break-item.highlight {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.14), rgba(248, 113, 113, 0.12));
  border-color: rgba(239, 68, 68, 0.2);
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

.status-success {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  box-shadow: 0 0 18px rgba(34, 197, 94, 0.55);
}

.status-warning {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  box-shadow: 0 0 18px rgba(245, 158, 11, 0.55);
}
</style>
