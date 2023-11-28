import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser } from 'protractor';

export class KnowsTheLandingPagePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.LANDING_PAGE;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Kyoto Protocol Public Reports',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Kyoto Protocol Public Reports')]`
      )
    );
    this.locators.set(
      'ETS Public Reports',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'ETS Public Reports')]`
      )
    );
    this.locators.set(
      'create a registry sign in',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'create a registry sign in')]`
      )
    );
    this.locators.set(
      'Sign in',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_IN_3)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Continue')]`)
    );
  }

  getData(): Map<string, Promise<string>> {
    return new Map();
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
