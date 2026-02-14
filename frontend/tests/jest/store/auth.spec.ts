import authModule from '@/store/modules/auth';
import { authService } from '@/services/authServices';

jest.mock('@/services/authServices', () => ({
  authService: {
    login: jest.fn(),
    getProfile: jest.fn(),
  },
}));

describe('auth store', () => {
  it('SET_USER updates user and isAuthenticated', () => {
    const state = authModule.state();
    authModule.mutations.SET_USER(state, { id: 1, name: 'A', email: 'a@b.com', role: 'USER', accountBalance: 0 });
    expect(state.user?.id).toBe(1);
    expect(state.isAuthenticated).toBe(true);
  });

  it('login action stores profile', async () => {
    (authService.login as jest.Mock).mockResolvedValue(undefined);
    (authService.getProfile as jest.Mock).mockResolvedValue({
      id: 2,
      name: 'Test',
      email: 'test@zeta.com',
      role: 'USER',
      accountBalance: 0,
    });

    const commit = jest.fn();
    await authModule.actions.login({ commit }, { email: 'test@zeta.com', password: 'pass' });

    expect(authService.login).toHaveBeenCalled();
    expect(authService.getProfile).toHaveBeenCalled();
    expect(commit).toHaveBeenCalledWith('SET_USER', expect.objectContaining({ id: 2 }));
  });
});
