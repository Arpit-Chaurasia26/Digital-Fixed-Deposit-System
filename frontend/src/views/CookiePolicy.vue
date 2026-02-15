<template>
  <div class="legal-page">
    <Navbar />
    <main class="container legal-content">
      <header class="legal-hero card">
        <h1>Cookie Policy</h1>
        <p>Last updated: 14 February 2026</p>
      </header>

      <section class="card legal-section">
        <h2>1. What Are Cookies</h2>
        <p>
          Cookies are small text files stored on your device to keep sessions secure, remember settings, and improve
          user experience.
        </p>
      </section>

      <section class="card legal-section">
        <h2>2. Cookies We Use</h2>
        <ul>
          <li><strong>Essential:</strong> login session and security controls</li>
          <li><strong>Preference:</strong> UI preferences such as theme mode</li>
          <li><strong>Performance:</strong> aggregate diagnostics for reliability</li>
        </ul>
      </section>

      <section class="card legal-section">
        <h2>3. Managing Cookies</h2>
        <p>
          You can manage cookie preferences in your browser settings. Disabling essential cookies may affect platform
          functionality.
        </p>
      </section>

      <section class="card legal-section">
        <h2>4. Contact</h2>
        <p>
          For questions about this cookie policy, email
          <a href="mailto:digitalfd.system@gmail.com">digitalfd.system@gmail.com</a>.
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
