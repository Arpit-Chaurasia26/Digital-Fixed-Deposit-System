import { authService } from '@/services/authServices';
import apiClient from '@/services/axios';

describe('authService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('login posts credentials', async () => {
    (apiClient.post as jest.Mock).mockResolvedValue({ data: {} });
    await authService.login({ email: 'a@b.com', password: 'pass' });
    expect(apiClient.post).toHaveBeenCalledWith('/auth/login', { email: 'a@b.com', password: 'pass' });
  });

  it('getProfile hits profile endpoint', async () => {
    (apiClient.get as jest.Mock).mockResolvedValue({ data: { id: 1 } });
    const profile = await authService.getProfile();
    expect(apiClient.get).toHaveBeenCalledWith('/user/profile');
    expect(profile).toEqual({ id: 1 });
  });
});
