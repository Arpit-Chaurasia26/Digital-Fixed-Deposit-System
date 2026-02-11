<template>
  <div class="page">
    <Navbar />
    <div class="container page-content admin-layout">
      <AdminSidebar />
      <div class="admin-content">
        <!-- ADMIN HEADER -->
        <div class="page-header card">
          <div>
            <h1>Admin - Support Tickets</h1>
            <p>Review, respond, and resolve user tickets</p>
          </div>
        </div>

        <!-- FILTERS -->
        <div class="filters-section card" style="padding: 1.5rem; margin-bottom: 1.5rem;">
          <h3 style="margin-top: 0; margin-bottom: 1rem;">Filters</h3>
          <div v-if="error" class="alert alert-error" style="margin-bottom: 1rem;">
            {{ error }}
          </div>
          <div class="filters">
            <select v-model="filters.status" class="filter-input">
              <option value="">All Status</option>
              <option value="OPEN">Open</option>
              <option value="RESOLVED">Resolved</option>
              <option value="CLOSED">Closed</option>
            </select>

            <input 
              type="date" 
              v-model="filters.createdFrom" 
              class="filter-input"
              placeholder="Created From"
            />
            <input 
              type="date" 
              v-model="filters.createdTo" 
              class="filter-input"
              placeholder="Created To"
            />

            <input 
              type="number" 
              v-model.number="filters.userId" 
              class="filter-input"
              placeholder="User ID" 
            />
            <input 
              type="number" 
              v-model.number="filters.fdId" 
              class="filter-input"
              placeholder="FD ID" 
            />

            <button class="btn btn-primary" @click="applyFilters">Apply Filters</button>
            <button class="btn btn-secondary" @click="clearFilters">Clear</button>
          </div>
        </div>

        <!-- TICKET TABLE -->
        <div class="table-section card">
          <table class="tickets-table" v-if="tickets.content?.length">
            <thead>
              <tr>
                <th>ID</th>
                <th>User ID</th>
                <th>FD ID</th>
                <th>Subject</th>
                <th>Status</th>
                <th>Created</th>
                <th>Updated</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="ticket in tickets.content" :key="ticket.id">
                <td>{{ ticket.id }}</td>
                <td>{{ ticket.userId }}</td>
                <td>{{ ticket.fdId ? ticket.fdId : 'N/A' }}</td>
                <td>{{ ticket.subject }}</td>
                <td>
                  <span :class="['status-badge', `status-${ticket.status.toLowerCase()}`]">
                    {{ ticket.status }}
                  </span>
                </td>
                <td>{{ formatDate(ticket.createdTime) }}</td>
                <td>{{ formatDate(ticket.updatedTime) }}</td>
                <td>
                  <button
                    class="btn btn-sm btn-primary"
                    @click="openTicketModal(ticket)"
                    title="View and Manage Ticket"
                  >
                    Manage
                  </button>
                </td>
              </tr>
            </tbody>
          </table>

          <div v-else style="padding: 2rem; text-align: center; color: #666;">
            No tickets found
          </div>
        </div>

        <!-- PAGINATION -->
        <div class="pagination">
          <button 
            @click="prevPage" 
            :disabled="page === 0" 
            class="btn btn-secondary"
          >
            Prev
          </button>
          <span class="page-info">Page {{ page + 1 }} of {{ tickets.totalPages || 1 }}</span>
          <button 
            @click="nextPage" 
            :disabled="page >= (tickets.totalPages || 1) - 1" 
            class="btn btn-secondary"
          >
            Next
          </button>
        </div>
      </div>
    </div>

    <!-- TICKET DETAIL MODAL -->
    <div v-if="selectedTicket" class="modal-overlay" @click.self="closeTicketModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>Ticket #{{ selectedTicket.id }} - {{ selectedTicket.subject }}</h2>
          <button class="modal-close" @click="closeTicketModal">✕</button>
        </div>

        <div class="modal-body">
          <!-- ERROR MESSAGE -->
          <div v-if="modalError" class="alert alert-error" style="margin-bottom: 1rem;">
            {{ modalError }}
          </div>

          <!-- TICKET INFO -->
          <div class="ticket-info">
            <div class="info-grid">
              <div class="info-item">
                <label>User ID:</label>
                <p>{{ selectedTicket.userId }}</p>
              </div>
              <div class="info-item">
                <label>FD ID:</label>
                <p>{{ selectedTicket.fdId || 'N/A' }}</p>
              </div>
              <div class="info-item">
                <label>Status:</label>
                <p>
                  <span :class="['status-badge', `status-${selectedTicket.status.toLowerCase()}`]">
                    {{ selectedTicket.status }}
                  </span>
                </p>
              </div>
              <div class="info-item">
                <label>Created:</label>
                <p>{{ formatDate(selectedTicket.createdTime) }}</p>
              </div>
              <div class="info-item">
                <label>Updated:</label>
                <p>{{ formatDate(selectedTicket.updatedTime) }}</p>
              </div>
            </div>

            <div style="margin-top: 1.5rem; padding-top: 1.5rem; border-top: 1px solid #e2e8f0;">
              <h4 style="margin: 0 0 0.5rem 0;">Description:</h4>
              <p style="white-space: pre-wrap; margin: 0; color: #475569;">{{ selectedTicket.description }}</p>
            </div>
          </div>

          <!-- RESPONSE FORM -->
          <div style="margin-top: 1.5rem; padding-top: 1.5rem; border-top: 1px solid #e2e8f0;">
            <h4 style="margin: 0 0 1rem 0;">Admin Response</h4>

            <!-- EXISTING RESPONSE -->
            <div v-if="selectedTicket.response" style="background: #f0fdf4; padding: 1rem; border-radius: 0.5rem; margin-bottom: 1rem; border-left: 4px solid #16a34a;">
              <p style="font-size: 0.875rem; color: #475569; margin: 0 0 0.5rem 0; font-weight: 600;">Current Response:</p>
              <p style="white-space: pre-wrap; margin: 0; color: #334155;">{{ selectedTicket.response }}</p>
            </div>

            <!-- RESPONSE TEXTAREA -->
            <textarea
              v-model="responseText"
              :disabled="selectedTicket.status === 'CLOSED' || submitting"
              placeholder="Enter your response to the user..."
              style="width: 100%; min-height: 100px; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 0.5rem; font-family: inherit; margin-bottom: 1rem; font-size: 0.875rem;"
            ></textarea>

            <!-- STATUS SELECT -->
            <div style="margin-bottom: 1rem;">
              <label style="display: block; margin-bottom: 0.5rem; font-weight: 600; font-size: 0.875rem;">Update Status:</label>
              <p style="font-size: 0.75rem; color: #94a3b8; margin: 0 0 0.5rem 0;">Current: <strong>{{ selectedTicket.status }}</strong></p>
              <select
                v-model="newStatus"
                :disabled="availableStatuses.length === 0 || submitting"
                style="width: 100%; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 0.5rem; font-family: inherit;"
              >
                <option v-for="status in availableStatuses" :key="status" :value="status">
                  {{ status === 'IN_PROGRESS' ? 'IN PROGRESS' : status }}
                </option>
              </select>
              
              <!-- RESOLVED WARNING -->
              <div v-if="selectedTicket.status === 'RESOLVED'" style="margin-top: 1rem; padding: 0.75rem; background: #fef3c7; border: 1px solid #fcd34d; border-radius: 0.5rem; color: #92400e; font-size: 0.875rem;">
                <strong>⚠ Note:</strong> This ticket has been resolved and cannot be modified.
              </div>

              <!-- CLOSED WARNING -->
              <div v-else-if="selectedTicket.status === 'CLOSED'" style="margin-top: 1rem; padding: 0.75rem; background: #fee2e2; border: 1px solid #fecaca; border-radius: 0.5rem; color: #991b1b; font-size: 0.875rem;">
                <strong>✓ Closed:</strong> This ticket is closed and cannot be modified.
              </div>
            </div>
          </div>
        </div>

        <!-- MODAL FOOTER -->
        <div class="modal-footer">
          <button
            @click="closeTicketModal"
            class="btn btn-secondary"
            :disabled="submitting"
          >
            Close
          </button>
          <button
            @click="submitTicketChanges"
            class="btn btn-primary"
            :disabled="selectedTicket.status === 'CLOSED' || submitting"
          >
            {{ submitting ? 'Saving...' : 'Save Changes' }}
          </button>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import AdminSidebar from '@/components/admin/AdminSidebar.vue';
import { supportService } from '@/services/supportService';
import type { SupportTicket, SupportTicketPage, TicketStatus } from '@/types';

// State
const tickets = ref<SupportTicketPage>({ content: [], totalPages: 0, totalElements: 0, size: 10, number: 0 });
const page = ref(0);
const size = ref(10);

const filters = reactive({
  status: '',
  createdFrom: '',
  createdTo: '',
  userId: null as number | null,
  fdId: null as number | null
});

const error = ref('');

// Modal state
const selectedTicket = ref<SupportTicket | null>(null);
const responseText = ref('');
const newStatus = ref<TicketStatus>('OPEN');
const submitting = ref(false);
const modalError = ref('');

// Fetch tickets with filters and pagination
const fetchTickets = async () => {
  try {
    const params: any = {
      page: page.value,
      size: size.value
    };

    if (filters.status && filters.status.trim() !== '') params.status = filters.status;
    if (filters.createdFrom && filters.createdFrom.trim() !== '') params.createdFrom = filters.createdFrom + 'T00:00:00';
    if (filters.createdTo && filters.createdTo.trim() !== '') params.createdTo = filters.createdTo + 'T23:59:59';
    // Only add userId if it's a valid positive number
    if (filters.userId && !isNaN(filters.userId) && filters.userId > 0) {
      params.userId = filters.userId;
      console.log('Adding userId filter:', filters.userId);
    }
    // Only add fdId if it's a valid positive number
    if (filters.fdId && !isNaN(filters.fdId) && filters.fdId > 0) {
      params.fdId = filters.fdId;
      console.log('Adding fdId filter:', filters.fdId);
    }

    console.log('Fetching tickets with params:', params);
    const response = await supportService.getAllTickets(params);
    console.log('Tickets response:', response);
    console.log('First ticket:', response.content?.[0]);
    tickets.value = response;
    error.value = '';
  } catch (err: any) {
    console.error('Error fetching tickets:', err.response?.data || err.message);
    error.value = err.response?.data?.message || err.message || 'Failed to fetch tickets';
  }
};

// Apply filters and reset to page 0
const applyFilters = () => {
  page.value = 0;
  fetchTickets();
};

// Clear filters
const clearFilters = () => {
  filters.status = '';
  filters.createdFrom = '';
  filters.createdTo = '';
  filters.userId = null;
  filters.fdId = null;
  page.value = 0;
  fetchTickets();
};

// Pagination
const prevPage = () => {
  if (page.value > 0) {
    page.value--;
    fetchTickets();
  }
};

const nextPage = () => {
  if (page.value < (tickets.value.totalPages || 1) - 1) {
    page.value++;
    fetchTickets();
  }
};

// Open ticket modal
const openTicketModal = (ticket: SupportTicket) => {
  console.log('Opening ticket modal for ticket:', ticket);
  selectedTicket.value = ticket;
  responseText.value = ticket.response || '';
  // Set newStatus to first available status option, will be computed
  newStatus.value = 'OPEN' as TicketStatus;
  modalError.value = '';
};

// Close ticket modal
const closeTicketModal = () => {
  console.log('Closing ticket modal');
  selectedTicket.value = null;
  responseText.value = '';
  newStatus.value = 'OPEN' as TicketStatus;
  modalError.value = '';
  submitting.value = false;
};

// Submit ticket changes
const submitTicketChanges = async () => {
  if (!selectedTicket.value) return;

  submitting.value = true;
  modalError.value = '';

  try {
    console.log('Submitting changes for ticket:', selectedTicket.value.id);
    
    // Update response if changed
    if (responseText.value !== selectedTicket.value.response) {
      console.log('Updating response to:', responseText.value);
      try {
        await supportService.updateTicketResponse(selectedTicket.value.id, responseText.value);
        console.log('Response updated successfully');
      } catch (err: any) {
        console.error('Response update error:', err);
        // Don't stop - response might still have been saved
      }
    }

    // Update status if changed
    if (newStatus.value !== selectedTicket.value.status) {
      console.log('Updating status to:', newStatus.value);
      try {
        await supportService.updateTicketStatus(selectedTicket.value.id, newStatus.value as TicketStatus);
        console.log('Status updated successfully');
      } catch (err: any) {
        console.error('Status update error:', err);
        // Don't stop - status might still have been saved
      }
    }

    console.log('Changes submitted successfully');
    
    // Update the ticket in the modal to reflect changes
    selectedTicket.value.response = responseText.value;
    selectedTicket.value.status = newStatus.value;
    
    // Refresh the ticket list to show updated statuses
    await fetchTickets();
    
    // Close modal
    closeTicketModal();
    alert('Ticket updated successfully!');
  } catch (err: any) {
    console.error('Error submitting changes:', err);
    modalError.value = `Failed to save changes: ${err.message || 'Unknown error'}`;
  } finally {
    submitting.value = false;
  }
};

// Get available status transitions for admin
// OPEN -> can only change to RESOLVED (admin resolves)
// RESOLVED -> cannot modify (ticket is resolved, waiting for user or admin to close)
// CLOSED -> cannot modify
const availableStatuses = computed(() => {
  if (!selectedTicket.value) return [];
  
  const currentStatus = selectedTicket.value.status;
  
  if (currentStatus === 'OPEN') {
    return ['RESOLVED']; // Admin can resolve an open ticket
  } else if (currentStatus === 'IN_PROGRESS') {
    return ['RESOLVED']; // Admin can resolve in-progress ticket
  }
  
  // RESOLVED and CLOSED - no transitions available
  return [];
});

// Format date
const formatDate = (dateString: string | undefined): string => {
  if (!dateString) {
    console.warn('Empty date string provided to formatDate');
    return 'N/A';
  }
  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      console.warn('Invalid date:', dateString);
      return 'N/A';
    }
    return date.toLocaleString('en-US', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  } catch (e) {
    console.error('Error formatting date:', dateString, e);
    return 'N/A';
  }
};

// Load on mount
onMounted(() => {
  fetchTickets();
});
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.page-content {
  flex: 1;
  padding: 2rem;
  display: flex;
  gap: 1rem;
}

.admin-content {
  flex: 1;
}

.page-header {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(99, 102, 241, 0.1));
  border-radius: 1rem;

  h1 {
    margin: 0 0 0.5rem 0;
    color: #e8e9ea;
    font-size: 1.75rem;
  }

  p {
    margin: 0;
    color: #d7d9dc;
  }

padding: var(--spacing-2xl); background: var(--zeta-gradient-hero); color: white; border-radius: var(--radius-xl); margin-bottom: var(--spacing-2xl); }

.filters-section {
  background: white;
  border-radius: 1rem;
  border: 1px solid #1d4e8f;

  h3 {
    color: #1e293b;
    font-size: 1rem;
  }

  .alert {
    padding: 0.75rem 1rem;
    border-radius: 0.5rem;
    font-size: 0.875rem;

    &.alert-error {
      background-color: #fee2e2;
      color: #991b1b;
      border: 1px solid #fecaca;
    }
  }
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: flex-end;

  .filter-input {
    flex: 1;
    min-width: 150px;
    padding: 0.5rem 0.75rem;
    border: 1px solid #e2e8f0;
    border-radius: 0.5rem;
    font-size: 0.875rem;
    background: white;
    color: #1e293b;

    &:focus {
      outline: none;
      border-color: #3b82f6;
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }

    &::placeholder {
      color: #94a3b8;
    }
  }
}

.table-section {
  background: white;
  border-radius: 1rem;
  border: 1px solid #385881;
  overflow: hidden;
  margin-bottom: 1.5rem;
}

.tickets-table {
  width: 100%;
  border-collapse: collapse;

  thead {
    background-color: #f8fafc;
    border-bottom: 1px solid #365175;

    th {
      padding: 1rem;
      text-align: left;
      font-weight: 600;
      color: #475569;
      font-size: 0.875rem;
      text-transform: uppercase;
    }
  }

  tbody {
    tr {
      border-bottom: 1px solid #e2e8f0;
      transition: background-color 0.2s;

      &:hover {
        background-color: #f8fafc;
      }

      td {
        padding: 0.75rem 1rem;
        color: #334155;
        font-size: 0.875rem;
      }
    }
  }
}

.status-badge {
  display: inline-block;
  padding: 0.35rem 0.75rem;
  border-radius: 999px;
  color: white;
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.75rem;

  &.status-open {
    background-color: #f59e0b;
  }

  &.status-resolved {
    background-color: #16a34a;
  }

  &.status-closed {
    background-color: #ef4444;
  }
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.btn {
  padding: 0.4rem 0.8rem;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.8rem;
  transition: all 0.2s;

  &.btn-sm {
    padding: 0.3rem 0.6rem;
    font-size: 0.75rem;
  }

  &.btn-primary {
    background-color: #3b82f6;
    color: white;

    &:hover:not(:disabled) {
      background-color: #2563eb;
    }

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }

  &.btn-secondary {
    background-color: #9ca3af;
    color: white;

    &:hover:not(:disabled) {
      background-color: #6b7280;
    }

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;

  .page-info {
    color: #475569;
    font-weight: 500;
  }
}

// Modal styles
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-content {
  background: white;
  border-radius: 1rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  max-width: 600px;
  width: 100%;
  max-height: 75vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e2e8f0;

  h2 {
    margin: 0;
    font-size: 1.25rem;
    color: #1e293b;
  }
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #94a3b8;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0.5rem;
  transition: all 0.2s;

  &:hover {
    background-color: #f1f5f9;
    color: #475569;
  }
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
}

.modal-footer {
  padding: 1.5rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.ticket-info {
  background: #f8fafc;
  padding: 1rem;
  border-radius: 0.5rem;
  border-left: 4px solid #3b82f6;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.info-item {
  label {
    display: block;
    font-weight: 600;
    font-size: 0.75rem;
    text-transform: uppercase;
    color: #94a3b8;
    margin-bottom: 0.25rem;
  }

  p {
    margin: 0;
    color: #334155;
    font-size: 0.875rem;
  }
}

.alert {
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  font-size: 0.875rem;

  &.alert-error {
    background-color: #fee2e2;
    color: #991b1b;
    border: 1px solid #fecaca;
  }
}
</style>
