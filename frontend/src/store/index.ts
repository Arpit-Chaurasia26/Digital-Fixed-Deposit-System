import { createStore } from 'vuex';
import authModule, { AuthState } from './modules/auth';
import fdModule, { FDState } from './modules/fd';
import supportModule, { SupportState } from './modules/support';

export interface RootState {
  auth: AuthState;
  fd: FDState;
  support: SupportState;
}

const store = createStore<RootState>({
  modules: {
    auth: authModule,
    fd: fdModule,
    support: supportModule,
  },
});

export default store;
