/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

const fs = require('fs');

const cucumberOptions = {
  name: 'UK ETS Registry',
  theme: 'bootstrap',
  jsonFile: 'reports/tests/e2e/cucumber_report.json',
  output: 'reports/tests/e2e/output/cucumber_report.html',
  reportSuiteAsScenarios: true,
  scenarioTimestamp: true,
  launchReport: true,
  metadata: {},
};

require('cucumber-html-reporter').generate(cucumberOptions);
