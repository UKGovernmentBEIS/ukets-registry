import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import {
  browser,
  until,
  by,
  ElementFinder,
  ExpectedConditions,
  element,
} from 'protractor';
import { AppRoutesPerScreenUtil } from '../../util/app.routes-per-screen.util';

export class KnowsTheSetupTwoAuthenticationFactorPage extends KnowsThePage {
  constructor() {
    super(false);
    this.name = RegistryScreen.ACCOUNT_OPENING_SETUP_TWO_AUTH_FACTOR;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'Problems with scanning?',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),' Problems with scanning?')]`
      )
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', `//*[@id="saveTOTPBtn"]`)
    );
    this.locators.set(
      'Verification code',
      new UserFriendlyLocator('xpath', `//*[@id="totp"]`)
    );
  }

  async waitForMe() {
    await Promise.all([browser.waitForAngularEnabled(false)]);
  }

  async navigateTo(screenName: string) {
    await Promise.all([
      browser.waitForAngularEnabled(false),
      browser.get(
        browser.baseUrl +
          AppRoutesPerScreenUtil.get(RegistryScreen.REGISTRY_DASHBOARD)
      ),
    ]);
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
