<template>
  <div class="legal-page">
    <Navbar />
    <main class="container legal-content">
      <header class="legal-hero card">
        <h1>Privacy Policy</h1>
        <p>Last updated: 14 February 2026</p>
      </header>

      <section class="card legal-section">
        <h2>1. Information We Collect</h2>
        <p>
          We collect account, KYC, transaction, and support information needed to provide fixed deposit services,
          secure your account, and meet legal obligations.
        </p>
      </section>

      <section class="card legal-section">
        <h2>2. How We Use Information</h2>
        <ul>
          <li>To open and manage your fixed deposits</li>
          <li>To process withdrawals, interest payouts, and support requests</li>
          <li>To send critical account notifications and service updates</li>
          <li>To prevent fraud, abuse, and unauthorized activity</li>
        </ul>
      </section>

      <section class="card legal-section">
        <h2>3. Data Security</h2>
        <p>
          We use industry-standard access controls, encryption practices, and monitoring controls to protect user
          data. Access is restricted to authorized personnel only.
        </p>
      </section>

      <section class="card legal-section">
        <h2>4. Data Sharing</h2>
        <p>
          We do not sell personal information. Data is shared only with regulated service partners or authorities
          when required by law.
        </p>
      </section>

      <section class="card legal-section">
        <h2>5. Contact</h2>
        <p>
          For privacy concerns, contact us at <a href="mailto:digitalfd.system@gmail.com">digitalfd.system@gmail.com</a> or call
          <a href="tel:+9118001234567">+91 1800-123-4567</a>.
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
