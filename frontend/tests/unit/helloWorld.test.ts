import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import HelloWorld from '@/components/HelloWorld.vue';

describe('HelloWorld', () => {
  it('renders message and increments count', async () => {
    const wrapper = mount(HelloWorld, {
      props: { msg: 'Hello Test' },
    });

    expect(wrapper.text()).toContain('Hello Test');
    expect(wrapper.text()).toContain('count is 0');

    await wrapper.get('button').trigger('click');
    expect(wrapper.text()).toContain('count is 1');
  });
});
