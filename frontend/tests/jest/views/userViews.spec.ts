import { mount } from '@vue/test-utils';
import { createTestStore } from '../utils/createStore';
import BookFD from '@/views/user/BookFD.vue';
import BreakFD from '@/views/user/BreakFD.vue';
import CreateTicket from '@/views/user/CreateTicket.vue';
import FDDetails from '@/views/user/FDDetails.vue';
import FDInterest from '@/views/user/FDInterest.vue';
import FDList from '@/views/user/FDList.vue';
import FullWithdrawalPage from '@/views/user/FullWIthdrawalPage.vue';
import Portfolio from '@/views/user/Portfolio.vue';
import SupportTickets from '@/views/user/SupportTickets.vue';
import TransactionHistory from '@/views/user/TransactionHistory.vue';
import UserDashboard from '@/views/user/UserDashboard.vue';
import UserFinancialSummary from '@/views/user/UserFinancialSummary.vue';
import UserMaturingFDs from '@/views/user/UserMaturingFDs.vue';

jest.mock('vue-router', () => ({
  useRouter: () => ({ push: jest.fn(), replace: jest.fn() }),
  useRoute: () => ({ params: { id: '1' }, query: {}, path: '/user', name: 'user', hash: '' }),
}));

jest.mock('@/services/fdService', () => ({
  fdService: {
    bookFD: jest.fn().mockResolvedValue({ id: 1 }),
    getUserFDs: jest.fn().mockResolvedValue([]),
    getFDById: jest.fn().mockResolvedValue({
      id: 1,
      userId: 1,
      amount: 1000,
      interestRate: 6.5,
      tenureMonths: 12,
      startDate: '2024-01-01',
      maturityDate: '2025-01-01',
      status: 'ACTIVE',
      interestScheme: 'STANDARD_12_MONTHS',
      accruedInterest: 0,
      createdAt: '',
      updatedAt: '',
    }),
    getMaturingFDs: jest.fn().mockResolvedValue([]),
    getFinancialYearSummary: jest.fn().mockResolvedValue({
      totalFDsCreated: 0,
      totalPrincipalDeposited: 0,
      totalInterestAccruedTillDate: 0,
    }),
    getUserPortfolio: jest.fn().mockResolvedValue({
      totalFDs: 0,
      activeFDs: 0,
      maturedFDs: 0,
      brokenFDs: 0,
      totalPrincipal: 0,
      totalInterestAccrued: 0,
    }),
    getAdminFinancialYearSummary: jest.fn().mockResolvedValue({
      totalFDsCreated: 0,
      totalPrincipalDeposited: 0,
      totalInterestAccruedTillDate: 0,
    }),
    getAllSchemes: jest.fn().mockResolvedValue([]),
  },
}));

jest.mock('@/services/withdrawalService', () => ({
  withdrawalService: {
    getBreakPreview: jest.fn().mockResolvedValue({
      withdrawalAmount: 100,
      accumulatedInterestAmount: 10,
      interestRate: 6.5,
      penalty: 0,
      netInterestAmount: 10,
      balanceAmount: 900,
    }),
    confirmBreak: jest.fn().mockResolvedValue({
      fdId: 1,
      userId: 1,
      principalAmount: 100,
      interestPaid: 10,
      totalPayout: 110,
      penaltyApplied: 0,
      breakDate: '2025-01-01',
      originalMaturityDate: '2026-01-01',
      status: 'BROKEN',
    }),
    checkEligibility: jest.fn().mockResolvedValue({ isEligible: true, rootCause: '' }),
    getTransactionHistory: jest.fn().mockResolvedValue([]),
  },
}));

jest.mock('@/services/supportService', () => ({
  supportService: {
    createTicket: jest.fn().mockResolvedValue({ id: 1 }),
    getMyTickets: jest.fn().mockResolvedValue([]),
    getAllTickets: jest.fn().mockResolvedValue({ content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 }),
    getTicketById: jest.fn().mockResolvedValue({ id: 1 }),
    updateTicketStatus: jest.fn().mockResolvedValue({ id: 1 }),
    updateTicketResponse: jest.fn().mockResolvedValue({ id: 1 }),
  },
}));

const mountView = (component: any, store = createTestStore()) => {
  if (!window.history.state) {
    window.history.replaceState({ amount: 1000 }, '');
  }
  return mount(component, {
    global: {
      plugins: [store as any],
      stubs: {
        RouterLink: true,
        RouterView: true,
        Navbar: true,
        Footer: true,
        UserSidebar: true,
        AdminSidebar: true,
      },
    },
  });
};

describe('User views', () => {
  it('renders UserDashboard', () => {
    const wrapper = mountView(UserDashboard);
    expect(wrapper.text()).toContain('Welcome Back');
  });

  it('renders BookFD', () => {
    const wrapper = mountView(BookFD);
    expect(wrapper.text()).toContain('Book Fixed Deposit');
  });

  it('renders BreakFD', () => {
    const wrapper = mountView(BreakFD);
    expect(wrapper.text()).toContain('Break Fixed Deposit');
  });

  it('renders FullWithdrawalPage', () => {
    const wrapper = mountView(FullWithdrawalPage);
    expect(wrapper.text()).toContain('Break Fixed Deposit');
  });

  it('renders FDList', () => {
    const wrapper = mountView(FDList);
    expect(wrapper.text()).toContain('My Fixed Deposits');
  });

  it('renders FDDetails', () => {
    const wrapper = mountView(
      FDDetails,
      createTestStore({
        fd: {
          getters: {
            currentFD: () => ({
              id: 1,
              amount: 1000,
              interestRate: 6.5,
              tenureMonths: 12,
              startDate: '2024-01-01',
              maturityDate: '2025-01-01',
              status: 'ACTIVE',
              interestScheme: 'STANDARD_12_MONTHS',
              accruedInterest: 0,
            }),
            loading: () => false,
            error: () => null,
          },
        },
      })
    );
    expect(wrapper.text()).toContain('FD Details');
  });

  it('renders FDInterest', () => {
    const wrapper = mountView(FDInterest);
    expect(wrapper.text()).toContain('Interest Timeline');
  });

  it('renders Portfolio', () => {
    const wrapper = mountView(Portfolio);
    expect(wrapper.text()).toContain('My Portfolio');
  });

  it('renders SupportTickets', () => {
    const wrapper = mountView(SupportTickets);
    expect(wrapper.text()).toContain('Support Tickets');
  });

  it('renders CreateTicket', () => {
    const wrapper = mountView(CreateTicket);
    expect(wrapper.text()).toContain('Create Support Ticket');
  });

  it('renders TransactionHistory', () => {
    const wrapper = mountView(TransactionHistory);
    expect(wrapper.text()).toContain('Transaction History');
  });

  it('renders UserFinancialSummary', () => {
    const wrapper = mountView(UserFinancialSummary);
    expect(wrapper.text()).toContain('Financial Year Summary');
  });

  it('renders UserMaturingFDs', () => {
    const wrapper = mountView(UserMaturingFDs);
    expect(wrapper.text()).toContain('Maturing Fixed Deposits');
  });
});
