import { reactive } from 'vue';

type NotificationType = 'info' | 'success' | 'error';

let hostEl: HTMLDivElement | null = null;

const getHost = () => {
  if (hostEl) return hostEl;

  const el = document.createElement('div');
  el.setAttribute('id', 'app-inline-notifications');
  el.style.position = 'fixed';
  el.style.top = '14px';
  el.style.left = '50%';
  el.style.transform = 'translateX(-50%)';
  el.style.zIndex = '9999';
  el.style.display = 'flex';
  el.style.flexDirection = 'column';
  el.style.gap = '8px';
  el.style.width = 'min(92vw, 680px)';
  el.style.alignItems = 'center';
  el.style.pointerEvents = 'none';
  document.body.appendChild(el);
  hostEl = el;
  return el;
};

const getTheme = () => document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';

const normalizeMessage = (message?: unknown) => {
  if (message === null || message === undefined) return 'Notification';
  if (typeof message === 'string') return message;
  return String(message);
};

const confirmState = reactive({
  visible: false,
  message: '',
});

let confirmResolver: ((value: boolean) => void) | null = null;

export const showNotification = (
  message?: unknown,
  type: NotificationType = 'info',
  duration = 4000,
) => {
  const host = getHost();
  const line = document.createElement('div');
  const isDark = getTheme() === 'dark';

  line.textContent = normalizeMessage(message);
  line.style.fontSize = '14px';
  line.style.fontWeight = '600';
  line.style.lineHeight = '1.4';
  line.style.padding = '10px 14px';
  line.style.borderRadius = '10px';
  line.style.border = isDark ? '1px solid rgba(255,255,255,0.16)' : '1px solid rgba(0,0,0,0.1)';
  line.style.background = isDark ? 'rgba(17,24,39,0.95)' : 'rgba(255,255,255,0.96)';
  line.style.boxShadow = isDark
    ? '0 8px 20px rgba(0, 0, 0, 0.45)'
    : '0 8px 18px rgba(0, 0, 0, 0.14)';
  line.style.maxWidth = '100%';
  line.style.width = 'fit-content';
  line.style.textAlign = 'center';
  line.style.pointerEvents = 'auto';
  line.style.opacity = '1';
  line.style.transition = 'opacity 220ms ease';

  if (type === 'success') line.style.color = isDark ? '#4ade80' : '#15803d';
  else if (type === 'error') line.style.color = isDark ? '#f87171' : '#b91c1c';
  else line.style.color = isDark ? '#93c5fd' : '#1d4ed8';

  host.appendChild(line);

  window.setTimeout(() => {
    line.style.opacity = '0';
    window.setTimeout(() => {
      line.remove();
    }, 220);
  }, duration);
};

export const showAlert = (message?: unknown, type: NotificationType = 'info') => {
  showNotification(message, type);
};

export const showConfirm = (message?: unknown) => new Promise<boolean>((resolve) => {
  if (confirmResolver) confirmResolver(false);
  confirmState.message = normalizeMessage(message);
  confirmState.visible = true;
  confirmResolver = resolve;
});

export const resolveConfirm = (accepted: boolean) => {
  if (confirmResolver) {
    confirmResolver(accepted);
    confirmResolver = null;
  }
  confirmState.visible = false;
};

export const useNotifications = () => ({
  showNotification,
  showAlert,
  showConfirm,
  resolveConfirm,
  confirmState,
});
