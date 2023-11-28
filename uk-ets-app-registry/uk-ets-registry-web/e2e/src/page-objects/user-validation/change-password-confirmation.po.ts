import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheChangePasswordConfirmationPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.CHANGE_PASSWORD_CONFIRMATION;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Sign in',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_IN_2)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(
        by.xpath(
          `//*[contains(text(),'You have successfully changed your password')]`
        )
      );
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
