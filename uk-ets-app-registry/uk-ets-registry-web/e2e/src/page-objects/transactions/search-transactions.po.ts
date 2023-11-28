import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheSearchTransactionsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.SEARCH_TRANSACTIONS;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'Reports',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Reports')]`)
    );
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
    );
    this.locators.set(
      'Generate current report',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_GENERATE_CURRENT_REPORT
      )
    );
    // labels
    this.locators.set(
      'Transaction ID label',
      new UserFriendlyLocator('xpath', `//*[.=' Transaction ID ']`)
    );
    this.locators.set(
      'Transaction Type label',
      new UserFriendlyLocator('xpath', `//*[.=' Transaction Type ']`)
    );
    this.locators.set(
      'Transaction Status label',
      new UserFriendlyLocator('xpath', `//*[.=' Transaction Status ']`)
    );
    this.locators.set(
      'Transaction last update date label',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Transaction last update date ']`
      )
    );
    this.locators.set(
      'otp',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_OTP)
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SUBMIT_2)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    this.locators.set(
      'Acquiring account type label',
      new UserFriendlyLocator('xpath', `//*[.=' Acquiring account type ']`)
    );
    this.locators.set(
      'Transferring account type label',
      new UserFriendlyLocator('xpath', `//*[.=' Transferring account type ']`)
    );
    this.locators.set(
      'Unit type label',
      new UserFriendlyLocator('xpath', `//*[.=' Unit type ']`)
    );
    this.locators.set(
      'Initiator user ID label',
      new UserFriendlyLocator('xpath', `//*[.=' Initiator user ID ']`)
    );
    this.locators.set(
      'Approver user ID label',
      new UserFriendlyLocator('xpath', `//*[.=' Approver user ID ']`)
    );
    this.locators.set(
      'Transaction proposal date label',
      new UserFriendlyLocator('xpath', `//*[.=' Transaction proposal date ']`)
    );
    // textboxes and dropdowns
    this.locators.set(
      'Acquiring account type',
      new UserFriendlyLocator('xpath', `//*[@id="acquiringAccountType"]`)
    );
    this.locators.set(
      'Transferring account type',
      new UserFriendlyLocator('xpath', `//*[@id="transferringAccountType"]`)
    );
    this.locators.set(
      'Unit type',
      new UserFriendlyLocator('xpath', `//*[@id="unitType"]`)
    );
    this.locators.set(
      'Transaction Status',
      new UserFriendlyLocator('xpath', `//*[@id="transactionStatus"]`)
    );
    this.locators.set(
      'Transaction Type',
      new UserFriendlyLocator('xpath', `//*[@id="transactionType"]`)
    );
    this.locators.set(
      'Transaction Status: All',
      new UserFriendlyLocator('xpath', `//*[@id="transactionStatus"]/option[1]`)
    );
    this.locators.set(
      'Transaction Status: Delayed',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="transactionStatus"]/option[14]`
      )
    );
    this.locators.set(
      'Transaction Status: Reversed',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="transactionStatus"]/option[13]`
      )
    );
    this.locators.set(
      'Reverse transaction',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Reverse transaction')]`
      )
    );
    this.locators.set(
      'Transaction Status: STL checked discrepancy',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="transactionStatus"]/option[12]`
      )
    );
    this.locators.set(
      'Transaction Status: STL checked no discrepancy',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="transactionStatus"]/option[11]`
      )
    );
    this.locators.set(
      'Transaction Status: Accepted',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="transactionStatus"]/option[10]`
      )
    );
    this.locators.set(
      'Transaction Status: Cancelled',
      new UserFriendlyLocator('xpath', `//*[@id="transactionStatus"]/option[9]`)
    );
    this.locators.set(
      'Transaction Status: Rejected',
      new UserFriendlyLocator('xpath', `//*[@id="transactionStatus"]/option[8]`)
    );
    this.locators.set(
      'Transaction Status: Terminated',
      new UserFriendlyLocator('xpath', `//*[@id="transactionStatus"]/option[7]`)
    );
    this.locators.set(
      'Transaction Status: Completed',
      new UserFriendlyLocator('xpath', `//*[@id="transactionStatus"]/option[6]`)
    );
    this.locators.set(
      'Transaction Status: Checked discrepancy',
      new UserFriendlyLocator('xpath', `//*[@id="transactionStatus"]/option[5]`)
    );
    this.locators.set(
      'Transaction Status: Checked no discrepancy',
      new UserFriendlyLocator('xpath', `//*[@id="transactionStatus"]/option[4]`)
    );
    this.locators.set(
      'Transaction Status: Proposed',
      new UserFriendlyLocator('xpath', `//*[@id="transactionStatus"]/option[3]`)
    );
    this.locators.set(
      'Transaction Status: Awaiting approval',
      new UserFriendlyLocator('xpath', `//*[@id="transactionStatus"]/option[2]`)
    );
    this.locators.set(
      'Transaction Type: Transfer to SOP for First External Transfer',
      new UserFriendlyLocator('xpath', `//*[@id="transactionType"]/option[3]`)
    );
    this.locators.set(
      'Transaction Type: Issuance of KP units',
      new UserFriendlyLocator('xpath', `//*[@id="transactionType"]/option[2]`)
    );
    this.locators.set(
      'Transaction ID',
      new UserFriendlyLocator('xpath', `//*[@id="transactionId"]`)
    );
    this.locators.set(
      'Transaction last update date From',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="transactionLastUpdateDateFrom"]`
      )
    );
    this.locators.set(
      'Transaction last update date To',
      new UserFriendlyLocator('xpath', `//*[@id="transactionLastUpdateDateTo"]`)
    );
    this.locators.set(
      'Transferring account number',
      new UserFriendlyLocator('xpath', `//*[@id="transferringAccountNumber"]`)
    );
    this.locators.set(
      'Acquiring account number',
      new UserFriendlyLocator('xpath', `//*[@id="acquiringAccountNumber"]`)
    );
    this.locators.set(
      'Reversed',
      new UserFriendlyLocator('xpath', `//*[@id="reversed"]`)
    );
    this.locators.set(
      'Transaction proposal date From',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="transactionalProposalDateFrom"]`
      )
    );
    this.locators.set(
      'Transaction proposal date To',
      new UserFriendlyLocator('xpath', `//*[@id="transactionalProposalDateTo"]`)
    );
    this.locators.set(
      'Initiator user ID',
      new UserFriendlyLocator('xpath', `//*[@id="initiatorUserId"]`)
    );
    this.locators.set(
      'Approver user ID',
      new UserFriendlyLocator('xpath', `//*[@id="approverUserId"]`)
    );
    this.locators.set(
      'Acquiring account type: The Operator holding account (ETS) type',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Operator holding account')])[1]`
      )
    );
    this.locators.set(
      'Acquiring account type: All KP government accounts',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'All KP government account')])[1]`
      )
    );
    this.locators.set(
      'Transferring account type: The Operator holding account (ETS) type',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Operator holding account')])[2]`
      )
    );
    this.locators.set(
      'Accounts',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Accounts')]`)
    );
    this.locators.set(
      'Tasks',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Tasks')]`)
    );
    this.locators.set(
      'Transferring account type: All KP government accounts',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'All KP government account')])[2]`
      )
    );
    this.locators.set(
      'Unit type: Assigned Amount Unit',
      new UserFriendlyLocator('xpath', `//*[@id="unitType"]/option[2]`)
    );
    this.locators.set(
      'Unit type: Removal Unit',
      new UserFriendlyLocator('xpath', `//*[@id="unitType"]/option[3]`)
    );
    // buttons and links
    this.locators.set(
      'Advanced search',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Advanced search')]`
      )
    );
    this.locators.set(
      'Clear',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Clear')]`)
    );
    this.locators.set(
      'Search',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Search')]`)
    );
    this.locators.set(
      'Show filters',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SHOW_FILTERS)
    );
    this.locators.set(
      'Hide filters',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_HIDE_FILTERS)
    );
    // results
    this.locators.set(
      'transactions table headers',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-transaction-list/app-search-transactions-results/table/thead/tr`
      )
    );
    // first row of transactions results
    this.locators.set(
      'transaction row result 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-transaction-list/app-search-transactions-results/table/tbody/tr/td[1]/a`
      )
    );
    this.locators.set(
      'transactions table results',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-transaction-list/app-search-transactions-results/table/tbody`
      )
    );
    // returned results
    this.locators.set(
      'Transactions list returned result rows',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(@class,'govuk-table__cell units-cell')]`
      )
    );
    this.locators.set(
      'transactions result row GB100000001',
      new UserFriendlyLocator('xpath', `//*[.=' GB100000001']`)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      // transactions label
      await this.awaitElement(
        by.xpath(
          `//*[@id="main-content"]/div[2]/div/app-transaction-list/div[1]/div/h1`
        )
      );
      // search button
      await this.awaitElement(by.xpath(`//*[contains(text(),'Search')]`));
    }
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
