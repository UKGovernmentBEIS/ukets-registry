import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheEmergencyPasswordAndOtpChangePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.EMERGENCY_PASSWORD_AND_OTP_CHANGE;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Submit')]`)
    );
    this.locators.set(
      'Email address',
      new UserFriendlyLocator('xpath', `//*[@id="email"]`)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Continue')]`)
    );
    this.locators.set(
      'Email address',
      new UserFriendlyLocator('xpath', `//*[@id="email"]`)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Continue')]`)
    );
    this.locators.set(
      'Email address',
      new UserFriendlyLocator('xpath', `//*[@id="email"]`)
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Submit')]`)
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
