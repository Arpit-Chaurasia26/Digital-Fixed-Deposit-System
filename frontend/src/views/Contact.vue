<template>
  <div class="contact-page">
    <Navbar />
    <div class="container contact-content">
      <section class="contact-hero card">
        <div class="hero-copy">
          <p class="hero-kicker">CONTACT & SUPPORT</p>
          <h1>Enterprise-Grade Customer Assistance</h1>
          <p>
            Connect with our support desk for account help, FD operations, maturity questions,
            or withdrawal-related assistance. We maintain clear response timelines and professional handling.
          </p>

          <div class="hero-actions">
            <a href="tel:+9118001234567" class="btn btn-primary">Call Support</a>
            <a href="mailto:digitalfd.system@gmail.com" class="btn btn-outline">Email Us</a>
          </div>
        </div>
        <div class="hero-meta">
          <div class="hero-chip">24/7 Monitoring</div>
          <div class="meta-grid">
            <div class="meta-item">
              <strong>Priority</strong>
              <span>High</span>
            </div>
            <div class="meta-item">
              <strong>First Response</strong>
              <span>&lt; 2 Hours</span>
            </div>
            <div class="meta-item">
              <strong>Escalation</strong>
              <span>Available</span>
            </div>
            <div class="meta-item">
              <strong>Availability</strong>
              <span>Mon - Sun</span>
            </div>
          </div>
        </div>
      </section>

      <section class="contact-grid">
        <article class="card contact-card">
          <h2>Support Channels</h2>
          <ul class="contact-list">
            <li><span>Email</span><a href="mailto:digitalfd.system@gmail.com">digitalfd.system@gmail.com</a></li>
            <li><span>Phone</span><a href="tel:+9118001234567">+91 1800-123-4567</a></li>
            <li><span>WhatsApp</span><a href="https://wa.me/9118001234567" target="_blank" rel="noopener noreferrer">Start Chat</a></li>
            <li>
              <span>Office</span>
              <a
                href="https://www.google.com/maps/search/?api=1&query=Sattva%20Knowledge%20Court%2C%2020%2C%2021%2C%20EPIP%20Zone%20Whitefield%20Rd%2C%20EPIP%20Zone%2C%20Brookefield%2C%20Bengaluru%2C%20Karnataka%20560048"
                target="_blank"
                rel="noopener noreferrer"
                class="office-link"
              >
                Sattva Knowledge Court, 20, 21, EPIP Zone Whitefield Rd, EPIP Zone, Brookefield, Bengaluru, Karnataka 560048
              </a>
            </li>
          </ul>
        </article>

        <article class="card contact-card">
          <h2>Service Window</h2>
          <ul class="hours-list">
            <li><span>Mon - Fri</span><strong>9:00 AM - 8:00 PM</strong></li>
            <li><span>Saturday</span><strong>10:00 AM - 6:00 PM</strong></li>
            <li><span>Sunday</span><strong>10:00 AM - 2:00 PM</strong></li>
            <li><span>Critical Alerts</span><strong>24/7</strong></li>
          </ul>
        </article>

        <article class="card contact-card">
          <h2>Issue Resolution</h2>
          <p class="muted">For account issues, FD details, or transaction queries, create a ticket for tracked resolution.</p>
          <router-link to="/user/support/create" class="btn btn-primary create-ticket-btn">Create Support Ticket</router-link>
          <div class="sla-box">
            <div><strong>P1</strong><span>Within 2 hours</span></div>
            <div><strong>P2</strong><span>Within 8 hours</span></div>
            <div><strong>P3</strong><span>Within 24 hours</span></div>
          </div>
        </article>
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
.contact-page { min-height: 100vh; display: flex; flex-direction: column; }
.contact-content {
  flex: 1;
  padding: var(--spacing-3xl) var(--spacing-lg);
  max-width: 1150px;
}

.contact-hero {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: var(--spacing-xl);
  padding: var(--spacing-2xl);
  background: var(--zeta-gradient-hero);
  color: white;
  border-radius: var(--radius-xl);
  margin-bottom: var(--spacing-2xl);
  box-shadow: var(--shadow-lg);
}

.hero-kicker {
  margin: 0 0 var(--spacing-sm);
  font-size: var(--font-size-sm);
  font-weight: 700;
  letter-spacing: 0.08em;
  color: rgba(255, 255, 255, 0.86);
}

.hero-copy h1 {
  margin: 0 0 var(--spacing-sm);
  font-size: clamp(1.8rem, 2.5vw, 2.4rem);
}

.hero-copy p {
  margin: 0;
  color: rgba(255, 255, 255, 0.92);
  line-height: 1.75;
}

.hero-actions {
  margin-top: var(--spacing-lg);
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.hero-chip {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: 999px;
  font-weight: 600;
  width: fit-content;
}

.hero-meta {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.meta-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
}

.meta-item {
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-lg);
  padding: var(--spacing-md);
  display: grid;
  gap: 6px;

  strong {
    font-size: var(--font-size-sm);
    opacity: 0.9;
  }

  span {
    font-weight: 700;
  }
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-lg);
}

.contact-card {
  padding: var(--spacing-xl);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
}

.contact-card h2 {
  margin: 0 0 var(--spacing-md);
  color: var(--zeta-primary);
}

.muted { color: var(--zeta-text-secondary); }

.contact-list,
.hours-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.contact-list li,
.hours-list li {
  display: flex;
  justify-content: space-between;
  gap: var(--spacing-md);
  padding: var(--spacing-sm) 0;
  color: var(--zeta-text-secondary);
  border-bottom: 1px dashed var(--zeta-divider);
}

.contact-list li:last-child,
.hours-list li:last-child {
  border-bottom: none;
}

.contact-list a {
  color: var(--zeta-primary);
  font-weight: 600;
}

.office-link {
  text-align: right;
}

.create-ticket-btn { margin-top: var(--spacing-md); }

.sla-box {
  margin-top: var(--spacing-lg);
  border: 1px solid var(--zeta-divider);
  border-radius: var(--radius-lg);
  overflow: hidden;

  div {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 12px;
    border-bottom: 1px solid var(--zeta-divider);

    &:last-child { border-bottom: none; }
  }

  strong { color: var(--zeta-primary); }
  span { color: var(--zeta-text-secondary); }
}

@media (max-width: 900px) {
  .contact-hero {
    grid-template-columns: 1fr;
  }

  .meta-grid {
    grid-template-columns: 1fr;
  }
}

:global([data-theme='dark']) .contact-card,
:global([data-theme='dark']) .sla-box {
  border-color: rgba(255, 255, 255, 0.08);
}

:global([data-theme='dark']) .contact-card h2 {
  color: #c7d2fe;
}

:global([data-theme='dark']) .contact-list li,
:global([data-theme='dark']) .hours-list li,
:global([data-theme='dark']) .muted,
:global([data-theme='dark']) .sla-box span {
  color: #d1d5db;
}

:global([data-theme='dark']) .contact-list a,
:global([data-theme='dark']) .sla-box strong {
  color: #93c5fd;
}
</style>
