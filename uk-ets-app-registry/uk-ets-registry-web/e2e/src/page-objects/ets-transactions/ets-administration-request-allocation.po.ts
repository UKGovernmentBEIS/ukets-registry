import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheEtsAdministrationProposeIssueUkAllowancesReqAllocPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ETS_ADMIN_REQUEST_ALLOCATION;
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
      'Upload emissions table',
      new UserFriendlyLocator('xpath', `//*[.=' Upload emissions table ']/a`)
    );
    this.locators.set(
      'Propose to issue UK allowances',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Propose to issue UK allowances ']/a`
      )
    );
    this.locators.set(
      'Request allocation of UK allowances',
      new UserFriendlyLocator(
        'xpath',
        `//*[.='Request allocation of UK allowances']`
      )
    );
    this.locators.set(
      'first radio button',
      new UserFriendlyLocator('xpath', `(//input[@type="radio"])[1]`)
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
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
