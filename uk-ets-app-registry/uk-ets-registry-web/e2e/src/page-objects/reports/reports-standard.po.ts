import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheReportsStandardPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.REPORTS_STANDARD;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'All Users: Generate report',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'All Users')]/../..)//*[contains(text(),'Generate report')]`
      )
    );
    this.locators.set(
      'Authorised representatives per account: Generate report',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Authorised representatives per account')]/../..)//*[contains(text(),'Generate report')]`
      )
    );
    this.locators.set(
      'Accounts with no ARs or AR nomination: Generate report',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Accounts with no ARs or AR nomination')]/../..)//*[contains(text(),'Generate report')]`
      )
    );
    this.locators.set(
      'Orphan Users: Generate report',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Orphan Users')]/../..)//*[contains(text(),'Generate report')]`
      )
    );
    this.locators.set(
      'Search Tasks: Generate report',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Search Tasks')]/../..)//*[contains(text(),'Generate report')]`
      )
    );
    this.locators.set(
      'Search Transactions: Generate report',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Search Transactions')]/../..)//*[contains(text(),'Generate report')]`
      )
    );
    this.locators.set(
      'Search Accounts: Generate report',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Search Accounts')]/../..)//*[contains(text(),'Generate report')]`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
    }
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
