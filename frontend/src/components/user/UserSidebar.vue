<template>
  <aside class="user-sidebar">
    <div class="sidebar-brand">
      <h3>My Account</h3>
      <p>Manage your FDs</p>
    </div>

    <nav class="sidebar-nav">
      <router-link to="/user/book-fd" class="sidebar-link">Book FD</router-link>
      <router-link to="/user/fd-list" class="sidebar-link">My FDs</router-link>
      <router-link to="/user/maturing" class="sidebar-link">Maturing FDs</router-link>
      <router-link to="/user/financial-summary" class="sidebar-link">Financial Summary</router-link>
      <router-link to="/user/portfolio" class="sidebar-link">Portfolio</router-link>
      <router-link to="/user/withdrawals" class="sidebar-link">Transactions</router-link>
      <router-link to="/user/support" class="sidebar-link">Support Tickets</router-link>
      <router-link to="/user/profile" class="sidebar-link">My Profile</router-link>
    </nav>
  </aside>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
.user-sidebar {
  width: 260px;
  min-height: calc(100vh - 76px);
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.98) 0%, rgba(30, 64, 175, 0.92) 60%, rgba(6, 182, 212, 0.85) 100%);
  color: white;
  padding: var(--spacing-2xl) var(--spacing-lg);
  position: fixed;
  top: 76px;
  left: 0;
  z-index: 900;
  box-shadow: 10px 0 30px rgba(15, 23, 42, 0.15);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.sidebar-brand h3 {
  margin: 0;
  font-size: var(--font-size-2xl);
  font-weight: 700;
}

.sidebar-brand p {
  margin-top: var(--spacing-xs);
  color: rgba(255, 255, 255, 0.75);
  font-size: var(--font-size-sm);
}

.sidebar-nav {
  display: grid;
  gap: var(--spacing-sm);
}

.sidebar-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  font-weight: 500;
}

.sidebar-link:hover,
.sidebar-link.router-link-active {
  background: rgba(255, 255, 255, 0.15);
  color: white;
}

@media (max-width: 1024px) {
  .user-sidebar {
    position: static;
    top: auto;
    width: 100%;
    min-height: auto;
    border-radius: var(--radius-xl);
    box-shadow: var(--shadow-md);
  }
}
</style>
