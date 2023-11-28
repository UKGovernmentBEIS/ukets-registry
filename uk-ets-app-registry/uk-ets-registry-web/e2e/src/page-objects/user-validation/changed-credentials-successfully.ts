import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheSuccessfullyChangedCredentialsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.SUCCESSFULLY_CHANGED_YOUR_CREDENTIALS;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'sign in to Registry',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="kc-info-message"]/div/div/p[2]/a`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
      await this.awaitElement(
        by.xpath(`//*[contains(text(),'sign in to the UK')]`)
      );
      await this.awaitElement(
        by.xpath(
          `//*[contains(text(),'You have successfully changed your credentials')]`
        )
      );
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
