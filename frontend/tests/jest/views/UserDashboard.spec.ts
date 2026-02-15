import { mount } from '@vue/test-utils';
import { createStore } from 'vuex';
import UserDashboard from '@/views/user/UserDashboard.vue';

const flushPromises = () => new Promise((resolve) => setTimeout(resolve));

describe('UserDashboard', () => {
  it('renders quick actions and dispatches initial data loads', async () => {
    const store = createStore({
      modules: {
        auth: {
          namespaced: true,
          getters: {
            userId: () => 1,
          },
        },
        fd: {
          namespaced: true,
          getters: {
            portfolio: () => ({
              totalPrincipal: 1000,
              totalInterestAccrued: 100,
              totalFDs: 2,
            }),
            financialSummary: () => ({
              totalInterestAccruedTillDate: 100,
              totalPrincipalDeposited: 1000,
              totalFDsCreated: 2,
            }),
            maturingFDs: () => ([
              { fdId: 10, amount: 1000, maturityDate: '2026-01-01', daysRemaining: 10 },
            ]),
          },
        },
      },
    });

    jest.spyOn(store, 'dispatch').mockResolvedValue(undefined as never);

    const wrapper = mount(UserDashboard, {
      global: {
        plugins: [store as any],
        stubs: {
          RouterLink: { template: '<a><slot /></a>' },
          RouterView: true,
          Navbar: true,
          Footer: true,
          UserSidebar: true,
        },
      },
    });

    await flushPromises();

    expect(wrapper.text()).toContain('Quick Actions');
    expect(wrapper.text()).toContain('Book New FD');
    expect(wrapper.text()).toContain('View All FDs');
    expect(wrapper.text()).toContain('My Portfolio');
    expect(wrapper.text()).toContain('Support Tickets');

    expect(store.dispatch).toHaveBeenCalledWith('fd/fetchPortfolio', 1);
    expect(store.dispatch).toHaveBeenCalledWith(
      'fd/fetchFinancialSummary',
      expect.objectContaining({ userId: 1 })
    );
    expect(store.dispatch).toHaveBeenCalledWith(
      'fd/fetchMaturingFDs',
      expect.objectContaining({ userId: 1 })
    );
  });
});
