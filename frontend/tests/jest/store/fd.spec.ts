import fdModule from '@/store/modules/fd';
import { fdService } from '@/services/fdService';
import { FDStatus } from '@/types';

jest.mock('@/services/fdService', () => ({
  fdService: {
    getUserPortfolio: jest.fn(),
  },
}));

describe('fd store', () => {
  it('activeFDs getter filters ACTIVE only', () => {
    const state = fdModule.state();
    state.fds = [
      { id: 1, status: FDStatus.ACTIVE },
      { id: 2, status: FDStatus.MATURED },
    ] as any;
    const result = fdModule.getters.activeFDs(state);
    expect(result).toHaveLength(1);
    expect(result[0].id).toBe(1);
  });

  it('fetchPortfolio commits portfolio', async () => {
    (fdService.getUserPortfolio as jest.Mock).mockResolvedValue({ totalFDs: 1 });
    const commit = jest.fn();
    await fdModule.actions.fetchPortfolio({ commit }, 1);
    expect(fdService.getUserPortfolio).toHaveBeenCalledWith(1);
    expect(commit).toHaveBeenCalledWith('SET_PORTFOLIO', expect.any(Object));
  });
});
