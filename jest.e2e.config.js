const base = require('./jest.config');

module.exports = {
  ...base,
  // Only run the single e2e test file
  testMatch: ['**/__tests__/e2e.test.ts'],
  // Increase timeout for slower network / integration steps
  testTimeout: 90000,
  // E2E often needs runInBand to avoid shared state/race conditions
  maxWorkers: 1,
  setupFiles: ['<rootDir>/__tests__/setup/jestSetup.ts'],
};
