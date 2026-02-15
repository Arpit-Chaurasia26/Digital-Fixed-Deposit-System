import { supportService } from '@/services/supportService';
import {
  SupportTicket,
  SupportTicketRequest,
  SupportTicketPage,
  TicketStatus,
} from '@/types';
import { getErrorMessage } from '@/utils/helpers';

export interface SupportState {
  myTickets: SupportTicket[];
  allTickets: SupportTicketPage | null;
  currentTicket: SupportTicket | null;
  loading: boolean;
  error: string | null;
}

type Commit = (type: string, payload?: unknown) => void;
type ActionCtx = { commit: Commit };

const supportModule = {
  namespaced: true,

  state: (): SupportState => ({
    myTickets: [],
    allTickets: null,
    currentTicket: null,
    loading: false,
    error: null,
  }),

  getters: {
    myTickets: (state: SupportState) => state.myTickets,
    allTickets: (state: SupportState) => state.allTickets,
    currentTicket: (state: SupportState) => state.currentTicket,
    openTickets: (state: SupportState) => state.myTickets.filter((t: SupportTicket) => t.status === TicketStatus.OPEN),
    resolvedTickets: (state: SupportState) =>
      state.myTickets.filter((t: SupportTicket) => t.status === TicketStatus.RESOLVED),
    closedTickets: (state: SupportState) => state.myTickets.filter((t: SupportTicket) => t.status === TicketStatus.CLOSED),
    loading: (state: SupportState) => state.loading,
    error: (state: SupportState) => state.error,
  },

  mutations: {
    SET_MY_TICKETS(state: SupportState, tickets: SupportTicket[]) {
      state.myTickets = tickets;
    },
    SET_ALL_TICKETS(state: SupportState, ticketPage: SupportTicketPage) {
      state.allTickets = ticketPage;
    },
    SET_CURRENT_TICKET(state: SupportState, ticket: SupportTicket | null) {
      state.currentTicket = ticket;
    },
    ADD_TICKET(state: SupportState, ticket: SupportTicket) {
      state.myTickets.unshift(ticket);
    },
    UPDATE_TICKET(state: SupportState, updatedTicket: SupportTicket) {
      const index = state.myTickets.findIndex((t: SupportTicket) => t.id === updatedTicket.id);
      if (index !== -1) {
        state.myTickets.splice(index, 1, updatedTicket);
      }
      if (state.currentTicket?.id === updatedTicket.id) {
        state.currentTicket = updatedTicket;
      }
      if (state.allTickets?.content) {
        const adminIndex = state.allTickets.content.findIndex((t: SupportTicket) => t.id === updatedTicket.id);
        if (adminIndex !== -1) {
          state.allTickets.content.splice(adminIndex, 1, updatedTicket);
        }
      }
    },
    SET_LOADING(state: SupportState, loading: boolean) {
      state.loading = loading;
    },
    SET_ERROR(state: SupportState, error: string | null) {
      state.error = error;
    },
    CLEAR_ERROR(state: SupportState) {
      state.error = null;
    },
  },

  actions: {
    async createTicket({ commit }: ActionCtx, data: SupportTicketRequest) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const ticket = await supportService.createTicket(data);
        commit('ADD_TICKET', ticket);
        return ticket;
      } catch (error: any) {
        commit('SET_ERROR', getErrorMessage(error, 'Unable to create your ticket'));
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async fetchMyTickets({ commit }: ActionCtx) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const tickets = await supportService.getMyTickets();
        commit('SET_MY_TICKETS', tickets);
      } catch (error: any) {
        commit('SET_ERROR', getErrorMessage(error, 'Unable to load your tickets'));
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async fetchTicketById({ commit }: ActionCtx, ticketId: number) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const ticket = await supportService.getTicketById(ticketId);
        commit('SET_CURRENT_TICKET', ticket);
        return ticket;
      } catch (error: any) {
        commit('SET_ERROR', getErrorMessage(error, 'Unable to load this ticket'));
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async updateTicketStatus(
      { commit }: ActionCtx,
      { ticketId, status }: { ticketId: number; status: TicketStatus }
    ) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const ticket = await supportService.updateTicketStatus(ticketId, status);
        commit('UPDATE_TICKET', ticket);
        return ticket;
      } catch (error: any) {
        commit('SET_ERROR', getErrorMessage(error, 'Unable to update ticket status'));
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async fetchAllTickets({ commit }: ActionCtx, params?: any) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const ticketPage = await supportService.getAllTickets(params);
        commit('SET_ALL_TICKETS', ticketPage);
      } catch (error: any) {
        commit('SET_ERROR', getErrorMessage(error, 'Unable to load tickets'));
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async updateTicketResponse(
      { commit }: ActionCtx,
      { ticketId, response }: { ticketId: number; response: string }
    ) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const ticket = await supportService.updateTicketResponse(ticketId, response);
        commit('UPDATE_TICKET', ticket);
        return ticket;
      } catch (error: any) {
        commit('SET_ERROR', getErrorMessage(error, 'Unable to send response'));
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    clearError({ commit }: ActionCtx) {
      commit('CLEAR_ERROR');
    },
  },
};

export default supportModule;
