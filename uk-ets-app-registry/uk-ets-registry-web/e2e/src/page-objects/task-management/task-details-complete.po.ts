import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheTaskDetailsCompletePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.TASK_DETAILS_COMPLETE;
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
      'Complete request label',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Complete request')]`
      )
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    // button
    this.locators.set(
      'Complete',
      new UserFriendlyLocator('xpath', `//*[.=' Complete ']`)
    );
    // textbox
    this.locators.set(
      'comment area',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_COMMENT)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//*[contains(text(),'100000001')]`));
    }
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
