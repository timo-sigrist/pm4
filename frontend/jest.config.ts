import type { Config } from 'jest';
import nextJest from 'next/jest';

const createJestConfig = nextJest({
  dir: './', // Specifies the directory of the Next.js application
});

const config: Config = {
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
    '^@/public/(.*)$': '<rootDir>/public/$1',
  },
  setupFilesAfterEnv: ['<rootDir>/jest.setup.ts'], // Specifies setup files
  clearMocks: true,
  collectCoverage: true,
  collectCoverageFrom: [
    '**/src/**/*.{js,jsx,ts,tsx}', // Ensure correct glob patterns
    '!**/src/**/_*.{js,jsx,ts,tsx}',
    '!**/src/**/*.stories.{js,jsx,ts,tsx}',
    '!**/*.d.ts',
    '!**/node_modules/**',
  ],
  coverageReporters: ['json', 'lcov', 'text', 'clover'],
  coverageThreshold: {
    global: {
      branches: 0,
      functions: 0,
      lines: 0,
      statements: 0,
    },
  },
  testEnvironment: 'jest-environment-jsdom',
  testPathIgnorePatterns: ['/node_modules/'], // Only ignore node_modules by default
  testMatch: [
    '**/tests/**/*.+(ts|tsx|js|jsx)', // Ensure this pattern matches your test files location
    '**/?(*.)+(spec|test).+(ts|tsx|js|jsx)' // General pattern for spec and test files
  ],
};

export default createJestConfig(config);
