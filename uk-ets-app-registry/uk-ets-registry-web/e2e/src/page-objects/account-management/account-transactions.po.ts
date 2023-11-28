import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheViewAccountOvPha1001TransactPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ACCOUNT_OVERVIEW_TRANSACTIONS;
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
      'continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE_2)
    );
    this.locators.set(
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
    this.locators.set(
      'Tasks',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Tasks')]`)
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Submit')]`)
    );
    this.locators.set(
      'Overview item',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Overview')]`)
    );
    this.locators.set(
      'account user Defined Country Code',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedCountryCode"]`)
    );
    this.locators.set(
      'account user Defined Account Type',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedAccountType"]`)
    );
    this.locators.set(
      'account user Defined Account Id',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedAccountId"]`)
    );
    this.locators.set(
      'account user Defined Period',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedPeriod"]`)
    );
    this.locators.set(
      'account user Defined Check Digits',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedCheckDigits"]`)
    );
    this.locators.set(
      'request Id value',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'The request ID is')]/strong`
      )
    );
    this.locators.set(
      'transaction Id value',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'The transaction ID is')]/strong`
      )
    );
    this.locators.set(
      'Transferring account content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="transferring-account-container"]/dl`
      )
    );
    this.locators.set(
      'Transferring account details',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Account name or description')]/../..`
      )
    );
    this.locators.set(
      'Acquiring account content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="acquiring-account-container"]/dl`
      )
    );
    this.locators.set(
      'Deletion of allowances',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Deletion of allowances ']`
      )
    );
    this.locators.set(
      'Transfer KP units',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Transfer KP units ']`
      )
    );
    this.locators.set(
      'Transfer to SOP for First External Transfer of AAU',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Transfer to SOP for First External Transfer of AAU ']`
      )
    );
    this.locators.set(
      'Ambition increase cancellation of AAU',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Ambition increase cancellation of AAU ']`
      )
    );
    this.locators.set(
      'Art. 3.7ter cancellation of AAU',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Art. 3.7ter cancellation of AAU ']`
      )
    );
    this.locators.set(
      'Voluntary cancellation of KP units',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Voluntary cancellation of KP units ']`
      )
    );
    this.locators.set(
      'Surrender allowances',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Surrender allowances ']`
      )
    );
    this.locators.set(
      'AAU (Not Subject to SOP)',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="checkbox"][following-sibling::*[1]/text()=' AAU (Not Subject to SOP)']`
      )
    );
    this.locators.set(
      'AAU (Subject to SOP)',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="checkbox"][following-sibling::*[1]/text()=' AAU (Subject to SOP)']`
      )
    );
    this.locators.set(
      'choice UK Auction Account 1011',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' UK Auction Account 1011 ']`
      )
    );
    this.locators.set(
      'Central transfer',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Central transfer ']`
      )
    );
    this.locators.set(
      'Transfer of allowances to auction delivery account',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Transfer of allowances to auction delivery account ']`
      )
    );
    this.locators.set(
      'quantity',
      new UserFriendlyLocator('xpath', `//*[@id="0.selectQuantity"]`)
    );
    this.locators.set(
      'select unit types and quantity page main content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-wizards-container/app-select-unit-types-and-quantity-container/app-specify-quantity/div`
      )
    );
    this.locators.set(
      'Transfer allowances',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Transfer allowances ']`
      )
    );
    this.locators.set(
      'Carry-over AAU',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Carry-over AAU ']`
      )
    );

    this.locators.set(
      'Carry-over CER or ERU from AAU units',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Carry-over CER or ERU from AAU units ']`
      )
    );

    this.locators.set(
      'Holdings item',
      new UserFriendlyLocator('xpath', `//*[.=' Holdings ']/a`)
    );
    // checkbox previous sibling of text
    this.locators.set(
      'RMU checkbox',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="checkbox"][following-sibling::*[1]/text()=' RMU']`
      )
    );
    this.locators.set(
      'AAU checkbox',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="checkbox"][following-sibling::*[1]/text()=' AAU']`
      )
    );
    this.locators.set(
      'CER checkbox',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="checkbox"][following-sibling::*[1]/text()=' CER']`
      )
    );
    this.locators.set(
      'ERU checkbox',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="checkbox"][following-sibling::*[1]/text()=' ERU']`
      )
    );
    this.locators.set(
      'Transaction type content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-transaction-details-container/app-check-transaction-details/div[2]/div`
      )
    );
    this.locators.set(
      'Transaction type details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-wizards-container/app-check-transaction-details-container/app-check-transaction-details/div[2]/div`
      )
    );
    this.locators.set(
      'unit types and quantity to transfer details',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Quantity to transfer')]/../../..`
      )
    );
    this.locators.set(
      'acquiring account details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-wizards-container/app-check-transaction-details-container/app-check-transaction-details/app-account-summary[2]/dl`
      )
    );
    this.locators.set(
      'acquiring account content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="acquiring-account-container"]/dl`
      )
    );
    this.locators.set(
      'unit types and quantity to transfer header',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Unit types and quantity to transfer')])`
      )
    );
    this.locators.set(
      'Transaction ID value',
      new UserFriendlyLocator(
        'xpath',
        `//app-transaction-signing-details//div[2]/p`
      )
    );
    this.locators.set(
      'Holdings main content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-data-container/app-account-data/div/div/div`
      )
    );
    this.locators.set(
      'proposal approval information',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'This proposal must be')]`
      )
    );
    this.locators.set(
      'sign proposal information',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-wizards-container/app-check-transaction-details-container/app-check-transaction-details/app-sign-request-form/div/div/form/div/p`
      )
    );
    this.locators.set(
      'Back to account details',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Back to account details')]`
      )
    );
    this.locators.set(
      'overview transaction notification for approval',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-transaction-proposal-submitted-container/app-transaction-proposal-submitted/div[1]/div/div/div[2]/div`
      )
    );
    this.locators.set(
      'what happens next content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-transaction-proposal-submitted-container/app-transaction-proposal-submitted/div[2]/div`
      )
    );
    this.locators.set(
      'Retirement',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Retirement')]`)
    );
    this.locators.set(
      'AAU Not subject to SOP',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Not subject to SOP')]`
      )
    );
    this.locators.set(
      'CER Not subject to SOP',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'CER')]`)
    );
    this.locators.set(
      'ERU Not subject to SOP',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'ERU')]`)
    );
    this.locators.set(
      'Cancel proposal',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Cancel proposal')]`
      )
    );
    this.locators.set(
      'otp',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_OTP)
    );
    this.locators.set(
      'proposal comment',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_COMMENT)
    );
    this.locators.set(
      'Enter the quantity to transfer',
      new UserFriendlyLocator('xpath', `//*[@id="0.quantity"]`)
    );
    this.locators.set(
      `userDefinedCountryCode`,
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedCountryCode"]`)
    );
    this.locators.set(
      `userDefinedAccountType`,
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedAccountType"]`)
    );
    this.locators.set(
      `userDefinedAccountId`,
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedAccountId"]`)
    );
    this.locators.set(
      'Return of excess auction',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Return of excess auction ']`
      )
    );
    this.locators.set(
      `page main content`,
      new UserFriendlyLocator('xpath', `//*[@id="main-content"]/div[2]/div`)
    );
    this.locators.set(
      `userDefinedPeriod`,
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedPeriod"]`)
    );
    this.locators.set(
      `userDefinedCheckDigits`,
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedCheckDigits"]`)
    );
  }

  async waitForMe() {}

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
