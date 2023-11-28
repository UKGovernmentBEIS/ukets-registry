import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheEtsAdministrationAllocationTableCheckSubmitPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ETS_ADMIN_ALLOCATION_TABLE_CHECK_SUBMIT;
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
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE_2)
    );
    this.locators.set(
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
    this.locators.set(
      'Propose to issue UK allowances',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Propose to issue UK allowances ']/a`
      )
    );
    this.locators.set(
      'Cancel request',
      new UserFriendlyLocator('xpath', `//*[.=' Cancel request ']`)
    );
    this.locators.set(
      'Submit request',
      new UserFriendlyLocator('xpath', `//*[.=' Submit request ']`)
    );
    this.locators.set(
      'Upload allocation table',
      new UserFriendlyLocator('xpath', `//*[.=' Upload allocation table ']/a`)
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Choose File',
      new UserFriendlyLocator('xpath', `//*[@id="file-upload-1"]`)
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
