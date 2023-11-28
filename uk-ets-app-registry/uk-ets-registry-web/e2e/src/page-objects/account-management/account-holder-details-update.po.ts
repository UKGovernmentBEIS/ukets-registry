import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser } from 'protractor';

export class KnowsTheAccountHolderDetailsUpdatePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ACCOUNT_HOLDER_DETAILS_UPDATE;
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
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE_2)
    );
    this.locators.set(
      'Back to account list',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_BACK_TO_ACCOUNT_LIST
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
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
    this.locators.set(
      'Address line 1',
      new UserFriendlyLocator('xpath', `//*[@id="address.buildingAndStreet"]`)
    );
    this.locators.set(
      'Address line 2 (optional)',
      new UserFriendlyLocator('xpath', `//*[@id="address.buildingAndStreet2"]`)
    );
    this.locators.set(
      'Address line 3 (optional)',
      new UserFriendlyLocator('xpath', `//*[@id="address.buildingAndStreet3"]`)
    );
    this.locators.set(
      'Town or city',
      new UserFriendlyLocator('xpath', `//*[@id="address.townOrCity"]`)
    );
    this.locators.set(
      'State or Province',
      new UserFriendlyLocator('xpath', `//*[@id="address.stateOrProvince"]`)
    );
    this.locators.set(
      'Postal Code or ZIP',
      new UserFriendlyLocator('xpath', `//*[@id="address.postCode"]`)
    );
    this.locators.set(
      'Name',
      new UserFriendlyLocator('xpath', `//*[@id="details.name"]`)
    );
    this.locators.set(
      'type of update content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Type of update')]/..`
      )
    );
    this.locators.set(
      'organisation details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Registration number')]/../../..`
      )
    );
    this.locators.set(
      'organisation address content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Postal Code or ZIP')]/../../..`
      )
    );
    this.locators.set(
      'Individual details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Country of birth')]/../../..`
      )
    );
    this.locators.set(
      'Individual contact details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Postal Code or ZIP')]/../../..`
      )
    );
    this.locators.set(
      'Full name',
      new UserFriendlyLocator('xpath', `//*[@id="details.name"]`)
    );
    this.locators.set(
      'Day of Date of birth',
      new UserFriendlyLocator('xpath', `//*[@id="permit-day"]`)
    );
    this.locators.set(
      'Month of Date of birth',
      new UserFriendlyLocator('xpath', `//*[@id="permit-month"]`)
    );
    this.locators.set(
      'Year of Date of birth',
      new UserFriendlyLocator('xpath', `//*[@id="permit-year"]`)
    );
    this.locators.set(
      'Primary contact work details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Company position')]/../../..`
      )
    );
    this.locators.set(
      'Change Primary contact details',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Primary contact details')])/../..//*[contains(text(),'Change')]`
      )
    );
    this.locators.set(
      'Change Primary contact work details',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Primary contact work details')])/../..//*[contains(text(),'Change')]`
      )
    );
    this.locators.set(
      'Change Organisation details',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Organization details')])/../..//*[contains(text(),'Change')]`
      )
    );
    this.locators.set(
      'Change Organisation address',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Organization address')])/../..//*[contains(text(),'Change')]`
      )
    );
    this.locators.set(
      'Change Individual details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-wizards-container/app-check-update-request-container/app-check-update-request/div/div/app-account-holder-summary-changes/div/div/app-summary-list[1]/dl/div/dd/a`
      )
    );
    this.locators.set(
      'Change Individual contact details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-wizards-container/app-check-update-request-container/app-check-update-request/div/div/app-account-holder-summary-changes/div/div/app-summary-list[3]/dl/div/dd/a`
      )
    );
    this.locators.set(
      'I confirm that the account holder is aged 18 or over',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="checkbox"][following-sibling::*[1]/text()=' I confirm that the account holder is aged 18 or over ']`
      )
    );
    this.locators.set(
      'Cancel request',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_CANCEL_REQUEST
      )
    );
    this.locators.set(
      'Update the account holder details',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Update the account holder details ']`
      )
    );
    this.locators.set(
      'Update the primary contact details',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Update the primary contact details ']`
      )
    );
    this.locators.set(
      'I confirm that the alternative primary contact is aged 18 or over',
      new UserFriendlyLocator('xpath', `//*[@id="isOverEighteen"]`)
    );
    this.locators.set(
      'I confirm that the account holder is aged 18 or over',
      new UserFriendlyLocator('xpath', `//*[@id="isOverEighteen"]`)
    );
    this.locators.set(
      'company position',
      new UserFriendlyLocator('xpath', `//*[@id="positionInCompany"]`)
    );
    this.locators.set(
      'Last name',
      new UserFriendlyLocator('xpath', `//*[@id="details.lastName"]`)
    );
    this.locators.set(
      'First and middle names',
      new UserFriendlyLocator('xpath', `//*[@id="details.firstName"]`)
    );
    this.locators.set(
      'Email address',
      new UserFriendlyLocator('xpath', `//*[@id="emailAddress.emailAddress"]`)
    );
    this.locators.set(
      'Confirm their email address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="emailAddress.emailAddressConfirmation"]`
      )
    );
    this.locators.set(
      'I do not have a registration number Company registration number',
      new UserFriendlyLocator(
        'xpath',
        `(//input[@type="radio"][following-sibling::*[1]/text()=' I do not have a registration number '])[1]`
      )
    );
    this.locators.set(
      'I do not have a VAT registration number',
      new UserFriendlyLocator(
        'xpath',
        `(//input[@type="radio"][following-sibling::*[1]/text()=' I do not have a registration number '])[2]`
      )
    );
    this.locators.set(
      'I do not have a Company registration number reason',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="details.regNum.noRegistrationNumJustification"]`
      )
    );
    this.locators.set(
      'I do not have a VAT registration number reason',
      new UserFriendlyLocator('xpath', `//*[@id="vat.noVatJustification"]`)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
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
