import { mount } from '@vue/test-utils';
import { createTestStore } from '../utils/createStore';
import AdminDashboard from '@/views/admin/AdminDashboard.vue';
import AdminFDManagement from '@/views/admin/AdminFDManagement.vue';
import AdminSupport from '@/views/admin/AdminSupport.vue';
import AdminFDAll from '@/views/admin/fd/AdminFDAll.vue';
import AdminFDInterestTimeline from '@/views/admin/fd/AdminFDInterestTimeline.vue';
import AdminFDMaturing from '@/views/admin/fd/AdminFDMaturing.vue';
import AdminFDStatus from '@/views/admin/fd/AdminFDStatus.vue';
import AdminFDUserPortfolio from '@/views/admin/fd/AdminFDUserPortfolio.vue';
import AdminFDYearly from '@/views/admin/fd/AdminFDYearly.vue';

jest.mock('vue-router', () => ({
  useRouter: () => ({ push: jest.fn(), replace: jest.fn() }),
  useRoute: () => ({ params: {}, query: {}, path: '/admin', name: 'admin', hash: '' }),
}));

jest.mock('@/services/fdService', () => ({
  fdService: {
    getAdminFinancialYearSummary: jest.fn().mockResolvedValue({
      totalFDsCreated: 0,
      totalPrincipalDeposited: 0,
      totalInterestAccruedTillDate: 0,
    }),
    getAdminAllFDs: jest.fn().mockResolvedValue([]),
    getAdminMaturingFDs: jest.fn().mockResolvedValue([]),
    getAdminFDsByYear: jest.fn().mockResolvedValue([]),
    updateFDStatus: jest.fn().mockResolvedValue({}),
    getAdminUserPortfolio: jest.fn().mockResolvedValue({
      totalFDs: 0,
      activeFDs: 0,
      maturedFDs: 0,
      brokenFDs: 0,
      totalPrincipal: 0,
      totalInterestAccrued: 0,
    }),
    getAdminInterestTimeline: jest.fn().mockResolvedValue({
      fdId: 1,
      principal: 0,
      interestRate: 0,
      interval: 'MONTHLY',
      timeline: [],
    }),
  },
}));

jest.mock('@/services/supportService', () => ({
  supportService: {
    getAllTickets: jest.fn().mockResolvedValue({
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 10,
      number: 0,
    }),
    getTicketById: jest.fn().mockResolvedValue({
      id: 1,
      userId: 1,
      subject: 'Test',
      description: 'Test',
      status: 'OPEN',
      createdTime: '',
      updatedTime: '',
    }),
    updateTicketStatus: jest.fn().mockResolvedValue({ id: 1 }),
    updateTicketResponse: jest.fn().mockResolvedValue({ id: 1 }),
  },
}));

const mountView = (component: any, store = createTestStore({
  auth: { getters: { isAdmin: () => true, isUser: () => false } },
})) =>
  mount(component, {
    global: {
      plugins: [store as any],
      stubs: {
        RouterLink: true,
        RouterView: true,
        Navbar: true,
        Footer: true,
        AdminSidebar: true,
      },
    },
  });

describe('Admin views', () => {
  it('renders AdminDashboard', () => {
    const wrapper = mountView(AdminDashboard);
    expect(wrapper.text()).toContain('Admin Dashboard');
  });

  it('renders AdminFDManagement', () => {
    const wrapper = mountView(AdminFDManagement);
    expect(wrapper.text()).toContain('Admin - FD Management');
  });

  it('renders AdminSupport', () => {
    const wrapper = mountView(AdminSupport);
    expect(wrapper.text()).toContain('Admin - Support Tickets');
  });

  it('renders AdminFDAll', () => {
    const wrapper = mountView(AdminFDAll);
    expect(wrapper.text()).toContain('All Fixed Deposits');
  });

  it('renders AdminFDInterestTimeline', () => {
    const wrapper = mountView(AdminFDInterestTimeline);
    expect(wrapper.text()).toContain('FD Interest Timeline');
  });

  it('renders AdminFDMaturing', () => {
    const wrapper = mountView(AdminFDMaturing);
    expect(wrapper.text()).toContain('Maturing Fixed Deposits');
  });

  it('renders AdminFDStatus', () => {
    const wrapper = mountView(AdminFDStatus);
    expect(wrapper.text()).toContain('Update FD Status');
  });

  it('renders AdminFDUserPortfolio', () => {
    const wrapper = mountView(AdminFDUserPortfolio);
    expect(wrapper.text()).toContain('User Portfolio Lookup');
  });

  it('renders AdminFDYearly', () => {
    const wrapper = mountView(AdminFDYearly);
    expect(wrapper.text()).toContain('FDs by Financial Year');
  });
});
