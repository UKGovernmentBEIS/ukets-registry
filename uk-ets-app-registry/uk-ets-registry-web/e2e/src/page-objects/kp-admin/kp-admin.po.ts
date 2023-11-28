import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheKpAdminPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.KP_ADMIN;
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
      'Specify the commitment period label',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Specify the commitment period and the acquiring account')]`
      )
    );
    this.locators.set(
      'ITL Notifications',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),' ITL Notifications ')]`
      )
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', `//*[contains(text(),' Submit ')]`)
    );
    this.locators.set(
      'ITL Messages',
      new UserFriendlyLocator('xpath', `//*[contains(text(),' ITL Messages ')]`)
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', `//*[contains(text(),' Submit ')]`)
    );
    this.locators.set(
      'Issue KP Units label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-request-and-sign-container/app-check-request-and-sign/div/div/span`
      )
    );
    this.locators.set(
      'Check and sign your proposal label',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Check and sign your proposal')]`
      )
    );
    this.locators.set(
      'Change Commitment period and acquiring account',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-request-and-submit-container/app-check-request-and-submit/div/div/dl/div[1]/dt[2]/a`
      )
    );
    this.locators.set(
      'Change Units to issue',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-request-and-submit-container/app-check-request-and-submit/div/div/dl/div[4]/dt[2]/a`
      )
    );
    this.locators.set(
      'Tasks',
      new UserFriendlyLocator('xpath', `//*[.=' Tasks ']`)
    );

    this.locators.set(
      'proposal status message',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),
      'KP units issuance proposal submitted')]`
      )
    );
    this.locators.set(
      'otp',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_OTP)
    );
    this.locators.set(
      '4 eyes principle notification content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),
      'The 4 eyes principle applies - the transaction must first be approved by another user.')]`
      )
    );
    this.locators.set(
      'Units to issue headers',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-request-and-submit-container//` +
          `app-issuance-transaction-summary-table/table/thead/tr`
      )
    );
    this.locators.set(
      'Units to issue content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-request-and-submit-container//` +
          `app-issuance-transaction-summary-table/table/tbody/tr`
      )
    );
    this.locators.set(
      'Select the unit type to specify the quantity to issue label',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Select the unit type to specify the quantity to issue')]`
      )
    );
    this.locators.set(
      'Commitment period option 1',
      new UserFriendlyLocator('xpath', `//*[@id="commitmentPeriod"]/option[2]`)
    );
    this.locators.set(
      'Commitment period option 2',
      new UserFriendlyLocator('xpath', `//*[@id="commitmentPeriod"]/option[3]`)
    );
    this.locators.set(
      'Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'GB-100-1000-1-94 - Party Holding Account 1000')]`
      )
    );
    this.locators.set(
      'Acquiring account option GB-100-1002-1-84 - Party Holding Account 1002',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'GB-100-1002-1-84 - Party Holding Account 1002')]`
      )
    );
    this.locators.set(
      'Acquiring account option GB-100-1001-1-89 - Party Holding Account 1001',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'GB-100-1001-1-89 - Party Holding Account 1001')]`
      )
    );
    this.locators.set(
      'Unit type table headers',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-select-unit-type-container-component/app-select-unit-type-component/div/div/form/fieldset/table/thead`
      )
    );
    this.locators.set(
      'Units headers',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-request-and-submit-container/app-check-request-and-submit/div/div/app-issuance-transaction-summary-table/table/thead/tr`
      )
    );
    this.locators.set(
      'Units content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-request-and-submit-container/app-check-request-and-submit/div/div/app-issuance-transaction-summary-table/table/tbody`
      )
    );
    this.locators.set(
      'Unit type table results',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-select-unit-type-container-component/app-select-unit-type-component/div/div/form/fieldset/table/tbody`
      )
    );
    this.locators.set(
      `unit type 100000001`,
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="unit-type-environmental-activity-100000001"]`
      )
    );
    this.locators.set(
      `issue quantity 100000001`,
      new UserFriendlyLocator('xpath', `//*[@id="quantity-to-issue-100000001"]`)
    );
    this.locators.set(
      `unit type 100000002`,
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="unit-type-environmental-activity-100000002"]`
      )
    );
    this.locators.set(
      `issue quantity 100000002`,
      new UserFriendlyLocator('xpath', `//*[@id="quantity-to-issue-100000002"]`)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      // await this.awaitElement(
      //   by.xpath(
      //     `//*[contains(text(),'Issue KP Units')]`
      //   )
      // );
    }
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
