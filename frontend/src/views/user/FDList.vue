<template>
 <div class="page">
   <Navbar />
   <div class="container page-content user-layout">
     <UserSidebar />
     <div class="user-content">
     <div class="page-header card">
       <div>
         <h1>My Fixed Deposits</h1>
         <p>Monitor all your deposits and track maturity</p>
       </div>
       <router-link to="/user/book-fd" class="btn btn-primary">Book New FD</router-link>
     </div>
    
     <div class="filters-section card">
       <div class="filters-row">
         <input v-model.number="searchFdId" type="number" class="form-control" placeholder="Search by FD ID" min="1" />
         <select v-model="filterStatus" class="form-control">
           <option value="">All Status</option>
           <option value="ACTIVE">Active</option>
           <option value="MATURED">Matured</option>
           <option value="BROKEN">Broken</option>
         </select>
         <input v-model.number="minAmount" type="number" class="form-control" placeholder="Min Amount" min="0" />
         <input v-model.number="maxAmount" type="number" class="form-control" placeholder="Max Amount" min="0" />
         <button class="btn btn-secondary" @click="applyFilters">Apply</button>
         <button class="btn btn-outline" @click="clearFilters">Clear</button>
       </div>
     </div>


     <div v-if="loading" class="spinner"></div>
    
     <div v-else-if="displayedFDs.length === 0" class="empty-state">
       <p>No fixed deposits found</p>
       <router-link to="/user/book-fd" class="btn btn-primary">Book Your First FD</router-link>
     </div>


     <div v-else class="fd-grid">
         <div v-for="fd in displayedFDs" :key="fd.id" class="fd-card card">
         <div class="fd-header">
           <h3>FD #{{ fd.id }}</h3>
           <span :class="['status-badge', `status-${getStatusClass(fd.status)}`]">{{ fd.status }}</span>
         </div>
         <div class="fd-details">
           <p><strong>Amount:</strong> {{ formatCurrency(fd.amount) }}</p>
           <p><strong>Interest Rate:</strong> {{ fd.interestRate }}%</p>
           <p><strong>Tenure:</strong> {{ fd.tenureMonths }} months</p>
           <p><strong>Maturity Date:</strong> {{ formatDate(fd.maturityDate) }}</p>
           <p><strong>Accrued Interest:</strong> {{ formatCurrency(fd.accruedInterest || 0) }}</p>
           <p><strong>Current Value:</strong> {{ formatCurrency((fd.amount || 0) + (fd.accruedInterest || 0)) }}</p>
         </div>
         <div class="fd-actions">
           <router-link :to="`/user/fd/${fd.id}`" class="btn btn-outline">View Details</router-link>
         </div>
       </div>
     </div>
     </div>
   </div>
   <Footer />
 </div>
</template>


<script setup lang="ts">
import { onMounted, computed, ref } from 'vue';
import { useStore } from 'vuex';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { formatCurrency, formatDate } from '@/utils/helpers';


const store = useStore();
const searchFdId = ref<number | null>(null);
const filterStatus = ref('');
const minAmount = ref<number | null>(null);
const maxAmount = ref<number | null>(null);


const userId = computed(() => store.getters['auth/userId']);
const fds = computed(() => store.getters['fd/allFDs']);
const loading = computed(() => store.getters['fd/loading']);


const displayedFDs = computed(() => {
  if (searchFdId.value) {
    return fds.value.filter((fd: any) => fd.id === searchFdId.value);
  }
  return fds.value;
});


const applyFilters = async () => {
 if (!userId.value) return;
 await store.dispatch('fd/fetchUserFDs', {
   userId: userId.value,
   filters: {
     status: filterStatus.value || undefined,
     minAmount: minAmount.value || undefined,
     maxAmount: maxAmount.value || undefined,
   },
 });
};


const clearFilters = async () => {
 filterStatus.value = '';
 minAmount.value = null;
 maxAmount.value = null;
 if (!userId.value) return;
 await store.dispatch('fd/fetchUserFDs', { userId: userId.value });
};


const getStatusClass = (status: string) => {
 const classes: { [key: string]: string } = {
   ACTIVE: 'success',
   MATURED: 'info',
   BROKEN: 'error',
 };
 return classes[status] || 'info';
};


onMounted(async () => {
 if (userId.value) {
   await store.dispatch('fd/fetchUserFDs', { userId: userId.value });
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
 gap: var(--spacing-lg);
 padding: var(--spacing-2xl);
 margin-bottom: var(--spacing-2xl);
 background: var(--zeta-gradient-hero);
 color: white;
 border-radius: var(--radius-xl);
}
.page-header p { margin: 0; color: rgba(255, 255, 255, 0.9); }
.filters-section { padding: var(--spacing-md); border-radius: var(--radius-lg); margin-bottom: var(--spacing-xl); }
.filters-section {
 background: var(--zeta-surface);
 border: 1px solid var(--zeta-border);
 box-shadow: var(--shadow-md);
}
.filters-row { display: flex; gap: var(--spacing-md); flex-wrap: wrap; align-items: center; }
.filters-row .form-control { max-width: 220px; }
.fd-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: var(--spacing-lg); }
.fd-card {
 padding: var(--spacing-xl);
 border-radius: var(--radius-xl);
 border: 1px solid var(--zeta-border);
 background: var(--zeta-surface);
 box-shadow: var(--shadow-md);
 position: relative;
 overflow: hidden;
}


.fd-card::after {
 content: '';
 position: absolute;
 inset: 0;
 background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.12), transparent 60%);
 pointer-events: none;
}
.fd-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--spacing-md); }
.fd-details { margin-bottom: var(--spacing-md); p { margin: var(--spacing-xs) 0; } }
.fd-actions { display: flex; gap: var(--spacing-sm); }
.empty-state { text-align: center; padding: var(--spacing-3xl); }


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
</style>



