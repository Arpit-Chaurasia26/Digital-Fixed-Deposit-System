<template>
  <div class="about-page">
    <Navbar />
    <div class="container about-content">
      <section class="about-hero card">
        <div class="hero-copy">
          <p class="kicker">ABOUT US</p>
          <h1>Trusted Digital Infrastructure for Fixed Deposit Operations</h1>
          <p class="subtitle">
            Zeta FD is designed for secure, transparent, and scalable deposit management —
            from booking to maturity and controlled withdrawal workflows.
          </p>
          <div class="hero-actions">
            <router-link to="/register" class="btn btn-primary">Open Account</router-link>
            <router-link to="/contact" class="btn btn-outline">Talk to Support</router-link>
          </div>
        </div>
        <div class="hero-metrics">
          <div class="metric-card"><strong>99.9%</strong><span>Platform uptime</span></div>
          <div class="metric-card"><strong>&lt; 2 hrs</strong><span>Priority response</span></div>
          <div class="metric-card"><strong>Bank-grade</strong><span>Security controls</span></div>
          <div class="metric-card"><strong>Realtime</strong><span>Portfolio insights</span></div>
        </div>
      </section>

      <section class="about-section two-col">
        <article class="card panel">
          <h2>Who We Are</h2>
          <p>
            We are a product-focused finance technology team building dependable fixed deposit tools for everyday
            users, support teams, and administrators.
          </p>
        </article>
        <article class="card panel">
          <h2>Our Mission</h2>
          <p>
            Deliver a professional, policy-driven platform that makes savings operations simpler while preserving
            compliance, clarity, and customer trust.
          </p>
        </article>
      </section>

      <section class="about-section">
        <h2>Why Customers Choose Zeta FD</h2>
        <div class="features-grid">
          <article class="feature card"><h3>Secure by Design</h3><p>Role-based access, protected sessions, and controlled transaction actions.</p></article>
          <article class="feature card"><h3>Transparent Returns</h3><p>Clear maturity, accrued interest, and withdrawal visibility at every step.</p></article>
          <article class="feature card"><h3>Operational Speed</h3><p>Fast booking and workflow-driven operations for users and admins.</p></article>
          <article class="feature card"><h3>Support Reliability</h3><p>Tracked support tickets with measurable response and resolution timelines.</p></article>
        </div>
      </section>

      <section class="about-section compliance card">
        <h2>Operating Principles</h2>
        <div class="principles-grid">
          <div><strong>Clarity</strong><span>Policy-first communication and transparent terms.</span></div>
          <div><strong>Security</strong><span>Defensive controls for account and transaction safety.</span></div>
          <div><strong>Reliability</strong><span>Stable architecture for consistent day-to-day operations.</span></div>
          <div><strong>Accountability</strong><span>Traceable actions and auditable service workflows.</span></div>
        </div>
      </section>

      <section class="about-section cta-section">
        <h2>Start Investing with Confidence</h2>
        <p>Experience a professional fixed deposit platform built for trust and long-term growth.</p>
        <div class="cta-buttons">
          <router-link to="/register" class="btn btn-primary btn-lg">Open Account</router-link>
          <router-link to="/contact" class="btn btn-outline btn-lg">Contact Support</router-link>
        </div>
      </section>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
</script>

<style scoped lang="scss">
.about-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.about-content {
  flex: 1;
  padding: var(--spacing-3xl) var(--spacing-lg);
  max-width: 1150px;
  margin: 0 auto;
}

.about-hero {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: var(--spacing-xl);
  padding: var(--spacing-2xl);
  margin-bottom: var(--spacing-2xl);
  background: var(--zeta-gradient-hero);
  color: white;
  box-shadow: var(--shadow-lg);
}

.kicker {
  margin: 0 0 var(--spacing-sm);
  font-size: var(--font-size-sm);
  font-weight: 700;
  letter-spacing: 0.08em;
  color: rgba(255, 255, 255, 0.86);
}

.hero-copy h1 {
  margin: 0 0 var(--spacing-md);
  font-size: clamp(1.8rem, 2.5vw, 2.5rem);
  color: #ffffff;
}

.subtitle { color: rgba(255, 255, 255, 0.92); line-height: 1.75; margin: 0; }

.hero-actions {
  margin-top: var(--spacing-lg);
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.hero-metrics {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
}

.metric-card {
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-lg);
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.metric-card strong { color: #ffffff; font-size: var(--font-size-lg); }
.metric-card span { color: rgba(255, 255, 255, 0.88); font-size: var(--font-size-sm); }

.about-section {
  margin-bottom: var(--spacing-3xl);

  h2 {
    font-size: var(--font-size-2xl);
    color: var(--zeta-primary);
    margin-bottom: var(--spacing-lg);
  }

  p {
    color: var(--zeta-text-secondary);
    line-height: 1.8;
    font-size: var(--font-size-lg);
  }
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-lg);
}

.panel { padding: var(--spacing-xl); }

.panel,
.feature,
.compliance {
  box-shadow: var(--shadow-md);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: var(--spacing-lg);
  margin-top: var(--spacing-xl);
}

.feature {
  padding: var(--spacing-lg);
  border: 1px solid var(--zeta-divider);
  transition: all var(--transition-base);

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-md);
  }

  h3 {
    margin: 0 0 var(--spacing-sm);
    color: var(--zeta-primary-dark);
    margin-bottom: var(--spacing-sm);
    font-size: var(--font-size-lg);
  }

  p {
    margin: 0;
    color: var(--zeta-text-secondary);
    font-size: var(--font-size-base);
  }
}

.compliance {
  padding: var(--spacing-xl);
}

.principles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: var(--spacing-md);
}

.principles-grid div {
  border: 1px solid var(--zeta-divider);
  border-radius: var(--radius-md);
  padding: 12px;
  display: grid;
  gap: 6px;
  background: var(--zeta-surface);
}

.principles-grid strong {
  color: var(--zeta-primary);
}

.principles-grid span {
  color: var(--zeta-text-secondary);
}

.cta-section {
  text-align: center;
  background: var(--zeta-gradient-hero);
  padding: var(--spacing-3xl);
  border-radius: var(--radius-xl);
  color: white;

  h2 {
    color: white;
    margin-bottom: var(--spacing-md);
  }

  p {
    color: rgba(255, 255, 255, 0.9);
    font-size: var(--font-size-xl);
    margin-bottom: var(--spacing-xl);
  }
}

.cta-buttons {
  display: flex;
  gap: var(--spacing-md);
  justify-content: center;
  flex-wrap: wrap;

  .btn-lg {
    padding: var(--spacing-md) var(--spacing-2xl);
    font-size: var(--font-size-lg);
  }

  .btn-outline {
    border-color: white;
    color: white;
    background: transparent;

    &:hover {
      background: white;
      color: var(--zeta-primary);
    }
  }
}

@media (max-width: 900px) {
  .about-hero,
  .two-col {
    grid-template-columns: 1fr;
  }

  .hero-metrics {
    grid-template-columns: 1fr;
  }
}

:global([data-theme='dark']) .hero-copy h1,
:global([data-theme='dark']) .feature h3,
:global([data-theme='dark']) .about-section h2,
:global([data-theme='dark']) .metric-card strong,
:global([data-theme='dark']) .principles-grid strong {
  color: #c7d2fe;
}

:global([data-theme='dark']) .hero-copy h1 {
  color: #ffffff;
}

:global(html[data-theme='dark']) .about-page .hero-copy h1 {
  color: #ffffff !important;
}

:global([data-theme='dark']) .subtitle,
:global([data-theme='dark']) .about-section p,
:global([data-theme='dark']) .feature p,
:global([data-theme='dark']) .metric-card span,
:global([data-theme='dark']) .principles-grid span {
  color: #d1d5db;
}

:global([data-theme='dark']) .about-hero.card,
:global([data-theme='dark']) .panel.card,
:global([data-theme='dark']) .feature.card,
:global([data-theme='dark']) .metric-card,
:global([data-theme='dark']) .compliance.card,
:global([data-theme='dark']) .principles-grid div {
  background: rgba(17, 24, 39, 0.9);
  border-color: rgba(255, 255, 255, 0.08);
}

:global([data-theme='dark']) .about-hero.card {
  background: var(--zeta-gradient-hero);
}

:global([data-theme='dark']) .about-hero .subtitle,
:global([data-theme='dark']) .about-hero .kicker,
:global([data-theme='dark']) .about-hero .metric-card span,
:global([data-theme='dark']) .about-hero .metric-card strong {
  color: #ffffff;
}
</style>
