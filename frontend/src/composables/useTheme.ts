import { ref, watch, onMounted } from 'vue';

export type Theme = 'light' | 'dark';

const theme = ref<Theme>('light');

export function useTheme() {
  const initTheme = () => {
    const stored = localStorage.getItem('zeta-fd-theme') as Theme | null;
    if (stored === 'light' || stored === 'dark') {
      theme.value = stored;
    } else {
      // Default to light mode
      theme.value = 'light';
    }
    applyTheme(theme.value);
  };

  const applyTheme = (t: Theme) => {
    document.documentElement.setAttribute('data-theme', t);
    localStorage.setItem('zeta-fd-theme', t);
  };

  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light';
  };

  const isDark = () => theme.value === 'dark';

  watch(theme, (newTheme) => {
    applyTheme(newTheme);
  });

  onMounted(() => {
    initTheme();
  });

  return {
    theme,
    toggleTheme,
    isDark,
    initTheme,
  };
}
