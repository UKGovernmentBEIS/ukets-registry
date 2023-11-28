import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheUpdateExclusionStatusPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.UPDATE_EXCLUSION_STATUS;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    this.locators.set(
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SUBMIT)
    );
    this.locators.set(
      'Go back to the account',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'to the account')]`)
    );
    this.locators.set(
      '2021',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'2021')]`)
    );
    this.locators.set(
      'Yes',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Yes')]`)
    );
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
