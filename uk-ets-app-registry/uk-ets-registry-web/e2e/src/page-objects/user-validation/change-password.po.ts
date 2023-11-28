import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheChangePasswordPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.CHANGE_PASSWORD;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE_2)
    );
    this.locators.set(
      'Current password',
      new UserFriendlyLocator('xpath', `//*[@id="currentPassword"]`)
    );
    this.locators.set(
      'Confirm new password',
      new UserFriendlyLocator('xpath', `//*[@id="pconfirm"]`)
    );
    this.locators.set(
      'New password',
      new UserFriendlyLocator('xpath', `//*[@id="password"]`)
    );
    this.locators.set(
      'otp',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_OTP)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
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
