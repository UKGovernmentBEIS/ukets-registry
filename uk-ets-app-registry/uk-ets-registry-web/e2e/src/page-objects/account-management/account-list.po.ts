import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheAccountListPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ACCOUNT_LIST;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
    );
    this.locators.set(
      'Account number result 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-list/app-search-accounts-results/table/tbody/tr[1]/td[1]/a`
      )
    );
    this.locators.set(
      'Account number result 2',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-list/app-search-accounts-results/table/tbody/tr[2]/td[1]/a`
      )
    );
    this.locators.set(
      'Account result 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-search/app-search-accounts-results/table/tbody/tr/td[2]/a`
      )
    );
    this.locators.set(
      'Account result number 1',
      new UserFriendlyLocator(
        'xpath',
        `(//*[@id="main-content"]/div[2]/div/app-account-list/app-search-accounts-results/table/tbody/tr)[1]`
      )
    );
    this.locators.set(
      'Account result number 2',
      new UserFriendlyLocator(
        'xpath',
        `(//*[@id="main-content"]/div[2]/div/app-account-list/app-search-accounts-results/table/tbody/tr)[2]`
      )
    );
    this.locators.set(
      'Generate current report',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_GENERATE_CURRENT_REPORT
      )
    );
    this.locators.set(
      'Reports',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Reports')]`)
    );
    this.locators.set(
      'Account UK-100-50001-2-11',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-50001-2-11']/a`)
    );
    this.locators.set(
      'Account UK-100-1016-0-17',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-1016-0-17']/a`)
    );
    this.locators.set(
      'Account GB-120-9999-2-99',
      new UserFriendlyLocator('xpath', `//*[.='GB-120-9999-2-99']/a`)
    );
    this.locators.set(
      'Account UK-100-10000052-0-92',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-10000052-0-92']/a`)
    );
    this.locators.set(
      'Account UK-100-10000062-2-36',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-10000062-2-36']/a`)
    );
    this.locators.set(
      'Account UK-100-10000056-0-72',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-10000056-0-72']/a`)
    );
    this.locators.set(
      'Account UK-100-10000019-2-11',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-10000019-2-11']/a`)
    );
    this.locators.set(
      'Account UK-100-10000019-0-63',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-10000019-0-63']/a`)
    );
    this.locators.set(
      'Account UK-100-10000025-0-33',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-10000025-0-33']/a`)
    );
    this.locators.set(
      'Account UK-100-10000002-0-51',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-10000002-0-51']/a`)
    );
    this.locators.set(
      'Account UK-100-50002-2-6',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-50002-2-6']/a`)
    );
    this.locators.set(
      'Account UK-100-1011-0-42',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-1011-0-42']/a`)
    );
    this.locators.set(
      'Account UK-100-50012-2-53',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-50012-2-53']/a`)
    );
    this.locators.set(
      'Account UK-100-50011-2-58',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-50011-2-58']/a`)
    );
    this.locators.set(
      'Account UK-100-1010-0-47',
      new UserFriendlyLocator('xpath', `//*[.='UK-100-1010-0-47']/a`)
    );
    this.locators.set(
      'Account GB-100-1000-1-94',
      new UserFriendlyLocator('xpath', `//*[.='GB-100-1000-1-94']/a`)
    );
    this.locators.set(
      'Account GB-121-50032-2-58',
      new UserFriendlyLocator('xpath', `//*[.='GB-121-50032-2-58']/a`)
    );
    this.locators.set(
      'Account GB-100-1003-2-76',
      new UserFriendlyLocator('xpath', `//*[.='GB-100-1003-2-76']/a`)
    );
    this.locators.set(
      'Account GB-100-1001-1-89',
      new UserFriendlyLocator('xpath', `//*[.='GB-100-1001-1-89']/a`)
    );

    this.locators.set(
      'Account GB-121-50035-2-40',
      new UserFriendlyLocator('xpath', `//*[.='GB-121-50035-2-40']/a`)
    );

    this.locators.set(
      'Search',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SEARCH)
    );
    this.locators.set(
      'Accounts label',
      new UserFriendlyLocator('xpath', `//*[.='Accounts']`)
    );
    this.locators.set(
      'Account name or ID',
      new UserFriendlyLocator('xpath', `//*[@id="accountIdOrName"]`)
    );
    this.locators.set(
      'Accounts status: Blocked',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Blocked')]`)
    );
    this.locators.set(
      'Accounts status: Open',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Open')])[1]`)
    );
    this.locators.set(
      'Accounts status: Suspended',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Suspended')]`)
    );
    this.locators.set(
      'Accounts status: Proposed',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Proposed')]`)
    );
    this.locators.set(
      'Tasks',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Tasks')]`)
    );
    this.locators.set(
      'Transactions',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Transactions')]`)
    );
    this.locators.set(
      'Accounts status: Transfer pending',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Transfer pending')]`
      )
    );
    this.locators.set(
      'Accounts status: Closed',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Closed')]`)
    );
    this.locators.set(
      'Accounts status: All except closed',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'All except closed')]`
      )
    );
    this.locators.set(
      'Account holder name',
      new UserFriendlyLocator('xpath', `//*[@id="accountHolderName"]`)
    );
    this.locators.set(
      'Permit or monitoring plan ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="permitOrMonitoringPlanIdentifier"]`
      )
    );
    this.locators.set(
      'Account type: Operator holding account',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Operator holding account')]`
      )
    );
    this.locators.set(
      'Account type: Aircraft operator holding account',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Aircraft operator holding account')]`
      )
    );
    this.locators.set(
      'Account type: Trading account',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Trading account')]`
      )
    );
    this.locators.set(
      'Account type: UK Auction delivery account',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'UK Auction delivery account')]`
      )
    );
    this.locators.set(
      'Account type: Person holding account',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Person holding account')]`
      )
    );
    this.locators.set(
      'Account type: Former operator holding account',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Former operator holding account')]`
      )
    );
    this.locators.set(
      'Account type: All KP government accounts',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'All KP government accounts')]`
      )
    );
    this.locators.set(
      'Account type: All ETS government accounts',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'All ETS government accounts')]`
      )
    );
    this.locators.set(
      'Excluded For Year',
      new UserFriendlyLocator('xpath', `//*[@id="excludedForYear"]`)
    );
    this.locators.set(
      'Dynamic compliance status: Compliant',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Compliant')]`)
    );
    this.locators.set(
      'Dynamic compliance status: Needs to enter emissions',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Needs to enter emissions')]`
      )
    );
    this.locators.set(
      'Dynamic compliance status: Needs to surrender',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Needs to surrender')]`
      )
    );
    this.locators.set(
      'Dynamic compliance status: Not calculated',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Not calculated')]`)
    );
    this.locators.set(
      'Dynamic compliance status: Excluded',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Excluded')]`)
    );
    this.locators.set(
      'Show filters',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SHOW_FILTERS)
    );
    this.locators.set(
      'Hide filters',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_HIDE_FILTERS)
    );
    this.locators.set(
      'Advanced search',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Advanced search')]`
      )
    );
    this.locators.set(
      'Authorized Representative ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="authorizedRepresentativeUrid"]`
      )
    );
    this.locators.set(
      'Account status dropdown',
      new UserFriendlyLocator('xpath', `//*[@id="accountStatus"]`)
    );
    this.locators.set(
      'Dynamic compliance status dropdown',
      new UserFriendlyLocator('xpath', `//*[@id="complianceStatus"]`)
    );
    this.locators.set(
      'Account type dropdown',
      new UserFriendlyLocator('xpath', `//*[@id="accountType"]`)
    );
    this.locators.set(
      'table rows results',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-list/app-search-accounts-results/table/tbody`
      )
    );
    this.locators.set(
      'table headers results',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-list/app-search-accounts-results/table/thead/tr`
      )
    );
    // returned results
    this.locators.set(
      'Account list returned result rows',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(@class,'govuk-table__cell')]/a`
      )
    );
    this.locators.set(
      'Regulator: SEPA',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'SEPA')]`)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//*[.='Accounts']`));
    }
  }

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }
}
