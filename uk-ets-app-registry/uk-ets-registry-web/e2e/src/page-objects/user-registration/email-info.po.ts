import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser } from 'protractor';

export class KnowsTheEmailInfoPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.EMAIL_INFO;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
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
