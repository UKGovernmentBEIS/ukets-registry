import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheTaskDetailsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.TASK_DETAILS;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'Accounts',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_MENU_ACCOUNTS)
    );
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
    );
    this.locators.set(
      'task handled title',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(@class,'govuk-panel__title')]`
      )
    );
    this.locators.set(
      'request Id value',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class,'govuk-panel__body')]/div)[2]/strong`
      )
    );
    this.locators.set(
      'Download a template',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Download a template')]`
      )
    );
    this.locators.set(
      'Upload the requested documents label',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Upload the requested documents')]`
      )
    );
    this.locators.set(
      'Upload the requested documents content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/ng-component/app-task-details/div/div/app-requested-documents-task-details/div/div[3]/details/div`
      )
    );
    this.locators.set(
      'Task details status',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Request ID:')]/../../app-govuk-tag/strong`
      )
    );
    this.locators.set(
      'Task details type',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="feature-header"]/app-task-header/div/div/div[2]/div[1]/h3`
      )
    );
    this.locators.set(
      'Back to task list',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Back to task list')]`
      )
    );
    this.locators.set(
      'Go to task list',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Go to task list')]`
      )
    );
    this.locators.set(
      'Print letter with registry activation code label',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Print letter with registry activation code')]`
      )
    );
    this.locators.set(
      'Open Account status content',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Open Account')]`)
    );
    this.locators.set(
      'more info',
      new UserFriendlyLocator('xpath', `//*[.='more info']`)
    );
    this.locators.set(
      'otp',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_OTP_2)
    );
    this.locators.set(
      'less info',
      new UserFriendlyLocator('xpath', `//*[.='less info']`)
    );
    this.locators.set(
      'Approve',
      new UserFriendlyLocator('xpath', `//*[.=' Approve ']`)
    );
    this.locators.set(
      'Reject',
      new UserFriendlyLocator('xpath', `//*[.=' Reject ']`)
    );
    this.locators.set(
      'List of authorised representatives',
      new UserFriendlyLocator(
        'xpath',
        `//*[.='List of authorised representatives']`
      )
    );
    this.locators.set(
      'SENIOR ADMIN USER',
      new UserFriendlyLocator('xpath', `(//*[.=' SENIOR ADMIN USER '])[1]`)
    );
    this.locators.set(
      'JUNIOR ADMIN USER',
      new UserFriendlyLocator('xpath', `(//*[.=' JUNIOR ADMIN USER '])[1]`)
    );
    this.locators.set(
      'ENROLLED USER 5',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'ENROLLED USER 5')]`
      )
    );
    this.locators.set(
      'Transaction details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/ng-component/app-task-details/div/div/app-transaction-task-details/` +
          `app-issue-kp-units-task-details/div/div/dl[1]`
      )
    );
    this.locators.set(
      'transferring account content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/ng-component/app-task-details/div/div/app-transaction-task-details/` +
          `fieldset/app-generic-transaction-task-details/div/div/app-account-summary[1]/dl`
      )
    );
    this.locators.set(
      'transaction type content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/ng-component/app-task-details/div/div/app-transaction-task-details/fieldset/` +
          `app-generic-transaction-task-details/div/div/dl[1]`
      )
    );
    this.locators.set(
      'page content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-details-container/app-task-details/div/div/app-authorise-representatives-update-task-details-container/app-authorise-representatives-update-task-details/div`
      )
    );
    this.locators.set(
      'authorized representatives content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-details-container/app-task-details/div/div`
      )
    );
    this.locators.set(
      'page main content',
      new UserFriendlyLocator('xpath', `//*[@id="main-content"]`)
    );
    this.locators.set(
      'Transaction details area',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/ng-component/app-task-details/div/div/app-transaction-task-details/` +
          `fieldset/app-issue-kp-units-task-details/div/div/dl[1]`
      )
    );
    this.locators.set(
      'Quantity to issue area',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/ng-component/app-task-details/div/div/app-transaction-task-details/` +
          `fieldset/app-issue-kp-units-task-details/div/div/dl[2]/div[2]/div/app-issuance-transaction-summary-table/table`
      )
    );
    this.locators.set(
      'KP Administration',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'KP Administration')]`
      )
    );
    this.locators.set(
      'Quantity to issue content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/ng-component/app-task-details/div/div/app-transaction-task-details/` +
          `app-issue-kp-units-task-details/div/div/dl[2]`
      )
    );
    this.locators.set(
      'ENROLLED USER 4',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'ENROLLED USER 4')]`
      )
    );
    this.locators.set(
      'ENROLLED USER 3',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'ENROLLED USER 3')]`
      )
    );
    this.locators.set(
      'ENROLLED USER 2',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'ENROLLED USER 2')]`
      )
    );
    this.locators.set(
      'ENROLLED USER 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'ENROLLED USER 1')]`
      )
    );
    this.locators.set(
      'UK689820232063',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'UK689820232063')]`)
    );
    this.locators.set(
      'UK977538690871',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'UK977538690871')]`)
    );
    this.locators.set(
      'Authorised Representative1',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Authorised Representative1')]`
      )
    );
    this.locators.set(
      'ENROLLED USER 0',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'ENROLLED USER 0')]`
      )
    );
    this.locators.set(
      'SENIOR ADMIN USER content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/ng-component/app-task-details/div/div/` +
          `app-account-opening-task-details/details[5]/div/details[7]/div/dl`
      )
    );
    this.locators.set(
      'Authorised Representative6 content',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'ENROLLED USER 5')]/../..)[2]`
      )
    );
    this.locators.set(
      'Authorised Representative5 content',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'ENROLLED USER 4')]/../..)[2]`
      )
    );
    this.locators.set(
      'Authorised Representative4 content',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'ENROLLED USER 3')]/../..)[2]`
      )
    );
    this.locators.set(
      'Authorised Representative3 content',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'ENROLLED USER 2')]/../..)[2]`
      )
    );
    this.locators.set(
      'Authorised Representative2 content',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'ENROLLED USER 1')]/../..)[2]`
      )
    );
    this.locators.set(
      'Authorised Representative1 content',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'ENROLLED USER 0')]/../..)[2]`
      )
    );
    this.locators.set(
      'Initiated by content',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Initiated by')]`)
    );
    this.locators.set(
      'Claimed by content',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Claimed by')]`)
    );
    this.locators.set(
      'Download the letter and select complete label',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Download the letter and select complete')]`
      )
    );
    this.locators.set(
      'Account holder & primary contact(s)',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Account Holder and primary contact(s)')]`
      )
    );
    this.locators.set(
      'transferring account details',
      new UserFriendlyLocator(
        'xpath',
        '//*[@id="main-content"]/div[2]/div/app-task-details-container/app-task-details/div/div/app-transaction-task-details/fieldset/app-generic-transaction-task-details/div/div/app-account-summary[1]/dl'
      )
    );
    this.locators.set(
      'transaction type details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-details-container/app-task-details/div/div/app-transaction-task-details/fieldset/app-generic-transaction-task-details/div/div/dl[1]`
      )
    );
    this.locators.set(
      'unit type details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-details-container/app-task-details/div/div/app-transaction-task-details/fieldset/app-generic-transaction-task-details/div/div/dl[2]/div[2]/div/app-transaction-quantity-table/table`
      )
    );
    this.locators.set(
      'acquiring account details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-details-container/app-task-details/div/div/app-transaction-task-details/fieldset/app-generic-transaction-task-details/div/div/app-account-summary[2]/dl`
      )
    );
    this.locators.set(
      'Change account holder',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Change account holder')]`
      )
    );
    this.locators.set(
      'Aircraft operator',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Aircraft Operator')])[3]`
      )
    );
    this.locators.set(
      'Change regulator',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Change regulator')]`
      )
    );
    this.locators.set(
      'Regulator: DAERA',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'DAERA')]`)
    );
    this.locators.set(
      'account holder ID change Apply',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Apply')])[1]`)
    );
    this.locators.set(
      'regulator change Apply',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Apply')])[2]`)
    );
    this.locators.set(
      'Enter account holder ID',
      new UserFriendlyLocator('xpath', `//*[@id="changed_account_holder"]`)
    );
    this.locators.set(
      'Account details',
      new UserFriendlyLocator('xpath', `//*[.=' Account details ']`)
    );
    this.locators.set(
      'History & comments',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'History and comments')]`
      )
    );
    this.locators.set(
      'History & comments label',
      new UserFriendlyLocator('xpath', `//*[.='History & comments']`)
    );
    // button
    this.locators.set(
      'Clear Comment',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/app-history-comments/form/details/div/fieldset/div/div/div/button[2]`
      )
    );
    this.locators.set(
      'comment table headers',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(@class,'govuk-table__head')]`
      )
    );
    this.locators.set(
      'comment table results',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/app-history-comments/app-task-history-container/app-task-history/table/tbody`
      )
    );
    this.locators.set(
      'comment',
      new UserFriendlyLocator(
        'xpath',
        `//app-task-history/table/tbody/tr/td[5]`
      )
    );
    this.locators.set(
      'comment text result 1',
      new UserFriendlyLocator(
        'xpath',
        `//table[@aria-describedby="Domain events table"]//tbody//tr[1]/td[5]`
      )
    );
    // link
    this.locators.set(
      'Add comment',
      new UserFriendlyLocator('xpath', `//*[.='Add comment']`)
    );
    // button
    this.locators.set(
      'Add Comment',
      new UserFriendlyLocator('xpath', `//*[.=' Add comment ']`)
    );
    this.locators.set(
      'comment area',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_COMMENT)
    );
    this.locators.set(
      'User first name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/ng-component/app-task-details/div/div/app-enrolment-letter-task-details/dl[1]/div[2]/dd`
      )
    );
    this.locators.set(
      'User last name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/ng-component/app-task-details/div/div/app-enrolment-letter-task-details/dl[1]/div[3]/dd`
      )
    );
    this.locators.set(
      'User ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/ng-component/app-task-details/div/div/app-enrolment-letter-task-details/dl[1]/div[4]/dd`
      )
    );
    this.locators.set(
      'Back to top',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Back to top')]`)
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Download letter',
      new UserFriendlyLocator('xpath', `//*[.=' () ']/a`)
    );
    this.locators.set(
      'Complete request',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Complete request')]`
      )
    );
    this.locators.set(
      'Complete task',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Complete task')]`)
    );
    this.locators.set(
      'Unit types and quantity to transfer information',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-details-container/app-task-details/div/div/app-transaction-task-details/fieldset/app-generic-transaction-task-details/div/div/dl[2]/div[2]/div/app-transaction-quantity-table/table`
      )
    );
    this.locators.set(
      'transaction type information',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-details-container/app-task-details/div/div/app-transaction-task-details/fieldset/app-generic-transaction-task-details/div/div/dl[1]/div[2]/dd`
      )
    );
    this.locators.set(
      'transferring account information',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-task-details-container/app-task-details/div/div/app-transaction-task-details/fieldset/app-generic-transaction-task-details/div/div/app-account-summary[1]/dl`
      )
    );
    this.locators.set(
      'update details info',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]//app-trusted-account-request-task-details/div/div/dl`
      )
    );
    this.locators.set(
      'account info',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]//app-trusted-account-request-task-details/div//dl`
      )
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
