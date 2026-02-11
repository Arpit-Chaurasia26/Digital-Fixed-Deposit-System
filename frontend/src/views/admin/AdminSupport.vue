<template>
  <div class="page">
    <Navbar />
    <div class="container page-content admin-layout">
      <AdminSidebar />
      <div class="admin-content">
      <div class="page-header card">
        <div>
          <h1>Admin - Support Tickets</h1>
          <p>Review, respond, and resolve user tickets</p>
        </div>
      </div>
      <div v-if="allTickets" class="tickets-container">
        <div v-for="ticket in allTickets.content" :key="ticket.id" class="card ticket-card">
          <div class="ticket-header">
            <h3>{{ ticket.subject }}</h3>
            <span :class="['status-badge', `status-${ticket.status.toLowerCase()}`]">{{ ticket.status }}</span>
          </div>
          <p class="ticket-desc">{{ ticket.description }}</p>
          <p class="ticket-meta"><strong>User:</strong> {{ ticket.userName }}</p>
          <p v-if="ticket.response" class="ticket-response"><strong>Response:</strong> {{ ticket.response }}</p>
          <div class="ticket-actions">
            <textarea
              v-model="responseDrafts[ticket.id]"
              class="form-control"
              rows="3"
              placeholder="Write a response..."
            ></textarea>
            <div v-if="sendError[ticket.id]" class="alert alert-error">
              {{ sendError[ticket.id] }}
            </div>
            <div class="actions-row">
              <button
                @click="sendResponse(ticket)"
                class="btn btn-primary"
                :disabled="sending[ticket.id]"
              >
                {{ sending[ticket.id] ? 'Sending...' : 'Send Response' }}
              </button>
            </div>
          </div>
        </div>
      </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue';
import { useStore } from 'vuex';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import AdminSidebar from '@/components/admin/AdminSidebar.vue';
import { getErrorMessage } from '@/utils/helpers';

const store = useStore();
const allTickets = computed(() => store.getters['support/allTickets']);
const responseDrafts = ref<Record<number, string>>({});
const sending = ref<Record<number, boolean>>({});
const sendError = ref<Record<number, string>>({});

onMounted(() => store.dispatch('support/fetchAllTickets', { page: 0, size: 10 }));

const sendResponse = async (ticket: { id: number; status: string }) => {
  const response = responseDrafts.value[ticket.id]?.trim();
  if (!response) return;
  sending.value[ticket.id] = true;
  sendError.value[ticket.id] = '';
  try {
    await store.dispatch('support/updateTicketResponse', { ticketId: ticket.id, response });
    await store.dispatch('support/fetchAllTickets', { page: 0, size: 10 });
    responseDrafts.value[ticket.id] = '';
  } catch (error) {
    sendError.value[ticket.id] = getErrorMessage(error, 'Unable to send the response');
  } finally {
    sending.value[ticket.id] = false;
  }
};
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.page-header {
  margin-bottom: var(--spacing-2xl);
  padding: var(--spacing-2xl);
  background: var(--zeta-gradient-hero);
  color: white;
  border-radius: var(--radius-xl);
}
.ticket-card {
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-lg);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.96));
  border: 1px solid rgba(99, 102, 241, 0.16);
}
.ticket-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(99, 102, 241, 0.18), transparent 55%);
  pointer-events: none;
}
.ticket-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--spacing-sm); position: relative; z-index: 1; }
.ticket-desc { color: var(--zeta-text-secondary); margin-bottom: var(--spacing-sm); position: relative; z-index: 1; }
.ticket-meta { color: var(--zeta-text-secondary); margin-bottom: var(--spacing-sm); position: relative; z-index: 1; }
.ticket-response { background: var(--zeta-background); padding: var(--spacing-sm) var(--spacing-md); border-radius: var(--radius-md); margin-bottom: var(--spacing-md); position: relative; z-index: 1; }
.ticket-actions { display: grid; gap: var(--spacing-md); }
.actions-row { display: flex; gap: var(--spacing-sm); flex-wrap: wrap; }
.error-text { color: var(--zeta-error-dark); font-size: var(--font-size-sm); }

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: var(--font-size-xs);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  position: relative;
  z-index: 1;
  color: white;
}

.status-open {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  box-shadow: 0 0 20px rgba(245, 158, 11, 0.55);
}

.status-in_progress {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  box-shadow: 0 0 20px rgba(59, 130, 246, 0.55);
}

.status-resolved {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  box-shadow: 0 0 20px rgba(34, 197, 94, 0.55);
}

.status-closed {
  background: linear-gradient(135deg, #ef4444, #f87171);
  box-shadow: 0 0 20px rgba(239, 68, 68, 0.55);
}
</style>
