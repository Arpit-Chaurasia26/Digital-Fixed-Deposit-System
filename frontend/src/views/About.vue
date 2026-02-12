<template>
  <div class="about-page">
    <Navbar />
    <div class="container about-content">
      <div class="about-header">
        <h1>About Zeta FD</h1>
        <p class="subtitle">Your trusted partner in secure financial investments</p>
      </div>

      <section class="about-section">
        <h2>Who We Are</h2>
        <p>
          Zeta FD is a leading digital fixed deposit platform that brings together the security of traditional 
          banking with the convenience of modern technology. With years of experience in the financial sector, 
          we've helped thousands of investors achieve their financial goals through our innovative fixed deposit solutions.
        </p>
      </section>

      <section class="about-section">
        <h2>Our Mission</h2>
        <p>
          To democratize fixed deposit investments by providing a seamless, transparent, and secure platform 
          that makes wealth creation accessible to everyone. We believe in empowering investors with the tools 
          and information they need to make informed financial decisions.
        </p>
      </section>

      <section class="about-section">
        <h2>Why Choose Us</h2>
        <div class="features-grid">
          <div class="feature">
            <div class="feature-icon">🔒</div>
            <h3>100% Secure</h3>
            <p>Bank-grade security with DICGC insurance coverage up to ₹5 lakhs</p>
          </div>
          <div class="feature">
            <div class="feature-icon">💹</div>
            <h3>Competitive Rates</h3>
            <p>Interest rates up to 7.25% per annum across different schemes</p>
          </div>
          <div class="feature">
            <div class="feature-icon">⚡</div>
            <h3>Instant Processing</h3>
            <p>Book your FD in minutes with our streamlined digital process</p>
          </div>
          <div class="feature">
            <div class="feature-icon">📱</div>
            <h3>Easy Management</h3>
            <p>Track and manage all your investments from a single dashboard</p>
          </div>
          <div class="feature">
            <div class="feature-icon">🎯</div>
            <h3>Flexible Options</h3>
            <p>Multiple schemes tailored to different investment needs and goals</p>
          </div>
          <div class="feature">
            <div class="feature-icon">💬</div>
            <h3>24/7 Support</h3>
            <p>Dedicated customer support team available round the clock</p>
          </div>
        </div>
      </section>

      <section class="about-section">
        <h2>Our Values</h2>
        <ul class="values-list">
          <li><strong>Transparency:</strong> Clear and honest communication about all terms and conditions</li>
          <li><strong>Security:</strong> Your financial safety is our top priority</li>
          <li><strong>Innovation:</strong> Continuously improving our platform to serve you better</li>
          <li><strong>Customer First:</strong> Every decision we make puts you at the center</li>
          <li><strong>Integrity:</strong> We operate with the highest ethical standards</li>
        </ul>
      </section>

      <section class="about-section cta-section">
        <h2>Ready to Start Your Investment Journey?</h2>
        <p>Join thousands of satisfied investors who trust Zeta FD with their savings</p>
        <div class="cta-buttons">
          <router-link to="/register" class="btn btn-primary btn-lg">Get Started</router-link>
          <router-link to="/contact" class="btn btn-outline btn-lg">Contact Us</router-link>
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
  max-width: 1200px;
  margin: 0 auto;
}

.about-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
  padding: var(--spacing-2xl) 0;

  h1 {
    font-size: var(--font-size-4xl);
    color: var(--zeta-primary);
    margin-bottom: var(--spacing-md);
  }

  .subtitle {
    font-size: var(--font-size-xl);
    color: var(--zeta-text-secondary);
  }
}

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

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-xl);
  margin-top: var(--spacing-xl);
}

.feature {
  text-align: center;
  padding: var(--spacing-xl);
  background: var(--zeta-surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-md);
  }

  .feature-icon {
    font-size: 48px;
    margin-bottom: var(--spacing-md);
  }

  h3 {
    color: var(--zeta-primary);
    margin-bottom: var(--spacing-sm);
    font-size: var(--font-size-lg);
  }

  p {
    color: var(--zeta-text-secondary);
    font-size: var(--font-size-base);
  }
}

.values-list {
  list-style: none;
  padding: 0;

  li {
    padding: var(--spacing-md) 0;
    color: var(--zeta-text-secondary);
    font-size: var(--font-size-lg);
    line-height: 1.8;
    border-bottom: 1px solid var(--zeta-divider);

    &:last-child {
      border-bottom: none;
    }

    strong {
      color: var(--zeta-primary);
    }
  }
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
</style>
