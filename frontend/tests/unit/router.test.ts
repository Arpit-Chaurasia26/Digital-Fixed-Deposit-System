import { describe, it, expect } from 'vitest';
import router from '@/router';

describe('router', () => {
  it('contains key application routes', () => {
    const routes = router.getRoutes();
    const names = routes.map((r) => String(r.name));

    expect(names).toContain('Home');
    expect(names).toContain('Login');
    expect(names).toContain('UserDashboard');
    expect(names).toContain('AdminDashboard');
    expect(names).toContain('NotFound');
  });

  it('uses default scroll behavior', () => {
    const scrollBehavior = router.options.scrollBehavior as any;
    const saved = { left: 10, top: 20 };

    expect(scrollBehavior({}, {}, saved)).toEqual(saved);
    expect(scrollBehavior({}, {}, null)).toEqual({ top: 0 });
  });
});
