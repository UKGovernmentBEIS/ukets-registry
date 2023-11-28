import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheUserStatusChangePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.CHANGE_USER_STATUS;
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
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
    this.locators.set(
      'Suspend user',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Suspend user ']`
      )
    );
    this.locators.set(
      'Restore user',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Restore user ']`
      )
    );
    this.locators.set(
      'Validate user',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Validate user ']`
      )
    );
    this.locators.set(
      'Suspend user label',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type='radio']/following-sibling::label[contains(text(),' Suspend user ')]`
      )
    );
    this.locators.set(
      'Validate user label',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type='radio']/following-sibling::label[contains(text(),' Validate user ')]`
      )
    );
    this.locators.set(
      'Restore user label',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type='radio']/following-sibling::label[contains(text(),' Restore user ')]`
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
