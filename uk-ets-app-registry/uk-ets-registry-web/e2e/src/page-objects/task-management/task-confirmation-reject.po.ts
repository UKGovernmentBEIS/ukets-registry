import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheTaskConfirmationRejectPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.TASK_CONFIRMATION_REJECT;
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
      'more info',
      new UserFriendlyLocator('xpath', `//*[.='more info']`)
    );
    this.locators.set(
      'less info',
      new UserFriendlyLocator('xpath', `//*[.='less info']`)
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Back')]`)
    );
    this.locators.set(
      'Reject',
      new UserFriendlyLocator('xpath', `//*[.=' Reject ']`)
    );
    this.locators.set(
      'Complete task',
      new UserFriendlyLocator('xpath', `//*[.=' Complete task ']`)
    );
    this.locators.set(
      'comment area',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_COMMENT)
    );
    this.locators.set(
      'Initiated by content',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Initiated by')]`)
    );
    this.locators.set(
      'Claimed by content',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Claimed by')]`)
    );
    this.locators.set(
      'Reject request title',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Reject')]`)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//*[contains(text(),'Reject')]`));
    }
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
