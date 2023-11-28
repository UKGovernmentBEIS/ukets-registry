import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheTaskListPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.TASK_LIST;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'Generate current report',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_GENERATE_CURRENT_REPORT
      )
    );
    this.locators.set(
      'Reports',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Reports')]`)
    );
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Task List',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/app-task-list/app-search-tasks-results/table`
      )
    );
    this.locators.set(
      'Request id result 1',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type='checkbox']/../../../td[2]/a`
      )
    );
    this.locators.set(
      'Request id result 2',
      new UserFriendlyLocator('xpath', `//tr[2]/td[2]/a`)
    );
    this.locators.set(
      'Checkbox result 1',
      new UserFriendlyLocator('xpath', `(//input[@type='checkbox'])[2]`)
    );
    this.locators.set(
      'Search',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SEARCH)
    );
    this.locators.set(
      'Task List table results',
      new UserFriendlyLocator(
        'xpath',
        `(//input[@type='checkbox']/../../../..)[2]`
      )
    );
    this.locators.set(
      'Task List table headers',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/app-task-search/app-search-tasks-results/table/thead`
      )
    );
    this.locators.set(
      'Request ID Header',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/app-task-search/app-search-tasks-results/table/thead/tr/th[2]/button`
      )
    );
    this.locators.set(
      'Task type Header',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/app-task-search/app-search-tasks-results/table/thead/tr/th[3]/button`
      )
    );
    this.locators.set(
      'Name of initiator Header',
      new UserFriendlyLocator('xpath', `//*[.='Name of initiator']`)
    );
    this.locators.set(
      'Name of claimant Header',
      new UserFriendlyLocator('xpath', `//*[.='Name of claimant']`)
    );
    this.locators.set(
      'Accounts',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Accounts')]`)
    );
    this.locators.set(
      'Account number Header',
      new UserFriendlyLocator('xpath', `//*[.='Account number']`)
    );
    this.locators.set(
      'Account type Header',
      new UserFriendlyLocator('xpath', `//*[.='Account type']`)
    );
    this.locators.set(
      'Account holder Header',
      new UserFriendlyLocator('xpath', `//*[.='Account holder']`)
    );
    this.locators.set(
      'Authorised representative Header',
      new UserFriendlyLocator('xpath', `//*[.='Authorised representative']`)
    );
    this.locators.set(
      'Transactions',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Transactions')]`)
    );
    this.locators.set(
      'Transaction ID Header',
      new UserFriendlyLocator('xpath', `//*[.='Transaction ID']`)
    );
    this.locators.set(
      'Created on Header',
      new UserFriendlyLocator('xpath', `//*[.='Created on']`)
    );
    this.locators.set(
      'Task status Header',
      new UserFriendlyLocator('xpath', `//*[.='Task status']`)
    );
    this.locators.set(
      'Clear',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CLEAR)
    );
    this.locators.set(
      'Advanced search',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Advanced search')]`
      )
    );
    this.locators.set(
      'Show filters',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Show filters')]`)
    );
    this.locators.set(
      'Hide filters',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Hide filters')]`)
    );
    this.locators.set(
      'Upper Assign',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Assign')])[1]`)
    );
    this.locators.set(
      'Lower Assign',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Assign')])[2]`)
    );
    this.locators.set(
      'Upper Claim',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Claim')])[1]`)
    );
    this.locators.set(
      'Lower Claim',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Claim')])[5]`)
    );
    this.locators.set(
      'Name of claimant',
      new UserFriendlyLocator('xpath', `//*[@id="claimantName"]`)
    );
    this.locators.set(
      'Task status label',
      new UserFriendlyLocator('xpath', `//*[@id="taskStatus-label"]`)
    );
    this.locators.set(
      'Name of claimant label',
      new UserFriendlyLocator('xpath', `//*[@id="claimantName-label"]`)
    );
    this.locators.set(
      'Task type label',
      new UserFriendlyLocator('xpath', `//*[@id="taskType-label"]`)
    );
    this.locators.set(
      'Request Id label',
      new UserFriendlyLocator('xpath', `//*[@id="requestId-label"]`)
    );
    this.locators.set(
      'Next',
      new UserFriendlyLocator('xpath', `//*[@id="pagination-label"]/ul/li[5]/a`)
    );
    // fields
    this.locators.set(
      'Account number',
      new UserFriendlyLocator('xpath', `//*[@id='accountNumber']`)
    );
    this.locators.set(
      'Account holder',
      new UserFriendlyLocator('xpath', `//*[@id='accountHolder']`)
    );
    this.locators.set(
      'Name of claimant',
      new UserFriendlyLocator('xpath', `//*[@id='claimantName']`)
    );
    this.locators.set(
      'Request Id',
      new UserFriendlyLocator('xpath', `//*[@id='requestId']`)
    );
    this.locators.set(
      'Task type',
      new UserFriendlyLocator('xpath', `//*[@id="taskType"]`)
    );
    this.locators.set(
      'Task status',
      new UserFriendlyLocator('xpath', `//*[@id="taskStatus"]`)
    );
    this.locators.set(
      'Task status: All',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="taskStatus"]/option[contains(text(),'')][1]`
      )
    );
    this.locators.set(
      'Task claimed by: All',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="claimedBy"]/option[contains(text(),'')][1]`
      )
    );
    this.locators.set(
      'Task status: All except Completed',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="taskStatus"]/option[contains(text(),'All except completed')]`
      )
    );
    this.locators.set(
      'Task status: Unclaimed',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="taskStatus"]/option[contains(text(),'Unclaimed')]`
      )
    );
    this.locators.set(
      'Task status: Claimed',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="taskStatus"]/option[contains(text(),'Claimed')]`
      )
    );
    this.locators.set(
      'Task status: Completed',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="taskStatus"]/option[contains(text(),'Completed')]`
      )
    );
    this.locators.set(
      'Task type: Open Account',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="taskType"]/option[contains(text(),'Open Account')][1]`
      )
    );
    this.locators.set(
      'Task type: Print letter with registry activation code',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="taskType"]/option[contains(text(),'Print letter with registry activation code')]`
      )
    );
    this.locators.set(
      'Task type: Transaction proposal',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="taskType"]/option[contains(text(),'Transaction Proposal')]`
      )
    );
    this.locators.set(
      'Task type: Allocate allowances proposal',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="taskType"]/option[contains(text(),'Allocate allowances proposal')]`
      )
    );
    this.locators.set(
      'Exclude user tasks: No',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="excludeUserTasks"]/option[contains(text(),'No')]`
      )
    );
    this.locators.set(
      'Transaction Id',
      new UserFriendlyLocator('xpath', `//*[@id="transactionId"]`)
    );
    this.locators.set(
      'Task outcome: Approved',
      new UserFriendlyLocator('xpath', `//*[@id="taskOutcome"]/option[2]`)
    );
    this.locators.set(
      'Name of initiator',
      new UserFriendlyLocator('xpath', `//*[@id="initiatorName"]`)
    );
    this.locators.set(
      'Account type: Trading account',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Trading account')]`
      )
    );
    this.locators.set(
      'Account type: Operator holding account',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Operator holding account')]`
      )
    );
    this.locators.set(
      'Initiated or claimed by role: Authorised Representative',
      new UserFriendlyLocator('xpath', `//*[@id="userRole"]/option[2]`)
    );
    this.locators.set(
      'Expand search to user tasks: Yes',
      new UserFriendlyLocator('xpath', `//*[@id="userTask"]/option[2]`)
    );
    this.locators.set(
      'Expand search to user tasks: No',
      new UserFriendlyLocator('xpath', `//*[@id="userTask"]/option[3]`)
    );
    // dates dropdown lists
    this.locators.set(
      'Claimed on From',
      new UserFriendlyLocator('xpath', `//*[@id="claimedOnFrom"]`)
    );
    this.locators.set(
      'Claimed on To',
      new UserFriendlyLocator('xpath', `//*[@id="claimedOnTo"]`)
    );
    this.locators.set(
      'Created on From',
      new UserFriendlyLocator('xpath', `//*[@id="createdOnFrom"]`)
    );
    this.locators.set(
      'Created on To',
      new UserFriendlyLocator('xpath', `//*[@id="createdOnTo"]`)
    );
    this.locators.set(
      'Completed on From',
      new UserFriendlyLocator('xpath', `//*[@id="completedOnFrom"]`)
    );
    this.locators.set(
      'Completed on To',
      new UserFriendlyLocator('xpath', `//*[@id="completedOnTo"]`)
    );
    // checkboxes
    this.locators.set(
      'Select All Result Rows',
      new UserFriendlyLocator(
        'xpath',
        `(//input[@class='govuk-checkboxes__input'])[1]`
      )
    );
    this.locators.set(
      'Select Result Row Number: 1',
      new UserFriendlyLocator(
        'xpath',
        `(//input[@class='govuk-checkboxes__input'])[2]`
      )
    );
    this.locators.set(
      'Select Result Row Number: 2',
      new UserFriendlyLocator(
        'xpath',
        `(//input[@class='govuk-checkboxes__input'])[3]`
      )
    );
    // Success message after claim
    this.locators.set(
      'Success message after claim',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'You have successfully claimed')]`
      )
    );
    this.locators.set(
      'Task list returned result rows',
      new UserFriendlyLocator('xpath', `(//*[@class="govuk-table__body"]/tr)`)
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
