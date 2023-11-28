import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheKpAdminItlSpecificNotificationPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.KP_ADMIN_ITL_SPECIFIC_NOTIFICATION;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'Back to ITL Notification list',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Back to ITL Notification list')]`
      )
    );
    this.locators.set(
      'Initial',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Initial')])[2]`)
    );
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
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
