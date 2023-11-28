import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheViewAccountOverviewPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.VIEW_ACCOUNT;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'otp',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_OTP)
    );
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    this.locators.set(
      'Close account',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Close account')]`)
    );
    this.locators.set(
      'Cancel addition',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Cancel addition')]`
      )
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SUBMIT)
    );
    this.locators.set(
      'submit',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SUBMIT_2)
    );
    this.locators.set(
      'Change status',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Change status')]`)
    );
    this.locators.set(
      'Account status',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Account Holder: ')]/../../div[2]`
      )
    );
    this.locators.set(
      'Total available quantity content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Total available quantity')]/..`
      )
    );
    this.locators.set(
      'Reportable emissions',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(), ' Reportable emissions ')]/../../../tbody/tr/td[1]`
      )
    );
    this.locators.set(
      'Aviation emissions',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(), ' Aviation emissions ')]/../../../tbody/tr/td[1]`
      )
    );
    this.locators.set(
      'Emissions and Surrenders',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Emissions and Surrenders')]`
      )
    );
    this.locators.set(
      'Total reserved quantity content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Total reserved quantity')]/..`
      )
    );
    this.locators.set(
      'Change withhold status',
      new UserFriendlyLocator('xpath', `//*[.=' Change withhold status ']`)
    );
    this.locators.set(
      'Change description',
      new UserFriendlyLocator('xpath', `//*[.='Change']/a`)
    );
    this.locators.set(
      'change description area',
      new UserFriendlyLocator('xpath', `//*[@id="description"]`)
    );
    this.locators.set(
      'description',
      new UserFriendlyLocator('xpath', `//dl/div[2]/dd[1]/span`)
    );
    this.locators.set(
      'Back to the account',
      new UserFriendlyLocator('xpath', `//*[.='Back to the account']/a`)
    );
    this.locators.set(
      'Overview label',
      new UserFriendlyLocator('xpath', `//*[.='Overview']`)
    );
    this.locators.set(
      'Account holder name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="feature-header"]/app-account-header/div/div/div[2]/div/dl/div[1]/dd[1]`
      )
    );
    this.locators.set(
      'Return allowances',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Return allowances')]`
      )
    );
    this.locators.set(
      'row content of Organization details: Name',
      new UserFriendlyLocator('xpath', `(//*[.='Name'])/..`)
    );
    // removed P from proposed transaction for case sensitivity issues
    this.locators.set(
      'Propose transaction',
      new UserFriendlyLocator(
        'xpath',
        `//button[contains(text(),'ropose transaction')]`
      )
    );
    this.locators.set(
      'Holdings main content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-data-container/app-account-data/div/div/div/app-holdings/div`
      )
    );
    this.locators.set(
      'Update exclusion status',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Update exclusion status')]`
      )
    );
    this.locators.set(
      'submit title',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'have submitted')]`)
    );
    this.locators.set(
      'Request Account Transfer',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Request Account Transfer')]`
      )
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
      'Tasks',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Tasks')]`)
    );
    this.locators.set(
      'Request documents',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Request documents')]`
      )
    );
    this.locators.set(
      'first year of verified emissions',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="yearsOfVerifiedEmission.firstYear"]`
      )
    );
    this.locators.set(
      'last year of verified emissions',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="yearsOfVerifiedEmission.lastYear"]`
      )
    );
    this.locators.set(
      'Request update',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),' Request update ')]`
      )
    );
    this.locators.set(
      'Request Update',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),' Request Update ')]`
      )
    );
    this.locators.set(
      'account name text box',
      new UserFriendlyLocator('xpath', `//*[@id="name"]`)
    );
    this.locators.set(
      'Update account',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Update account')]`)
    );
    this.locators.set(
      'Account name label',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Account name: ')]/..`
      )
    );
    this.locators.set(
      'Account name',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Account name')])[2]/../dd`
      )
    );
    this.locators.set(
      'Account type',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Account type')])/../dd`
      )
    );
    this.locators.set(
      'Back to account list',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_BACK_TO_ACCOUNT_LIST
      )
    );
    this.locators.set(
      'Back to top',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Back to top')]`)
    );
    this.locators.set(
      'Allocation status for UK allowances submenu',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Allocation status for UK allowances ']`
      )
    );
    this.locators.set(
      'Overview item',
      new UserFriendlyLocator('xpath', `//*[.=' Overview ']/a`)
    );
    this.locators.set(
      'Account details item',
      new UserFriendlyLocator('xpath', `//*[.=' Account details ']/a`)
    );
    this.locators.set(
      'Account holder item',
      new UserFriendlyLocator('xpath', `//*[.=' Account Holder ']/a`)
    );
    this.locators.set(
      'Installation details item',
      new UserFriendlyLocator('xpath', `//*[.=' Installation details ']/a`)
    );
    this.locators.set(
      'Aircraft operator details item',
      new UserFriendlyLocator('xpath', `//*[.=' Aircraft operator details ']/a`)
    );
    this.locators.set(
      'Authorised representatives item',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Authorised representatives ']/a`
      )
    );
    this.locators.set(
      'Compliance item',
      new UserFriendlyLocator('xpath', `//*[.=' Compliance ']/a`)
    );
    this.locators.set(
      'History and comments item',
      new UserFriendlyLocator('xpath', `//*[.=' History and comments ']/a`)
    );
    this.locators.set(
      'Holdings item',
      new UserFriendlyLocator('xpath', `//*[.=' Holdings ']/a`)
    );
    this.locators.set(
      'Transactions item',
      new UserFriendlyLocator('xpath', `(//*[.=' Transactions ']/a)[2]`)
    );
    this.locators.set(
      'Trusted accounts item',
      new UserFriendlyLocator('xpath', `//*[.=' Trusted accounts ']/a`)
    );
    this.locators.set(
      'Transactions',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Transactions')])[1]`
      )
    );
    this.locators.set(
      'Account holder content',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Account Holder: ')])/..`
      )
    );
    this.locators.set(
      'Accounts',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_MENU_ACCOUNTS)
    );
    this.locators.set(
      'Available quantity',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Total available quantity')]/..`
      )
    );
    this.locators.set(
      'Authorised Representatives content',
      new UserFriendlyLocator('xpath', `//*[@class="govuk-table"]`)
    );
    this.locators.set(
      'Cumulative verified emissions content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Cumulative verified emissions')]/..`
      )
    );
    this.locators.set(
      'Cumulative surrenders content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Cumulative surrenders')]/..`
      )
    );
    this.locators.set(
      'First year of verified emission submission',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'First year of verified emission submission')])[2]`
      )
    );
    this.locators.set(
      'Last year of verified emission submission',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Last year of verified emission submission')])[2]`
      )
    );
    this.locators.set(
      'First year of verified emission submission text',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(text(), 'First year of verified emission submission')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Last year of verified emission submission text',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(text(), 'Last year of verified emission submission')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Year',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Year of emissions')]/following-sibling::td[2]`
      )
    );
    this.locators.set(
      'Page main content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-data-container/app-account-data/div/div/div`
      )
    );
    this.locators.set(
      'column 2 content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-data-container/app-account-data/div/div/div/app-history-and-comments-container/app-domain-events/table/tbody/tr[position() > 0]/td[2]`
      )
    );
    this.locators.set(
      'column 3 content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-data-container/app-account-data/div/div/div/app-history-and-comments-container/app-domain-events/table/tbody/tr[position() > 0]/td[3]`
      )
    );
    this.locators.set(
      'column 4 content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-data-container/app-account-data/div/div/div/app-history-and-comments-container/app-domain-events/table/tbody/tr[position() > 0]/td[4]`
      )
    );
    this.locators.set(
      'column 5 content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-data-container/app-account-data/div/div/div/app-history-and-comments-container/app-domain-events/table/tbody/tr[position() > 0]/td[5]`
      )
    );
    this.locators.set(
      'comment table headers',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(@class,'govuk-table')]/thead`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//*[.='Overview']`));
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
