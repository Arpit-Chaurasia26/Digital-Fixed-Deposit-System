<template>
 <div class="page">
   <Navbar />
   <div class="container page-content user-layout">
     <UserSidebar />
     <div class="user-content">
     <div class="page-header card hero-card">
       <div class="hero-text">
         <h1>Book Fixed Deposit</h1>
         <p>Invest in secure fixed deposits with competitive returns</p>
       </div>
       <div class="hero-badge">Guaranteed Returns</div>
     </div>


     <div class="book-fd-container">
       <form @submit.prevent="handleBookFD" class="fd-form card">
         <div class="form-group">
           <label class="form-label">Select Scheme</label>
           <select v-model="formData.interestScheme" @change="onSchemeChange" class="form-control" required>
             <option value="">Choose a scheme</option>
             <option v-for="scheme in schemes" :key="scheme.name" :value="scheme.name">
               {{ formatSchemeName(scheme.name) }} ({{ scheme.annualInterestRate }}% p.a. - {{ scheme.tenureInMonths }} months)
             </option>
           </select>
         </div>


         <div class="form-group">
           <label class="form-label">Principal Amount (₹)</label>
           <input
             v-model.number="formData.amount"
             type="number"
             class="form-control"
             placeholder="Enter amount"
             min="5000"
             required
           />
           <span v-if="errors.amount" class="form-error">{{ errors.amount }}</span>
         </div>


         <div class="form-group">
           <label class="form-label">Tenure</label>
           <input
             type="text"
             class="form-control"
             :value="selectedScheme ? `${selectedScheme.tenureInMonths} months (Fixed)` : ''"
             readonly
             disabled
           />
           <small class="form-help">Tenure is automatically set based on the selected scheme</small>
         </div>


         <div v-if="maturityPreview && selectedScheme" class="maturity-preview">
           <h3>Maturity Preview</h3>
           <div class="preview-details">
             <div class="detail-row">
               <span>Principal Amount:</span>
               <strong>{{ formatCurrency(formData.amount) }}</strong>
             </div>
             <div class="detail-row">
               <span>Interest Rate:</span>
               <strong>{{ selectedScheme.annualInterestRate }}% p.a.</strong>
             </div>
             <div class="detail-row">
               <span>Tenure:</span>
               <strong>{{ selectedScheme.tenureInMonths }} months</strong>
             </div>
             <div class="detail-row">
               <span>Interest Frequency:</span>
               <strong>{{ selectedScheme.interestFrequency }}</strong>
             </div>
             <div class="detail-row">
               <span>Premature Break:</span>
               <span
                 :class="[
                   'status-badge',
                   selectedScheme.prematureBreakAllowed ? 'status-allowed' : 'status-blocked',
                 ]"
               >
                 {{ selectedScheme.prematureBreakAllowed ? 'Allowed' : 'Not Allowed' }}
               </span>
             </div>
             <div class="detail-row highlight">
               <span>Estimated Maturity Amount:</span>
               <strong>{{ formatCurrency(calculateMaturity()) }}</strong>
             </div>
           </div>
         </div>


         <div v-if="error" class="alert alert-error">{{ error }}</div>
         <div v-if="schemesError" class="alert alert-error">{{ schemesError }}</div>
         <div v-if="loadingSchemes" class="alert alert-info">Loading schemes...</div>


         <button type="submit" class="btn btn-primary btn-full" :disabled="loading || !isFormValid || loadingSchemes">
           <span v-if="loading" class="spinner"></span>
           <span v-else>Book FD</span>
         </button>
       </form>


       <div class="info-panel card">
         <h3>Important Information</h3>
         <ul>
           <li>✓ Minimum amount ₹5,000</li>
           <li>✓ Interest is compounded based on scheme</li>
           <li>✓ Premature withdrawal depends on scheme</li>
           <li>✓ Auto-renewal option available</li>
           <li>✓ Loan facility up to 90% of FD value</li>
         </ul>


         <h3 class="mt-3">Available Schemes</h3>
         <div v-if="loadingSchemes" class="loading-text">Loading...</div>
         <div v-else-if="schemes.length === 0" class="empty-schemes">
           <p>No schemes available. Please try again later.</p>
         </div>
         <div v-else class="rates-table">
           <div v-for="scheme in schemes" :key="scheme.name" class="rate-row">
             <span>{{ formatSchemeName(scheme.name) }}:</span>
             <strong>{{ scheme.annualInterestRate }}% p.a.</strong>
           </div>
         </div>
       </div>
     </div>
     </div>
   </div>
   <Footer />
 </div>
</template>


<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter, useRoute } from 'vue-router';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { formatCurrency } from '@/utils/helpers';
import { BookFDRequest, SchemeResponse, InterestScheme } from '@/types';
import { fdService } from '@/services/fdService';


const store = useStore();
const router = useRouter();
const route = useRoute();


const schemes = ref<SchemeResponse[]>([]);
const loadingSchemes = ref(false);
const schemesError = ref('');


type BookFDForm = {
 amount: number;
 tenureMonths: number;
 interestScheme: string;
};


const formData = ref<BookFDForm>({
 amount: 10000,
 tenureMonths: 0,
 interestScheme: '',
});


const errors = ref({
 amount: '',
 tenureMonths: '',
});


const maturityPreview = ref(false);
const loading = computed(() => store.getters['fd/loading']);
const error = computed(() => store.getters['fd/error']);


const selectedScheme = computed(() => {
 return schemes.value.find((s: SchemeResponse) => s.name === formData.value.interestScheme);
});


const isFormValid = computed(() => {
 return (
   !!formData.value.interestScheme &&
   formData.value.amount >= 5000 &&
   formData.value.tenureMonths > 0
 );
});


const formatSchemeName = (name: string): string => {
 return name.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
};


const onSchemeChange = () => {
 if (selectedScheme.value) {
   formData.value.tenureMonths = selectedScheme.value.tenureInMonths;
   maturityPreview.value = true;
 }
};


const calculateMaturity = (): number => {
 if (!selectedScheme.value) return 0;
  const P = formData.value.amount;
 const r = selectedScheme.value.annualInterestRate / 100;
  // Determine compounding frequency
 let n = 12; // Default monthly
 if (selectedScheme.value.interestFrequency === 'QUARTERLY') {
   n = 4;
 } else if (selectedScheme.value.interestFrequency === 'YEARLY') {
   n = 1;
 }
  const t = formData.value.tenureMonths / 12;
  return P * Math.pow(1 + r / n, n * t);
};


const loadSchemes = async () => {
 loadingSchemes.value = true;
 schemesError.value = '';
 try {
   schemes.value = await fdService.getAllSchemes();
  
   // Pre-fill scheme from query param
   if (route.query.scheme && schemes.value.some((s: SchemeResponse) => s.name === route.query.scheme)) {
     formData.value.interestScheme = route.query.scheme as string;
     onSchemeChange();
   }
 } catch (err) {
   console.error('Failed to load schemes:', err);
   schemesError.value = 'Unable to load schemes. Please refresh the page.';
 } finally {
   loadingSchemes.value = false;
 }
};


watch([() => formData.value.amount, () => formData.value.interestScheme], () => {
 maturityPreview.value = isFormValid.value;
});


const handleBookFD = async () => {
 if (!isFormValid.value) return;


 try {
   const payload: BookFDRequest = {
     amount: formData.value.amount,
     interestScheme: formData.value.interestScheme as InterestScheme,
   };


   const fd = await store.dispatch('fd/bookFD', payload);
   alert(`FD booked successfully! FD ID: ${fd.id}`);
   router.push('/user/fd-list');
 } catch (err) {
   console.error('Failed to book FD:', err);
 }
};


onMounted(() => {
 loadSchemes();
});
</script>


<style scoped lang="scss">
.page {
 min-height: 100vh;
 display: flex;
 flex-direction: column;
}


.page-content {
 flex: 1;
 padding: var(--spacing-3xl) var(--spacing-lg);
}


.user-content { min-width: 0; }


.page-header {
 display: flex;
 justify-content: space-between;
 align-items: center;
 gap: var(--spacing-xl);
 padding: var(--spacing-2xl);
 margin-bottom: var(--spacing-2xl);
 background: var(--zeta-gradient-hero);
 color: white;
 border-radius: var(--radius-xl);
}


.hero-card {
     box-shadow: 0 24px 55px rgba(15, 23, 42, 0.18);
 position: relative;
 overflow: hidden;
}


.hero-card::after {
 content: '';
 position: absolute;
 inset: 0;
 background: linear-gradient(120deg, rgba(15, 23, 42, 0.15), rgba(15, 23, 42, 0));
 pointer-events: none;
}


.hero-text h1 {
 margin: 0 0 var(--spacing-sm) 0;
 font-size: var(--font-size-3xl);
 color: white;
 text-shadow: 0 6px 20px rgba(15, 23, 42, 0.35);
}


.hero-text p {
 margin: 0;
 color: rgba(255, 255, 255, 0.9);
 text-shadow: 0 4px 16px rgba(15, 23, 42, 0.25);
}


.hero-badge {
     background: rgba(255, 255, 255, 0.18);
     border: 1px solid rgba(255, 255, 255, 0.45);
 padding: var(--spacing-sm) var(--spacing-lg);
 border-radius: 999px;
     font-weight: 700;
     letter-spacing: 0.3px;
     text-transform: uppercase;
     box-shadow: 0 0 18px rgba(255, 255, 255, 0.3);
}






.book-fd-container {
 display: grid;
 grid-template-columns: 2fr 1fr;
 gap: var(--spacing-xl);


 @media (max-width: 968px) {
   grid-template-columns: 1fr;
 }
}


.fd-form {
 padding: var(--spacing-2xl);
 border-radius: var(--radius-xl);
     border: 1px solid rgba(148, 163, 184, 0.2);
     background: linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.96));
     box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
     position: relative;
     overflow: hidden;
}


   .fd-form::after {
     content: '';
     position: absolute;
     inset: 0;
     background: radial-gradient(circle at top right, rgba(14, 116, 144, 0.12), transparent 60%);
     pointer-events: none;
   }


.maturity-preview {
     background: linear-gradient(135deg, #0ea5a4, #22c55e);
 padding: var(--spacing-lg);
 border-radius: var(--radius-lg);
 margin-bottom: var(--spacing-lg);
 color: white;
     box-shadow: 0 18px 32px rgba(15, 23, 42, 0.18);
     position: relative;
     overflow: hidden;




   .maturity-preview::after {
     content: '';
     position: absolute;
     inset: 0;
     background: radial-gradient(circle at top right, rgba(255, 255, 255, 0.25), transparent 55%);
     pointer-events: none;
   }
 h3 {
   margin-bottom: var(--spacing-md);
 }


 .preview-details {
   .detail-row {
     display: flex;
     justify-content: space-between;
     padding: var(--spacing-sm) 0;
     border-bottom: 1px solid rgba(255, 255, 255, 0.2);


     &.highlight {
       font-size: var(--font-size-lg);
       border-bottom: none;
       margin-top: var(--spacing-sm);
       padding-top: var(--spacing-md);
       border-top: 2px solid rgba(255, 255, 255, 0.5);
     }
   }
 }
}


.info-panel {
 padding: var(--spacing-xl);
 border-radius: var(--radius-xl);
     border: 1px solid rgba(148, 163, 184, 0.2);
     background: linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.96));
     box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
     position: relative;
     overflow: hidden;




   .info-panel::after {
     content: '';
     position: absolute;
     inset: 0;
     background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.14), transparent 60%);
     pointer-events: none;
   }
 h3 {
   color: var(--zeta-primary);
   margin-bottom: var(--spacing-md);
 }


 ul {
   list-style: none;
   padding: 0;


   li {
     padding: var(--spacing-sm) 0;
     color: var(--zeta-text-secondary);
   }
 }


 .rates-table {
   background: var(--zeta-background);
   padding: var(--spacing-md);
   border-radius: var(--radius-md);
       box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.2);


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
     box-shadow: 0 0 18px rgba(34, 197, 94, 0.5);
   }


   .status-allowed {
     background: linear-gradient(135deg, #16a34a, #22c55e);
   }


   .status-blocked {
     background: linear-gradient(135deg, #ef4444, #f87171);
     box-shadow: 0 0 18px rgba(239, 68, 68, 0.5);
   }
   .rate-row {
     display: flex;
     justify-content: space-between;
     padding: var(--spacing-sm) 0;
     border-bottom: 1px solid var(--zeta-divider);


     &:last-child {
       border-bottom: none;
     }


     strong {
       color: var(--zeta-accent);
     }
   }
 }
}


.btn-full {
 width: 100%;
}


.empty-schemes {
 padding: var(--spacing-md);
 color: var(--zeta-text-secondary);
}


.alert {
 padding: var(--spacing-md);
 border-radius: var(--radius-md);
 margin-bottom: var(--spacing-lg);
}


.alert-error {
 background: var(--zeta-error-light);
 color: var(--zeta-error-dark);
}
</style>



