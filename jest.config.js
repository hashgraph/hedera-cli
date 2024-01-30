/** @type {import('ts-jest').JestConfigWithTsJest} */
module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  testTimeout: 40000,
  testPathIgnorePatterns: [
    "<rootDir>/__tests__/helpers/"
  ]
};