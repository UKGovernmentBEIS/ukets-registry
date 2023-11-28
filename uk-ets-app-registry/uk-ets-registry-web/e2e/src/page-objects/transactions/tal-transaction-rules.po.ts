import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheViewAccount50001TalTransactionRulesPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ACCOUNT_OVERVIEW_TAL_TRANSACTION_RULES;
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
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
    this.locators.set(
      'Cancel request',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Cancel request')])[2]`
      )
    );
    this.locators.set(
      'Is the approval of a second authorised question info',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Is the approval of a second authorised')]`
      )
    );
    this.locators.set(
      'Are transfers to accounts question info',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Are transfers to accounts')]`
      )
    );
    this.locators.set(
      'Submit request',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Submit request')]`)
    );
    this.locators.set(
      'Tasks',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Tasks')]`)
    );
    this.locators.set(
      'account holder info',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Account Holder')]`)
    );
    this.locators.set(
      'Account number info',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Account number:')]`
      )
    );
    this.locators.set(
      'Account name info',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Account name:')]`)
    );
    this.locators.set(
      'Go back to the account',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Go back to the account')]`
      )
    );
    // radio button previous sibling of text
    this.locators.set(
      'Yes',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Yes ']`
      )
    );
    this.locators.set(
      'No',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' No ']`
      )
    );
    this.locators.set(
      'Change second approval',
      new UserFriendlyLocator('xpath', `(//*[.=' Change ']/a)[1]`)
    );
    this.locators.set(
      'Change account not in tal',
      new UserFriendlyLocator('xpath', `(//*[.=' Change ']/a)[2]`)
    );
    this.locators.set(
      'Change surrender transaction or a return of excess allocation',
      new UserFriendlyLocator('xpath', `(//*[.=' Change ']/a)[3]`)
    );

    this.locators.set(
      'seconds approval info',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Is the approval of a second')]/..`
      )
    );
    this.locators.set(
      'transfers allowed info',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Are transfers to accounts not')]/..`
      )
    );
    this.locators.set(
      'surrender allowed info',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'surrender transaction or a return of excess allocation')]/..`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(
        by.xpath(
          `//*[contains(text(),'Request to update rules for transactions')]`
        )
      );
    }
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
