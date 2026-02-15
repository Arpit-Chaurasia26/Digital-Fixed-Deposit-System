import { describe, it, expect, beforeEach } from 'vitest';
import { defineComponent, nextTick } from 'vue';
import { mount } from '@vue/test-utils';
import { useTheme } from '@/composables/useTheme';

const ThemeHarness = defineComponent({
  template: '<div />',
  setup() {
    return useTheme();
  },
});

describe('useTheme', () => {
  beforeEach(() => {
    localStorage.clear();
    document.documentElement.removeAttribute('data-theme');
  });

  it('initializes with light theme by default', () => {
    mount(ThemeHarness);

    expect(document.documentElement.getAttribute('data-theme')).toBe('light');
    expect(localStorage.getItem('zeta-fd-theme')).toBe('light');
  });

  it('toggles theme and persists value', async () => {
    const wrapper = mount(ThemeHarness);

    (wrapper.vm as any).toggleTheme();
    await nextTick();

    expect(document.documentElement.getAttribute('data-theme')).toBe('dark');
    expect(localStorage.getItem('zeta-fd-theme')).toBe('dark');
    expect((wrapper.vm as any).isDark()).toBe(true);
  });
});
