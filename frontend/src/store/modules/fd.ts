import { fdService } from '@/services/fdService';
import { withdrawalService } from '@/services/withdrawalService';
import {
  FixedDeposit,
  BookFDRequest,
  FDMaturityResponse,
  FDFinancialYearSummary,
  FDPortfolio,
  FDInterestTimeline,
  FDInterestResponse,
  FDStatus,
  EligibilityMessage
} from '@/types';

export interface FDState {
  fds: FixedDeposit[];
  currentFD: FixedDeposit | null;
  maturingFDs: FDMaturityResponse[];
  financialSummary: FDFinancialYearSummary | null;
  portfolio: FDPortfolio | null;
  interestTimeline: FDInterestTimeline | null;
  accruedInterest: FDInterestResponse | null;
  loading: boolean;
  error: string | null;
  eligibilityMessage:EligibilityMessage | null;
}

type Commit = (type: string, payload?: unknown) => void;
type ActionCtx = { commit: Commit };

const fdModule = {
  namespaced: true,

  state: (): FDState => ({
    fds: [],
    currentFD: null,
    maturingFDs: [],
    financialSummary: null,
    portfolio: null,
    interestTimeline: null,
    accruedInterest: null,
    loading: false,
    error: null,
    eligibilityMessage:null,
  }),

  getters: {
    allFDs: (state: FDState) => state.fds,
    currentFD: (state: FDState) => state.currentFD,
    maturingFDs: (state: FDState) => state.maturingFDs,
    financialSummary: (state: FDState) => state.financialSummary,
    portfolio: (state: FDState) => state.portfolio,
    interestTimeline: (state: FDState) => state.interestTimeline,
    accruedInterest: (state: FDState) => state.accruedInterest,
    activeFDs: (state: FDState) => state.fds.filter((fd: FixedDeposit) => fd.status === FDStatus.ACTIVE),
    maturedFDs: (state: FDState) => state.fds.filter((fd: FixedDeposit) => fd.status === FDStatus.MATURED),
    brokenFDs: (state: FDState) => state.fds.filter((fd: FixedDeposit) => fd.status === FDStatus.BROKEN),
    loading: (state: FDState) => state.loading,
    error: (state: FDState) => state.error,
    eligibilityMessage:(state: FDState)=>state.eligibilityMessage,
  },

  mutations: {
    SET_FDS(state: FDState, fds: FixedDeposit[]) {
      state.fds = fds;
    },
    SET_CURRENT_FD(state: FDState, fd: FixedDeposit | null) {
      state.currentFD = fd;
    },
    ADD_FD(state: FDState, fd: FixedDeposit) {
      state.fds.unshift(fd);
    },
    UPDATE_FD(state: FDState, updatedFD: FixedDeposit) {
      const index = state.fds.findIndex((fd) => fd.id === updatedFD.id);
      if (index !== -1) {
        state.fds.splice(index, 1, updatedFD);
      }
      if (state.currentFD?.id === updatedFD.id) {
        state.currentFD = updatedFD;
      }
    },
    SET_MATURING_FDS(state: FDState, fds: FDMaturityResponse[]) {
      state.maturingFDs = fds;
    },
    SET_FINANCIAL_SUMMARY(state: FDState, summary: FDFinancialYearSummary) {
      state.financialSummary = summary;
    },
    SET_PORTFOLIO(state: FDState, portfolio: FDPortfolio) {
      state.portfolio = portfolio;
    },
    SET_INTEREST_TIMELINE(state: FDState, timeline: FDInterestTimeline) {
      state.interestTimeline = timeline;
    },
    SET_ACCRUED_INTEREST(state: FDState, interest: FDInterestResponse) {
      state.accruedInterest = interest;
    },
    SET_LOADING(state: FDState, loading: boolean) {
      state.loading = loading;
    },
    SET_ERROR(state: FDState, error: string | null) {
      state.error = error;
    },
    CLEAR_ERROR(state: FDState) {
      state.error = null;
    },
    SET_ELIGIBILITY_MESSAGE(state: FDState, eligibilityMessage: EligibilityMessage){
      state.eligibilityMessage=eligibilityMessage;
    }
  },

  actions: {
    async bookFD({ commit }: ActionCtx, data: BookFDRequest) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const fd = await fdService.bookFD(data);
        commit('ADD_FD', fd);
        return fd;
      } catch (error: any) {
        const message = error.response?.data?.message || 'Failed to book FD';
        commit('SET_ERROR', message);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async fetchUserFDs({ commit }: ActionCtx, { userId, filters }: { userId: number; filters?: any }) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const fds = await fdService.getUserFDs(userId, filters);
        commit('SET_FDS', fds);
      } catch (error: any) {
        const message = error.response?.data?.message || 'Failed to fetch FDs';
        commit('SET_ERROR', message);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async fetchFDById({ commit }: ActionCtx, { userId, fdId }: { userId: number; fdId: number }) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const fd = await fdService.getFDById(userId, fdId);
        commit('SET_CURRENT_FD', fd);
        return fd;
      } catch (error: any) {
        const message = error.response?.data?.message || 'Failed to fetch FD';
        commit('SET_ERROR', message);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async fetchMaturingFDs({ commit }: ActionCtx, { userId, days }: { userId: number; days?: number }) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const fds = await fdService.getMaturingFDs(userId, days);
        commit('SET_MATURING_FDS', fds);
      } catch (error: any) {
        const message = error.response?.data?.message || 'Failed to fetch maturing FDs';
        commit('SET_ERROR', message);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async fetchFinancialSummary({ commit }: ActionCtx, { userId, year }: { userId: number; year?: number }) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const summary = await fdService.getFinancialYearSummary(userId, year);
        commit('SET_FINANCIAL_SUMMARY', summary);
      } catch (error: any) {
        const message = error.response?.data?.message || 'Failed to fetch summary';
        commit('SET_ERROR', message);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async fetchPortfolio({ commit }: ActionCtx, userId: number) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const portfolio = await fdService.getUserPortfolio(userId);
        commit('SET_PORTFOLIO', portfolio);
      } catch (error: any) {
        const message = error.response?.data?.message || 'Failed to fetch portfolio';
        commit('SET_ERROR', message);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async fetchInterestTimeline(
      { commit }: ActionCtx,
      { fdId, interval }: { fdId: number; interval?: string }
    ) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const timeline = await fdService.getInterestTimeline(fdId, interval);
        commit('SET_INTEREST_TIMELINE', timeline);
      } catch (error: any) {
        const message = error.response?.data?.message || 'Failed to fetch timeline';
        commit('SET_ERROR', message);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async fetchAccruedInterest({ commit }: ActionCtx, fdId: number) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      try {
        const interest = await fdService.getAccruedInterest(fdId);
        commit('SET_ACCRUED_INTEREST', interest);
      } catch (error: any) {
        const message = error.response?.data?.message || 'Failed to fetch interest';
        commit('SET_ERROR', message);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    clearError({ commit }: ActionCtx) {
      commit('CLEAR_ERROR');
    },

    async fetchEligibilityCheck({commit}:ActionCtx, fdId: number){
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      console.log("hello");
      try{
        const message = await withdrawalService.checkEligibility(fdId);
        commit('SET_ELIGIBILITY_MESSAGE', message);
        console.log(message)
      }catch (error: any) {
        const message = error.response?.data?.message || 'Failed to fetch interest';
        commit('SET_ERROR', message);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    }

  },
};

export default fdModule;
