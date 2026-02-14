import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import UserSidebar from '@/components/user/UserSidebar.vue';

describe('UserSidebar', () => {
  it('renders user navigation links', () => {
    const wrapper = mount(UserSidebar, {
      global: {
        stubs: {
          RouterLink: { template: '<a><slot /></a>' },
        },
      },
    });

    const links = wrapper.findAll('a');
    expect(links.length).toBeGreaterThan(7);
    expect(wrapper.text()).toContain('Book FD');
    expect(wrapper.text()).toContain('Support Tickets');
  });
});
