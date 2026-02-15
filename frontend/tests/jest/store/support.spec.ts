import supportModule from '@/store/modules/support';
import { supportService } from '@/services/supportService';
import { TicketStatus } from '@/types';

jest.mock('@/services/supportService', () => ({
  supportService: {
    createTicket: jest.fn(),
  },
}));

describe('support store', () => {
  it('UPDATE_TICKET updates lists', () => {
    const state = supportModule.state();
    state.myTickets = [{ id: 1, status: TicketStatus.OPEN }] as any;
    state.allTickets = { content: [{ id: 1, status: TicketStatus.OPEN }] } as any;

    supportModule.mutations.UPDATE_TICKET(state, { id: 1, status: TicketStatus.RESOLVED } as any);

    expect(state.myTickets[0].status).toBe(TicketStatus.RESOLVED);
    expect(state.allTickets?.content[0].status).toBe(TicketStatus.RESOLVED);
  });

  it('createTicket action adds ticket', async () => {
    (supportService.createTicket as jest.Mock).mockResolvedValue({ id: 2, status: TicketStatus.OPEN });
    const commit = jest.fn();
    await supportModule.actions.createTicket({ commit }, { subject: 'S', description: 'D' });
    expect(supportService.createTicket).toHaveBeenCalled();
    expect(commit).toHaveBeenCalledWith('ADD_TICKET', expect.objectContaining({ id: 2 }));
  });
});
