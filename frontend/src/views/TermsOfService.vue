<template>
  <div class="legal-page">
    <Navbar />
    <main class="container legal-content">
      <header class="legal-hero card">
        <h1>Terms of Service</h1>
        <p>Last updated: 14 February 2026</p>
      </header>

      <section class="card legal-section">
        <h2>1. Acceptance of Terms</h2>
        <p>
          By using Zeta Fixed Deposit services, you agree to these terms, all applicable regulations, and platform
          policies.
        </p>
      </section>

      <section class="card legal-section">
        <h2>2. Account Responsibility</h2>
        <ul>
          <li>You must provide accurate information and complete KYC requirements</li>
          <li>You are responsible for protecting your account credentials</li>
          <li>Unauthorized or fraudulent usage may lead to account suspension</li>
        </ul>
      </section>

      <section class="card legal-section">
        <h2>3. Fixed Deposit Operations</h2>
        <p>
          Interest rates, maturity terms, early withdrawal penalties, and product rules are shown at booking time.
          Transactions are processed according to the selected FD product policy.
        </p>
      </section>

      <section class="card legal-section">
        <h2>4. Limitation of Liability</h2>
        <p>
          We are not liable for losses caused by incorrect user input, external banking outages, force majeure events,
          or violation of these terms.
        </p>
      </section>

      <section class="card legal-section">
        <h2>5. Contact</h2>
        <p>
          For service-related concerns, contact <a href="mailto:digitalfd.system@gmail.com">digitalfd.system@gmail.com</a>.
        </p>
      </section>
    </main>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
</script>

<style scoped lang="scss">
.legal-page { min-height: 100vh; display: flex; flex-direction: column; }
.legal-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); max-width: 980px; }
.legal-hero { padding: var(--spacing-2xl); margin-bottom: var(--spacing-xl); }
.legal-hero h1 { margin: 0 0 var(--spacing-sm); color: var(--zeta-primary); }
.legal-hero p { margin: 0; color: var(--zeta-text-secondary); }
.legal-section { padding: var(--spacing-xl); margin-bottom: var(--spacing-lg); }
.legal-section h2 { margin: 0 0 var(--spacing-md); color: var(--zeta-primary); }
.legal-section p, .legal-section li { color: var(--zeta-text-secondary); line-height: 1.75; }
.legal-section ul { margin: 0; padding-left: 1.2rem; }
</style>
