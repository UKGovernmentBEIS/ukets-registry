import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheRemoveTrustedAccountPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.REMOVE_TRUSTED_ACCOUNT;
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
      'Check the update',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-check-update-request/div/fieldset/div[1]`
      )
    );
    this.locators.set(
      'Submit request',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Submit request')]`)
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
