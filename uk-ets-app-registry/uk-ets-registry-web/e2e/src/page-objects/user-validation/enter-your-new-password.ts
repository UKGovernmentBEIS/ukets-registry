import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheEnterYourNewPasswordPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ENTER_YOUR_NEW_PASSWORD;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', `//*[@id="kc-form-buttons"]/input`)
    );
    this.locators.set(
      'New Password',
      new UserFriendlyLocator('xpath', `//*[@id="password-new"]`)
    );
    this.locators.set(
      'Confirm password',
      new UserFriendlyLocator('xpath', `//*[@id="password-confirm"]`)
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
