import { mount } from '@vue/test-utils';
import Footer from '@/components/common/Footer.vue';

describe('Footer', () => {
  it('renders the updated social links', () => {
    const wrapper = mount(Footer);
    const links = wrapper.findAll('.social-links a');
    expect(links).toHaveLength(4);
    const labels = links.map((link) => link.attributes('aria-label'));
    expect(labels).toEqual(['Mail', 'X', 'LinkedIn', 'YouTube']);
  });
});
