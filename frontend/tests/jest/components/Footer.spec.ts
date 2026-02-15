import { mount } from '@vue/test-utils';
import Footer from '@/components/common/Footer.vue';

describe('Footer', () => {
  it('renders the updated social links', () => {
    const wrapper = mount(Footer, {
      global: {
        stubs: {
          RouterLink: true,
        },
      },
    });

    const links = wrapper.findAll('.social-links a');
    expect(links).toHaveLength(4);
    const labels = links.map((link) => link.attributes('aria-label'));
    expect(labels).toEqual(['Email', 'WhatsApp', 'LinkedIn', 'YouTube']);
    expect(links[0].attributes('href')).toBe('mailto:digitalfd.system@gmail.com');
  });
});
