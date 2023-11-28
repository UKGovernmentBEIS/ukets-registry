import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheKpAdminItlNotificationsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.KP_ADMIN_ITL_NOTIFICATIONS;
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
      'first type result name',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),' - ')])[2]`)
    );
    this.locators.set(
      'first notification id name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-notice-list-container/app-notice-list/div/div/table/tbody/tr/td[1]/a`
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
