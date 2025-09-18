const { pathsToModuleNameMapper } = require('ts-jest');
// In the following statement, replace `./tsconfig` with the path to your `tsconfig` file
// which contains the path mapping (ie the `compilerOptions.paths` option):
const { compilerOptions } = require('./tsconfig');

module.exports = {
  preset: 'jest-preset-angular',
  roots: ['<rootDir>/src'],
  setupFilesAfterEnv: ['<rootDir>/src/setupJest.ts'],
  testResultsProcessor: 'jest-sonar-reporter',
  // moduleNameMapper allows  local modules to be visible by jest
  moduleNameMapper: {
    ...pathsToModuleNameMapper(compilerOptions.paths, {
      prefix: '<rootDir>/',
    }),
    '^uuid$': require.resolve('uuid'),
  }, //See https://stackoverflow.com/questions/73203367/jest-syntaxerror-unexpected-token-export-with-uuid-library
  cacheDirectory: 'tmp/jest/cache',
  transformIgnorePatterns: [
    'node_modules/(?!@angular|@ngx-translate|ngx-page-scroll-core|keycloak-js|keycloak-angular|@ngrx|@ng-bootstrap|ngx-quill)',
  ],
  coveragePathIgnorePatterns: [
    '/dist/',
    '/node_modules/',
    '/src/testing/',
    '/src/stories/',
    '/src/jestGlobalMocks.ts',
  ],
  globals: {
    'ts-jest': {
      isolatedModules: true,
    },
  },
  coverageReporters: ['clover', 'json', 'lcov'],
  modulePaths: ['<rootDir>'],
  testEnvironment: 'jsdom',
  testRunner: 'jest-jasmine2',
};
