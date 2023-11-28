import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheFillinInstallationDetailsViewPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.FILL_IN_INSTALLATION_DETAILS_VIEW;
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
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE_2)
    );
    this.locators.set(
      'Edit',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_EDIT)
    );
    this.locators.set(
      'Delete',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_DELETE)
    );
    this.locators.set(
      'Change installation details',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_CHANGE_OCCURRENCE_1
      )
    );
    this.locators.set(
      'Installation name',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Installation Name')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Installation activity type',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Installation activity type')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Permit ID',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Permit ID')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Permit entry into force',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Permit entry into force')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Regulator',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Regulator')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'First year of verified emission submission',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'First year of verified emission submission')]/following-sibling::dd[1]`
      )
    );
  }

  getData(): Map<string, Promise<string>> {
    return new Map();
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
