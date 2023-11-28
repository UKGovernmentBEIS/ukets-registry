/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

import { assert, expect } from 'chai';
import {
  After,
  AfterAll,
  Before,
  BeforeAll,
  Given,
  setDefaultTimeout,
  TableDefinition,
  Then,
  When,
} from 'cucumber';
import { browser, By, by } from 'protractor';
import { KnowsThePage } from '../util/knows-the-page.po';
import { PageObjectPerScreenFactory } from '../util/app.po-per-screen.util';
import { AppRoutesPerScreenUtil } from '../util/app.routes-per-screen.util';
import { KeycloakClient } from '../util/keycloak-client.util';
import { MailhogClient } from '../util/mail-client.util';
import { RegistryScreen } from '../util/screens';
import { A11yMatrix } from '../util/a11y-matrix.util';
import {
  delay,
  setDynamicGherkinValueToActualData,
  millisecondsToMinutesRounded,
  getValidCurrentUrl,
  getRegistryRolesByGherkinName,
  clearBrowserData,
  removeAllSpacesLineBreaksAndTabsInString,
  replaceAllInString,
  existsSubstringInArrayElements,
  getCurrentMomentByFormat,
  getRelativeDate,
} from '../util/step.util';
import { RegistryDbUserTestData } from '../util/test-data/registry-db/user/utils/registry-db-user.util';
import { RegistryDbTestUser } from '../util/test-data/registry-db/user/model/registry-db-test-user';
import {
  submitItlNotificationRequest,
  submitProposalRequestInboundTransaction,
} from '../util/emulator-client.util';
import * as path from 'path';
import { TwoFactorAuthenticationUtils } from '../util/two-factor-authentication-utils';
import {
  addEntryToMap,
  deleteAllMapEntries,
  deleteMapEntry,
  getMapEntryValueAttributeByKey,
  printMapEntries,
  updateEntryKey,
} from '../util/user-2fa-map-utils';
import { ResetAllTestData } from '../util/test-data/test-data.util';
import { RegistryDbAllocationEntryBuilder } from '../util/test-data/registry-db/allocation-entry/model/registry-db-allocation-entry.builder';
import { RegistryDbAllocationEntryTestData } from '../util/test-data/registry-db/allocation-entry/registry-db-allocation-entry.util';
import { RegistryDbAllocationStatusBuilder } from '../util/test-data/registry-db/allocation-status/model/registry-db-allocation-status.builder';
import { RegistryDbAllocationStatusTestData } from '../util/test-data/registry-db/allocation-status/registry-db-allocation-status.util';
import { RegistryDbAccountAccessTestData } from '../util/test-data/registry-db/account/utils/registry-db-account-access.util';
import { RegistryDbAccountAccessBuilder } from '../util/test-data/registry-db/account/model/account-access/registry-db-account-access.builder';

let twoFactorAuth = null;
const emailClient = new MailhogClient();
const keycloakClient = new KeycloakClient('uk-ets');
const a11yMatrix = new A11yMatrix();
let scenarioTitle = '';

const waitNeededWebElements: Array<string> = [
  'Show filters',
  'Search',
  'Accept and continue',
  'Submit',
  'Submit request',
  'Is this an installation transfer: Yes',
  'Is this an installation transfer: No',
  'typeahead',
  'id',
  'item',
  'Enter registry activation code link',
  'ETS Administration',
  'Emissions and Surrenders',
];

let beforeDateAll = null;
let afterDateAll = null;
let execDurationTotal = null;
let testCasesResultsSynopsis = new Map();
let testCaseId = '';

export class GlobalState {
  public static page: KnowsThePage;
  public static currentEmailLink: string;
  public static currentEmailBody: string;
  public static protractorConnectionUp = true;
  public static currentUser: RegistryDbTestUser;
  public static hasScenarioTestDataError: boolean;
}

export class CommonFunctions {
  public static expectEqualityWithSnapshot(knowsThePage: KnowsThePage) {
    knowsThePage.snapshotData.forEach(
      async (promise: Promise<string>, key: string) => {
        expect(await promise).to.equal(knowsThePage.getData().get(key));
      }
    );
  }
}

process.on('unhandledRejection', (reason, promise) => {
  console.log('Unhandled Rejection at:', promise, 'reason:', reason);
  assert.fail();
});

process.on('uncaughtException', function (err) {
  console.error(`uncaughtException: '${err}'.`);
});

process.on('beforeExit', function (err) {
  console.error(`beforeExit: '${err}'.`);
});

BeforeAll(async () => {
  beforeDateAll = new Date();
  console.log(`Starting Test Execution: '${beforeDateAll}'.\n`);
  setDefaultTimeout(KnowsThePage.TIMEOUT);
});

Before(async (scenario) => {
  await Promise.all([delay(2000)]);
  testCaseId = '';
  scenarioTitle = scenario.pickle.name;
  GlobalState.hasScenarioTestDataError = false;
  let scenarioTags = '';
  scenario.pickle.tags.forEach((elem) => {
    if (elem.name.includes('test-case-id')) {
      testCaseId = elem.name;
      console.log(`testCaseId: '${testCaseId}'.`);
    }
    scenarioTags = scenarioTags + `, ${elem.name}`;
  });

  scenarioTags = scenarioTags.substr(2);
  if (scenarioTags.includes(`@exec-manual`)) {
    // assertion failure in before hook sets the
    // scenario results to "skipped", instead of "failed":
    assert.fail(
      `About to skip current scenario due to @exec-manual existence.`
    );
  }

  const beforeScenarioMetrics = {
    Item_0: {
      ATTRIBUTE: `Current scenario ` + scenario.pickle.tags.length + ` tag(s)`,
      STARTING_TEST_EXECUTION: scenarioTags,
    },
    Item_1: {
      ATTRIBUTE: 'Cucumber Scenario title',
      STARTING_TEST_EXECUTION: scenarioTitle,
    },
    Item_2: {
      ATTRIBUTE: 'Gherkin language',
      STARTING_TEST_EXECUTION: scenario.pickle.language,
    },
    Item_3: {
      ATTRIBUTE: 'Feature line',
      STARTING_TEST_EXECUTION: `${scenario.sourceLocation.line}`,
    },
    Item_4: {
      ATTRIBUTE: 'File location path',
      STARTING_TEST_EXECUTION: scenario.sourceLocation.uri,
    },
  };
  console.table(beforeScenarioMetrics);

  await Promise.all([
    console.log(`Before scenario hook: about to call keycloakClient.auth()`),
    keycloakClient.auth(),
    delay(2000),
  ]).then((values) => {
    console.log(`keycloakClient.auth finished.`);
  });

  await Promise.all([
    console.log(
      `Before scenario hook: about to call browser.waitForAngularEnabled(true)`
    ),
    browser.waitForAngularEnabled(true),
    console.log(`Before scenario hook: about to call ResetAllTestData()`),
    ResetAllTestData(),
  ]).then((values) => {
    console.log(`ResetAllTestData finished.`);
  });

  await Promise.all([
    console.log(`Before scenario hook: about to call emailClient.deleteAll()`),
    emailClient.deleteAll(),
  ]).then((values) => {
    console.log(`emailClient.deleteAll finished.`);
  });

  GlobalState.currentEmailLink = '';
  GlobalState.currentEmailBody = '';
  GlobalState.protractorConnectionUp = true;
  PageObjectPerScreenFactory.initAllPageObjects();
});

After(async (scenario) => {
  const lastHtmlScreen = await browser.getPageSource();
  await clearBrowserData(browser);

  let scenarioAfterTags = '';
  scenario.pickle.tags.forEach((elem) => {
    if (elem.name.includes('test-case-id')) {
      console.log(
        `Test case with id '${elem.name}' will be added into map with '${scenario.result.status}' result.`
      );

      // test case id is not always unique because a test case may have multiple examples.
      // Map command 'set' override thus existing test cases.
      // This problem is resolved setting map key with the value of test case id plus datetime stamp:
      testCasesResultsSynopsis.set(
        new Date() + `, ` + testCaseId,
        scenario.result.status
      );
    }
    scenarioAfterTags = scenarioAfterTags + `, ${elem.name}`;
  });
  scenarioAfterTags = scenarioAfterTags.substr(2);

  console.log(`Latest testCasesResultsSynopsis:`);
  testCasesResultsSynopsis.forEach((value: string, key: string) => {
    console.log(key, value);
  });

  if (KnowsThePage.getGovernmentAccountHolderId() !== '') {
    KnowsThePage.setGovernmentAccountHolderId('');
  }

  const afterScenarioMetrics = {
    Item_0: {
      Attribute: 'Feature File Relative Location',
      Value: scenario.sourceLocation.uri,
    },
    Item_1: {
      Attribute: 'Scenario Title',
      Value: scenario.pickle.name,
    },
    Item_2: {
      Attribute: 'Scenario Line',
      Value: scenario.sourceLocation.line,
    },
    Item_3: {
      Attribute: 'Scenario Execution Result',
      Value: scenario.result.status,
    },
    Item_4: {
      Attribute: 'Scenario Execution Duration (minutes)',
      Value: `${millisecondsToMinutesRounded(scenario.result.duration)}`,
    },
    Item_5: {
      Attribute: 'Scenario Tags',
      Value: `${scenarioAfterTags}`,
    },
  };

  console.log(`About to clear user 2fa map entries during after scenario hook`);
  deleteAllMapEntries(KnowsThePage.users2fa);
  await ResetAllTestData();
  await emailClient.deleteAll();
  await console.table(afterScenarioMetrics);

  GlobalState.hasScenarioTestDataError
    ? assert.fail(
        `\n\nPostgrest Test data exception. Test case will be marked as failed. \n\n${lastHtmlScreen}\n\n`
      )
    : console.log(
        `\n\nSuccessfully no error found during test data database transactions of insert/delete/update.`
      );

  if (scenario.result.status !== 'passed') {
    console.log(
      `Scenario result status is "${scenario.result.status}" instead of "passed".\n` +
        `For debugging purpose the last screen page source below can be used:\n` +
        `e.g. search web elements or depict html in a browser window): \n\n${lastHtmlScreen}\n\n`
    );
  }

  await delay(2000);
  const signOutWebElements = await browser.findElements(
    by.partialLinkText(`Sign out`)
  );

  if (
    (await browser.getPageSource()).includes(`Sign out`) &&
    signOutWebElements.length > 0
  ) {
    console.log(`⚓  Executing Signing out.\n`);
    (await browser.findElement(by.partialLinkText(`Sign out`))).click();
    expect((await browser.getPageSource()).includes(`Sign in`));
  } else {
    console.log(`No need for sign out.`);
  }

  if (GlobalState.currentUser !== undefined) {
    await keycloakClient.deleteUsers(Array(GlobalState.currentUser.email));
    await delay(2500);
  } else {
    console.log(`No need for user deletion via keycloakClient.`);
  }
});

AfterAll(async () => {
  afterDateAll = new Date();
  execDurationTotal =
    String(
      millisecondsToMinutesRounded(Math.abs(afterDateAll - beforeDateAll))
    ) + ' minutes';

  console.log(
    `\n► T E R M I N A T I N G  A L L  T E S T  C A S E S  E X E C U T I O N  A T:` +
      `\n►'${afterDateAll}'.` +
      `\n► Total Test Execution Duration: '${execDurationTotal}'.\n`
  );

  // printA11yReport();
});

async function a11yTheCurrentScreen() {
  console.log(`Entered a11yTheCurrentScreen.`);
  let shouldRetry = true;
  let retries = 1;
  const millisecondsIntermediateWait = 500;
  while (shouldRetry && retries < 4) {
    try {
      await require('axe-webdriverjs')(browser.driver).analyze(
        (err, results) => {
          if (err) {
            console.error(`\n A11y error: '${err}'\n.`);
          }
          if (results && results.violations) {
            a11yMatrix.update(GlobalState.page.name, results.violations);
          }
        }
      );
      shouldRetry = false;
    } catch (ex) {
      retries++;
      console.log(`About to wait for '${millisecondsIntermediateWait}'.`);
      await delay(millisecondsIntermediateWait);
      console.log(`Error while trying to a11y at try number '${retries}'.`);
    }
  }
  console.log(`Exiting a11yTheCurrentScreen.`);
}

Before('@deleteUnregisteredUser', async () => {
  console.log(`\n⚓  Executing Before hook: '@deleteUnregisteredUser'.`);
  await keycloakClient.deleteUserByEmail('user_unregistered@test.com');
});

Before('@deleteNewTestUser', async () => {
  console.log(`\n⚓  Executing Before hook: '@deleteNewTestUser'.`);
  await keycloakClient.deleteUserByEmail('new_test@test.com');
});

After('@deleteUnregisteredUser', async () => {
  console.log(`\n⚓  Executing After hook: '@deleteUnregisteredUser'.`);
  await keycloakClient.deleteUserByEmail('user_unregistered@test.com');
  await delay(2500);
});

Then('I refresh the current page', async () => {
  console.log(`Executing step: I refresh the current page.`);
  await Promise.all([
    browser.waitForAngularEnabled(),
    browser.refresh(),
    browser.wait(
      async () =>
        (await browser.driver.executeScript('return document.readyState;')) ===
        'complete',
      5000
    ),
  ]);
  console.log(`Exiting step: I refresh the current page.`);
});

Then(
  'I see the elements {string} being {string} sorted by {string}',
  async (
    elements: string,
    sort: 'descending' | 'ascending',
    sortBy: string
  ) => {
    console.log(`Executing step: I refresh the current page.`);
    browser.waitForAngularEnabled();
  }
);

Then(
  'I choose using {string} from {string} the {string} file',
  async (uploadButtonId, fileResourceSubfolderFolder, fileName) => {
    console.log(
      `Executing step: I choose using '${uploadButtonId}' from '${fileResourceSubfolderFolder}' the '${fileName}' file`
    );
    browser.waitForAngularEnabled();
    await delay(1000);
    await (
      await browser.findElement(by.xpath(`//*[@id='` + uploadButtonId + `']`))
    ).sendKeys(
      path.resolve(
        path.resolve(
          __dirname,
          `../resources/` + fileResourceSubfolderFolder + `/` + fileName
        )
      )
    );
  }
);

Then(
  'I receive an {string} email message regarding the {string} email address',
  async (emailSubject, emailAddress) => {
    console.log(
      `Executing step: I receive an "${emailSubject}" email message regarding the "${emailAddress}" email address.`
    );
    await Promise.all([
      (GlobalState.currentEmailBody = await emailClient.getLatestEmail(
        emailSubject,
        emailAddress
      )),
    ]);
  }
);

When('I click the correct email link', async () => {
  console.log(
    `Executing step: I click the correct email link.\n` +
      `About to click href email body: '${GlobalState.currentEmailBody}'.`
  );

  GlobalState.currentEmailLink = removeAllSpacesLineBreaksAndTabsInString(
    GlobalState.currentEmailBody.match(new RegExp(`http[^">]*`)).toString()
  );

  GlobalState.currentEmailLink =
    GlobalState.currentEmailLink.split('Ifyoudidnotrequest')[0];
  GlobalState.currentEmailLink = GlobalState.currentEmailLink.split(
    'Thislinkwillexpirein'
  )[0];

  console.log(
    `GlobalState.currentEmailLink before: '${GlobalState.currentEmailLink}'.`
  );

  GlobalState.currentEmailLink = replaceAllInString(
    GlobalState.currentEmailLink,
    new Map().set('&#61;', '=')
  );

  console.log(
    `GlobalState.currentEmailLink: '${GlobalState.currentEmailLink}'.`
  );
  assert.isTrue(
    !(
      GlobalState.currentEmailLink === '' ||
      GlobalState.currentEmailLink === null ||
      GlobalState.currentEmailLink === undefined
    ),
    `GlobalState.currentEmailLink is '${GlobalState.currentEmailLink}'.`
  );

  await browser.get(GlobalState.currentEmailLink);
  console.log(`Got currentEmailLink.`);
});

When(
  'I access {string} in {string}',
  async (
    value: string,
    menu:
      | 'user-details'
      | 'transaction-details'
      | 'task-details'
      | 'account-details'
  ) => {
    console.log(`Executing step: I access '${value}' in '${menu}'.`);

    let latestPageSource = '';
    let expectedPage: string;
    let hasError = false;

    try {
      // case of account access
      if (menu.includes('account')) {
        expectedPage = 'Account list';
        await Promise.all([
          browser
            .findElement(by.xpath(KnowsThePage.LOCATOR_XPATH_MENU_ACCOUNTS))
            .click(),
          expect(
            browser
              .getCurrentUrl()
              .then((url) => url.includes(`${browser.baseUrl}/account-list`))
          ),
          sleep(100),
        ]);
      }

      // case of task access
      else if (menu.includes('task')) {
        expectedPage = 'Task List';
        await Promise.all([
          browser
            .findElement(by.xpath(KnowsThePage.LOCATOR_XPATH_MENU_TASKS))
            .click(),
          expect(
            browser
              .getCurrentUrl()
              .then((url) => url.includes(`${browser.baseUrl}/task-list`))
          ),
          sleep(700),
        ]);
        console.log(`Accessed tasks screen`);
      }

      // case of user access
      else if (menu.includes('user')) {
        expectedPage = 'User Administration';
        await Promise.all([
          browser
            .findElement(
              by.xpath(KnowsThePage.LOCATOR_XPATH_MENU_USER_ADMINISTRATION)
            )
            .click(),
          expect(
            browser
              .getCurrentUrl()
              .then((url) => url.includes(`${browser.baseUrl}/user-list`))
          ),
          sleep(600),
        ]);
        console.log(`Accessed users screen`);
      }

      // case of transaction access
      else if (menu.includes('transaction')) {
        expectedPage = 'Search Transactions';
        await Promise.all([
          browser
            .findElement(by.xpath(KnowsThePage.LOCATOR_XPATH_MENU_TRANSACTIONS))
            .click(),
          expect(
            browser
              .getCurrentUrl()
              .then((url) =>
                url.includes(`${browser.baseUrl}/transaction-list`)
              )
          ),
          sleep(600),
        ]);
        console.log(`Accessed transactions screen`);
      }
      // invalid bdd input
      else {
        hasError = true;
        console.log(`Invalid arguments given.`);
      }

      // initialize web elements
      GlobalState.page = PageObjectPerScreenFactory.getFromScreen(expectedPage);
      await browser.waitForAngularEnabled(GlobalState.protractorConnectionUp);
      browser.waitForAngularEnabled(false);
      await delay(1500);

      // in case of tasks select Task status: All because the default value is All except complete
      if (
        menu.includes('task') &&
        GlobalState.page.locators.get(`Show filters`)
      ) {
        await GlobalState.page.clickButton(`Show filters`);
        await GlobalState.page.clickButton(`Task status: All`);
        await GlobalState.page.clickButton(`Advanced search`);
        await GlobalState.page.webElementActionApply(
          '',
          GlobalState.page.locators.get(`Exclude user tasks: No`),
          'select'
        );
        //await GlobalState.page.clickButton(`Exclude user tasks: No`);
      }
      if (menu.includes('Accounts')) {
        await GlobalState.page.clickButton(`Search accounts`);
      }

      // in case of transactions

      // click on Show filters
      if (GlobalState.page.locators.get(`Search`)) {
        await GlobalState.page.clickButton(`Search`);
      }

      // initialize web elements
      GlobalState.page = PageObjectPerScreenFactory.getFromScreen(expectedPage);
      await browser.waitForAngularEnabled(GlobalState.protractorConnectionUp);
      browser.waitForAngularEnabled(false);
      await delay(1000);
      // await GlobalState.page.waitForMe();

      // click search result item
      await Promise.all([
        expect(browser.getPageSource().then((ps) => (latestPageSource = ps))),
      ]);

      await Promise.all([
        expect(latestPageSource.includes(value)),
        browser
          .findElement(by.xpath(`(//*[contains(text(),'${value}')])[1]`))
          .click(),
      ]);
    } catch (e) {
      hasError = true;
      console.error(`Exception in I access element flow: '${e}'.`);
    }
    if (hasError) {
      assert.fail(
        `Could not access '${value}' in '${menu}'. Latest page source is: '${latestPageSource}'.`
      );
    }
  }
);

When('I get {string} screen', async (urlGherkinName) => {
  console.log(`Executing step: I get ${urlGherkinName} screen.`);
  const fullUrl = browser.baseUrl + urlGherkinName;
  console.log(`Trying to get fullUrl: '${fullUrl}'.`);
  await browser.get(fullUrl);
});

const sleep = (milliseconds) => {
  return new Promise((resolve) => setTimeout(resolve, milliseconds));
};

Then(
  'The current page {string} in read-only mode',
  async (readOnlyModeEnabled: 'is' | 'is not') => {
    console.log(
      `Executing step: The current page "${readOnlyModeEnabled}" in read-only mode.`
    );

    browser.waitForAngularEnabled();
    const currentPageSource = browser.getPageSource().toString();

    assert.isTrue(
      !(
        currentPageSource.includes('type="text"') ||
        currentPageSource.includes('<option value=')
      ) ===
        (readOnlyModeEnabled === 'is')
    );
  }
);

Then('I get a Request Id', async () => {
  console.log(`Executing step: I get a Request Id.`);
  await KnowsThePage.setRequestId(
    await GlobalState.page.webElementActionApply(
      '',
      GlobalState.page.locators.get('Request ID'),
      'getText'
    )
  );
});

Then('I get a User Id', async () => {
  console.log(`Executing step: I get a User Id.`);
  await KnowsThePage.setUserId(
    await GlobalState.page.webElementActionApply(
      '',
      GlobalState.page.locators.get('User ID'),
      'getText'
    )
  );
});

Then(
  'I get a new otp based on the existing secret for the {string} user',
  async (username: string) => {
    console.log(
      `Executing step: I get a new otp based on the existing secret for the '${username}' user.`
    );

    printMapEntries(KnowsThePage.users2fa);

    const secret = getMapEntryValueAttributeByKey(
      KnowsThePage.users2fa,
      username,
      'secret'
    );

    const oldOtp = getMapEntryValueAttributeByKey(
      KnowsThePage.users2fa,
      username,
      'otp'
    );
    if (twoFactorAuth === null) {
      twoFactorAuth = new TwoFactorAuthenticationUtils();
    }
    await Promise.all([
      twoFactorAuth.setVerificationCodeManually(
        username,
        secret,
        'sha256',
        'totp',
        '6'
      ),
    ]);

    const newOtp = getMapEntryValueAttributeByKey(
      KnowsThePage.users2fa,
      username,
      'otp'
    );

    console.log(
      `Old otp was '${oldOtp}' and based on the ` +
        `existing secret '${secret}' the new otp is '${newOtp}'.`
    );
  }
);

When(
  'I sign in as {string} user',
  async (
    userRole:
      | 'unregistered'
      | 'registered'
      | 'validated'
      | 'enrolled'
      | 'senior admin'
      | 'junior admin'
      | 'read only admin'
      | 'unenrollment pending'
      | 'unenrolled'
      | 'registry admin'
  ) => {
    console.log(`Executing step: I sign in as "${userRole}" user.`);
    await browser.waitForAngularEnabled(false);
    try {
      await Promise.all([preSignIn()]);
    } catch (ex) {
      console.error(`Could not apply preSignIn, ex: '${ex}'.`);
    }

    let email: string;
    let password: string;
    if (userRole === 'registry admin') {
      email = password = 'uk-ets-admin';
    } else {
      GlobalState.currentUser = await createUser(userRole, 0);
      email = GlobalState.currentUser.email;
      password = KnowsThePage.DEFAULT_SIGN_IN_PASSWORD;
    }

    console.log(`About to performSignIn`);
    await performSignIn(GlobalState.currentUser.username, email, password);
  }
);

When(
  'I sign in as {string} user having current registry activation code issued before {string} days',
  async (
    userRole:
      | 'unregistered'
      | 'registered'
      | 'validated'
      | 'enrolled'
      | 'senior admin'
      | 'junior admin'
      | 'read only admin'
      | 'unenrollment pending'
      | 'unenrolled'
      | 'registry admin',
    daysPassedFromActivationCodeIssuance: number
  ) => {
    console.log(
      `I sign in as ${userRole} user having current registry activation code issued before ${daysPassedFromActivationCodeIssuance} days.`
    );
    await browser.waitForAngularEnabled(false);
    try {
      await Promise.all([preSignIn()]);
    } catch (ex) {
      console.error(`Could not apply preSignIn, ex: '${ex}'.`);
    }
    let email: string;
    let password: string;

    if (userRole === 'registry admin') {
      email = password = 'uk-ets-admin';
    } else {
      GlobalState.currentUser = await createUser(
        userRole,
        daysPassedFromActivationCodeIssuance
      );
      email = GlobalState.currentUser.email;
      password = KnowsThePage.DEFAULT_SIGN_IN_PASSWORD;
    }
    await performSignIn(GlobalState.currentUser.username, email, password);
  }
);

Then(
  'I have a {string} user with the following status and permissions to accounts:',
  async (
    user:
      | 'authorized representative'
      | 'authority_1'
      | 'authority_2'
      | 'authority_3'
      | 'authority_4'
      | 'validated',
    dataTable: TableDefinition
  ) => {
    console.log(
      `Executing step: I have a ${user} user with the following status and permissions to accounts:`
    );
    await browser.waitForAngularEnabled(false);
    // use delay in order to ensure that the account has been successfully inserted in the db
    await delay(1000);
    const rows: string[][] = dataTable.raw();
    try {
      await Promise.all([preSignIn()]);
    } catch (ex) {
      console.error(`Could not apply preSignIn, ex: '${ex}'.`);
    }

    GlobalState.currentUser = await createUser(user, 0);
    for (const row of rows) {
      const accountUserAccessState = row[0];
      const accessRight = row[1];
      const accountId = row[2];
      if (
        !accountUserAccessState ||
        accountUserAccessState === '' ||
        !accessRight ||
        accessRight === '' ||
        !accountId ||
        accountId === ''
      ) {
        assert.fail('Provide data in every column');
      }
      console.log(
        `| ${accountUserAccessState} | ${accessRight} | ${accountId} |`
      );
      await RegistryDbAccountAccessTestData.loadAccountAccesses([
        new RegistryDbAccountAccessBuilder()
          .state(accountUserAccessState)
          .account_id(accountId)
          .user_id(GlobalState.currentUser.id)
          .accessRight(
            user === 'authorized representative' || user === 'validated'
              ? accessRight
              : 'INITIATE_AND_APPROVE'
          )
          .build(),
      ]);
    }
  }
);

Then(
  'I sign in as {string} user with the following status and permissions to accounts:',
  async (
    user:
      | 'authorized representative'
      | 'authority_1'
      | 'authority_2'
      | 'authority_3'
      | 'authority_4'
      | 'validated',
    dataTable: TableDefinition
  ) => {
    console.log(
      `Executing step: I sign in as '${user}' user with the following status and permissions to accounts:`
    );
    await browser.waitForAngularEnabled(false);
    // use delay in order to ensure that the account has been successfully inserted in the db
    const rows: string[][] = dataTable.raw();
    try {
      await Promise.all([preSignIn()]);
    } catch (ex) {
      console.error(`Could not apply preSignIn, ex: '${ex}'.`);
    }

    GlobalState.currentUser = await createUser(user, 0);
    for (const row of rows) {
      const accountUserAccessState = row[0];
      const accessRight = row[1];
      const accountId = row[2];
      if (
        !accountUserAccessState ||
        accountUserAccessState === '' ||
        !accessRight ||
        accessRight === '' ||
        !accountId ||
        accountId === ''
      ) {
        assert.fail('Provide data in every column');
      }
      console.log(
        `| ${accountUserAccessState} | ${accessRight} | ${accountId} |`
      );
      await RegistryDbAccountAccessTestData.loadAccountAccesses([
        new RegistryDbAccountAccessBuilder()
          .state(accountUserAccessState)
          .account_id(accountId)
          .user_id(GlobalState.currentUser.id)
          .accessRight(
            user === 'authorized representative' || user === 'validated'
              ? accessRight
              : 'INITIATE_AND_APPROVE'
          )
          .build(),
      ]);
    }

    await Promise.all([
      performSignIn(
        GlobalState.currentUser.username,
        GlobalState.currentUser.email,
        KnowsThePage.DEFAULT_SIGN_IN_PASSWORD
      ),
    ]);
  }
);

When(
  'User {string} two factor authentication corresponds to {string} user due to email change',
  async (oldEmail: string, newEmail: string) => {
    console.log(
      `Executing step: User '${oldEmail}' two factor` +
        ` authentication corresponds to '${newEmail}' user due to email change`
    );
    updateEntryKey(KnowsThePage.users2fa, oldEmail, newEmail);
  }
);

export async function preSignIn() {
  console.log(`Entered preSignIn`);
  GlobalState.page = PageObjectPerScreenFactory.getFromScreen('Landing page');
  await GlobalState.page.navigateTo('Landing page');
  console.log(`> current url is: '${await browser.getCurrentUrl()}'`);
  console.log(`Navigated to Landing Page.`);
  await delay(500);
  await browser.waitForAngularEnabled(false);
  await delay(500);
  console.log(`> current url is: '${await browser.getCurrentUrl()}'`);
  await GlobalState.page.clickButton('Sign in');
  console.log(`>> current url is: '${await browser.getCurrentUrl()}'`);
  console.log(`Sign in button clicked.`);

  GlobalState.page = PageObjectPerScreenFactory.getFromScreen('Sign in');
  GlobalState.protractorConnectionUp =
    GlobalState.protractorConnectionUp &&
    GlobalState.page.name !== RegistryScreen.SIGN_IN;

  await browser.waitForAngularEnabled(GlobalState.protractorConnectionUp);
  let shouldRetry = true;
  let retry = 0;
  let currentUrl: string;
  while (shouldRetry === true && retry < 50) {
    currentUrl = await GlobalState.page.getCurrentUrl();
    if (currentUrl.includes('auth')) {
      shouldRetry = false;
    } else {
      await Promise.all([delay(100)]);
      retry++;
      if (retry === 49) {
        assert.fail('auth screen failed to load. Current url: ' + currentUrl);
      }
    }
  }
  console.log(`Exiting preSignIn`);
}

export async function performSignIn(
  username: string,
  email: string,
  password: string
) {
  console.log(`Entered performSignIn`);
  browser.waitForAngularEnabled(false);
  const acceptCookies = await browser.findElements(
    by.xpath(`//*[contains(text(),'Accept all cookies')]`)
  );

  console.log(`About to accept cookies`);
  try {
    acceptCookies.length === 1 &&
    acceptCookies[0].isDisplayed() &&
    acceptCookies[0].isEnabled()
      ? await Promise.all([delay(500), acceptCookies[0].click()])
      : console.log(
          `All cookies are already accepted - Web Element 'Accept all cookies' not found.`
        );
  } catch (e) {
    console.log(`Unable to click on accept cookies: ` + e);
  }

  browser.waitForAngularEnabled();
  console.log(`About to get otp`);
  const otp = getMapEntryValueAttributeByKey(
    KnowsThePage.users2fa,
    username,
    'otp'
  );

  console.log(
    `Performing sign in with email '${email}', password '${password}' and otp '${otp}'.`
  );
  await delay(500);
  await GlobalState.page.webElementActionApply(
    email,
    GlobalState.page.locators.get('Email'),
    'sendKeys'
  );

  // enter password
  await delay(100);
  await GlobalState.page.webElementActionApply(
    password,
    GlobalState.page.locators.get('Password'),
    'sendKeys'
  );
  // click sign in
  await delay(100);
  await browser.driver.findElement(By.id(`sign-in`)).click();
  await delay(200);
  await browser.driver.findElement(By.id(`otpCode`)).sendKeys(otp);
  await delay(100);
  await browser.driver.findElement(By.id(`sign-in`)).click();

  let shouldRetry = true;
  let retry = 0;
  let currentUrl: string;
  const totalTries = 50;

  while (shouldRetry === true && retry < totalTries) {
    retry++;
    currentUrl = await GlobalState.page.getCurrentUrl();
    if (currentUrl.includes('dashboard')) {
      shouldRetry = false;
      console.log(
        `Successfully found dashboard in currentUrl '${currentUrl}' on try '${retry}'/'${totalTries}'.`
      );
    } else {
      await Promise.all([delay(500)]);
      console.log(
        `Retrying for time '${retry}'/'${totalTries}' to` +
          ` find dashboard in currentUrl '${currentUrl}'.`
      );
    }
  }
  assert.isTrue(currentUrl.includes('dashboard'));
  GlobalState.page =
    PageObjectPerScreenFactory.getFromScreen('Registry dashboard');
  GlobalState.protractorConnectionUp =
    GlobalState.protractorConnectionUp &&
    GlobalState.page.name !== RegistryScreen.SIGN_IN;
  await browser.waitForAngularEnabled(GlobalState.protractorConnectionUp);
  console.log(`Sign in successfully performed.`);
}

Given(
  'I see {string} elements of {string}',
  async (expectedElementOccurrences: string, locat: string) => {
    console.log(
      `Executing step: I see "${expectedElementOccurrences}" elements of "${locat}".`
    );
    try {
      let shouldRetry = true;
      let actualElementOccurrences: string;
      let retries = 0;

      while (shouldRetry && retries < 25) {
        actualElementOccurrences = String(
          (
            await browser.findElements(
              GlobalState.page.locators.get(locat).by()
            )
          ).length
        );

        if (actualElementOccurrences === expectedElementOccurrences) {
          shouldRetry = false;
        } else {
          console.log(
            `Could not find the expected '${expectedElementOccurrences}'` +
              `elements, actually found '${actualElementOccurrences}'.`
          );
          await Promise.all([delay(100)]);
          retries++;
        }
      }

      assert.isTrue(
        expectedElementOccurrences === actualElementOccurrences,
        `\nExpected number of '${locat}' elements is: '${expectedElementOccurrences}'.` +
          `\nActual number of '${locat}' elements is  : '${actualElementOccurrences}'.\n`
      );
    } catch (e) {
      assert.fail('Could not find the number of elements. Error: ' + e);
    }
  }
);

Then('I see an error summary with {string}', async (expectedMessage) => {
  console.log(
    `Executing step: I see an error summary with "${expectedMessage}".`
  );

  if (RegistryScreen.SIGN_IN === GlobalState.page.name) {
    expect(await browser.getCurrentUrl()).to.contain('auth');
  }

  let errorSummaryNotFound = true;
  let tries = 1;
  const maxTries = 20;
  const xpathLocator = `(//div[contains(@class, 'error') and contains(@class, 'summary')])[1]`;
  let actualErrorSummary = '';

  let expectedMessageTrimmedLineBreaks = expectedMessage.replace(
    /(\r\n|\n|\r)/gm,
    ''
  );

  let actualMessageTrimmedLineBreaks = '';

  while (errorSummaryNotFound && tries < maxTries) {
    console.log(
      `About to try for time '${tries}' of total '${maxTries}' to get error summary with content '${expectedMessage}'.`
    );

    // populate actual error message
    try {
      actualErrorSummary = await browser
        .findElement(by.xpath(xpathLocator))
        .getText();

      if (actualErrorSummary === '') {
        console.log(`empty actualErrorSummary`);
      } else {
        actualMessageTrimmedLineBreaks = actualErrorSummary
          .replace(/(\r\n|\n|\r)/gm, '')
          .replace(`There is a problem`, '');

        // if actual message last character is dot, trim it
        if (
          actualMessageTrimmedLineBreaks.charAt(
            actualMessageTrimmedLineBreaks.length - 1
          ) === '.'
        ) {
          actualMessageTrimmedLineBreaks =
            actualMessageTrimmedLineBreaks.substr(
              0,
              actualMessageTrimmedLineBreaks.length - 1
            );
        }

        console.log(
          `Found on time '${tries}' of total ${maxTries} times the actualMessageTrimmedLineBreaks '${actualMessageTrimmedLineBreaks}'.`
        );
      }
    } catch (e) {
      console.log(
        `Could find error summary for time '${tries}' of total ${maxTries} times: '${e}'.`
      );
    }

    // compare actual and expected messages
    if (actualMessageTrimmedLineBreaks === expectedMessageTrimmedLineBreaks) {
      errorSummaryNotFound = false;
    } else {
      console.log(`Could not find error summary for time '${tries}'.`);
      tries++;
      await Promise.all([delay(200)]);
    }
  }
  actualMessageTrimmedLineBreaks = actualMessageTrimmedLineBreaks.toLowerCase();
  expectedMessageTrimmedLineBreaks =
    expectedMessageTrimmedLineBreaks.toLowerCase();

  // final assertion
  assert.isTrue(
    actualMessageTrimmedLineBreaks === expectedMessageTrimmedLineBreaks,
    `\n\nError in assertion:` +
      `\nActual message is: '${actualMessageTrimmedLineBreaks}'.` +
      `\nExpected message is: '${expectedMessageTrimmedLineBreaks}'.\n\n`
  );

  console.log(
    `Exiting step: I see an error summary with "${expectedMessage}".`
  );
});

Then(
  'I see an error detail for field {string} with content {string}',
  async (fieldName, message) => {
    const num = Number.parseInt('1', 10);
    await delay(num ? num * 1000 : KnowsThePage.TIMEOUT);
    console.log(
      `Executing step: I see an error detail for field "${fieldName}" with content "${message}".`
    );

    // populate actual message
    let actualMessage: string;
    if ((await browser.getPageSource()).includes(`-error2`)) {
      console.log(`error details finally found using -error2`);
      actualMessage = await (
        await browser.findElement(by.id(`${fieldName}-error2`))
      ).getText();
    } else if ((await browser.getPageSource()).includes(`-error-label`)) {
      console.log(`error details finally found using -error-label`);
      actualMessage = await (
        await browser.findElement(by.id(`${fieldName}-error-label`))
      ).getText();
    } else if ((await browser.getPageSource()).includes(`-error`)) {
      console.log(`error details finally found using -error`);
      actualMessage = await (
        await browser.findElement(by.id(`${fieldName}-error`))
      ).getText();
    } else {
      assert.fail(
        `\nAssertion error in step:` +
          `\nI see an error detail for field '${fieldName}' with content '${message}'.` +
          `\nHowever, current page ` +
          `${
            (await browser.getPageSource()).includes(message)
              ? 'contains'
              : 'does not contain'
          }` +
          ` the error message '${message}', so probably the respective field is wrong.`
      );
    }

    const actualMessageTrimmed = actualMessage.replace('\n', ' ');
    let potentialSolution = '';
    if (
      actualMessage.includes(message) ||
      actualMessageTrimmed.includes(message)
    ) {
      potentialSolution =
        `\n\nHowever the actual message is included in the expected one.` +
        `\nDid you use the message phrase accurately at Gherkin level?`;
    }

    const errorMessage =
      `\n\nActual message is: '${actualMessage}'.` +
      `\n\nActual message trimmed is: '${actualMessageTrimmed}'.` +
      `\n\nExpected message is      : '${message}'.${potentialSolution}\n\n`;

    assert.isTrue(
      actualMessage === message || actualMessageTrimmed === message,
      errorMessage
    );
  }
);

Then(
  'I see an error detail with id {string} and content {string}',
  async (errorId, message) => {
    let actualResult = '';
    await GlobalState.page
      .getErrorDetail(errorId)
      .then((actualErrorDetail) => (actualResult = actualErrorDetail));
    if (actualResult.startsWith('Error')) {
      actualResult = actualResult.replace('Error:\n', '');
    }
    assert.isTrue(
      actualResult.toLowerCase() === message.toLowerCase(),
      `actual: "${actualResult}"  expected: "${message}"`
    );
  }
);

Then(
  'I have the following itl notifications',
  async (dataTable: TableDefinition) => {
    const valuePerFieldIds: string[][] = dataTable.raw();
    const map = new Map<
      string,
      { notificationType: string; notificationStatus: string }
    >();
    console.log(`Executing step: I have the following itl notifications`);

    for (const valuePerFieldId of valuePerFieldIds) {
      if (valuePerFieldId[0] === 'notificationId') {
        continue;
      }
      map.set(valuePerFieldId[0], {
        notificationType: valuePerFieldId[1],
        notificationStatus: valuePerFieldId[2],
      });
    }
    const keys = Array.from(map.keys());
    try {
      for (const key of keys) {
        const noticeId = key;
        const noticeType = map.get(key).notificationType;
        const noticeStatus = map.get(key).notificationStatus;

        console.log(
          `| ${noticeId} | ${noticeType} | ${noticeStatus} |   ... About to create it via itl emulator.`
        );
        await submitItlNotificationRequest(noticeId, noticeType, noticeStatus);
      }
    } catch (e) {
      assert.fail('Could create itl notification. Error: ' + e);
    }
  }
);

Then(
  'I see the following values in the fields',
  async (dataTable: TableDefinition) => {
    const valuePerFieldIds: string[][] = dataTable.raw();
    const map = new Map<string, { type: string; value: string }>();
    console.log(`Executing step: I see the following values in the fields`);

    for (const valuePerFieldId of valuePerFieldIds) {
      if (valuePerFieldId[0] === 'fieldId') {
        continue;
      }
      map.set(valuePerFieldId[0], {
        type: valuePerFieldId[1],
        value: valuePerFieldId[2],
      });
    }

    const keys = Array.from(map.keys());
    let ps = '';
    try {
      await Promise.all([browser.getPageSource()]).then(
        (result) => (ps = result.toString())
      );

      for (const key of keys) {
        const type = map.get(key).type;
        const actualValue = await GlobalState.page.getValue(key, type);
        const actualValueTrimmed = actualValue.replace(' ', '');
        const expectedValue = map.get(key).value;
        assert.isTrue(
          actualValue === expectedValue || actualValueTrimmed === expectedValue,
          `Could verify fields: '${GlobalState.page.name}'.\n` +
            `Page source is: '${ps}'.\n`
        );
      }
    } catch (e) {
      console.log(`Error: '${e}'.\n`);
    }
  }
);

Then(
  'I see the following fields having the values:',
  async (dataTable: TableDefinition) => {
    console.log(
      `Executing step: I see the following fields having the values:`
    );

    const valuePerFieldLocators: string[][] = dataTable.raw();
    const map = new Map<string, { type: string; value: string }>();
    let actualResult = null;
    let expectedResult = null;
    let actualResultWithoutLineBreaks = null;

    for (const valuePerFieldLocator of valuePerFieldLocators) {
      if (valuePerFieldLocator[0] === 'fieldName') {
        continue;
      }

      map.set(valuePerFieldLocator[0], {
        type: valuePerFieldLocator[1],
        value: valuePerFieldLocator[2],
      });
    }

    const keys = Array.from(map.keys());
    try {
      for (const key of keys) {
        await browser.waitForAngularEnabled();
        expectedResult = map.get(key).type;
        console.log(`| '${key}'| '${expectedResult}' |`);

        let retries = 6;
        let shouldRetry = true;
        let assertionExpression = false;

        while (retries > 0 && shouldRetry) {
          // handle [empty] substring at gherkin level
          if (expectedResult.includes(`[empty]`)) {
            expectedResult = expectedResult.replace('[empty]', '');
          }

          if (key.includes('radio button')) {
            actualResult = await GlobalState.page.webElementActionApply(
              '',
              await GlobalState.page.locators.get(
                key.replace('radio button ', '')
              ),
              'getRadioButtonSelectionStatus'
            );
          } else if (key.includes('checkbox button')) {
            actualResult = await GlobalState.page.webElementActionApply(
              '',
              GlobalState.page.locators.get(
                key.replace('checkbox button ', '')
              ),
              'getCheckBoxButtonSelectionStatus'
            );
          } else if (key.includes('dropdown')) {
            actualResult = await GlobalState.page.webElementActionApply(
              '',
              GlobalState.page.locators.get(key.replace('dropdown ', '')),
              'getDropdownSelectionStatus'
            );
          } else {
            actualResult = await GlobalState.page.webElementActionApply(
              '',
              GlobalState.page.locators.get(key),
              'getText'
            );
          }

          if (expectedResult === '[null]' && actualResult === null) {
            actualResult = '[null]';
          } else if (expectedResult === '[empty]' && actualResult === '') {
            actualResult = '[empty]';
          } else {
            expectedResult = setDynamicGherkinValueToActualData(expectedResult)
              .trim()
              .toLowerCase();
          }

          if (actualResult.includes('Registry activation code')) {
            actualResult = actualResult.substring(0, actualResult.length - 67);
          }
          if (actualResult.includes('Opened on')) {
            actualResult = actualResult.substring(
              0,
              actualResult.indexOf('Opened on')
            );
          }
          // replace characters such as line break with spaces and finally trim the trailing spaces
          actualResultWithoutLineBreaks = replaceAllInString(
            actualResult,
            new Map().set(/(?:\r\n|\r|\n)/g, ' ')
          )
            .trim()
            .toLowerCase();

          if (expectedResult !== '[not empty nor null value]') {
            assertionExpression =
              actualResult === expectedResult ||
              actualResultWithoutLineBreaks === expectedResult;
          } else {
            assertionExpression = !(
              actualResult === null ||
              actualResult === undefined ||
              actualResult === ''
            );
          }

          if (assertionExpression) {
            shouldRetry = false;
          } else {
            await delay(500);
            retries--;
          }
        }
        assert.isTrue(assertionExpression);
      }
    } catch (e) {
      const message =
        `\nError in page named: '${GlobalState.page.name}'.` +
        `\nExpected value:                      '${expectedResult}'.` +
        `\nActualResultWithoutLineBreaks value: '${actualResultWithoutLineBreaks}'` +
        `\nActual value:                        '${actualResult}'.\n`;

      console.log(message);
      assert.fail(message + `\nError: '${e}'.`);
    }
  }
);

Then(
  'The page {string} the {string} text',
  async (containsPositiveness: 'contains' | 'does not contain', phrase) => {
    console.log(
      `Executing step: The page "${containsPositiveness}" the "${phrase}" text.`
    );

    // current date short format
    if (phrase.includes(`current date short format`)) {
      phrase = phrase.replace(
        'current date short format',
        getRelativeDate('current date short format')
      );
    }
    phrase = phrase.toLowerCase();
    let expression = false;
    let tries = 1;
    const maxTries = 8;
    let pageSource = '';

    browser.waitForAngularEnabled();
    while (tries < maxTries && !expression) {
      tries++;
      await delay(500);
      await browser.getPageSource().then(async (actualPageSource) => {
        pageSource = actualPageSource.toLowerCase();
        expression =
          (pageSource.includes(phrase) &&
            containsPositiveness === 'contains') ||
          (!pageSource.includes(phrase) &&
            containsPositiveness === 'does not contain');
      });
    }

    assert.isTrue(
      expression,
      `phrase: '${phrase}': containsPositiveness: '${containsPositiveness}'\n` +
        `tried '${tries}'/'${maxTries}' times. Actual page source: '\n\n${pageSource}\n\n'.`
    );
  }
);

Then('I accept all cookies', async () => {
  console.log(`Executing step: I accept all cookies.`);
  browser.waitForAngularEnabled(false);
  await Promise.all([delay(100)]);
  const acceptCookies = await browser.findElements(
    by.xpath(`//*[contains(text(),'Accept all cookies')]`)
  );

  acceptCookies.length === 1
    ? await Promise.all([acceptCookies[0].click()])
    : console.log(
        `All cookies are already accepted - Web Element 'Accept all cookies' not found.`
      );

  browser.waitForAngularEnabled();
});

Then(
  'I set for {string} user {string} verification code after {string} seconds using {string} method with {string} algorithm and {string} otp standard for {string} digits',
  async (
    username: string,
    code: 'correct' | 'incorrect',
    waitSecondsBeforeSendCode: number,
    method: 'QR barcode scan' | 'Manual Key',
    algorithmType: 'sha1' | 'sha256' | 'sha512',
    otpType: 'totp' | 'hotp',
    digitsCount: '6' | '8'
  ) => {
    console.log(
      `Executing step: I set for '${username}' user '${code}' verification code after '${waitSecondsBeforeSendCode}'` +
        ` seconds using '${method}' method with '${algorithmType}' algorithm and '${otpType}' otp type.`
    );

    let correctCode = '';
    let secret = '';
    let loops = 10;
    twoFactorAuth = new TwoFactorAuthenticationUtils();

    // delete old item if user already exists in map
    deleteMapEntry(KnowsThePage.users2fa, username);
    console.log(`1. Variables initialized.`);

    // method: manual key
    if (method === 'Manual Key') {
      console.log(`Method: manual setup`);
      let uiSecret = await browser
        .findElement(by.xpath(`//*[contains(text(),'Secret')]`))
        .getText();
      console.log(`uiSecret before replacement: '${uiSecret}'.`);
      uiSecret = uiSecret.replace('Secret: ', '').replace(/ /g, '');
      console.log(`finally found uiSecret: '${uiSecret}'.`);

      // add secret to map if it does not exist / update if it exists due to precious setup of 2fa
      addEntryToMap(KnowsThePage.users2fa, username, {
        secret: uiSecret,
        otp: '',
      });

      await twoFactorAuth.setVerificationCodeManually(
        username,
        uiSecret,
        algorithmType,
        otpType,
        digitsCount
      );
      correctCode = getMapEntryValueAttributeByKey(
        KnowsThePage.users2fa,
        username,
        'otp'
      );
    }
    // method: qr code scan
    else {
      console.log(`Method: qr code scan`);
      const qrCodeImageWebElement = await browser.findElement(
        by.id(KnowsThePage.LOCATOR_ID_QR_CODE_IMAGE)
      );
      const weSrc = await qrCodeImageWebElement.getAttribute('src');
      console.log(`weSrc: '${weSrc}'.`);

      let weWidth = 0;
      let weHeight = 0;
      let weImageSize: any;
      await qrCodeImageWebElement.getSize().then((s) => (weImageSize = s));
      weWidth = weImageSize['width'];
      weHeight = weImageSize['height'];
      console.log(`weHeight: '${weHeight}'.`);
      console.log(`weWidth: '${weWidth}'.`);

      await Promise.all([
        twoFactorAuth.setSecretByQrCodeImage(
          username,
          weSrc,
          weWidth,
          weHeight
        ),
        delay(1000),
      ]);

      while (
        // tslint:disable-next-line:tsr-detect-possible-timing-attacks
        (secret === '' || secret === undefined || secret === null) &&
        loops > 0
      ) {
        secret = getMapEntryValueAttributeByKey(
          KnowsThePage.users2fa,
          username,
          'secret'
        );
        console.log(`secret found in loop: '${secret}'.`);
        await delay(1000);
        loops--;
      }

      console.log(`finally found secret: '${secret}'.`);

      await Promise.all([
        twoFactorAuth.setVerificationCodeManually(
          username,
          secret,
          algorithmType,
          otpType,
          digitsCount
        ),
      ]);

      // correctCode = KnowsThePage.getVerificationOtpCode();
      correctCode = getMapEntryValueAttributeByKey(
        KnowsThePage.users2fa,
        username,
        'otp'
      );
    }
    console.log(`verification code: '${correctCode}'.`);

    // enter Verification code value after specified time wait
    console.log(
      `About to wait for '${waitSecondsBeforeSendCode}' seconds before setting the verification code.`
    );

    await Promise.all([
      delay(1000 * waitSecondsBeforeSendCode),
      browser
        .findElement(by.xpath(KnowsThePage.LOCATOR_XPATH_OTP))
        .sendKeys(code === 'correct' ? correctCode : `00000a`),
    ]);
  }
);

Then('I am presented with the {string} screen', async (screenName) => {
  console.log(
    `Executing step: I am presented with the "${screenName}" screen.`
  );

  let currentUrl: string;
  let expectedPhrase: string;
  let correctScreenNotFound = true;
  let retries = 0;
  const totalTries = 5;

  while (correctScreenNotFound && retries < totalTries) {
    try {
      await Promise.all([delay(600)]);

      retries++;
      console.log(
        `Trying to find screen '${screenName}' for '${retries}'/'${totalTries}' time.`
      );

      // actual screen
      GlobalState.page = PageObjectPerScreenFactory.getFromScreen(screenName);

      GlobalState.protractorConnectionUp =
        GlobalState.protractorConnectionUp &&
        !(
          GlobalState.page.name !== RegistryScreen.SIGN_IN &&
          // tslint:disable-next-line:tsr-detect-possible-timing-attacks
          GlobalState.page.name !==
            RegistryScreen.ACCOUNT_OPENING_SETUP_TWO_AUTH_FACTOR
        );

      await browser.waitForAngularEnabled(GlobalState.protractorConnectionUp);
      browser.waitForAngularEnabled(false);
      await Promise.all([delay(300)]);
      currentUrl = await GlobalState.page.getCurrentUrl();
      console.log(`currentUrl: '${currentUrl}'.`);

      const authPages: Array<string> = [
        RegistryScreen.SIGN_IN,
        RegistryScreen.ACCOUNT_OPENING_SETUP_TWO_AUTH_FACTOR,
      ];

      if (authPages.includes(screenName)) {
        expectedPhrase = 'auth';
        if (currentUrl.includes(expectedPhrase)) {
          correctScreenNotFound = false;
          console.log(`Successfully found correct screen.`);
        }
      } else {
        expectedPhrase = AppRoutesPerScreenUtil.get(screenName);
        currentUrl = getValidCurrentUrl(currentUrl);
        if (currentUrl === expectedPhrase) {
          correctScreenNotFound = false;
        }
      }
      await delay(700);
    } catch (ex) {
      assert.fail(
        `Exception while trying to be presented with screen '${ex}'.`
      );
    }
  }

  assert.isTrue(
    !correctScreenNotFound,
    `\nError in step: I am presented with the '${screenName}' screen.` +
      `\nExpected screen is: '${screenName}'.` +
      `\nActual screen is:   '${currentUrl}'.`
  );
});

Then(
  'The {string} {string} is {string}',
  async (
    fieldName,
    element: 'button' | 'link',
    expectedStatus: 'enabled' | 'disabled'
  ) => {
    console.log(
      `Executing step: The "${fieldName}" "${element}" is "${expectedStatus}".`
    );
    if (element === 'button') {
      let expEnabledStatus: boolean;
      if (expectedStatus === 'enabled') {
        expEnabledStatus = true;
        expect(
          await GlobalState.page.getButton(fieldName).isEnabled()
        ).to.equal(expEnabledStatus);
      } else {
        expEnabledStatus = false;
        expect(
          await GlobalState.page.getButton(fieldName).isEnabled()
        ).to.equal(expEnabledStatus);
      }
    } else if (element === 'link') {
      let expEnabledStatus: boolean;
      if (expectedStatus === 'enabled') {
        expEnabledStatus = true;
        expect(await GlobalState.page.getLink(fieldName).isEnabled()).to.equal(
          expEnabledStatus
        );
      } else {
        expEnabledStatus = null;
        expect(
          await browser
            .findElement(by.xpath(`//*[contains(text(),'` + fieldName + `')]`))
            .getAttribute('href')
        ).to.equal(expEnabledStatus);
      }
    }
  }
);

When(
  'I have the following Inbound transaction',
  async (dataTable: TableDefinition) => {
    const valuePerFieldIds: string[][] = dataTable.raw();
    const map = new Map<string, string>();
    console.log(`Executing step: I have the following Inbound transaction`);
    try {
      // log fields and set map elements
      for (const valuePerFieldId of valuePerFieldIds) {
        if (valuePerFieldId[0] === 'fieldName') {
          continue;
        }
        map.set(valuePerFieldId[0], valuePerFieldId[1]);
        console.log(`| ${valuePerFieldId[0]} | ${valuePerFieldId[1]} |`);
      }
      // send request based on map elements
      await submitProposalRequestInboundTransaction(map);
    } catch (e) {
      assert.fail(
        `Error in step: 'I have the following Inbound transaction' Error: '${e}'.`
      );
    }
  }
);

When(
  'I enter the mandatory fields which are',
  async (dataTable: TableDefinition) => {
    const valuePerFieldIds: string[][] = dataTable.raw();
    const map = new Map<string, string>();
    console.log(`Executing step: I enter the mandatory fields which are.`);

    for (const valuePerFieldId of valuePerFieldIds) {
      if (valuePerFieldId[0] === 'fieldId') {
        continue;
      }
      console.log(`| '${valuePerFieldId[0]}' | '${valuePerFieldId[1]}' |`);
      map.set(valuePerFieldId[0], valuePerFieldId[1]);
    }
    try {
      await GlobalState.page.fillWith(map);
    } catch (e) {
      assert.fail(
        'Could not fill screen: ' + GlobalState.page.name + ' . Error: ' + e
      );
    }
  }
);

Then(
  'I enter the following values to the fields:',
  async (dataTable: TableDefinition) => {
    const valuePerFieldLocators: string[][] = dataTable.raw();
    const map = new Map<string, { type: string; value: string }>();
    console.log(`Executing step: I enter the following values to the fields:`);

    for (const valuePerFieldLocator of valuePerFieldLocators) {
      if (valuePerFieldLocator[0] === 'field') {
        continue;
      }

      console.log(
        `| '${valuePerFieldLocator[0]}' | '${valuePerFieldLocator[1]}' | '${valuePerFieldLocator[2]}' |`
      );
      map.set(valuePerFieldLocator[0], {
        type: valuePerFieldLocator[1],
        value: valuePerFieldLocator[2],
      });
    }

    const keys = Array.from(map.keys());
    let value = null;
    let currentKey = null;

    try {
      for (const key of keys) {
        currentKey = key;
        await browser.waitForAngularEnabled();
        value = map.get(key).type;
        console.log(`| ${key} | ${value} |`);

        // if value is [empty] then do not set value
        if (value === '[empty]') {
        }
        // radio button element
        else if (key.includes('radio button')) {
          await Promise.all([GlobalState.page.clickButton(key)]);
        }
        // dropdown web element
        else if (key.includes('dropdown')) {
          await Promise.all([
            GlobalState.page.webElementActionApply(
              '',
              GlobalState.page.locators.get(value),
              'click'
            ),
          ]);
        }
        // checkbox web element
        else if (key.includes('checkbox')) {
          await Promise.all([
            GlobalState.page.webElementActionApply(
              '',
              GlobalState.page.locators.get(key),
              'check'
            ),
          ]);
        }
        // textbox web element
        else {
          value = setDynamicGherkinValueToActualData(value);
          if (GlobalState.page.locators.get(key)) {
            await Promise.all([
              GlobalState.page.webElementActionApply(
                value,
                GlobalState.page.locators.get(key),
                'sendKeys'
              ),
            ]);
          } else {
            await Promise.all([GlobalState.page.setInputTextById(key, value)]);
          }
        }
      }
    } catch (e) {
      assert.fail(
        `Error in page named: '${GlobalState.page.name}'.` +
          ` For the field '${currentKey}' the value '${value}' has not been successfully set. ` +
          `Did you set the expected value correctly at Gherkin level? Error: '${e}'.`
      );
    }
  }
);

When('I enter the mandatory fields', async () => {
  console.log(`Executing step: I enter the mandatory fields.`);
  try {
    await GlobalState.page.fillExcluding();
  } catch (e) {
    assert.fail(
      'Could not fill screen: ' + GlobalState.page.name + '. Error: ' + e
    );
  }
});

When(
  'I enter the mandatory fields leaving the mandatory field {string} empty',
  async (fieldName: string) => {
    console.log(
      `Executing step: I enter the mandatory fields leaving the mandatory field "${fieldName}" empty.`
    );
    try {
      await GlobalState.page.fillExcluding(Array.of(fieldName));
    } catch (e) {
      assert.fail(
        'Could not fill screen: ' + GlobalState.page.name + '. Error: ' + e
      );
    }
  }
);

When('I clear the {string} field', async (clearElement) => {
  console.log(`Executing step: I clear the "${clearElement}" field.`);
  try {
    await browser.waitForAngularEnabled();
    await GlobalState.page.webElementActionApply(
      '',
      GlobalState.page.locators.get(clearElement),
      'clear'
    );
    await GlobalState.page.webElementActionApply(
      ' \b',
      GlobalState.page.locators.get(clearElement),
      'sendKeys'
    );
    await delay(500);
  } catch (e) {
    assert.fail(
      'Could not clear element: ' +
        clearElement +
        ' in screen: ' +
        GlobalState.page.name +
        '. Error: ' +
        e
    );
  }
});

When(
  'I {string} the {string} checkbox',
  async (action: 'check' | 'uncheck', elem) => {
    console.log(`Executing step: I "${action}" the "${elem}" checkbox.`);

    try {
      await browser.waitForAngularEnabled();
      await GlobalState.page.webElementActionApply(
        '',
        GlobalState.page.locators.get(elem),
        'check'
      );
    } catch (e) {
      assert.fail(
        'Could not check/uncheck element: ' +
          elem +
          ' in screen: ' +
          GlobalState.page.name +
          '. Error: ' +
          e
      );
    }
  }
);

When('I select the {string} option', async (optionText) => {
  try {
    await browser.waitForAngularEnabled();
    console.log(`Executing step: I select the "${optionText}" option.`);
    await Promise.all([delay(1000)]);
    await GlobalState.page.webElementActionApply(
      '',
      GlobalState.page.locators.get(optionText),
      'select'
    );
    await Promise.all([delay(1000)]);
  } catch (e) {
    assert.fail(
      `Could not select element: '${optionText}' in screen: '${GlobalState.page.name}'. Error: '${e}'.`
    );
  }
});

When('I enter value {string} in {string} field', async (value, field) => {
  let hasException = false;
  try {
    // case of no value set
    if (value === '[empty]') {
      console.log(
        `[empty] value defined at Gherkin level. No value will be set.`
      );
      // no empty value set
    } else {
      value = setDynamicGherkinValueToActualData(value);
      await browser.waitForAngularEnabled();

      await Promise.all([delay(100)]);

      console.log(
        `Executing step: I enter value "${value}" in "${field}" field.`
      );
      if (GlobalState.page.locators.get(field)) {
        await Promise.all([
          GlobalState.page.webElementActionApply(
            value,
            GlobalState.page.locators.get(field),
            'sendKeys'
          ),
        ]);
      } else {
        console.log(
          'locator does not exist in map: about to use setInputTextById.'
        );
        await Promise.all([GlobalState.page.setInputTextById(field, value)]);
      }
    }
  } catch (e) {
    hasException = true;
    console.log(`Error: '${e}'.`);
  }

  const messageBase = `value '${value}' into '${field}' in screen '${GlobalState.page.name}'`;
  hasException
    ? assert.fail(`Could not set ${messageBase}.`)
    : console.log(`Successfully ${messageBase}.`);
});

When('I click the {string} link', async (linkName) => {
  console.log(`Executing step: I click the "${linkName}" link.`);

  try {
    await Promise.all([
      existsSubstringInArrayElements(linkName, waitNeededWebElements)
        ? delay(2500)
        : delay(500),
      GlobalState.page.clickLink(linkName),
      existsSubstringInArrayElements(linkName, waitNeededWebElements)
        ? delay(2500)
        : delay(1000),
    ]);

    if (linkName === 'Sign out') {
      await Promise.all([
        keycloakClient.deleteUsers(Array(GlobalState.currentUser.email)),
      ]);
    }
  } catch (e) {
    assert.fail(
      `Could not click link '${linkName}' in screen '${GlobalState.page.name}'. '${e}'`
    );
  }
});

When('I click the {string} button', async (buttonName) => {
  try {
    console.log(`Executing step: I click the "${buttonName}" button.`);
    await Promise.all([
      delay(
        existsSubstringInArrayElements(buttonName, waitNeededWebElements)
          ? 3000
          : 700
      ),
      GlobalState.page.clickButton(buttonName),
      delay(
        existsSubstringInArrayElements(buttonName, waitNeededWebElements)
          ? 3000
          : 700
      ),
    ]);
  } catch (e) {
    console.log(
      `Could not click button: '${buttonName}' in screen: '${GlobalState.page.name}'. Error: '${e}'.`
    );
  }
});

Given(
  'There are the following allocated allowances for the compliant entity {string} of {string} allocation table of {string} status',
  async (
    compliantEntityId: string,
    allocationEntryType: string,
    allocationStatusValue: string,
    dataTable: TableDefinition
  ) => {
    console.log(
      `Executing step: 'There are the following allocated allowances for the compliant ` +
        `entity ${compliantEntityId} of ${allocationEntryType} allocation table of ${allocationStatusValue} status'.`
    );

    // initialize elements
    const rows: string[][] = dataTable.raw();
    let allocationYearId: string;
    let allocated: string;
    let returned: string;
    let reversed: string;
    let allocationEntryEntitlement: string;

    for (const row of rows) {
      console.log(`| ${row} |`);

      if (row[0] === 'allocation_year_id') {
        continue;
      }

      allocationYearId = row[0];
      allocationEntryEntitlement = row[1];
      allocated = row[2];
      returned = row[3];
      reversed = row[4];

      /**
       * Building the allocation entry
       */
      const allocationEntry = new RegistryDbAllocationEntryBuilder()
        .setAllocationYearId(allocationYearId)
        .setCompliantEntityId(compliantEntityId)
        .setType(allocationEntryType)
        .setEntitlement(allocationEntryEntitlement)
        .setAllocated(allocated === `[null]` ? null : allocated)
        .setReturned(returned === `[null]` ? null : returned)
        .setReversed(reversed === `[null]` ? null : reversed)
        .build();
      await RegistryDbAllocationEntryTestData.loadAllocationEntry([
        allocationEntry,
      ]);

      /**
       * Building the allocation status
       */
      const allocationStatus = new RegistryDbAllocationStatusBuilder()
        .setAllocationYearId(allocationYearId === `[null]` ? null : row[0])
        .setCompliantEntityId(compliantEntityId)
        .setStatus(allocationStatusValue)
        .build();
      await RegistryDbAllocationStatusTestData.loadAllocationStatus([
        allocationStatus,
      ]);
    }
  }
);

Given('I navigate to the {string} screen', async (screenName) => {
  console.log(`Executing step: I navigate to "${screenName}" screen.`);
  await browser.waitForAngularEnabled(false);
  GlobalState.page = PageObjectPerScreenFactory.getFromScreen(screenName);
  let shouldRetry = true;
  let retry = 1;
  let currentUrl: string;

  try {
    await GlobalState.page.navigateTo(screenName);
    if (RegistryScreen.SIGN_IN !== screenName) {
      browser.waitForAngularEnabled(false);
    }
    while (shouldRetry === true && retry < 50) {
      currentUrl = await browser.getCurrentUrl();
      if (currentUrl.includes(AppRoutesPerScreenUtil.get(screenName))) {
        console.log(screenName, ' screen ready after try: ', retry);
        shouldRetry = false;
      } else {
        delay(100);
        retry++;
        if (retry === 40) {
          assert.fail(
            GlobalState.page.name +
              ' screen failed to load. Current url: ' +
              currentUrl
          );
        }
      }
    }
  } catch (e) {
    console.log(
      `Could not navigate to '${GlobalState.page.name}'. Error: '${e}'.`
    );
  }
});

When(
  'I wait for {string} {string}',
  { timeout: 2000000 },
  async (timeSize: number, timeUnit: 'seconds' | 'minutes') => {
    console.log(`Executing step: I wait for "${timeSize}" "${timeUnit}".`);

    let timeToWait = 0;
    timeUnit === 'seconds'
      ? (timeToWait = timeSize * 1000)
      : (timeToWait = timeSize * 1000 * 60);
    await Promise.all([browser.sleep(timeToWait)]);
  }
);

export async function createUser(
  role: string,
  daysPassedFromActivationCodeIssuance: number
): Promise<RegistryDbTestUser> {
  console.log(`About to call getRegistryRolesByGherkinName`);
  const registryRoles = getRegistryRolesByGherkinName(role);
  const user = new RegistryDbTestUser();
  // change in case of no registryRoles set above
  if (registryRoles.length === 0) {
    user.state = role
      .toUpperCase()
      .replace(' ', '_')
      .replace('_1', '')
      .replace('_2', '')
      .replace('_3', '')
      .replace('_4', '');
  } else {
    user.state = 'ENROLLED';
  }

  user.username = `test_${role.replace(' ', '_')}_user`;
  user.email = `test_${role.replace(' ', '_')}_user@test.com`;
  user.firstName = role.toUpperCase();
  user.lastName = `USER`;
  user.registryRoles = registryRoles;
  if (role.includes('admin')) {
    user.disclosedName = 'Registry Administrator';
  } else {
    user.disclosedName = user.firstName + ' ' + user.lastName;
  }

  return await RegistryDbUserTestData.createUser(
    user,
    daysPassedFromActivationCodeIssuance
  );
}

function printA11yReport() {
  a11yMatrix.print();
}
