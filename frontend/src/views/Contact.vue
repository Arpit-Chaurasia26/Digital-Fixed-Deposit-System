<template>
  <div class="contact-page">
    <Navbar />
    <div class="container contact-content">
      <div class="contact-hero card">
        <div>
          <h1>Contact Us</h1>
          <p>We’re here to help with your fixed deposit queries.</p>
        </div>
        <div class="hero-chip">24/7 Support</div>
      </div>

      <div class="contact-grid">
        <div class="card contact-card">
          <h2>Get in Touch</h2>
          <p>Email us or call our support team for quick assistance.</p>
          <ul class="contact-list">
            <li>📧 support@zetafd.com</li>
            <li>📞 1800-123-4567</li>
            <li>📍 123 Finance Street, Mumbai, India</li>
          </ul>
        </div>

        <div class="card contact-card">
          <h2>Business Hours</h2>
          <ul class="hours-list">
            <li><span>Mon - Fri</span><strong>9:00 AM - 8:00 PM</strong></li>
            <li><span>Saturday</span><strong>10:00 AM - 6:00 PM</strong></li>
            <li><span>Sunday</span><strong>Closed</strong></li>
          </ul>
        </div>

        <div class="card contact-card">
          <h2>Quick Help</h2>
          <p>For account issues or FD details, create a support ticket.</p>
          <router-link to="/user/support/create" class="btn btn-primary create-ticket-btn">Create Ticket</router-link>
        </div>
      </div>
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
.contact-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.contact-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-xl);
  padding: var(--spacing-2xl);
  background: var(--zeta-gradient-hero);
  color: white;
  border-radius: var(--radius-xl);
  margin-bottom: var(--spacing-2xl);
  box-shadow: var(--shadow-md);
}
.hero-chip {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: 999px;
  font-weight: 600;
}
.contact-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: var(--spacing-lg); }
.contact-card { padding: var(--spacing-xl); border-radius: var(--radius-xl); box-shadow: var(--shadow-md); }
.contact-list, .hours-list { list-style: none; padding: 0; margin: var(--spacing-md) 0 0 0; }
.hours-list li { display: flex; justify-content: space-between; padding: var(--spacing-sm) 0; color: var(--zeta-text-secondary); }
.create-ticket-btn { margin-top: var(--spacing-md); }
</style>
