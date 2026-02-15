import { mount } from '@vue/test-utils';
import AdminDashboard from '@/views/admin/AdminDashboard.vue';
import { fdService } from '@/services/fdService';

const flushPromises = () => new Promise((resolve) => setTimeout(resolve, 0));

jest.mock('@/services/fdService', () => ({
  fdService: {
    getAdminFinancialYearSummary: jest.fn(),
  },
}));

describe('AdminDashboard', () => {
  it('loads and displays financial summary', async () => {
    (fdService.getAdminFinancialYearSummary as jest.Mock).mockResolvedValue({
      totalFDsCreated: 3,
      totalPrincipalDeposited: 3000,
      totalInterestAccruedTillDate: 300,
    });

    const wrapper = mount(AdminDashboard, {
      global: {
        stubs: {
          RouterLink: true,
          RouterView: true,
          Navbar: true,
          Footer: true,
          AdminSidebar: true,
        },
      },
    });

    await flushPromises();

    expect(fdService.getAdminFinancialYearSummary).toHaveBeenCalled();
    expect(wrapper.text()).toContain('Financial Summary');
    expect(wrapper.text()).toContain('Total FDs Created');
  });
});
