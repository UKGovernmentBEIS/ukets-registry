import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheKpAdminItlMessagesSpecificMessagePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.KP_ADMIN_ITL_MESSAGES_SPECIFIC_MESSAGE;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
    );
    this.locators.set(
      'Back to ITL Message list',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Back to ITL Message list')]`
      )
    );
    this.locators.set(
      'Send message',
      new UserFriendlyLocator('xpath', `//*[contains(text(),' Send message ')]`)
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
