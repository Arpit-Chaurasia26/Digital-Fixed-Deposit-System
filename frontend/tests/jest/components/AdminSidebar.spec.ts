import { mount } from '@vue/test-utils';
import AdminSidebar from '@/components/admin/AdminSidebar.vue';

describe('AdminSidebar', () => {
  it('renders admin navigation links', () => {
    const wrapper = mount(AdminSidebar, {
      global: {
        stubs: {
          RouterLink: { template: '<a><slot /></a>' },
        },
      },
    });
    const links = wrapper.findAll('a');
    expect(links.length).toBeGreaterThan(5);
    expect(wrapper.text()).toContain('FD Management');
    expect(wrapper.text()).toContain('Support');
  });
});
