<template>
  <aside class="admin-sidebar">
    <div class="sidebar-brand">
      <div class="logo">
        <span class="logo-icon">Ζ</span>
        <span class="logo-text">Admin</span>
      </div>
      <p>FD Operations</p>
    </div>

    <nav class="sidebar-nav">
      <router-link to="/admin/dashboard" class="sidebar-link">Dashboard</router-link>
      <router-link to="/admin/fd-management" class="sidebar-link">FD Management</router-link>
      <router-link to="/admin/fd-management/all" class="sidebar-link">All FDs</router-link>
      <router-link to="/admin/fd-management/maturing" class="sidebar-link">Maturing FDs</router-link>
      <router-link to="/admin/fd-management/yearly" class="sidebar-link">FDs by Year</router-link>
      <router-link to="/admin/fd-management/update-status" class="sidebar-link">Update Status</router-link>
      <router-link to="/admin/fd-management/user-portfolio" class="sidebar-link">User Portfolio</router-link>
      <router-link to="/admin/fd-management/interest-timeline" class="sidebar-link">Interest Timeline</router-link>
      <router-link to="/admin/support" class="sidebar-link">Support</router-link>
    </nav>
  </aside>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
.admin-sidebar {
  width: 260px;
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(49, 46, 129, 0.98) 0%, rgba(79, 70, 229, 0.92) 60%, rgba(6, 182, 212, 0.85) 100%);
  color: white;
  padding: var(--spacing-2xl) var(--spacing-lg);
  position: fixed;
  top: 0;
  left: 0;
  z-index: var(--z-sticky);
  box-shadow: 10px 0 30px rgba(15, 23, 42, 0.15);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.sidebar-brand .logo {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.logo-icon {
  font-size: var(--font-size-3xl);
  font-weight: 700;
  color: var(--zeta-secondary-light);
}

.logo-text {
  font-size: var(--font-size-xl);
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
  .admin-sidebar {
    position: static;
    width: 100%;
    min-height: auto;
    border-radius: var(--radius-xl);
    box-shadow: var(--shadow-md);
  }
}
</style>
