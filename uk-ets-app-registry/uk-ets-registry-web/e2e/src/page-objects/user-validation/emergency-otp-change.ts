import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheEmergencyOtpChangePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.EMERGENCY_OTP_CHANGE;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Sign in',
      new UserFriendlyLocator('xpath', `//*[contains(text(),' Sign in ')]`)
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Back')]`)
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Submit')]`)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    this.locators.set(
      'I didnt get an email',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'get an email')]`)
    );
    this.locators.set(
      'request emergency access again',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'request emergency access again')]`
      )
    );
    this.locators.set(
      'Email address',
      new UserFriendlyLocator('xpath', `//*[@id="email"]`)
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
