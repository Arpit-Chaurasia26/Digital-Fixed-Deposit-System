<template>
  <div class="page">
    <Navbar />
    <div class="container page-content user-layout">
      <UserSidebar />
      <div class="user-content">
      <div class="page-header card">
        <div>
          <h1>Interest Timeline</h1>
          <p>Track interest accruals across your FD lifecycle</p>
        </div>
        <div class="hero-chip">Bank-grade Insights</div>
      </div>
      <div v-if="interestTimeline" class="timeline-container">
        <div class="fd-info card">
          <div class="fd-info-header">
            <h3>FD Details</h3>
            <span class="info-chip">{{ interestFrequencyBadge }}</span>
          </div>
          <div class="fd-info-grid">
            <div>
              <label>Principal </label>
              <strong>{{ formatCurrency(interestTimeline.principal) }}</strong>
            </div>
            <div>
              <label>Interest Rate </label>
              <strong>{{ interestTimeline.interestRate }}%</strong>
            </div>
            <div>
              <label>Accrual Interval </label>
              <strong>{{ interestTimeline.interval }}</strong>
            </div>
          </div>
        </div>
        <div class="timeline">
          <div v-for="(accrual, index) in interestTimeline.timeline" :key="index" class="timeline-item card">
            <div class="timeline-marker">{{ displayIndex(index) }}</div>
            <div class="timeline-content">
              <div class="timeline-row">
                <div>
                  <label>Period</label>
                  <strong>{{ accrual.period }}</strong>
                </div>
                <div class="amount">
                  <label>Interest Accrued</label>
                  <strong>{{ formatCurrency(accrual.accruedInterest) }}</strong>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <p v-else>Loading interest timeline...</p>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { formatCurrency } from '@/utils/helpers';

const store = useStore();
const route = useRoute();
const interestTimeline = computed(() => store.getters['fd/interestTimeline']);
const currentFD = computed(() => store.getters['fd/currentFD']);
const userId = computed(() => store.getters['auth/userId']);

const displayIndex = (index: string | number) => Number(index) + 1;

const getFrequencyFromScheme = (scheme: string): 'MONTHLY' | 'QUARTERLY' | 'YEARLY' => {
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

const interestFrequencyBadge = computed(() => {
  if (currentFD.value?.interestScheme) {
    return formatFrequencyLabel(getFrequencyFromScheme(currentFD.value.interestScheme));
  }
  return interestTimeline.value?.interval || '';
});

onMounted(async () => {
  const fdId = parseInt(route.params.id as string);
  if (fdId) {
    await Promise.all([
      store.dispatch('fd/fetchInterestTimeline', { fdId }),
      userId.value ? store.dispatch('fd/fetchFDById', { userId: userId.value, fdId }) : Promise.resolve(),
    ]);
  }
});
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.user-content { min-width: 0; }
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-xl);
  padding: var(--spacing-2xl);
  background: var(--zeta-gradient-hero);
  color: white;
  border-radius: var(--radius-xl);
  margin-bottom: var(--spacing-2xl);
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
.hero-chip {
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.45);
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: 999px;
  font-weight: 700;
  letter-spacing: 0.3px;
  text-transform: uppercase;
  box-shadow: 0 0 18px rgba(255, 255, 255, 0.3);
}
.timeline-container { max-width: 900px; margin: 0 auto; }
.fd-info {
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-xl);
  border-radius: var(--radius-xl);
  border: 1px solid var(--zeta-border);
  background: var(--zeta-surface);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
}

.fd-info::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.14), transparent 60%);
  pointer-events: none;
}
.fd-info-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--spacing-md); }
.info-chip {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  color: white;
  padding: 6px 14px;
  border-radius: 999px;
  font-weight: 700;
  letter-spacing: 0.4px;
  text-transform: uppercase;
  box-shadow: 0 0 18px rgba(59, 130, 246, 0.55);
}
.fd-info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: var(--spacing-lg); }
.fd-info-grid label { color: var(--zeta-text-secondary); font-size: var(--font-size-sm); }
.fd-info-grid strong { font-size: var(--font-size-lg); color: var(--zeta-primary); }
.timeline { position: relative; padding-left: 30px; }
.timeline::before { content: ''; position: absolute; left: 10px; top: 0; bottom: 0; width: 2px; background: var(--zeta-divider); }
.timeline-item {
  position: relative;
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-xl);
  margin-left: 30px;
  border-radius: var(--radius-xl);
  border: 1px solid var(--zeta-border);
  background: var(--zeta-surface);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.timeline-item::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(14, 116, 144, 0.12), transparent 60%);
  pointer-events: none;
}
.timeline-marker {
  position: absolute;
  left: -40px;
  top: 24px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #06b6d4, #22c55e);
  border: 3px solid var(--zeta-surface);
  box-shadow: 0 0 18px rgba(34, 197, 94, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  color: #fff;
  font-weight: bold;
}
.timeline-row { display: flex; justify-content: space-between; align-items: center; gap: var(--spacing-lg); }
.timeline-row label { color: var(--zeta-text-secondary); font-size: var(--font-size-sm); display: block; }
.timeline-row .amount strong { color: var(--zeta-primary); font-size: var(--font-size-lg); }
</style>
