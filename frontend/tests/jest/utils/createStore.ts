import { createStore } from 'vuex';

export const createTestStore = (overrides?: {
  auth?: { getters?: Record<string, any>; actions?: Record<string, any> };
  fd?: { getters?: Record<string, any>; actions?: Record<string, any> };
  support?: { getters?: Record<string, any>; actions?: Record<string, any> };
}) => {
  return createStore({
    modules: {
      auth: {
        namespaced: true,
        getters: {
          isAuthenticated: () => false,
          isAdmin: () => false,
          isUser: () => true,
          user: () => ({ id: 1, name: 'Test User', email: 'test@zeta.com', role: 'USER', accountBalance: 0 }),
          userId: () => 1,
          loading: () => false,
          error: () => null,
          ...(overrides?.auth?.getters || {}),
        },
        actions: {
          logout: jest.fn(),
          fetchProfile: jest.fn(),
          login: jest.fn(),
          register: jest.fn(),
          updateProfile: jest.fn(),
          changePassword: jest.fn(),
          ...(overrides?.auth?.actions || {}),
        },
        mutations: {
          SET_ERROR: jest.fn(),
        },
      },
      fd: {
        namespaced: true,
        getters: {
          portfolio: () => null,
          financialSummary: () => null,
          maturingFDs: () => [],
          allFDs: () => [],
          currentFD: () => null,
          interestTimeline: () => null,
          accruedInterest: () => null,
          eligibilityMessage: () => null,
          ...(overrides?.fd?.getters || {}),
        },
        actions: {
          fetchPortfolio: jest.fn(),
          fetchFinancialSummary: jest.fn(),
          fetchMaturingFDs: jest.fn(),
          fetchUserFDs: jest.fn(),
          fetchFDById: jest.fn(),
          fetchInterestTimeline: jest.fn(),
          fetchAccruedInterest: jest.fn(),
          checkEligibility: jest.fn(),
          ...(overrides?.fd?.actions || {}),
        },
      },
      support: {
        namespaced: true,
        getters: {
          myTickets: () => [],
          allTickets: () => null,
          currentTicket: () => null,
          loading: () => false,
          error: () => null,
          ...(overrides?.support?.getters || {}),
        },
        actions: {
          createTicket: jest.fn(),
          fetchMyTickets: jest.fn(),
          fetchAllTickets: jest.fn(),
          fetchTicketById: jest.fn(),
          updateTicketStatus: jest.fn(),
          updateTicketResponse: jest.fn(),
          ...(overrides?.support?.actions || {}),
        },
      },
    },
  });
};
