declare module 'vuex' {
  export interface Store<S = any> {
    state: S;
    getters: Record<string, any>;
    dispatch: (type: string, payload?: any) => Promise<any> | any;
    commit: (type: string, payload?: any) => void;
  }

  export function createStore<S = any>(options: any): Store<S>;
  export function useStore<S = any>(): Store<S>;
}
