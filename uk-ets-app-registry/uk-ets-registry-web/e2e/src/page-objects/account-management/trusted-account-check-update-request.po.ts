import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheTrustedAccountCheckUpdateRequestPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.TRUSTED_ACCOUNT_CHECK_UPDATE_REQUEST;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Go back to the account',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Go back to the account')]`
      )
    );
    this.locators.set(
      'Cancel request',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_CANCEL_REQUEST
      )
    );
    this.locators.set(
      'Tasks',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Tasks')]`)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//*[.='BETA']`));
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
