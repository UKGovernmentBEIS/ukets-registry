import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheRequestaNewAccountOverviewPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.REQUEST_A_NEW_ACCOUNT_OVERVIEW;
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
      'Account type',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-list/div/div/form/p[1]/strong`
      )
    );
    this.locators.set(
      'Account type',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-list/div/div/form/p[1]/strong`
      )
    );
    this.locators.set(
      'Account type',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-list/div/div/form/p[1]/strong`
      )
    );
  }

  getData(): Map<string, Promise<string>> {
    return new Map();
  }
}
