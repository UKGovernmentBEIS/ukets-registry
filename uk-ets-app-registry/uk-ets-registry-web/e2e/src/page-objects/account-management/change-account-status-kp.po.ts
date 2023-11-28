import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheChangeAccountStatusKpPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.CHANGE_ACCOUNT_STATUS_KP;
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
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Account holder content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/app-account-header/div/div/div/div/dl/div[1]/dd[1]`
      )
    );
    this.locators.set(
      'Account number content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/app-account-header/div/div/div/div/dl/div[2]/dd`
      )
    );
    this.locators.set(
      'Account name content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="feature-header"]/app-account-header/div/div/div/div/dl/div[4]/dd[2]`
      )
    );
    this.locators.set(
      'Account status',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Account number:')]/../../div[2]`
      )
    );

    this.locators.set(
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    // find radio buttons using following sibling (preceding simpling is considered the radio button for the corresponding text)
    this.locators.set(
      'Suspend account (fully)',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Suspend account (fully) ']`
      )
    );
    this.locators.set(
      'Suspend account (partially)',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Suspend account (partially) ']`
      )
    );
    this.locators.set(
      'Restrict some transactions',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Restrict some transactions ']`
      )
    );
    this.locators.set(
      'Remove restrictions',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Remove restrictions ']`
      )
    );
    this.locators.set(
      'Unsuspend account',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Unsuspend account ']`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(
        by.xpath(`//*[contains(text(),'Change the account status')]`)
      );
      await this.awaitElement(
        by.xpath(`//*[contains(text(),'Select Action')]`)
      );
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
