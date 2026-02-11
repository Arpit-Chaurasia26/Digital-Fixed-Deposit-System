/// <reference path="./shims-vue.d.ts" />
/// <reference path="./vuex-shim.d.ts" />

interface ImportMetaEnv {
	readonly VITE_API_BASE_URL?: string;
}

interface ImportMeta {
	readonly env: ImportMetaEnv;
}
