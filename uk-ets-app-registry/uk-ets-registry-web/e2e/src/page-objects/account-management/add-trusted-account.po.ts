import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheAddTrustedAccountPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ADD_TRUSTED_ACCOUNT;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'type of update content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Type of update')]/../..`
      )
    );
    this.locators.set(
      'account area content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-wizards-container/app-check-update-request-container/app-check-update-request/div/fieldset/div[2]/app-trusted-account-table/table`
      )
    );
    this.locators.set(
      'Check the update',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-check-update-request/div/fieldset/div[1]`
      )
    );
    this.locators.set(
      'Submit request',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Submit request')]`)
    );
    this.locators.set(
      'user defined country code',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedCountryCode"]`)
    );
    this.locators.set(
      'user defined account type',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedAccountType"]`)
    );
    this.locators.set(
      'user defined account id',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedAccountId"]`)
    );
    this.locators.set(
      'user defined account period',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedPeriod"]`)
    );
    this.locators.set(
      'user defined check digits period',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedCheckDigits"]`)
    );
    this.locators.set(
      'comment area',
      new UserFriendlyLocator('xpath', `//*[@id="accountDescription"]`)
    );
    this.locators.set(
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//*[.='BETA']`));
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
