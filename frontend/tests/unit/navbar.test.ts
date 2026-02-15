import { describe, it, expect, vi } from 'vitest';
import { mount, RouterLinkStub } from '@vue/test-utils';
import { createStore } from 'vuex';
import { createRouter as createVueRouter, createMemoryHistory } from 'vue-router';
import Navbar from '@/components/common/Navbar.vue';

const createAuthStore = (opts: { isAuthenticated: boolean; isAdmin: boolean; userName?: string }) => {
  const logout = vi.fn().mockResolvedValue(undefined);

  const store = createStore({
    modules: {
      auth: {
        namespaced: true,
        getters: {
          isAuthenticated: () => opts.isAuthenticated,
          isAdmin: () => opts.isAdmin,
          user: () => ({ name: opts.userName ?? 'Test User' }),
        },
        actions: {
          logout,
        },
      },
    },
  });

  return { store, logout };
};

const createTestRouter = () =>
  createVueRouter({
    history: createMemoryHistory(),
    routes: [{ path: '/', component: { template: '<div>Home</div>' } }],
  });

describe('Navbar', () => {
  it('uses home route on brand logo click target', async () => {
    const guest = createAuthStore({ isAuthenticated: false, isAdmin: false }).store;
    const user = createAuthStore({ isAuthenticated: true, isAdmin: false }).store;
    const admin = createAuthStore({ isAuthenticated: true, isAdmin: true }).store;
    const router = createTestRouter();
    await router.push('/');
    await router.isReady();

    const guestWrapper = mount(Navbar, {
      global: {
        plugins: [guest as any, router],
        stubs: { RouterLink: RouterLinkStub },
      },
    });
    const userWrapper = mount(Navbar, {
      global: {
        plugins: [user as any, router],
        stubs: { RouterLink: RouterLinkStub },
      },
    });
    const adminWrapper = mount(Navbar, {
      global: {
        plugins: [admin as any, router],
        stubs: { RouterLink: RouterLinkStub },
      },
    });

    expect(guestWrapper.findComponent(RouterLinkStub).props('to')).toBe('/');
    expect(userWrapper.findComponent(RouterLinkStub).props('to')).toBe('/');
    expect(adminWrapper.findComponent(RouterLinkStub).props('to')).toBe('/');
  });

  it('renders guest links when not authenticated', async () => {
    const { store } = createAuthStore({ isAuthenticated: false, isAdmin: false });
    const router = createTestRouter();
    await router.push('/');
    await router.isReady();

    const wrapper = mount(Navbar, {
      global: {
        plugins: [store as any, router],
        stubs: {
          RouterLink: { template: '<a><slot /></a>' },
        },
      },
    });

    expect(wrapper.text()).toContain('Home');
    expect(wrapper.text()).toContain('Register');
    expect(wrapper.text()).toContain('Login');
  });

  it('logs out and redirects for authenticated admin', async () => {
    const { store, logout } = createAuthStore({ isAuthenticated: true, isAdmin: true, userName: 'Admin' });
    const router = createTestRouter();
    await router.push('/');
    await router.isReady();
    const pushSpy = vi.spyOn(router, 'push').mockResolvedValue(undefined as never);

    const wrapper = mount(Navbar, {
      global: {
        plugins: [store as any, router],
        stubs: {
          RouterLink: { template: '<a><slot /></a>' },
        },
      },
    });

    const logoutBtn = wrapper.find('button.btn.btn-outline.navbar-btn');
    await logoutBtn.trigger('click');

    expect(logout).toHaveBeenCalled();
    expect(pushSpy).toHaveBeenCalledWith('/');
  });
});
