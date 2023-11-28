import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheReportsDownloadsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.REPORTS_DOWNLOADS;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'latest uk ets Search Tasks xlsx report',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'uk_ets_Search_Tasks_')])[1]`
      )
    );
    this.locators.set(
      'latest uk ets Search Transactions xlsx report',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'uk_ets_Search_Transactions_')])[1]`
      )
    );
    this.locators.set(
      'latest uk ets Search Accounts xlsx report',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'uk_ets_Search_Accounts_')])[1]`
      )
    );
    this.locators.set(
      'Standard reports',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Standard reports')]`
      )
    );
    this.locators.set(
      'Download files',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Download files')]`)
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
