<template>
  <div class="page">
    <Navbar />
    <div class="container page-content user-layout">
      <UserSidebar />
      <div class="user-content">
      <router-link to="/user/fd-list" class="back-link">← Back to FD List</router-link>
      
      <div v-if="loading" class="spinner"></div>
      
      <div v-else-if="fd && !withdrawalReceipt" class="fd-detail-container">
        <div class="fd-detail-card card">
          <h1>FD Details #{{ fd.id }}</h1>
          <span :class="['status-badge', `status-${getStatusClass(fd.status)}`]">{{ fd.status }}</span>
          
          <div class="details-grid">
            <div class="detail-item">
              <label>Amount:</label>
              <strong>{{ formatCurrency(fd.amount) }}</strong>
            </div>
            <div class="detail-item">
              <label>Interest Rate:</label>
              <strong>{{ fd.interestRate }}% p.a.</strong>
            </div>
            <div class="detail-item">
              <label>Tenure:</label>
              <strong>{{ fd.tenureMonths }} months</strong>
            </div>
            <div class="detail-item">
              <label>Start Date:</label>
              <strong>{{ formatDate(fd.startDate) }}</strong>
            </div>
            <div class="detail-item">
              <label>Maturity Date:</label>
              <strong>{{ formatDate(fd.maturityDate) }}</strong>
            </div>
            <div class="detail-item">
              <label>Accrued Interest:</label>
              <strong>{{ formatCurrency(fd.accruedInterest || 0) }}</strong>
            </div>
            <div class="detail-item">
              <label>Current Value:</label>
              <strong>{{ formatCurrency((fd.amount || 0) + (fd.accruedInterest || 0)) }}</strong>
            </div>
            <div class="detail-item">
              <label>Scheme:</label>
              <strong>{{ formatSchemeName(fd.interestScheme) }}</strong>
            </div>
          </div>

          <div v-if="interestError" class="alert alert-error">{{ interestError }}</div>

          <!-- ========== STATUS-BASED ACTIONS ========== -->

          <!-- MATURED: Show Timeline + Break FD -->
          <div v-if="fd.status === 'MATURED'" class="status-actions-panel">
            <div class="status-message info-message">
              <span class="status-icon">🎉</span>
              <div>
                <strong>Your FD has matured!</strong>
                <p>You can now withdraw the full amount with all accrued interest. No penalty will be applied.</p>
              </div>
            </div>
            <div class="actions-section">
              <router-link :to="`/user/fd/${fd.id}/interest`" class="btn btn-primary">
                <span class="btn-icon">📊</span> View Interest Timeline
              </router-link>
              <router-link :to="{ path: `/user/fd/${fd.id}/break`, query: { amount: fd.amount } }" class="btn btn-danger">
                <span class="btn-icon">💰</span> Break FD
              </router-link>
            </div>
          </div>

          <!-- CLOSED: Show Timeline only -->
          <div v-else-if="fd.status === 'CLOSED'" class="status-actions-panel">
            <div class="status-message neutral-message">
              <span class="status-icon">✅</span>
              <div>
                <strong>This FD has been closed.</strong>
                <p>The deposit has been fully settled. You can view the interest timeline for your records.</p>
              </div>
            </div>
            <div class="actions-section">
              <router-link :to="`/user/fd/${fd.id}/interest`" class="btn btn-primary">
                <span class="btn-icon">📊</span> View Interest Timeline
              </router-link>
            </div>
          </div>

          <!-- BROKEN: Show Timeline only -->
          <div v-else-if="fd.status === 'BROKEN'" class="status-actions-panel">
            <div class="status-message warning-message">
              <span class="status-icon">⚠️</span>
              <div>
                <strong>This FD was broken prematurely.</strong>
                <p>The deposit was withdrawn before maturity. You can view the interest timeline for your records.</p>
              </div>
            </div>
            <div class="actions-section">
              <router-link :to="`/user/fd/${fd.id}/interest`" class="btn btn-primary">
                <span class="btn-icon">📊</span> View Interest Timeline
              </router-link>
            </div>
          </div>

          <!-- ACTIVE: Show Timeline + Check Eligibility → conditional Break/Partial Withdrawal -->
          <div v-else-if="fd.status === 'ACTIVE'" class="status-actions-panel">
            <div class="status-message active-message">
              <span class="status-icon">🔒</span>
              <div>
                <strong>Your FD is active and earning interest.</strong>
                <p>You can view the interest timeline or check if premature withdrawal is allowed under your scheme.</p>
              </div>
            </div>

            <div class="actions-section">
              <router-link :to="`/user/fd/${fd.id}/interest`" class="btn btn-primary">
                <span class="btn-icon">📊</span> View Interest Timeline
              </router-link>
              <button class="btn btn-accent" @click.prevent="handleEligibilityCheck" :disabled="checkingEligibility">
                <span class="btn-icon">🔍</span>
                {{ checkingEligibility ? 'Checking...' : 'Check Premature Withdrawal Eligibility' }}
              </button>
            </div>

            <!-- Eligibility Result -->
            <div v-if="eligibilityMessage" class="eligibility-result">
              <!-- Eligible: premature withdrawal allowed -->
              <div v-if="eligibilityMessage?.eligible" class="eligibility-card eligible">
                <div class="eligibility-header">
                  <span class="eligibility-badge badge-success">✓ Eligible</span>
                  <span class="eligibility-scheme">{{ formatSchemeName(fd.interestScheme) }}</span>
                </div>
                <p class="eligibility-cause">{{ eligibilityMessage?.rootCause }}</p>
                <div class="eligibility-info">
                  <p>💡 <strong>Note:</strong> Premature withdrawal is allowed under your FD scheme. Breaking the FD before maturity may attract a <strong>1% penalty</strong> on the interest rate. Withdrawals within the first 3 months will receive <strong>no interest</strong>.</p>
                </div>
                <div class="eligibility-actions">
                  <router-link :to="{ path: `/user/fd/${fd.id}/break`, query: { amount: fd.amount } }" class="btn btn-danger">
                    <span class="btn-icon">💔</span> Break FD (Full Withdrawal)
                  </router-link>
                  <button class="btn btn-warning" @click="showPartialWithdrawal = !showPartialWithdrawal">
                    <span class="btn-icon">💸</span> Partial Withdrawal
                  </button>
                </div>

                <!-- Inline Partial Withdrawal Section -->
                <div v-if="showPartialWithdrawal" class="partial-withdrawal-section">
                  <h4>Partial Withdrawal</h4>
                  <p class="partial-info">Enter the amount you wish to withdraw. The remaining balance will continue to earn interest.</p>
                  <div class="partial-form">
                    <div class="partial-input-group">
                      <label for="partialAmount">Withdrawal Amount (₹)</label>
                      <input
                        id="partialAmount"
                        type="number"
                        class="form-control"
                        v-model.number="partialAmount"
                        :max="fd.amount"
                        :min="1"
                        placeholder="Enter amount"
                      />
                      <span class="partial-hint">Max: {{ formatCurrency(fd.amount) }}</span>
                    </div>
                    <button
                      class="btn btn-primary"
                      @click="fetchPartialPreview"
                      :disabled="!partialAmount || partialAmount <= 0 || partialAmount > fd.amount || loadingPartialPreview"
                    >
                      {{ loadingPartialPreview ? 'Loading...' : 'Get Preview' }}
                    </button>
                  </div>
                  <div v-if="partialError" class="alert alert-error" style="margin-top: var(--spacing-md);">{{ partialError }}</div>

                  <!-- Inline Partial Withdrawal Preview -->
                  <div v-if="partialPreview" class="partial-preview">
                    <h4>Withdrawal Preview</h4>
                    <div class="partial-preview-grid">
                      <div class="preview-item">
                        <label>Withdrawal Amount</label>
                        <strong>{{ formatCurrency(partialPreview.withdrawalAmount || 0) }}</strong>
                      </div>
                      <div class="preview-item">
                        <label>Accumulated Interest</label>
                        <strong>{{ formatCurrency(partialPreview.accumulatedInterestAmount || 0) }}</strong>
                      </div>
                      <div class="preview-item">
                        <label>Penalty Rate</label>
                        <strong>{{ formatRate(partialPreview.penalty || 0) }}%</strong>
                      </div>
                      <div class="preview-item">
                        <label>Net Interest</label>
                        <strong>{{ formatCurrency(partialPreview.netInterestAmount || 0) }}</strong>
                      </div>
                      <div class="preview-item highlight">
                        <label>Total Payout</label>
                        <strong>{{ formatCurrency((partialPreview.withdrawalAmount || 0) + (partialPreview.netInterestAmount || 0)) }}</strong>
                      </div>
                      <div class="preview-item balance">
                        <label>Remaining Balance</label>
                        <strong>{{ formatCurrency(partialPreview.balanceAmount || 0) }}</strong>
                      </div>
                    </div>
                    <div class="partial-confirm-actions">
                      <button class="btn btn-danger" @click="confirmPartialWithdrawal" :disabled="confirmingWithdrawal">
                        {{ confirmingWithdrawal ? 'Processing...' : 'Confirm Withdrawal' }}
                      </button>
                      <button class="btn btn-outline" @click="cancelPartialPreview">Cancel</button>
                    </div>
                  </div>
                </div>
              </div>
              <!-- Not Eligible: premature withdrawal not allowed -->
              <div v-else class="eligibility-card not-eligible">
                <div class="eligibility-header">
                  <span class="eligibility-badge badge-error">✗ Not Eligible</span>
                  <span class="eligibility-scheme">{{ formatSchemeName(fd.interestScheme) }}</span>
                </div>
                <p class="eligibility-cause">{{ eligibilityMessage?.rootCause }}</p>
                <div class="eligibility-info">
                  <p>🔒 Premature withdrawal is <strong>not allowed</strong> under your current FD scheme. The deposit will continue to earn interest until the maturity date.</p>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>

      <!-- Withdrawal Receipt (shown after successful partial withdrawal) -->
      <div v-if="withdrawalReceipt" class="receipt-container slide-in-up">
        <div class="card receipt-card">
          <div class="receipt-header">
            <div class="receipt-header-overlay">
              <h3 class="mb-1" style="color: white;">Withdrawal Successful</h3>
              <span class="badge badge-success">Fixed Deposit ID: #{{ withdrawalReceipt.id }}</span>
            </div>
          </div>

          <div class="receipt-body">
            <div class="text-center mb-4">
              <p class="text-secondary mb-1">Total Amount Received</p>
              <h2 class="withdrawal-amount">{{ formatCurrency(Number(withdrawalReceipt.withdrawalAmount || 0) + Number(withdrawalReceipt.accruedInterest || 0)) }}</h2>
              <p class="text-xs text-secondary mt-1">
                ({{ formatCurrency(withdrawalReceipt.withdrawalAmount) }} Principal + {{ formatCurrency(withdrawalReceipt.accruedInterest) }} Interest)
              </p>
            </div>

            <hr class="zeta-divider mb-3">

            <div class="receipt-details-grid">
              <div class="receipt-detail-item">
                <span class="detail-label">Withdrawal Amount</span>
                <span class="detail-value">{{ formatCurrency(withdrawalReceipt.withdrawalAmount) }}</span>
              </div>
              <div class="receipt-detail-item">
                <span class="detail-label">Interest Earned</span>
                <span class="detail-value text-success">+{{ formatCurrency(withdrawalReceipt.accruedInterest) }}</span>
              </div>
              <div class="receipt-detail-item">
                <span class="detail-label">Interest Rate</span>
                <span class="detail-value text-primary">{{ formatRate(Number(withdrawalReceipt.interestRate || 0)) }}%</span>
              </div>
              <div class="receipt-detail-item">
                <span class="detail-label">Closure Date</span>
                <span class="detail-value">{{ withdrawalReceipt.closureDate || '-' }}</span>
              </div>
            </div>

            <div class="closure-box mt-3">
              <div class="flex justify-between align-center">
                <div class="flex flex-column">
                  <span class="detail-label" style="margin-bottom: 0;">Period</span>
                  <span class="font-bold" style="font-size: var(--font-size-sm);">
                    {{ withdrawalReceipt.startDate || '-' }} to {{ withdrawalReceipt.maturityDate || '-' }}
                  </span>
                </div>
                <span class="badge badge-info">Withdrawn</span>
              </div>
            </div>
          </div>

          <div class="receipt-footer mt-4 p-3">
            <button class="btn btn-primary btn-full-width mb-2" @click="printReceipt">
              Download Receipt
            </button>
            <button class="btn btn-outline btn-full-width" @click="router.push('/user/fd-list')">
              Return to My FDs
            </button>
          </div>
        </div>
      </div>

      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, onUnmounted, ref } from 'vue';
import { useStore } from 'vuex';
import { useRoute, useRouter } from 'vue-router';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { withdrawalService } from '@/services/withdrawalService';
import { formatCurrency, formatDate, getErrorMessage } from '@/utils/helpers';

const store = useStore();
const route = useRoute();
const router = useRouter();

const userId = computed(() => store.getters['auth/userId']);
const fd = computed(() => store.getters['fd/currentFD']);
const loading = computed(() => store.getters['fd/loading']);
const interestError = computed(() => store.getters['fd/error']);
const eligibilityMessage = computed(() => store.getters['fd/eligibilityMessage']);
const checkingEligibility = ref(false);

// Partial withdrawal state
const showPartialWithdrawal = ref(false);
const partialAmount = ref<number>(0);
const partialPreview = ref<any>(null);
const loadingPartialPreview = ref(false);
const partialError = ref<string | null>(null);
const confirmingWithdrawal = ref(false);
const withdrawalReceipt = ref<any>(null);

const getStatusClass = (status: string) => {
  const classes: { [key: string]: string } = {
    ACTIVE: 'success',
    MATURED: 'info',
    BROKEN: 'error',
    CLOSED: 'closed',
  };
  return classes[status] || 'info';
};

const formatSchemeName = (name: string) => {
  return name.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
};

const formatRate = (value: number) => (Number.isFinite(value) ? value.toFixed(2) : '0.00');

onMounted(async () => {
  const fdId = parseInt(route.params.id as string);
  if (userId.value && fdId) {
    await store.dispatch('fd/fetchFDById', { userId: userId.value, fdId });
    await store.dispatch('fd/fetchAccruedInterest', fdId);
  }
});

const handleEligibilityCheck = async () => {
  const fdId = parseInt(route.params.id as string);
  if (!fdId) return;
  checkingEligibility.value = true;
  try {
    await store.dispatch('fd/fetchEligibilityCheck', fdId);
  } catch (err) {
    store.commit('fd/SET_ERROR', getErrorMessage(err, 'Unable to check eligibility'));
  } finally {
    checkingEligibility.value = false;
  }
};

const fetchPartialPreview = async () => {
  const fdId = parseInt(route.params.id as string);
  if (!fdId || !partialAmount.value || partialAmount.value <= 0) return;
  loadingPartialPreview.value = true;
  partialError.value = null;
  partialPreview.value = null;
  try {
    partialPreview.value = await withdrawalService.getBreakPreview(fdId, partialAmount.value);
  } catch (err: any) {
    partialError.value = err.response?.data?.message || 'Failed to get withdrawal preview';
  } finally {
    loadingPartialPreview.value = false;
  }
};

const confirmPartialWithdrawal = async () => {
  const fdId = parseInt(route.params.id as string);
  if (!fdId || !partialAmount.value) return;
  if (!confirm('Are you sure you want to proceed with this partial withdrawal?')) return;
  confirmingWithdrawal.value = true;
  try {
    const receipt = await withdrawalService.confirmBreak(fdId, partialAmount.value);
    withdrawalReceipt.value = receipt;
    // Clear partial withdrawal UI
    partialPreview.value = null;
    showPartialWithdrawal.value = false;
    store.commit('fd/SET_ELIGIBILITY_MESSAGE', null);
  } catch (err: any) {
    partialError.value = err.response?.data?.message || 'Withdrawal failed';
  } finally {
    confirmingWithdrawal.value = false;
  }
};

const printReceipt = () => {
  window.print();
};

const cancelPartialPreview = () => {
  partialPreview.value = null;
  partialAmount.value = 0;
  partialError.value = null;
  showPartialWithdrawal.value = false;
};

onUnmounted(() => {
  store.commit('fd/SET_ELIGIBILITY_MESSAGE', null);
});
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.user-content { min-width: 0; }
.back-link { display: inline-block; margin-bottom: var(--spacing-lg); color: var(--zeta-primary); }
.fd-detail-card {
  padding: var(--spacing-2xl);
  max-width: 900px;
  margin: 0 auto;
  border-radius: var(--radius-xl);
  border: 1px solid var(--zeta-border);
  background: var(--zeta-surface);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
}

.fd-detail-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.14), transparent 60%);
  pointer-events: none;
}
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
  box-shadow: var(--shadow-sm);
}
.detail-item { label { display: block; color: var(--zeta-text-secondary); margin-bottom: var(--spacing-xs); } }

/* Status Actions Panel */
.status-actions-panel {
  margin-top: var(--spacing-xl);
  position: relative;
  z-index: 1;
}

.status-message {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  border-radius: var(--radius-lg);
  margin-bottom: var(--spacing-lg);
  border: 1px solid;
}

.status-message p {
  margin: 4px 0 0;
  font-size: var(--font-size-sm);
  opacity: 0.85;
}

.status-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
  margin-top: 2px;
}

.info-message {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08), rgba(96, 165, 250, 0.05));
  border-color: rgba(59, 130, 246, 0.3);
  color: var(--zeta-text);
}

.neutral-message {
  background: linear-gradient(135deg, rgba(107, 114, 128, 0.08), rgba(156, 163, 175, 0.05));
  border-color: rgba(107, 114, 128, 0.3);
  color: var(--zeta-text);
}

.warning-message {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.08), rgba(248, 113, 113, 0.05));
  border-color: rgba(239, 68, 68, 0.3);
  color: var(--zeta-text);
}

.active-message {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.08), rgba(74, 222, 128, 0.05));
  border-color: rgba(34, 197, 94, 0.3);
  color: var(--zeta-text);
}

.actions-section {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.btn-icon {
  margin-right: 6px;
}

/* Eligibility Result */
.eligibility-result {
  margin-top: var(--spacing-lg);
}

.eligibility-card {
  padding: var(--spacing-lg);
  border-radius: var(--radius-lg);
  border: 1px solid;
  box-shadow: var(--shadow-sm);
}

.eligibility-card.eligible {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.06), rgba(74, 222, 128, 0.03));
  border-color: rgba(34, 197, 94, 0.3);
}

.eligibility-card.not-eligible {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.06), rgba(248, 113, 113, 0.03));
  border-color: rgba(239, 68, 68, 0.3);
}

.eligibility-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
}

.eligibility-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 999px;
  font-size: var(--font-size-xs);
  font-weight: 700;
  letter-spacing: 0.3px;
  color: white;
}

.badge-success {
  background: linear-gradient(135deg, #16a34a, #22c55e);
}

.badge-error {
  background: linear-gradient(135deg, #ef4444, #f87171);
}

.eligibility-scheme {
  font-size: var(--font-size-sm);
  color: var(--zeta-text-secondary);
  font-weight: 500;
}

.eligibility-cause {
  font-size: var(--font-size-sm);
  color: var(--zeta-text-secondary);
  margin: var(--spacing-xs) 0;
}

.eligibility-info {
  margin: var(--spacing-md) 0;
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  background: var(--zeta-background-hover);
  border: 1px solid var(--zeta-border);
  font-size: var(--font-size-sm);
  line-height: 1.6;
}

.eligibility-info p {
  margin: 0;
}

.eligibility-actions {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-md);
  flex-wrap: wrap;
}

.btn-warning {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  color: #1a1a1a;
  border: none;
  padding: 10px 20px;
  border-radius: var(--radius-md);
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  transition: all 0.2s ease;
}

.btn-warning:hover {
  background: linear-gradient(135deg, #d97706, #f59e0b);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.35);
}

/* Status Badge */
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

.status-closed {
  background: linear-gradient(135deg, #6b7280, #9ca3af);
  box-shadow: 0 0 18px rgba(107, 114, 128, 0.55);
}

/* Withdrawal Receipt */
.receipt-container {
  max-width: 500px;
  margin: var(--spacing-xl) auto;
}

.receipt-card {
  padding: 0;
  overflow: hidden;
  border: 1px solid var(--zeta-border);
}

.receipt-header {
  background: var(--zeta-gradient-hero);
  padding: var(--spacing-xl) var(--spacing-lg);
  text-align: center;
  position: relative;
}

.receipt-header-overlay {
  position: relative;
  z-index: 1;
}

.receipt-body {
  padding: var(--spacing-xl);
}

.withdrawal-amount {
  color: var(--zeta-primary);
  font-size: var(--font-size-4xl);
  font-weight: 700;
}

.receipt-details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-lg);
}

.receipt-detail-item {
  display: flex;
  flex-direction: column;
}

.detail-label {
  font-size: var(--font-size-xs);
  color: var(--zeta-text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: var(--spacing-xs);
}

.detail-value {
  font-weight: 600;
  font-size: var(--font-size-base);
}

.closure-box {
  background-color: var(--zeta-surface-2);
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  border-left: 4px solid var(--zeta-primary);
}

.receipt-footer {
  padding: var(--spacing-lg);
}

.btn-full-width {
  width: 100%;
}

.text-primary { color: var(--zeta-primary); }
.text-success { color: var(--zeta-success); }
.text-secondary { color: var(--zeta-text-secondary); }
.text-xs { font-size: var(--font-size-xs); }
.font-bold { font-weight: 600; }
.zeta-divider {
  border: none;
  border-top: 1px solid var(--zeta-divider);
}

@media print {
  .navbar, .footer, .user-sidebar,
  .fd-detail-card, .back-link {
    display: none !important;
  }

  .container, .page-content, .user-layout, .user-content {
    margin: 0 !important;
    padding: 0 !important;
    display: block !important;
    width: 100% !important;
  }

  .receipt-container {
    margin: 0 auto !important;
    width: 100% !important;
    max-width: 600px;
    box-shadow: none !important;
  }

  .receipt-footer {
    display: none !important;
  }

  .receipt-header {
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
  }
}

/* Partial Withdrawal Section */
.partial-withdrawal-section {
  margin-top: var(--spacing-lg);
  padding: var(--spacing-lg);
  border-radius: var(--radius-lg);
  background: var(--zeta-background-hover);
  border: 1px solid var(--zeta-border);
}

.partial-withdrawal-section h4 {
  margin: 0 0 var(--spacing-xs) 0;
  font-size: var(--font-size-lg);
}

.partial-info {
  font-size: var(--font-size-sm);
  color: var(--zeta-text-secondary);
  margin: 0 0 var(--spacing-md) 0;
}

.partial-form {
  display: flex;
  align-items: flex-end;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.partial-input-group {
  flex: 1;
  min-width: 200px;
}

.partial-input-group label {
  display: block;
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--zeta-text-secondary);
  margin-bottom: var(--spacing-xs);
}

.partial-input-group .form-control {
  width: 100%;
}

.partial-hint {
  display: block;
  font-size: var(--font-size-xs);
  color: var(--zeta-text-secondary);
  margin-top: 4px;
}

/* Partial Preview */
.partial-preview {
  margin-top: var(--spacing-lg);
  padding: var(--spacing-lg);
  border-radius: var(--radius-lg);
  background: var(--zeta-surface);
  border: 1px solid var(--zeta-border);
  box-shadow: var(--shadow-sm);
}

.partial-preview h4 {
  margin: 0 0 var(--spacing-md) 0;
}

.partial-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.preview-item {
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  background: var(--zeta-background-hover);
  border: 1px solid var(--zeta-border);
}

.preview-item label {
  display: block;
  font-size: var(--font-size-xs);
  color: var(--zeta-text-secondary);
  margin-bottom: var(--spacing-xs);
}

.preview-item.highlight {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(96, 165, 250, 0.05));
  border-color: rgba(59, 130, 246, 0.2);
}

.preview-item.balance {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.1), rgba(74, 222, 128, 0.05));
  border-color: rgba(34, 197, 94, 0.2);
}

.partial-confirm-actions {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.btn-outline {
  background: transparent;
  border: 1px solid var(--zeta-border);
  color: var(--zeta-text);
  padding: 10px 20px;
  border-radius: var(--radius-md);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-outline:hover {
  background: var(--zeta-background-hover);
}

@media (max-width: 768px) {
  .details-grid { grid-template-columns: 1fr; }
  .actions-section { flex-direction: column; }
  .eligibility-actions { flex-direction: column; }
  .partial-form { flex-direction: column; align-items: stretch; }
  .partial-preview-grid { grid-template-columns: 1fr; }
  .partial-confirm-actions { flex-direction: column; }
}
</style>
