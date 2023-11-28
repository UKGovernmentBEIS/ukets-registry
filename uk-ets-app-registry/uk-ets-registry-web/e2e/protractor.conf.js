exports.config = {
  allScriptsTimeout: 60000,
  baseUrl: 'http://ukets.docker.internal',

  capabilities: {
    browserName: 'chrome',
    chromeOptions: {
      prefs: {
        download: {
          prompt_for_download: false,
          directory_upgrade: true,
          default_directory:
            process.cwd() + '/reports/tests/e2e/execution_outcomes',
        },
      },
      args: [
        '--safe-mode',
        '--no-sandbox',
        '--disable-gpu',
        '--disable-web-security',
        '--disable-popup-blocking',
        '--allow-unchecked-dangerous-downloads',
        '--disable-download-notification',
      ],
    },
  },

  cucumberOpts: {
    compiler: 'ts:ts-node/register',
    'fail-fast': false,
    format: 'json:reports/tests/e2e/cucumber_report.json',
    require: ['./src/**/*.ts'],
    strict: false,
    tags: '(@run) and (not @exec-manual) and (not @api-security)',
  },

  directConnect: true,

  framework: 'custom',
  frameworkPath: require.resolve('protractor-cucumber-framework'),

  onPrepare() {
    require('ts-node').register({
      project: require('path').join(__dirname, './tsconfig.e2e.json'),
    });
  },

  specs: [
    '../../uk-ets-registry-specs/src/test/resources/features/**/*.feature',
  ],
};
