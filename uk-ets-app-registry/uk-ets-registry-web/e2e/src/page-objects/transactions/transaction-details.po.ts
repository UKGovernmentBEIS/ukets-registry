import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheTransactionDetailsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.TRANSACTION_DETAILS;
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
      'transaction details label',
      new UserFriendlyLocator('xpath', `//*[.='Transaction details']`)
    );
    this.locators.set(
      'Back to transaction list',
      new UserFriendlyLocator('xpath', `//*[.='Back to transaction list']/a`)
    );
    this.locators.set(
      'transaction status',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="feature-header"]/app-transaction-header/div/div/div[3]/app-govuk-tag/strong`
      )
    );
    this.locators.set(
      'transaction ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="feature-header"]/app-transaction-header/div/div/div[2]/div[1]`
      )
    );
    this.locators.set(
      'transaction details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-transaction-details-container/app-transaction-info/dl`
      )
    );
    this.locators.set(
      'quantity to transfer content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-transaction-details-container/app-transaction-quantity/app-transaction-quantity-table/table`
      )
    );
    this.locators.set(
      'History & comments content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-domain-events/table/thead`
      )
    );
    this.locators.set(
      'History & comments area',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-transaction-details-container/app-domain-events/span`
      )
    );
    this.locators.set(
      'Warning and error messages content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-transaction-details-container/app-response-messages/span`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(
        by.xpath(`//*[contains(text(),'Transaction ID:')]`)
      );
      await this.awaitElement(by.xpath(`//*[.=' Transaction details ']`));
    }
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
