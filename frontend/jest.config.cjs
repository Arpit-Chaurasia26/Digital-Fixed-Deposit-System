module.exports = {
  testEnvironment: 'jsdom',
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'vue'],
  testMatch: ['<rootDir>/tests/jest/**/*.spec.ts'],
  transform: {
    '^.+\\.vue$': '@vue/vue3-jest',
    '^.+\\.(ts|tsx)$': ['ts-jest', { useESM: true, tsconfig: 'tsconfig.app.json' }],
  },
  extensionsToTreatAsEsm: ['.ts', '.vue'],
  moduleNameMapper: {
    '^@vue/test-utils$': '<rootDir>/node_modules/@vue/test-utils/dist/vue-test-utils.cjs.js',
    '^@/services/axios$': '<rootDir>/tests/jest/mocks/axios.ts',
    '^\./axios$': '<rootDir>/tests/jest/mocks/axios.ts',
    '^@/(.*)$': '<rootDir>/src/$1',
    '\\.(css|scss|sass)$': 'identity-obj-proxy',
  },
  setupFilesAfterEnv: ['<rootDir>/tests/jest/setup.ts'],
  testEnvironmentOptions: {
    url: 'http://localhost/',
  },
};
