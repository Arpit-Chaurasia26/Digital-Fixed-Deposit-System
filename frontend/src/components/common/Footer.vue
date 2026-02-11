<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3>Zeta Fixed Deposit</h3>
          <p>Your trusted partner for secure and high-return fixed deposit investments.</p>
          <div class="social-links">
            <a href="#" aria-label="Facebook"><i class="icon">📘</i></a>
            <a href="#" aria-label="Twitter"><i class="icon">🐦</i></a>
            <a href="#" aria-label="LinkedIn"><i class="icon">💼</i></a>
            <a href="#" aria-label="Instagram"><i class="icon">📷</i></a>
          </div>
        </div>

        <div class="footer-section">
          <h4>Quick Links</h4>
          <ul>
            <li><router-link to="/">Home</router-link></li>
            <li><router-link to="/about">About Us</router-link></li>
            <li><router-link to="/contact">Contact</router-link></li>
            <li><router-link :to="{ path: '/', hash: '#faq' }">FAQ</router-link></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4>Services</h4>
          <ul>
            <li><router-link :to="{ path: '/', hash: '#scheme-STANDARD_6_MONTHS' }">Standard 6 Months FD</router-link></li>
            <li><router-link :to="{ path: '/', hash: '#scheme-STANDARD_12_MONTHS' }">Standard 12 Months FD</router-link></li>
            <li><router-link :to="{ path: '/', hash: '#scheme-SENIOR_CITIZEN_12_MONTHS' }">Senior Citizen FD</router-link></li>
            <li><router-link :to="{ path: '/', hash: '#scheme-TAX_SAVER_5_YEARS' }">Tax Saver FD</router-link></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4>Contact Info</h4>
          <ul class="contact-info">
            <li>📧 support@zetafd.com</li>
            <li>📞 1800-123-4567</li>
            <li>📍 123 Finance Street, Mumbai, India</li>
          </ul>
        </div>
      </div>

      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} Zeta Fixed Deposit System. All rights reserved.</p>
        <div class="footer-links">
          <a href="#">Privacy Policy</a>
          <a href="#">Terms of Service</a>
          <a href="#">Cookie Policy</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const currentYear = computed(() => new Date().getFullYear());
</script>

<style scoped lang="scss">
.footer {
  background: var(--zeta-primary-dark);
  color: white;
  padding: var(--spacing-3xl) 0 var(--spacing-lg);
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
  }
}

.footer-section {
  h3 {
    font-size: var(--font-size-xl);
    margin-bottom: var(--spacing-md);
    color: var(--zeta-secondary);
  }

  h4 {
    font-size: var(--font-size-lg);
    margin-bottom: var(--spacing-md);
    color: var(--zeta-secondary-light);
  }

  p {
    color: rgba(255, 255, 255, 0.8);
    line-height: 1.6;
    margin-bottom: var(--spacing-md);
  }

  ul {
    list-style: none;
    padding: 0;

    li {
      margin-bottom: var(--spacing-sm);

      a {
        color: rgba(255, 255, 255, 0.8);
        transition: color var(--transition-fast);

        &:hover {
          color: var(--zeta-secondary);
        }
      }
    }
  }

  .contact-info li {
    color: rgba(255, 255, 255, 0.8);
    display: flex;
    align-items: start;
    gap: var(--spacing-sm);
  }
}

.social-links {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-md);

  a {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    transition: all var(--transition-fast);

    &:hover {
      background: var(--zeta-secondary);
      transform: translateY(-3px);
    }

    .icon {
      font-size: var(--font-size-lg);
    }
  }
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: var(--spacing-lg);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-md);

  @media (max-width: 768px) {
    flex-direction: column;
    text-align: center;
  }

  p {
    color: rgba(255, 255, 255, 0.6);
    margin: 0;
  }

  .footer-links {
    display: flex;
    gap: var(--spacing-lg);

    a {
      color: rgba(255, 255, 255, 0.6);
      font-size: var(--font-size-sm);
      transition: color var(--transition-fast);

      &:hover {
        color: var(--zeta-secondary);
      }
    }
  }
}
</style>
