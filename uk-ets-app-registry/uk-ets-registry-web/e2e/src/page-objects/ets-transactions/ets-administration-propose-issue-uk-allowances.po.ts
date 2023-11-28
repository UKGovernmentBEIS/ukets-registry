import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheEtsAdministrationProposeIssueUkAllowancesPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ETS_ADMIN_PROPOSE_ISSUE_UK_ALLOWANCES;
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
      'Propose to issue UK allowances',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Propose to issue UK allowances ']/a`
      )
    );
    this.locators.set(
      'Upload allocation table',
      new UserFriendlyLocator('xpath', `//*[.=' Upload allocation table ']/a`)
    );
    this.locators.set(
      'otp',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_OTP)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    this.locators.set(
      'continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE_2)
    );
    this.locators.set(
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'ETS Administration',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'ETS Administration')]`
      )
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Submit')]`)
    );
    this.locators.set(
      'Submit Proposal',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Submit Proposal')]`
      )
    );
    this.locators.set(
      'Change',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Change')])[1]`)
    );
    this.locators.set(
      'table headers',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-specify-allowance-quantity-container/app-specify-allowance-quantity/div/div/form/fieldset/app-allowance-transaction-quantity-table/table/thead`
      )
    );
    this.locators.set(
      'Table Headers',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-allowances-request-container/app-check-allowances-request/div/div/div[2]/div/app-allowance-transaction-quantity-table/table/thead/tr`
      )
    );
    // get parent node web element of row with specific year
    this.locators.set(
      '2030 row',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-table__cell') and contains(text(),'2030')])/..`
      )
    );
    this.locators.set(
      '2029 row',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-table__cell') and contains(text(),'2029')])/..`
      )
    );
    this.locators.set(
      '2028 row',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-table__cell') and contains(text(),'2028')])/..`
      )
    );
    this.locators.set(
      '2027 row',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-table__cell') and contains(text(),'2027')])/..`
      )
    );
    this.locators.set(
      '2026 row',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-table__cell') and contains(text(),'2026')])/..`
      )
    );
    this.locators.set(
      '2025 row',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-table__cell') and contains(text(),'2025')])/..`
      )
    );
    this.locators.set(
      '2024 row',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-table__cell') and contains(text(),'2024')])/..`
      )
    );
    this.locators.set(
      '2023 row',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-table__cell') and contains(text(),'2023')])/..`
      )
    );
    this.locators.set(
      '2022 row',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-table__cell') and contains(text(),'2022')])/..`
      )
    );
    this.locators.set(
      '2021 row',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-table__cell') and contains(text(),'2021')])/..`
      )
    );
    this.locators.set(
      '2021 Row after proposal',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-allowances-request-container/app-check-allowances-request/div/div/div[2]/div/app-allowance-transaction-quantity-table/table/tbody/tr[1]`
      )
    );
    this.locators.set(
      'quantity',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-allowances-request-container/app-check-allowances-request/div/div/div[2]/div/app-allowance-transaction-quantity-table/table/tbody/tr[1]/td[5]`
      )
    );
    this.locators.set(
      'quantity to issue',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-allowances-request-container/app-check-allowances-request/div/div/div[2]/div/app-allowance-transaction-quantity-table/table/tbody/tr[1]/td[5]`
      )
    );
    this.locators.set(
      'Total in phase row',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Total in phase')]/..`
      )
    );
    this.locators.set(
      'rows data content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-specify-allowance-quantity-container/app-specify-allowance-quantity/div/div/form/fieldset/app-allowance-transaction-quantity-table/table/tbody`
      )
    );
    this.locators.set(
      'Quantity',
      new UserFriendlyLocator('xpath', `//*[@id="quantity"]`)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      // await this.awaitElement(
      //   by.xpath(`//*[contains(text(),'Propose to issue UK allowances')]`)
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
