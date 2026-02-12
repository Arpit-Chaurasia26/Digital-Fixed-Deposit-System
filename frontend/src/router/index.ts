import { createRouter, createWebHistory, RouteRecordRaw, NavigationGuardNext, RouteLocationNormalized } from 'vue-router';
import store from '@/store';
import Home from '@/views/Home.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: false },
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/About.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/contact',
    name: 'Contact',
    component: () => import('@/views/Contact.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { requiresAuth: false, requiresGuest: true },
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { requiresAuth: false, requiresGuest: true },
  },
  {
    path: '/user/dashboard',
    name: 'UserDashboard',
    component: () => import('@/views/user/UserDashboard.vue'),
    meta: { requiresAuth: true, role: 'USER' },
  },
  {
    path: '/user/book-fd',
    name: 'BookFD',
    component: () => import('@/views/user/BookFD.vue'),
    meta: { requiresAuth: true, role: 'USER' },
  },
  {
    path: '/user/fd-list',
    name: 'FDList',
    component: () => import('@/views/user/FDList.vue'),
    meta: { requiresAuth: true, role: 'USER' },
  },
  {
    path: '/user/maturing',
    name: 'UserMaturingFDs',
    component: () => import('@/views/user/UserMaturingFDs.vue'),
    meta: { requiresAuth: true, role: 'USER' },
  },
  {
    path: '/user/financial-summary',
    name: 'UserFinancialSummary',
    component: () => import('@/views/user/UserFinancialSummary.vue'),
    meta: { requiresAuth: true, role: 'USER' },
  },
  {
    path: '/user/fd/:id',
    name: 'FDDetails',
    component: () => import('@/views/user/FDDetails.vue'),
    meta: { requiresAuth: true, role: 'USER' },
  },
  {
    path: '/user/fd/:id/interest',
    name: 'FDInterest',
    component: () => import('@/views/user/FDInterest.vue'),
    meta: { requiresAuth: true, role: 'USER' },
  },
  {
    path: '/user/fd/:id/break',
    name: 'BreakFD',
    component: () => import('@/views/user/BreakFD.vue'),
    meta: { requiresAuth: true, role: 'USER' },
  },
  {
    path: '/user/support',
    name: 'SupportTickets',
    component: () => import('@/views/user/SupportTickets.vue'),
    meta: { requiresAuth: true, role: 'USER' },
  },
  {
    path: '/user/support/create',
    name: 'CreateTicket',
    component: () => import('@/views/user/CreateTicket.vue'),
    meta: { requiresAuth: true, role: 'USER' },
  },
  {
    path: '/user/portfolio',
    name: 'Portfolio',
    component: () => import('@/views/user/Portfolio.vue'),
    meta: { requiresAuth: true, role: 'USER' },
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true, role: 'USER' },
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('@/views/admin/AdminDashboard.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/admin/support',
    name: 'AdminSupport',
    component: () => import('@/views/admin/AdminSupport.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/admin/fd-management',
    name: 'AdminFDManagement',
    component: () => import('@/views/admin/AdminFDManagement.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/admin/fd-management/all',
    name: 'AdminFDAll',
    component: () => import('@/views/admin/fd/AdminFDAll.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/admin/fd-management/update-status',
    name: 'AdminFDStatus',
    component: () => import('@/views/admin/fd/AdminFDStatus.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/admin/fd-management/maturing',
    name: 'AdminFDMaturing',
    component: () => import('@/views/admin/fd/AdminFDMaturing.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/admin/fd-management/yearly',
    name: 'AdminFDYearly',
    component: () => import('@/views/admin/fd/AdminFDYearly.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/admin/fd-management/user-portfolio',
    name: 'AdminFDUserPortfolio',
    component: () => import('@/views/admin/fd/AdminFDUserPortfolio.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/admin/fd-management/interest-timeline',
    name: 'AdminFDInterestTimeline',
    component: () => import('@/views/admin/fd/AdminFDInterestTimeline.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/admin/profile',
    name: 'AdminProfile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 };
    }
  },
});

// Navigation guard
router.beforeEach(async (
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext
) => {
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth);
  const requiresGuest = to.matched.some((record) => record.meta.requiresGuest);
  const requiredRole = to.meta.role as string | undefined;

  // Try to fetch user profile if not already loaded
  if (!store.state.auth.user) {
    if (store.state.auth.loading) {
      // Another call already started fetchProfile — wait for it to finish
      await new Promise<void>((resolve) => {
        const unwatch = store.watch(
          (state: any) => state.auth.loading,
          (loading: boolean) => {
            if (!loading) {
              unwatch();
              resolve();
            }
          },
          { immediate: true }
        );
      });
    } else {
      try {
        await store.dispatch('auth/fetchProfile');
      } catch (error) {
        // User not authenticated
      }
    }
  }

  const isAuthenticated = store.getters['auth/isAuthenticated'];
  const user = store.getters['auth/user'];

  // If route requires guest (login/register) and user is authenticated
  if (requiresGuest && isAuthenticated) {
    if (user?.role === 'ADMIN') {
      next({ name: 'AdminDashboard' });
    } else {
      next({ name: 'UserDashboard' });
    }
    return;
  }

  // If route requires authentication
  if (requiresAuth && !isAuthenticated) {
    next({ name: 'Home' });
    return;
  }

  // If route requires specific role
  if (requiredRole && user && user.role !== requiredRole) {
    // Redirect to appropriate dashboard
    if (user.role === 'ADMIN') {
      next({ name: 'AdminDashboard' });
    } else {
      next({ name: 'UserDashboard' });
    }
    return;
  }

  next();
});

export default router;
