import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsEtsTheUploadEmissionsTablePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ETS_ADMIN_UPLOAD_EMISSIONS_TABLE;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'otp',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_OTP)
    );
    this.locators.set(
      'Choose file',
      new UserFriendlyLocator('xpath', `//*[@id="emissionsTable"]`)
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SUBMIT)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
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
