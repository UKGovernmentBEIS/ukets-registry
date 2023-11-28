import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheUserStatusChangeConfirmationPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.CHANGE_USER_STATUS_CONFIRM;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
    );
    this.locators.set(
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Apply',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Apply')]`)
    );
    this.locators.set(
      'comment area',
      new UserFriendlyLocator('xpath', `//*[@id="comment"]`)
    );
    this.locators.set(
      'Check your update and confirm',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Check your update and confirm')]`
      )
    );
    this.locators.set(
      'Current user status',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Current user status')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Action',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Action')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'New user status',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'New user status')]/following-sibling::dd[1]`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//*[.='Change the user status']`));
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
