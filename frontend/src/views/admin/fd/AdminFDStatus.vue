<template>
  <div class="page">
    <Navbar />
    <div class="container page-content admin-layout">
      <AdminSidebar />
      <div class="admin-content">
      <div class="page-header card">
        <div>
          <h1>Update FD Status</h1>
          <p>Change the status of any fixed deposit with validation and clear feedback.</p>
        </div>
      </div>

      <div class="card panel">
        <div class="form-row">
          <input v-model.number="statusForm.fdId" type="number" class="form-control" placeholder="FD ID" min="1" />
          <select v-model="statusForm.status" class="form-control">
            <option value="">Select Status</option>
            <option value="ACTIVE">ACTIVE</option>
            <option value="MATURED">MATURED</option>
            <option value="BROKEN">BROKEN</option>
            <option value="CLOSED">CLOSED</option>
          </select>
          <button class="btn btn-primary" @click="updateStatus" :disabled="updating">Update</button>
        </div>
        <div v-if="statusMessage" class="alert alert-success">{{ statusMessage }}</div>
        <div v-if="statusError" class="alert alert-error">{{ statusError }}</div>
      </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import AdminSidebar from '@/components/admin/AdminSidebar.vue';
import { fdService } from '@/services/fdService';
import { FDStatus } from '@/types';
import { getErrorMessage } from '@/utils/helpers';

const updating = ref(false);
const statusMessage = ref('');
const statusError = ref('');

const statusForm = ref<{ fdId: number | null; status: FDStatus | '' }>({
  fdId: null,
  status: '',
});

const updateStatus = async () => {
  if (!statusForm.value.fdId || !statusForm.value.status) {
    statusError.value = 'Please enter a valid FD ID and select a status.';
    return;
  }
  updating.value = true;
  statusMessage.value = '';
  statusError.value = '';
  try {
    await fdService.updateFDStatus(statusForm.value.fdId, statusForm.value.status as FDStatus);
    statusMessage.value = `FD #${statusForm.value.fdId} updated successfully.`;
  } catch (error) {
    statusError.value = getErrorMessage(error, 'Unable to update status');
  } finally {
    updating.value = false;
  }
};
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.page-header { padding: var(--spacing-2xl); background: var(--zeta-gradient-hero); color: white; border-radius: var(--radius-xl); margin-bottom: var(--spacing-2xl); }
.panel { padding: var(--spacing-xl); border-radius: var(--radius-xl); box-shadow: var(--shadow-md); }
.form-row { display: grid; grid-template-columns: 1fr 1fr auto; gap: var(--spacing-md); align-items: center; }
@media (max-width: 768px) {
  .form-row { grid-template-columns: 1fr; }
}
</style>
