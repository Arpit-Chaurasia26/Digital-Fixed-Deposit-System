<template>
  <div class="page">
    <Navbar />
    <div class="container page-content user-layout">
      <UserSidebar />
      
      <div class="user-content">
        <div class="page-header card mb-4">
          <div>
            <h1>Transaction History</h1>
            <p>View all your past Fixed Deposit withdrawals and interest payouts.</p>
          </div>
        </div>

        <div v-if="withdrawalHistory" class="history-section slide-in-up">
          <div class="card">
            <div class="flex justify-between align-center mb-3">
              <h3>Withdrawal Records</h3>
              <span class="badge badge-info">{{ withdrawalHistory.length }} Records Found</span>
            </div>

            <div class="table-responsive">
              <table class="zeta-table">
                <thead>
                  <tr>
                    <th>FD ID</th>
                    <th>Withdrawal Amount</th>
                    <th>Interest Paid</th>
                    <th>Date Withdrawn</th>
                    <th class="text-right">Total Payout</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="history in withdrawalHistory" :key="history.fdId + history.withdrawnDate">
                    <td>
                      <span class="fd-id-tag">#{{ history.fdId }}</span>
                    </td>
                    <td class="font-bold">
                      {{ formatCurrency(history.withdrawalAmount) }}
                    </td>
                    <td class="text-success">
                      + {{ formatCurrency(history.interestPaid) }}
                    </td>
                    <td>
                      <div class="date-cell">
                        {{ history.withdrawnDate }}
                      </div>
                    </td>
                    <td class="text-right font-bold text-primary">
                      {{ formatCurrency(calculateTotal(history.withdrawalAmount, history.interestPaid)) }}
                    </td>
                  </tr>
                  <tr v-if="withdrawalHistory.length === 0">
                    <td colspan="5" class="text-center p-4 text-secondary">
                      No withdrawal transactions found.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useStore } from 'vuex';
import { formatCurrency } from '@/utils/helpers';
import { withdrawalService } from '@/services/withdrawalService';
import Footer from '@/components/common/Footer.vue';
import Navbar from '@/components/common/Navbar.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';

const store = useStore();
const userId = computed(() => store.getters['auth/userId']);
const withdrawalHistory = ref<any | null>(null);

onMounted(async () => {
  if (userId.value) {
    try {
      withdrawalHistory.value = await withdrawalService.getTransactionHistory(userId.value);
    } catch (error: any) {
      alert(error.response?.data?.message || "Failed to fetch history");
    }
  }
});

const calculateTotal = (amount: number, interest: number) => {
  return Number(amount) + Number(interest);
};
</script>

<style scoped lang="scss">

.user-content {
  flex: 1;
  min-width: 0;
  padding-left: var(--spacing-lg);
}


.page-header {
    margin-left:0px;
  padding: var(--spacing-xl);
  background: var(--zeta-gradient-hero);
  color: white;
  border-radius: var(--radius-xl);
  
  h1 { margin: 0; font-size: var(--font-size-2xl); }
  p { margin: 0; opacity: 0.9; }
}


.table-responsive {
  width: 100%;
  overflow-x: auto;
}

.zeta-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  table-layout: fixed;
  
  th, td {
    text-align: left;
    vertical-align: middle;
  }

  th:nth-child(1), td:nth-child(1) { width: 12%; }
  th:nth-child(2), td:nth-child(2) { width: 22%; }
  th:nth-child(3), td:nth-child(3) { width: 20%; }
  th:nth-child(4), td:nth-child(4) { width: 22%; }
  th:nth-child(5), td:nth-child(5) { width: 24%; text-align: right; }

  th {
    background-color: var(--zeta-background-paper);
    color: var(--zeta-text-secondary);
    font-size: var(--font-size-xs);
    text-transform: uppercase;
    padding: var(--spacing-md);
    border-bottom: 2px solid var(--zeta-divider);
    white-space: nowrap;
  }

  td {
    padding: var(--spacing-md);
    border-bottom: 1px solid var(--zeta-divider);
    font-size: var(--font-size-sm);
  }

  tr:hover td {
    background-color: var(--zeta-background-hover);
  }

  .fd-id-tag {
    background: var(--zeta-surface-2);
    padding: 4px 8px;
    border-radius: var(--radius-sm);
    border: 1px solid var(--zeta-border);
    font-family: monospace;
  }
}

.text-success { color: var(--zeta-success); }
.text-primary { color: var(--zeta-primary); }
.font-bold { font-weight: 600; }
</style>