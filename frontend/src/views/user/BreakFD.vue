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
       <span v-if="breakPreview" :class="['status-badge', penaltyApplied ? 'status-warning' : 'status-success']">
         {{ penaltyApplied ? 'Penalty Applied' : 'No Penalty' }}
       </span>
     </div>

   

     <div v-if="(!breakPreview)&&(!withdrawalReciept) && !autoLoading" class="form-group">
       <form @submit.prevent="getPreview">
         <span style="display: flex; justify-content: space-between;">
          <label class="form-label" for="withdrawalAmount" style="display:inline; font-size: large">Amount</label>
          <button type="submit" class="btn btn-primary">Preview</button>
        </span>
        <br>
        <input class="form-control" id="withdrawalAmount" type="number" v-model="withdrawalAmount" style="width:fit-content">
       </form>
     </div>
     <div v-if="autoLoading" class="spinner"></div>

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
         <div class="break-item balance">
           <label>Balance Amount</label>
           <strong>{{ formatCurrency(balanceAmount) }}</strong>
         </div>
       </div>
       <button @click="confirmBreak" class="btn btn-danger">
         {{ isMatured ? 'Close FD' : 'Confirm Break' }}
       </button>
     </div>

     <!-- Withdrawal Receipt -->
      <div v-if="withdrawalReciept" class="receipt-container slide-in-up">
    <div class="card receipt-card">
        <div class="receipt-header">
            <div class="receipt-header-overlay">
                <h3 class="mb-1" style="color: white;">Withdrawal Successful</h3>
                <span class="badge badge-success">Fixed Deposit ID: #{{ receiptId }}</span>
            </div>
        </div>

        <div class="receipt-body">
            <div class="text-center mb-4">
                <p class="text-secondary mb-1">Total Amount Received</p>
                <h2 class="withdrawal-amount">{{ formatCurrency(totalReceived) }}</h2>
                <p class="text-xs text-secondary mt-1">
                    ({{ formatCurrency(receiptAmount) }} Principal + {{ formatCurrency(receiptInterest) }} Interest)
                </p>
            </div>

            <hr class="zeta-divider mb-3">

            <div class="receipt-details-grid">
                <div class="detail-item">
                    <span class="detail-label">Withdrawal Amount</span>
                    <span class="detail-value">{{ formatCurrency(receiptAmount) }}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Interest Earned</span>
                    <span class="detail-value text-success">+{{ formatCurrency(receiptInterest) }}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Interest Rate</span>
                    <span class="detail-value text-primary">{{ formatRate(receiptRate) }}%</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Closure Date</span>
                    <span class="detail-value">{{ receiptClosure }}</span>
                </div>
            </div>

            <div class="closure-box mt-3">
                <div class="flex justify-between align-center">
                    <div class="flex flex-column">
                        <span class="detail-label" style="margin-bottom: 0;">Period</span>
                        <span class="font-bold" style="font-size: var(--font-size-sm);">
                            {{ receiptStart }} to {{ receiptMaturity }}
                        </span>
                    </div>
                    <span class="badge badge-info">Closed</span>
                </div>
            </div>
        </div>

        <div class="receipt-footer mt-4 p-3">
            <button class="btn btn-primary btn-full-width mb-2" @click="print">
                Download Receipt
            </button>
            <button class="btn btn-outline btn-full-width" @click="router.push('/user/fd-list')">
                Return to Dashboard
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
import { ref, onMounted, computed, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { withdrawalService } from '@/services/withdrawalService';
import { fdService } from '@/services/fdService';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { formatCurrency } from '@/utils/helpers';
import { FDStatus } from '@/types';


const route = useRoute();
const router = useRouter();
const store = useStore();
const breakPreview = ref<any>(null);
const withdrawalAmount = ref<number>(0);
const message = ref<string>('Hello');
const withdrawalReciept = ref<any>(null);
const autoLoading = ref(false);
const fdStatus = ref<FDStatus | null>(null);

// Auto-preview if amount is passed via query param (from Break FD button)
onMounted(async () => {
  const fdId = parseInt(route.params.id as string);
  const userId = store.getters['auth/userId'];
  if (fdId && userId) {
    try {
      const fd = await fdService.getFDById(userId, fdId);
      fdStatus.value = fd.status;
    } catch (e: any) {
      console.warn('Failed to load FD status', e);
    }
  }
  const queryAmount = window.history.state.amount;
  if(!queryAmount){
    router.push('/user/fd-list');
  }
  if (queryAmount) {
    const amount = parseFloat(queryAmount as string);
    
    if (amount > 0) {
      withdrawalAmount.value = amount;
      autoLoading.value = true;
      if (fdId) {
        try {
          breakPreview.value = await withdrawalService.getBreakPreview(fdId, amount);
        } catch (e: any) {
          alert(e.response?.data?.message || 'Failed to load preview');
        } finally {
          autoLoading.value = false;
        }
      } else {
        autoLoading.value = false;
      }
    }
  }
});

onUnmounted(() => {
  const newState = { ...window.history.state, amount: null };
  window.history.replaceState(newState, '');
});


const principalAmount = computed(() =>
 Number(breakPreview.value?.withdrawalAmount ?? breakPreview.value?.withdrawalAmount ?? 0)
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
const balanceAmount = computed(()=>breakPreview.value?.balanceAmount)
const isMatured = computed(() => fdStatus.value === FDStatus.MATURED);

const receiptId = computed(() => withdrawalReciept.value?.id || 'N/A');

// Converting to Number for formatting functions (handles null/undefined)
const receiptAmount = computed(() => Number(withdrawalReciept.value?.withdrawalAmount || 0));
const receiptInterest = computed(() => Number(withdrawalReciept.value?.accruedInterest || 0));
const receiptRate = computed(() => Number(withdrawalReciept.value?.interestRate || 0));

// Date strings
const receiptStart = computed(() => withdrawalReciept.value?.startDate || '-');
const receiptMaturity = computed(() => withdrawalReciept.value?.maturityDate || '-');
const receiptClosure = computed(() => withdrawalReciept.value?.closureDate || '-');
const totalReceived = computed(()=>Number(withdrawalReciept.value?.withdrawalAmount || 0)+Number(withdrawalReciept.value?.accruedInterest || 0))

const formatRate = (value: number) => (Number.isFinite(value) ? value.toFixed(2) : '0.00');


const getPreview = async () => {
  if(withdrawalAmount.value<=0){
      alert("Amount should be positive");
      return;
  }
 const fdId = parseInt(route.params.id as string);
 if (fdId) {
  try{
    breakPreview.value = await withdrawalService.getBreakPreview(fdId, withdrawalAmount.value);
    console.log(breakPreview);
  }catch(e:any){
    alert(e.response?.data?.message);
  }
 }
};

function setMessage(text:string){
  message.value=text;
  setTimeout(()=>{message.value=""}, 2000);
}


const confirmBreak = async () => {
  if(withdrawalAmount.value<=0){
      setMessage("Amount should be positive");
  }
 const confirmMessage = isMatured.value
   ? 'Are you sure you want to close this FD?'
   : 'Are you sure you want to break this FD?';
 if (confirm(confirmMessage)) {
   const fdId = parseInt(route.params.id as string);
   try{
      withdrawalReciept.value = await withdrawalService.confirmBreak(fdId, withdrawalAmount.value);
      alert('FD broken successfully');
      breakPreview.value=null;
   }catch(error:any){
    alert(error.response?.data?.message);
   }
 }
};
//Handle Print Window
function print(){
  window.print();
}


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


.break-card {
 padding: var(--spacing-2xl);
 border-radius: var(--radius-xl);
 border: 1px solid var(--zeta-border);
 background: var(--zeta-surface);
 box-shadow: var(--shadow-md);
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
 border: 1px solid var(--zeta-border);
 background: var(--zeta-background-hover);
}


.break-item label { display: block; color: var(--zeta-text-secondary); margin-bottom: var(--spacing-xs); }


.break-item.highlight {
 background: linear-gradient(135deg, rgba(164, 33, 10, 0.14), rgba(59, 103, 166, 0.516));
 border-color: rgba(213, 114, 44, 0.2);
}
.break-item.balance {
 background: linear-gradient(135deg, rgba(8, 169, 94, 0.14), rgba(155, 126, 126, 0.619));
 border-color: rgba(131, 239, 68, 0.2);
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
/* Custom Additions for the Withdrawal Receipt */
.receipt-container {
    max-width: 450px;
    margin: var(--spacing-3xl) auto;
}

.receipt-card {
    padding: 0; /* Override card padding for header bleed */
    overflow: hidden;
    border: 1px solid var(--zeta-border);
}

.receipt-header {
    background: var(--zeta-gradient-hero);
    padding: var(--spacing-xl) var(--spacing-lg);
    text-align: center;
    position: relative;
}

.receipt-body {
    padding: var(--spacing-xl);
}

.withdrawal-amount {
    color: var(--zeta-primary-dark);
    font-size: var(--font-size-4xl);
    font-weight: 700;
}

.receipt-details-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: var(--spacing-lg);
}

.detail-item {
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

.btn-full-width {
    width: 100%;
}

.text-primary { color: var(--zeta-primary); }
.text-success { color: var(--zeta-success); }
.font-bold { font-weight: 600; }

@media print {
  
  .navbar, 
  .footer, 
  .user-sidebar,
  .page-header,
  .form-group,
  .break-card,
  h1, p {
    display: none !important;
  }

  .container, 
  .page-content, 
  .user-layout, 
  .user-content {
    margin: 0 !important;
    padding: 0 !important;
    display: block !important;
    width: 100% !important;
    position: static !important;
  }

  .receipt-container {
    display: block !important;
    margin: 0 auto !important;
    width: 100% !important;
    max-width: 600px; 
    box-shadow: none !important;
    transform: none !important;
  }

 
  .receipt-footer {
    display: none !important;
  }

  .receipt-header {
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
    background: #312e81 !important; 
    background: var(--zeta-gradient-hero) !important;
  }

  .card {
    border: 1px solid #e2e8f0 !important;
  }
}

</style>



