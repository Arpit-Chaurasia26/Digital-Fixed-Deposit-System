import { supportService } from '@/services/supportService';
import apiClient from '@/services/axios';
import { TicketStatus } from '@/types';

describe('supportService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('createTicket posts payload', async () => {
    (apiClient.post as jest.Mock).mockResolvedValue({ data: { id: 1 } });
    const result = await supportService.createTicket({ subject: 'S', description: 'D' });
    expect(apiClient.post).toHaveBeenCalledWith('/support', { subject: 'S', description: 'D' });
    expect(result).toEqual({ id: 1 });
  });

  it('updateTicketStatus uses text/plain payload', async () => {
    (apiClient.patch as jest.Mock).mockResolvedValue({ data: { id: 1 } });
    await supportService.updateTicketStatus(1, TicketStatus.RESOLVED);
    expect(apiClient.patch).toHaveBeenCalledWith(
      '/support/1/status',
      TicketStatus.RESOLVED,
      expect.objectContaining({ headers: { 'Content-Type': 'text/plain' } })
    );
  });
});
