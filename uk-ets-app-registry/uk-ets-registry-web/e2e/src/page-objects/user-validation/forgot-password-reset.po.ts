import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheForgotPasswordResetPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.FORGOT_PASSWORD_RESET;
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
      'Continue',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Continue')]`)
    );
    this.locators.set(
      'password',
      new UserFriendlyLocator('xpath', `//*[@id="password"]`)
    );
    this.locators.set(
      'confirm password',
      new UserFriendlyLocator('xpath', `//*[@id="pconfirm"]`)
    );
    this.locators.set(
      'otp',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_OTP_2)
    );
    this.locators.set(
      'sign in to the UK Emissions Registry',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'sign in to the UK Emissions Registry')]`
      )
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
