<template>
  <div class="page">
    <Navbar />
    <div class="container page-content admin-layout">
      <AdminSidebar />
      <div class="admin-content">
      <div class="page-header card">
        <div>
          <h1>Admin - FD Management</h1>
          <p>Update FD status and monitor maturing deposits</p>
        </div>
      </div>

      <div class="grid">
        <router-link to="/admin/fd-management/all" class="card panel link-card">
          <h3>All Fixed Deposits</h3>
          <p>Chronological list of all deposits across users.</p>
          <span class="link-arrow">Browse →</span>
        </router-link>

        <router-link to="/admin/fd-management/update-status" class="card panel link-card">
          <h3>Update FD Status</h3>
          <p>Change the status of any fixed deposit.</p>
          <span class="link-arrow">Manage →</span>
        </router-link>

        <router-link to="/admin/fd-management/maturing" class="card panel link-card">
          <h3>Maturing FDs</h3>
          <p>Review deposits maturing in the next N days.</p>
          <span class="link-arrow">Open →</span>
        </router-link>

        <router-link to="/admin/fd-management/yearly" class="card panel link-card">
          <h3>FDs by Financial Year</h3>
          <p>Browse all deposits created in a financial year.</p>
          <span class="link-arrow">View →</span>
        </router-link>

        <router-link to="/admin/fd-management/user-portfolio" class="card panel link-card">
          <h3>User Portfolio Lookup</h3>
          <p>Fetch portfolio summary for any user.</p>
          <span class="link-arrow">Lookup →</span>
        </router-link>

        <router-link to="/admin/fd-management/interest-timeline" class="card panel link-card">
          <h3>FD Interest Timeline</h3>
          <p>Inspect accrual timeline for a fixed deposit.</p>
          <span class="link-arrow">Inspect →</span>
        </router-link>
      </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import AdminSidebar from '@/components/admin/AdminSidebar.vue';
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.page-header { padding: var(--spacing-2xl); background: var(--zeta-gradient-hero); color: white; border-radius: var(--radius-xl); margin-bottom: var(--spacing-2xl); }
.grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: var(--spacing-xl); }
.panel { padding: var(--spacing-xl); border-radius: var(--radius-xl); box-shadow: var(--shadow-md); }
.link-card {
  text-decoration: none;
  color: inherit;
  display: grid;
  gap: var(--spacing-sm);
  transition: transform var(--transition-base), box-shadow var(--transition-base);
}

.link-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.link-arrow {
  color: var(--zeta-primary);
  font-weight: 600;
}
</style>
