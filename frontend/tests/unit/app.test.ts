import { describe, it, expect, afterEach } from 'vitest';
import { mount } from '@vue/test-utils';
import { nextTick } from 'vue';
import { createStore } from 'vuex';
import { createRouter, createMemoryHistory } from 'vue-router';
import App from '@/App.vue';

describe('App shell classes', () => {
  afterEach(() => {
    document.body.classList.remove('admin-shell');
    document.body.classList.remove('user-shell');
  });

  it('toggles body shell classes based on route path', async () => {
    const router = createRouter({
      history: createMemoryHistory(),
      routes: [
        { path: '/admin/dashboard', component: { template: '<div>Admin</div>' } },
        { path: '/user/dashboard', component: { template: '<div>User</div>' } },
      ],
    });

    const store = createStore({});

    await router.push('/admin/dashboard');
    await router.isReady();

    mount(App, {
      global: {
        plugins: [store, router],
      },
    });

    expect(document.body.classList.contains('admin-shell')).toBe(true);
    expect(document.body.classList.contains('user-shell')).toBe(false);

    await router.push('/user/dashboard');
    await nextTick();

    expect(document.body.classList.contains('admin-shell')).toBe(false);
    expect(document.body.classList.contains('user-shell')).toBe(true);
  });
});
