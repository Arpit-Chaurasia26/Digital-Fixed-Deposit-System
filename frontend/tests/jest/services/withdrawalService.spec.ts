import { withdrawalService } from '@/services/withdrawalService';
import apiClient from '@/services/axios';

describe('withdrawalService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('getBreakPreview requests preview', async () => {
    (apiClient.get as jest.Mock).mockResolvedValue({ data: { amount: 100 } });
    const result = await withdrawalService.getBreakPreview(5, 1000);
    expect(apiClient.get).toHaveBeenCalledWith('/fd/5/break-preview?amount=1000');
    expect(result).toEqual({ amount: 100 });
  });

  it('confirmBreak posts break request', async () => {
    (apiClient.post as jest.Mock).mockResolvedValue({ data: { id: 5 } });
    const result = await withdrawalService.confirmBreak(5, 1000);
    expect(apiClient.post).toHaveBeenCalledWith('/fd/5/break?amount=1000');
    expect(result).toEqual({ id: 5 });
  });
});
