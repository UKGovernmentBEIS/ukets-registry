import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheTaskConfirmationApprovedPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.TASK_CONFIRMATION_APPROVED;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
    );
    this.locators.set(
      'Accounts',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Accounts')]`)
    );
    this.locators.set(
      'Tasks',
      new UserFriendlyLocator('xpath', `//*[.=' Tasks ']`)
    );
    this.locators.set(
      'Go to task list',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Go to task list')]`
      )
    );
    this.locators.set(
      'Back to task list',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Back to task list')]`
      )
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
