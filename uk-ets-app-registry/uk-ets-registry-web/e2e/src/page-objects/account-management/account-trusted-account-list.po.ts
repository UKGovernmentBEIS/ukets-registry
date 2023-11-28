import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheAccOverviewTALPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ACCOUNT_TRUSTED_ACCOUNT_LIST;
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
      'Trusted accounts item',
      new UserFriendlyLocator('xpath', `//*[.=' Trusted accounts ']/a`)
    );
    this.locators.set(
      'Request to update the trusted account list',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Request to update the trusted account list')]`
      )
    );
    this.locators.set(
      'Check the update',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-check-update-request/div/fieldset/div[1]`
      )
    );
    this.locators.set(
      'Submit request',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Submit request')]`)
    );
    this.locators.set(
      'Request update',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Request update')]`)
    );
    this.locators.set(
      'Cancel request',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Cancel request')])[2]`
      )
    );
    this.locators.set(
      'Select type of update',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Select type of update')]`
      )
    );
    this.locators.set(
      'Account holder info',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Account holder:')]`
      )
    );
    this.locators.set(
      'Account number info',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Account number:')]`
      )
    );
    this.locators.set(
      'comment area',
      new UserFriendlyLocator('xpath', `//*[@id="accountDescription"]`)
    );
    this.locators.set(
      'Account name info',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Account name:')]`)
    );

    this.locators.set(
      'user defined country code',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedCountryCode"]`)
    );
    this.locators.set(
      'user defined account type',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedAccountType"]`)
    );
    this.locators.set(
      'user defined account id',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedAccountId"]`)
    );
    this.locators.set(
      'user defined account period',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedPeriod"]`)
    );
    this.locators.set(
      'user defined check digits period',
      new UserFriendlyLocator('xpath', `//*[@id="userDefinedCheckDigits"]`)
    );

    this.locators.set(
      'Go back to the account',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Go back to the account')]`
      )
    );
    this.locators.set(
      'Tasks',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Tasks')]`)
    );
    this.locators.set(
      'page main content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-update-request-container/app-check-update-request/div`
      )
    );
    this.locators.set(
      'page basic content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-data-container/app-account-data/div/div/div`
      )
    );
    this.locators.set(
      'type of update content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Type of update')]/../..`
      )
    );
    this.locators.set(
      'account area content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-wizards-container/app-check-update-request-container/app-check-update-request/div/fieldset/div[2]/app-trusted-account-table/table`
      )
    );
    this.locators.set(
      'tal page main content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-account-data/div/div/div`
      )
    );
    // find radio buttons using following sibling
    // (preceding simpling is considered the radio button for the corresponding text)
    this.locators.set(
      'Add account',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Add account ']`
      )
    );
    this.locators.set(
      'first account checkbox',
      new UserFriendlyLocator('xpath', `//*[@id="trusted-account-0"]`)
    );
    this.locators.set(
      'Remove account(s)',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Remove account(s) ']`
      )
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
