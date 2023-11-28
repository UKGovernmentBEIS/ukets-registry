import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheTaskListBulkAssignPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.TASK_LIST_BULK_ASSIGN;
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
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'User',
      new UserFriendlyLocator('xpath', `//input[@id="user"]`)
    );
    this.locators.set(
      'SENIOR ADMIN 0',
      new UserFriendlyLocator('xpath', `//*[.='SENIOR ADMIN 0']`)
    );
    this.locators.set(
      'JUNIOR ADMIN 0',
      new UserFriendlyLocator('xpath', `//*[.='JUNIOR ADMIN 0']`)
    );
    this.locators.set(
      'Available typeahead JUNIOR ADMIN 0',
      new UserFriendlyLocator('xpath', `//*[@class="dropdown-item active"]`)
    );
    this.locators.set(
      'Enter some comments',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_COMMENT)
    );
    this.locators.set(
      'Assign',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Assign')])[2]`)
    );
    this.locators.set(
      'Assign task',
      new UserFriendlyLocator('xpath', `//*[contains(text(),' Assign task ')]`)
    );
    this.locators.set(
      'Inset text',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-bulk-assign/form/div[1]`
      )
    );
    this.locators.set(
      'Heading text',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-bulk-assign/form/h1`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`(//*[contains(text(),'Assign')])[2]`));
    }
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
