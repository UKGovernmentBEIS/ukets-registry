import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheEmergencyAccessRequestSubmittedPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.EMERGENCY_ACCESS_REQUEST_SUBMITTED;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Sign in',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_IN_2)
    );
    this.locators.set(
      'submit title',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Emergency access request submitted')]`
      )
    );
    this.locators.set(
      'request Id value',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(@class,'govuk-!-font-weight-bold')]`
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
