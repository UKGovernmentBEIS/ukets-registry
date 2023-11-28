import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheChangeAccountStatusConfirmKpPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.CHANGE_ACCOUNT_STATUS_CONFIRM_KP;
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
      'comment area',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_COMMENT)
    );
    this.locators.set(
      'page main content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Current account status')]/../..`
      )
    );
    this.locators.set(
      'Account holder content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/app-account-header/div/div/div/div/dl/div[1]/dd[1]`
      )
    );
    this.locators.set(
      'Account number content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/app-account-header/div/div/div/div/dl/div[2]/dd`
      )
    );
    this.locators.set(
      'Account name content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Account name: ')]/..`
      )
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Apply',
      new UserFriendlyLocator('xpath', `//*[.=' Apply ']`)
    );
    this.locators.set(
      'Cancel',
      new UserFriendlyLocator('xpath', `//*[.='Cancel']`)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(
        by.xpath(`//*[contains(text(),'Change the account status')]`)
      );
      await this.awaitElement(
        by.xpath(`//*[contains(text(),'Check your update and confirm')]`)
      );
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
