import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheUserListPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.USER_LIST;
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
      'Search',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SEARCH)
    );
    this.locators.set(
      'Clear',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CLEAR)
    );
    this.locators.set(
      'Users label',
      new UserFriendlyLocator('xpath', `//*[.='Users']`)
    );
    this.locators.set(
      'Name or user ID',
      new UserFriendlyLocator('xpath', `//*[@id="nameOrUserId-label"]`)
    );
    this.locators.set(
      'User status',
      new UserFriendlyLocator('xpath', `//*[@id="status-label"]`)
    );
    this.locators.set(
      'User email address',
      new UserFriendlyLocator('xpath', `//*[@id="email-label"]`)
    );
    this.locators.set(
      'Date From',
      new UserFriendlyLocator('xpath', `//*[@id="lastSignInFrom-hint"]`)
    );
    this.locators.set(
      'Date To',
      new UserFriendlyLocator('xpath', `//*[@id="lastSignInTo-hint"]`)
    );
    this.locators.set(
      'Last sign in date Date From',
      new UserFriendlyLocator('xpath', `//*[@id="lastSignInFrom"]`)
    );
    this.locators.set(
      'Last sign in date Date To',
      new UserFriendlyLocator('xpath', `//*[@id="lastSignInTo"]`)
    );
    this.locators.set(
      'User role',
      new UserFriendlyLocator('xpath', `//*[@id="role-label"]`)
    );
    this.locators.set(
      'User role: User',
      new UserFriendlyLocator('xpath', `//*[@id="role-label"]`)
    );
    this.locators.set(
      'User role: Senior registry administrator',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Senior registry administrator')]`
      )
    );
    this.locators.set(
      'User role: Junior registry administrator',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Junior registry administrator')]`
      )
    );
    this.locators.set(
      'User status: REGISTERED',
      new UserFriendlyLocator('xpath', `//*[@id="status"]/option[2]`)
    );
    this.locators.set(
      'User status: VALIDATED',
      new UserFriendlyLocator('xpath', `//*[@id="status"]/option[3]`)
    );
    this.locators.set(
      'User status: ENROLLED',
      new UserFriendlyLocator('xpath', `//*[@id="status"]/option[4]`)
    );
    this.locators.set(
      'result rows',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-list/app-search-users-results/table/tbody`
      )
    );
    this.locators.set(
      'result row number 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-search-users-results/table/tbody/tr[1]/td/a`
      )
    );
    this.locators.set(
      'Show filters',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SHOW_FILTERS)
    );
    this.locators.set(
      'Hide filters',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_HIDE_FILTERS)
    );
    this.locators.set(
      'Authorized Representative ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="authorizedRepresentativeUrid"]`
      )
    );
    this.locators.set(
      'Results headers',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-list/app-search-users-results/table/thead`
      )
    );
    this.locators.set(
      'Name or user ID textbox',
      new UserFriendlyLocator('xpath', `//*[@id="nameOrUserId"]`)
    );
    this.locators.set(
      'User status dropdown',
      new UserFriendlyLocator('xpath', `//*[@id="status"]`)
    );
    this.locators.set(
      'User email address textbox',
      new UserFriendlyLocator('xpath', `//*[@id="email"]`)
    );
    this.locators.set(
      'Last sign in date From datetime',
      new UserFriendlyLocator('xpath', `//*[@id="lastSignInFrom"]`)
    );
    this.locators.set(
      'Last sign in date To datetime',
      new UserFriendlyLocator('xpath', `//*[@id="lastSignInTo"]`)
    );
    this.locators.set(
      'User role dropdown',
      new UserFriendlyLocator('xpath', `//*[@id="role"]`)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//*[.='Users']`));
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
