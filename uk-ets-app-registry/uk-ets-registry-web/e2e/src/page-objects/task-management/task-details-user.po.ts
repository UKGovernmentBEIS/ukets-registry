import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheTaskDetailsUserPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.TASK_DETAILS_USER;
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
      'User details item',
      new UserFriendlyLocator('xpath', `//*[contains(text(),' User details ')]`)
    );
    this.locators.set(
      'History and comments item',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),' History and comments ')]`
      )
    );
    this.locators.set(
      'Last signed in value',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-user-details-container/app-user-details-container/app-user-details/div/div/div/div[2]/dl/div/dd`
      )
    );
    this.locators.set(
      'Authorized representative in accounts content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-user-details-container/app-user-details-container/app-user-details/div/div/div/app-ar-in-accounts/div`
      )
    );
    this.locators.set(
      'Identification documentation content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-user-details-container/app-user-details-container/app-user-details/div/div/div/app-identification-documentation/div`
      )
    );
    this.locators.set(
      'Registration details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-user-details-container/app-user-details-container/app-user-details/div/div/div/app-registration-details/div`
      )
    );
    this.locators.set(
      'Personal details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-user-details-container/app-user-details-container/app-user-details/div/div/div/app-personal-details/div`
      )
    );
    this.locators.set(
      'Work contact details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-user-details-container/app-user-details-container/app-user-details/div/div/div/app-work-contact-details/div`
      )
    );
    this.locators.set(
      'Request documents',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Request documents')]`
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
