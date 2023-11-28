import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheAccountOperatorUpdatePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.VIEW_ACCOUNT_OPERATOR_UPDATE;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT_2)
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
      'Submit',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SUBMIT)
    );
    this.locators.set(
      'Manufacture of ceramic products by firing',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Manufacture of ceramic products by firing')]`
      )
    );
    this.locators.set(
      'BEIS-OPRED',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'BEIS-OPRED')]`)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//*[.=' Continue ']`));
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
