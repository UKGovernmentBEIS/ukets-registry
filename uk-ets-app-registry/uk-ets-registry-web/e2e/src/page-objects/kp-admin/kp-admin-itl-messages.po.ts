import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheKpAdminItlMessagesPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.KP_ADMIN_ITL_MESSAGES;
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
      'result 1 message ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-message-list-container/app-search-messages-results/table/tbody/tr/td[1]/a`
      )
    );
    this.locators.set(
      'itl message content',
      new UserFriendlyLocator('xpath', `//*[@id="content"]`)
    );
    this.locators.set(
      'Received on from date',
      new UserFriendlyLocator('xpath', `//*[@id="messageDateFrom"]`)
    );
    this.locators.set(
      'Received on to date',
      new UserFriendlyLocator('xpath', `//*[@id="messageDateTo"]`)
    );
    this.locators.set(
      'Back to ITL messages',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Back to ITL messages')]`
      )
    );
    this.locators.set(
      'Send message',
      new UserFriendlyLocator('xpath', `//*[contains(text(),' Send message ')]`)
    );
    this.locators.set(
      'Search',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SEARCH)
    );
    this.locators.set(
      'Clear',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CLEAR)
    );
    this.locators.set(
      'Hide filters',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_HIDE_FILTERS)
    );
    this.locators.set(
      'Show filters',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SHOW_FILTERS)
    );
    this.locators.set(
      'confirmation itl message id value',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-send-message-confirmation/div[1]/div/div/div/div/strong`
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
