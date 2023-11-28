import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheViewAccount50001AuthRepsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ACCOUNT_AUTHORIZED_REPRESENTATIVES;
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
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    this.locators.set(
      'Tasks',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Tasks')]`)
    );
    this.locators.set(
      'Cancel request',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Cancel request')])[2]`
      )
    );
    this.locators.set(
      'Authorised Representative1',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Authorised Representative1')]`
      )
    );
    this.locators.set(
      'remove authorized representative content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="authorisedRepresentative-label"]/table/tbody/tr`
      )
    );
    this.locators.set(
      'Please select',
      new UserFriendlyLocator('xpath', `//*[@id="selection-from-dropdown"]`)
    );
    this.locators.set(
      'authorized representative user id',
      new UserFriendlyLocator('xpath', `//*[@id="selection-by-id"]`)
    );
    this.locators.set(
      'Account name label',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Account name:')]/..`
      )
    );
    this.locators.set(
      'comment area',
      new UserFriendlyLocator('xpath', `//*[@id="comment"]`)
    );
    this.locators.set(
      'Add representative',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Add representative ']`
      )
    );
    this.locators.set(
      'Remove representative',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Remove representative ']`
      )
    );
    this.locators.set(
      'Replace representative',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Replace representative ']`
      )
    );
    this.locators.set(
      'Authorised - Representative6',
      new UserFriendlyLocator('xpath', `//*[@id="selectCurrentAr"]/option[2]`)
    );
    this.locators.set(
      'Authorised Representative6',
      new UserFriendlyLocator('xpath', `(//input[@type="radio"])[1]`)
    );
    this.locators.set(
      'Change permissions',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Change permissions ']`
      )
    );
    this.locators.set(
      'Suspend representative',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Suspend representative ']`
      )
    );
    this.locators.set(
      'Restore representative',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Restore representative ']`
      )
    );
    this.locators.set(
      'select list ar',
      new UserFriendlyLocator('xpath', `//*[@id="selectCurrentAr"]`)
    );
    this.locators.set(
      'Authorised Representative1',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Authorised Representative1')]`
      )
    );
    // sibling radio button of element with specific text
    this.locators.set(
      'From the list below',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()[contains(.,'From the list')]]`
      )
    );
    this.locators.set(
      'By user ID',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' By user ID ']`
      )
    );
    this.locators.set(
      'Approve',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Approve transfers and Trusted Account List (TAL) updates ']`
      )
    );
    this.locators.set(
      'Initiate and approve',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Initiate and approve transactions and Trusted Account List (TAL) updates ']`
      )
    );
    this.locators.set(
      'Initiate',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Initiate transfers and Trusted Account List (TAL) updates ']`
      )
    );
    this.locators.set(
      'Read only',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Read only ']`
      )
    );
    this.locators.set(
      'Go back to the account',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'to the account')]`)
    );
    this.locators.set(
      'check and confirm page main content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-wizards-container/app-check-update-request-container/app-check-ar-update-request/div`
      )
    );
    this.locators.set(
      'Account status',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="feature-header"]/app-account-header/div/div/div/div/dl/div[2]/dt//app-govuk-tag/strong`
      )
    );
    this.locators.set(
      'Submit request',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Submit request')]`)
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
