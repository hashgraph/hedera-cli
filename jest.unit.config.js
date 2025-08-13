const base = require('./jest.config');

module.exports = {
  ...base,
  // Match all test files except the dedicated e2e entry (we ignore it below)
  testMatch: ['**/__tests__/**/*.test.ts'],
  testPathIgnorePatterns: [
    ...(base.testPathIgnorePatterns || []),
    '<rootDir>/__tests__/e2e.test.ts',
  ],
  // Load test user config fixture before any modules
  setupFiles: ['<rootDir>/__tests__/setup/jestSetup.ts'],
  // Optionally tighten timeout for unit tests
  testTimeout: 20000,
};
