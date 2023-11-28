import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheFillinInstallationDetailsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.FILL_IN_INSTALLATION_DETAILS;
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
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    this.locators.set(
      'Installation name',
      new UserFriendlyLocator('xpath', `//*[@id="name"]`)
    );
    this.locators.set(
      'Installation Id',
      new UserFriendlyLocator('xpath', `//*[@id="identifier"]`)
    );
    this.locators.set(
      'Permit ID',
      new UserFriendlyLocator('xpath', `//*[@id="permit.id"]`)
    );
    this.locators.set(
      'Permit entry into force: Day',
      new UserFriendlyLocator('xpath', `//*[@id="permit-day"]`)
    );
    this.locators.set(
      'Permit entry into force: Month',
      new UserFriendlyLocator('xpath', `//*[@id="permit-month"]`)
    );
    this.locators.set(
      'Permit entry into force: Year',
      new UserFriendlyLocator('xpath', `//*[@id="permit-year"]`)
    );
    this.locators.set(
      'First year of verified emission submission',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="yearsOfVerifiedEmission.firstYear"]`
      )
    );
    this.locators.set(
      'Regulator',
      new UserFriendlyLocator('xpath', `//*[@id="regulator"]`)
    );
    this.locators.set(
      'Regulator: EA',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'EA')])[2]`)
    );
    this.locators.set(
      'Regulator: NRW',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'NRW')]`)
    );
    this.locators.set(
      'Regulator: SEPA',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'SEPA')]`)
    );
    this.locators.set(
      'Regulator: DAERA',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'DAERA')]`)
    );
    this.locators.set(
      'Regulator: BEIS-OPRED',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'BEIS-OPRED')]`)
    );
    this.locators.set(
      'Installation activity type',
      new UserFriendlyLocator('xpath', `//*[@id="activityType"]`)
    );
    this.locators.set(
      'Installation activity type: Combustion of fuels',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Combustion of fuels')]`
      )
    );
    this.locators.set(
      'Installation activity type: Capture of greenhouse gases from other installations',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Capture of greenhouse gases from other installations')]`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(KnowsThePage.LOCATOR_XPATH_BACK));
    }
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
