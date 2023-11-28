import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';
import { AppRoutesPerScreenUtil } from '../../util/app.routes-per-screen.util';

export class KnowsTheSigninPage extends KnowsThePage {
  private readonly REGISTER_ONE_LINK = 'Register one';

  constructor() {
    super(false);
    this.name = RegistryScreen.SIGN_IN;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpLocators() {
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
    );
    this.locators.set(
      'otp',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_OTP)
    );
    this.locators.set(
      'Email',
      new UserFriendlyLocator('xpath', `//*[@id="username"]`)
    );
    this.locators.set(
      'Password',
      new UserFriendlyLocator('xpath', `//*[@id="password"]`)
    );
    this.locators.set(
      'Accept all cookies',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Accept all cookies')]`
      )
    );
    this.locators.set(
      'I have lost access to my authenticator app',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'I have lost access to my authenticator app')]`
      )
    );
    this.locators.set(
      'I have forgotten or I have to reset my password',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'I have forgotten or I have to reset my password')]`
      )
    );
    this.locators.set(
      'I have forgotten my password and lost access to my authenticator app',
      new UserFriendlyLocator('xpath', `//*[@id="lostAuthAndDeviceAppLink"]`)
    );
    this.locators.set(
      'Sign in',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_IN)
    );
    this.locators.set(
      'Register one',
      new UserFriendlyLocator('xpath', `//*[@id="kc-registration"]/span/a`)
    );
  }

  async waitForMe() {
    await Promise.all([
      browser.waitForAngularEnabled(false),
      this.awaitElement(by.xpath(`//*[@id="username"]`)),
      this.awaitElement(by.xpath(`//*[@id="password"]`)),
      this.awaitElement(by.xpath(`//*[@id="kc-registration"]/span/a`)),
    ]);
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

  async getErrorSummary(errorMessageToLocate: string) {
    await this.awaitElement(by.xpath(`//*[text()='${errorMessageToLocate}']`));
    let error = await browser.driver.findElement(
      by.xpath(`//*[text()='${errorMessageToLocate}']`)
    );
    if (!error) {
      error = await browser.driver.findElement(
        by.xpath(`//*[@id="main-content"]/div[2]/div/div[1]/div/ul/li/a`)
      );
    }
    return error ? await error.getText() : undefined;
  }

  setUpTestData() {
    this.testData
      .set('username', 'user_registered@test.com')
      .set('password', KnowsThePage.DEFAULT_SIGN_IN_PASSWORD);
  }

  getData(): Map<string, Promise<string>> {
    try {
      return new Map<string, Promise<string>>()
        .set('username', this.getValue('username'))
        .set('password', this.getValue('password'));
    } catch (e) {
      throw e;
    }
  }
}

