<template>
  <div class="page">
    <Navbar />
    <div class="container page-content user-layout">
      <UserSidebar />
      <div class="user-content">
      <div class="page-header card">
        <div>
          <h1>Support Tickets</h1>
          <p>Track your requests and responses</p>
        </div>
        <router-link to="/user/support/create" class="btn btn-primary">Create New Ticket</router-link>
      </div>
      <div v-if="createdNotice" class="alert alert-success">
        Ticket created successfully. Our team will respond soon.
      </div>
      <div v-if="errorMessage" class="alert alert-error">
        {{ errorMessage }}
      </div>
      <div v-if="tickets.length > 0" class="tickets-list">
        <div v-for="ticket in tickets" :key="ticket.id" class="card ticket-card">
          <div class="ticket-header">
            <h3>{{ ticket.subject }}</h3>
            <span :class="['status-badge', `status-${ticket.status.toLowerCase()}`]">{{ ticket.status }}</span>
          </div>
          <div v-if="ticket.fdId" class="ticket-meta">
            <span class="fd-chip">FD #{{ ticket.fdId }}</span>
          </div>
          <p class="ticket-desc">{{ ticket.description }}</p>
          <p v-if="ticket.response"><strong>Response:</strong> {{ ticket.response }}</p>
          <div v-if="ticket.status === 'RESOLVED'" class="ticket-actions">
            <button
              class="btn btn-secondary"
              @click="closeTicket(ticket.id)"
              :disabled="closing[ticket.id]"
            >
              {{ closing[ticket.id] ? 'Closing...' : 'Close Ticket' }}
            </button>
            <div v-if="closeError[ticket.id]" class="alert alert-error">
              {{ closeError[ticket.id] }}
            </div>
          </div>
        </div>
      </div>
      <p v-else class="empty">No tickets found yet.</p>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { getErrorMessage } from '@/utils/helpers';

const store = useStore();
const route = useRoute();
const tickets = computed(() => store.getters['support/myTickets']);
const errorMessage = computed(() => store.getters['support/error']);
const createdNotice = computed(() => route.query.created === '1');
const closing = ref<Record<number, boolean>>({});
const closeError = ref<Record<number, string>>({});

onMounted(() => store.dispatch('support/fetchMyTickets'));

const closeTicket = async (ticketId: number) => {
  closing.value[ticketId] = true;
  closeError.value[ticketId] = '';
  try {
    await store.dispatch('support/updateTicketStatus', { ticketId, status: 'CLOSED' });
    await store.dispatch('support/fetchMyTickets');
  } catch (error) {
    closeError.value[ticketId] = getErrorMessage(error, 'Unable to close this ticket');
  } finally {
    closing.value[ticketId] = false;
  }
};
</script>

<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.user-content { min-width: 0; }
.tickets-list { display: grid; gap: var(--spacing-lg); }
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-lg);
  padding: var(--spacing-2xl);
  margin-bottom: var(--spacing-2xl);
  background: var(--zeta-gradient-hero);
  color: white;
  border-radius: var(--radius-xl);
  box-shadow: 0 24px 55px rgba(15, 23, 42, 0.18);
  position: relative;
  overflow: hidden;
}
.page-header p { margin: 0; color: rgba(255, 255, 255, 0.9); }
.page-header::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(255, 255, 255, 0.25), transparent 60%);
  pointer-events: none;
}

.ticket-card {
  padding: var(--spacing-xl);
  border-radius: var(--radius-xl);
  border: 1px solid rgba(148, 163, 184, 0.2);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.96));
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
  position: relative;
  overflow: hidden;
}

.ticket-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(99, 102, 241, 0.14), transparent 60%);
  pointer-events: none;
}
.ticket-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--spacing-sm); }
.ticket-meta { margin-bottom: var(--spacing-sm); }
.fd-chip {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 999px;
  font-size: var(--font-size-xs);
  font-weight: 700;
  letter-spacing: 0.4px;
  text-transform: uppercase;
  background: linear-gradient(135deg, #0ea5e9, #38bdf8);
  color: white;
  box-shadow: 0 0 16px rgba(56, 189, 248, 0.55);
}
.ticket-desc { color: var(--zeta-text-secondary); }
.empty { color: var(--zeta-text-secondary); margin-top: var(--spacing-md); }
.ticket-actions {
  display: grid;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-sm);
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border-radius: 999px;
  font-size: var(--font-size-xs);
  font-weight: 700;
  letter-spacing: 0.4px;
  text-transform: uppercase;
  color: white;
}

.status-open {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  box-shadow: 0 0 18px rgba(59, 130, 246, 0.55);
}

.status-in_progress {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  box-shadow: 0 0 18px rgba(245, 158, 11, 0.55);
}

.status-resolved {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  box-shadow: 0 0 18px rgba(34, 197, 94, 0.55);
}

.status-closed {
  background: linear-gradient(135deg, #ef4444, #f87171);
  box-shadow: 0 0 18px rgba(239, 68, 68, 0.55);
}
</style>
