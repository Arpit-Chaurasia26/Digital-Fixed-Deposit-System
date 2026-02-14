import { fdService } from '@/services/fdService';
import apiClient from '@/services/axios';

describe('fdService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('getUserFDs builds query params', async () => {
    (apiClient.get as jest.Mock).mockResolvedValue({ data: [] });
    await fdService.getUserFDs(1, { status: 'ACTIVE' } as any);
    expect(apiClient.get).toHaveBeenCalledWith('/fd/user/1?status=ACTIVE');
  });

  it('getFDById hits correct endpoint', async () => {
    (apiClient.get as jest.Mock).mockResolvedValue({ data: { id: 2 } });
    const result = await fdService.getFDById(1, 2);
    expect(apiClient.get).toHaveBeenCalledWith('/fd/user/1/2');
    expect(result).toEqual({ id: 2 });
  });
});
