import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser } from 'protractor';

export class KnowsTheViewAccountAllocationStatusPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.VIEW_ACCOUNT_ALLOCATION_STATUS;
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
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    this.locators.set(
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
    this.locators.set(
      'current allocation status information',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-update-allocation-status-wizard/div/div/app-update-allocation-status-form-container/app-update-allocation-status-form/div/fieldset/div[2]/form/table`
      )
    );
    this.locators.set(
      'Allowed',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Allowed ']`
      )
    );
    this.locators.set(
      'Withheld',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Withheld ']`
      )
    );
    this.locators.set(
      'page main content',
      new UserFriendlyLocator(
        'xpath',
        `//table[@aria-describedby="Allocation status"]`
      )
    );
    this.locators.set(
      'Justification',
      new UserFriendlyLocator('xpath', `//*[@id="justification"]`)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
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
