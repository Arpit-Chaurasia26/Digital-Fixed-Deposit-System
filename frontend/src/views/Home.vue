<template>
  <div class="home-page">
    <Navbar />
    
    <!-- Hero Section -->
    <section class="hero">
      <div class="container hero-content">
        <div class="hero-text slide-in-up">
          <h1>Secure Your Future with <span class="highlight">Zeta FD</span></h1>
          <p class="hero-subtitle">
            Experience high returns, guaranteed safety, and flexible investment options
            with India's most trusted fixed deposit platform
          </p>
          <div class="hero-cta">
            <router-link to="/register" class="btn btn-accent btn-lg">Get Started</router-link>
            <router-link to="/about" class="btn btn-outline btn-lg">Learn More</router-link>
          </div>
          <div class="hero-stats">
            <div class="stat">
              <h3>₹500Cr+</h3>
              <p>Total Deposits</p>
            </div>
            <div class="stat">
              <h3>50,000+</h3>
              <p>Happy Investors</p>
            </div>
            <div class="stat">
              <h3>8.5%</h3>
              <p>Interest Rate</p>
            </div>
          </div>
        </div>
        <div class="hero-image fade-in">
          <div class="floating-card">💰</div>
          <div class="floating-card delay-1">📈</div>
          <div class="floating-card delay-2">🏦</div>
        </div>
      </div>
    </section>

    <!-- Maturity Preview Section -->
    <section class="calculator-section" id="preview">
      <div class="container">
        <div class="section-header">
          <h2>Estimate Your Maturity Value</h2>
          <p>Select a scheme and amount to preview your returns</p>
        </div>
        <div class="calculator-grid">
          <div class="calculator-card card">
            <h3>Quick Preview</h3>
            <div class="form-group">
              <label>Select Scheme</label>
              <select v-model="selectedSchemeName" class="form-control">
                <option value="">Choose a scheme</option>
                <option v-for="scheme in fdSchemes" :key="scheme.schemeName" :value="scheme.schemeName">
                  {{ scheme.name }} ({{ scheme.annualInterestRate }}% • {{ scheme.tenureInMonths }} months)
                </option>
              </select>
            </div>
            <div class="form-group">
              <label>Amount (₹)</label>
              <input
                v-model.number="amount"
                type="number"
                class="form-control"
                min="5000"
                placeholder="Enter amount"
              />
              <small class="form-help">Minimum ₹5,000</small>
            </div>
            <div class="form-group">
              <label>Tenure</label>
              <input
                type="text"
                class="form-control"
                :value="selectedScheme ? `${selectedScheme.tenureInMonths} months` : ''"
                disabled
              />
            </div>
            <router-link v-if="isAuthenticated" to="/user/book-fd" class="btn btn-primary btn-full">
              Book a Fixed Deposit
            </router-link>
            <router-link v-else to="/login" class="btn btn-primary btn-full">
              Login to Book
            </router-link>
            <p v-if="loadingSchemes" class="loading-message">Loading schemes...</p>
          </div>

          <div class="preview-card card">
            <h3>Maturity Preview</h3>
            <div v-if="showPreview" class="preview-content">
              <div class="preview-row">
                <span>Principal</span>
                <strong>{{ formatCurrency(amount) }}</strong>
              </div>
              <div class="preview-row">
                <span>Interest Rate</span>
                <strong>{{ selectedScheme?.annualInterestRate }}% p.a.</strong>
              </div>
              <div class="preview-row">
                <span>Interest Frequency</span>
                <strong>{{ selectedScheme?.interestFrequency }}</strong>
              </div>
              <div class="preview-row">
                <span>Premature Break</span>
                <strong>{{ selectedScheme?.prematureBreakAllowed ? 'Allowed' : 'Not Allowed' }}</strong>
              </div>
              <div class="preview-row highlight">
                <span>Estimated Maturity</span>
                <strong>{{ formatCurrency(maturityAmount) }}</strong>
              </div>
            </div>
            <div v-else class="preview-empty">
              <p>Select a scheme and amount to see your estimated returns.</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- FD Schemes Section -->
    <section class="schemes-section" id="schemes">
      <div class="container">
        <div class="section-header">
          <h2>Our Fixed Deposit Schemes</h2>
          <p>Choose the plan that best fits your investment goals</p>
        </div>
        
        <div class="schemes-grid">
          <div v-if="loadingSchemes" class="loading-message">
            <p>Loading schemes...</p>
          </div>
          <div v-else-if="fdSchemes.length === 0" class="no-schemes-message">
            <p>No schemes available at the moment.</p>
          </div>
          <div
            v-else
            v-for="(scheme, index) in fdSchemes"
            :key="index"
            class="scheme-card card"
            :id="`scheme-${scheme.schemeName}`"
            :style="{ animationDelay: `${index * 0.1}s` }"
          >
            <div class="scheme-icon">{{ scheme.icon }}</div>
            <h3>{{ scheme.name }}</h3>
            <div class="scheme-rate">{{ scheme.annualInterestRate }}% p.a.</div>
            <p class="scheme-description">{{ scheme.description }}</p>
            <ul class="scheme-features">
              <li v-for="(feature, idx) in scheme.features" :key="idx">✓ {{ feature }}</li>
            </ul>
            <div class="scheme-details">
              <div class="detail">
                <span class="label">Tenure:</span>
                <span class="value">{{ scheme.tenureInMonths }} months</span>
              </div>
              <div class="detail">
                <span class="label">Interest Payout:</span>
                <span class="value">{{ scheme.interestFrequency }}</span>
              </div>
              <div class="detail">
                <span class="label">Premature Break:</span>
                <span class="value">{{ scheme.prematureBreakAllowed ? 'Allowed' : 'Not Allowed' }}</span>
              </div>
            </div>
            <router-link v-if="isAuthenticated" :to="`/user/book-fd?scheme=${scheme.schemeName}`" class="btn btn-primary btn-full">
              Book Now
            </router-link>
            <router-link v-else to="/login" class="btn btn-primary btn-full">
              Login to Book
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="container">
        <h2 class="text-center">Why Choose Zeta FD?</h2>
        <div class="features-grid">
          <div class="feature-card fade-in">
            <div class="feature-icon">🔒</div>
            <h3>100% Secure</h3>
            <p>Your deposits are insured and protected by government regulations</p>
          </div>
          <div class="feature-card fade-in">
            <div class="feature-icon">💹</div>
            <h3>High Returns</h3>
            <p>Competitive interest rates up to 8.5% per annum</p>
          </div>
          <div class="feature-card fade-in">
            <div class="feature-icon">⚡</div>
            <h3>Instant Processing</h3>
            <p>Quick and hassle-free FD booking within minutes</p>
          </div>
          <div class="feature-card fade-in">
            <div class="feature-icon">📱</div>
            <h3>Easy Management</h3>
            <p>Track and manage all your FDs from one dashboard</p>
          </div>
        </div>
      </div>
    </section>

    <!-- FAQ Section -->
    <section class="faq-section" id="faq">
      <div class="container">
        <h2 class="text-center">Frequently Asked Questions</h2>
        <div class="faq-list">
          <div v-for="(faq, index) in faqs" :key="index" class="faq-item">
            <button @click="toggleFaq(index)" class="faq-question">
              <span>{{ faq.question }}</span>
              <span class="faq-icon">{{ openFaq === index ? '−' : '+' }}</span>
            </button>
            <div v-show="openFaq === index" class="faq-answer">
              {{ faq.answer }}
            </div>
          </div>
        </div>
      </div>
    </section>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import { FDSchemeCard, SchemeResponse } from '@/types';
import { fdService } from '@/services/fdService';
import { formatCurrency } from '@/utils/helpers';

const store = useStore();
const openFaq = ref<number | null>(null);

const isAuthenticated = computed(() => store.getters['auth/isAuthenticated']);

const fdSchemes = ref<FDSchemeCard[]>([]);
const loadingSchemes = ref(false);
const selectedSchemeName = ref('');
const amount = ref(10000);

const selectedScheme = computed(() =>
  fdSchemes.value.find((scheme) => scheme.schemeName === selectedSchemeName.value)
);

const showPreview = computed(() => !!selectedScheme.value && amount.value >= 5000);

const maturityAmount = computed(() => {
  if (!showPreview.value || !selectedScheme.value) return 0;
  const principal = amount.value;
  const rate = selectedScheme.value.annualInterestRate / 100;
  let n = 12;
  if (selectedScheme.value.interestFrequency === 'QUARTERLY') {
    n = 4;
  } else if (selectedScheme.value.interestFrequency === 'YEARLY') {
    n = 1;
  }
  const t = selectedScheme.value.tenureInMonths / 12;
  return principal * Math.pow(1 + rate / n, n * t);
});

const schemeIcons: { [key: string]: string } = {
  'STANDARD_6_MONTHS': '🏦',
  'STANDARD_12_MONTHS': '📈',
  'SENIOR_CITIZEN_12_MONTHS': '👴',
  'TAX_SAVER_5_YEARS': '🏢'
};

const schemeDescriptions: { [key: string]: string } = {
  'STANDARD_6_MONTHS': 'Perfect for short-term savings with flexible tenure and good returns',
  'STANDARD_12_MONTHS': 'Ideal for medium-term investment with higher interest rates',
  'SENIOR_CITIZEN_12_MONTHS': 'Exclusive benefits for senior citizens above 60 years of age',
  'TAX_SAVER_5_YEARS': 'Long-term investment with tax benefits under Section 80C'
};

const schemeFeatures: { [key: string]: string[] } = {
  'STANDARD_6_MONTHS': [
    'Short tenure - 6 months',
    'Monthly interest payout',
    'Premature withdrawal allowed',
    'Loan facility available'
  ],
  'STANDARD_12_MONTHS': [
    'Flexible 12-month tenure',
    'Monthly interest payout',
    'Premature withdrawal allowed',
    'Higher returns than 6 months'
  ],
  'SENIOR_CITIZEN_12_MONTHS': [
    'Highest interest rates',
    'Monthly interest payout',
    'Priority support',
    'Flexible payout options'
  ],
  'TAX_SAVER_5_YEARS': [
    'Tax benefits under 80C',
    'Yearly interest payout',
    'No premature withdrawal',
    'Long-term wealth creation'
  ]
};

const loadSchemes = async () => {
  loadingSchemes.value = true;
  try {
    const schemesData = await fdService.getAllSchemes();
    fdSchemes.value = schemesData.map((scheme: SchemeResponse) => ({
      name: formatSchemeName(scheme.name),
      schemeName: scheme.name,
      description: schemeDescriptions[scheme.name] || 'Secure your future with guaranteed returns',
      annualInterestRate: scheme.annualInterestRate,
      tenureInMonths: scheme.tenureInMonths,
      interestFrequency: scheme.interestFrequency,
      prematureBreakAllowed: scheme.prematureBreakAllowed,
      features: schemeFeatures[scheme.name] || [
        'Competitive interest rates',
        'Secure investment',
        'Easy management',
        'Trusted platform'
      ],
      icon: schemeIcons[scheme.name] || '💰'
    }));
  } catch (err) {
    console.error('Failed to load schemes:', err);
    // Fallback to empty array if API fails
    fdSchemes.value = [];
  } finally {
    loadingSchemes.value = false;
  }
};

const formatSchemeName = (name: string): string => {
  return name.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
};

const faqs = [
  {
    question: 'What is the minimum amount required to open an FD?',
    answer: 'The minimum amount is ₹5,000 for most schemes. Please check individual scheme details for specific requirements.'
  },
  {
    question: 'Can I withdraw my FD before maturity?',
    answer: 'Yes, premature withdrawal is allowed for most schemes. However, a penalty will be applied on the interest rate, and you will receive reduced interest. Some schemes like Tax Saver do not allow premature withdrawal.'
  },
  {
    question: 'How is the interest calculated?',
    answer: 'Interest is calculated based on the principal amount, interest rate, and tenure. It can be paid monthly, quarterly, or yearly based on the scheme selected.'
  },
  {
    question: 'Is my deposit insured?',
    answer: 'Yes, all deposits are insured up to ₹5,00,000 per depositor as per DICGC guidelines.'
  },
  {
    question: 'Can I take a loan against my FD?',
    answer: 'Yes, you can avail a loan up to 90% of your FD value at competitive interest rates for eligible schemes.'
  },
  {
    question: 'What documents are required to open an FD?',
    answer: 'You need a valid ID proof (Aadhaar, PAN), address proof, and a recent photograph. Senior citizens need age proof.'
  }
];

const toggleFaq = (index: number) => {
  openFaq.value = openFaq.value === index ? null : index;
};

onMounted(() => {
  loadSchemes();
});
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100vh;
}

.hero {
  background: var(--zeta-gradient-hero);
  padding: var(--spacing-3xl) 0;
  min-height: 600px;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.hero-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-3xl);
  align-items: center;

  @media (max-width: 968px) {
    grid-template-columns: 1fr;
    text-align: center;
  }
}

.hero-text {
  h1 {
    font-size: var(--font-size-4xl);
    color: white;
    margin-bottom: var(--spacing-lg);
    line-height: 1.2;

    .highlight {
      color: var(--zeta-secondary);
    }
  }

  .hero-subtitle {
    font-size: var(--font-size-lg);
    color: rgba(255, 255, 255, 0.9);
    margin-bottom: var(--spacing-xl);
    line-height: 1.6;
  }
}

.hero-cta {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);

  @media (max-width: 968px) {
    justify-content: center;
  }

  .btn-lg {
    padding: var(--spacing-md) var(--spacing-xl);
    font-size: var(--font-size-lg);
  }

  .btn-outline {
    border-color: white;
    color: white;

    &:hover {
      background: white;
      color: var(--zeta-primary);
    }
  }
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-lg);
  margin-top: var(--spacing-2xl);

  .stat {
    text-align: center;

    h3 {
      font-size: var(--font-size-2xl);
      color: var(--zeta-secondary);
      margin-bottom: var(--spacing-xs);
    }

    p {
      color: rgba(255, 255, 255, 0.8);
      font-size: var(--font-size-sm);
    }
  }
}

.hero-image {
  position: relative;
  height: 400px;

  .floating-card {
    position: absolute;
    font-size: 80px;
    animation: float 3s ease-in-out infinite;

    &:nth-child(1) {
      top: 20%;
      left: 10%;
    }

    &:nth-child(2) {
      top: 50%;
      right: 20%;
      animation-delay: 1s;
    }

    &:nth-child(3) {
      bottom: 10%;
      left: 40%;
      animation-delay: 2s;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

.schemes-section {
  padding: var(--spacing-3xl) 0;
  background: var(--zeta-background);
}

.calculator-section {
  padding: var(--spacing-3xl) 0;
  background: white;
}

.calculator-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-xl);
  align-items: start;
}

.calculator-card,
.preview-card {
  padding: var(--spacing-2xl);
  border-radius: var(--radius-xl);
  border: 1px solid rgba(148, 163, 184, 0.2);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.96));
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
  position: relative;
  overflow: hidden;
}

.calculator-card::after,
.preview-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(14, 116, 144, 0.12), transparent 60%);
  pointer-events: none;
}

.calculator-card h3,
.preview-card h3 {
  margin-bottom: var(--spacing-lg);
  color: var(--zeta-primary);
}

.preview-content {
  display: grid;
  gap: var(--spacing-md);
}

.preview-row {
  display: flex;
  justify-content: space-between;
  padding: var(--spacing-sm) 0;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
  color: var(--zeta-text-secondary);
}

.preview-row strong {
  color: var(--zeta-text-primary);
}

.preview-row.highlight {
  border-bottom: none;
  padding-top: var(--spacing-md);
  margin-top: var(--spacing-sm);
  font-size: var(--font-size-lg);
  color: var(--zeta-primary);
}

.preview-empty {
  color: var(--zeta-text-secondary);
  background: rgba(148, 163, 184, 0.08);
  padding: var(--spacing-lg);
  border-radius: var(--radius-lg);
  text-align: center;
}

.section-header {
  text-align: center;
  margin-bottom: var(--spacing-2xl);

  h2 {
    font-size: var(--font-size-3xl);
    color: var(--zeta-primary);
    margin-bottom: var(--spacing-sm);
  }

  p {
    color: var(--zeta-text-secondary);
    font-size: var(--font-size-lg);
  }
}

.schemes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-xl);
}

.scheme-card {
  animation: slideInUp var(--transition-base);
  padding: var(--spacing-xl);
  transition: all var(--transition-base);

  &:hover {
    transform: translateY(-8px);
  }

  .scheme-icon {
    font-size: 60px;
    margin-bottom: var(--spacing-md);
  }

  h3 {
    color: var(--zeta-primary);
    margin-bottom: var(--spacing-sm);
  }

  .scheme-rate {
    font-size: var(--font-size-2xl);
    font-weight: 700;
    color: var(--zeta-accent);
    margin-bottom: var(--spacing-md);
  }

  .scheme-description {
    color: var(--zeta-text-secondary);
    margin-bottom: var(--spacing-lg);
    line-height: 1.6;
  }

  .scheme-features {
    list-style: none;
    padding: 0;
    margin-bottom: var(--spacing-lg);

    li {
      padding: var(--spacing-xs) 0;
      color: var(--zeta-text-secondary);
    }
  }

  .scheme-details {
    background: var(--zeta-background);
    padding: var(--spacing-md);
    border-radius: var(--radius-md);
    margin-bottom: var(--spacing-lg);

    .detail {
      display: flex;
      justify-content: space-between;
      padding: var(--spacing-xs) 0;

      .label {
        color: var(--zeta-text-secondary);
        font-size: var(--font-size-sm);
      }

      .value {
        font-weight: 600;
        color: var(--zeta-text-primary);
      }
    }
  }
}

.features-section {
  padding: var(--spacing-3xl) 0;
  background: white;

  h2 {
    font-size: var(--font-size-3xl);
    color: var(--zeta-primary);
    margin-bottom: var(--spacing-2xl);
  }
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-xl);
}

.feature-card {
  text-align: center;
  padding: var(--spacing-xl);

  .feature-icon {
    font-size: 60px;
    margin-bottom: var(--spacing-md);
  }

  h3 {
    color: var(--zeta-primary);
    margin-bottom: var(--spacing-sm);
  }

  p {
    color: var(--zeta-text-secondary);
    line-height: 1.6;
  }
}

.faq-section {
  padding: var(--spacing-3xl) 0;
  background: var(--zeta-background);

  h2 {
    font-size: var(--font-size-3xl);
    color: var(--zeta-primary);
    margin-bottom: var(--spacing-2xl);
  }
}

.faq-list {
  max-width: 800px;
  margin: 0 auto;
}

.faq-item {
  background: white;
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-md);
  box-shadow: var(--shadow-sm);

  .faq-question {
    width: 100%;
    text-align: left;
    padding: var(--spacing-lg);
    background: none;
    border: none;
    cursor: pointer;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: var(--font-size-lg);
    font-weight: 600;
    color: var(--zeta-primary);
    transition: background var(--transition-fast);

    &:hover {
      background: var(--zeta-background-hover);
    }

    .faq-icon {
      font-size: var(--font-size-2xl);
      font-weight: 300;
    }
  }

  .faq-answer {
    padding: 0 var(--spacing-lg) var(--spacing-lg);
    color: var(--zeta-text-secondary);
    line-height: 1.6;
    animation: slideInUp var(--transition-fast);
  }
}

.btn-full {
  width: 100%;
}
</style>
