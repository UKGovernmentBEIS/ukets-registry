import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheAccountTransferPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ACCOUNT_TRANSFER;
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
      'Submit',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SUBMIT)
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
      'Name',
      new UserFriendlyLocator('xpath', `//*[@id="details.name"]`)
    );
    this.locators.set(
      'Registration number',
      new UserFriendlyLocator('xpath', `//*[@id="regNum"]`)
    );
    this.locators.set(
      'Company registration number: I do not have a registration number',
      new UserFriendlyLocator('xpath', `//*[@id="regNumReason"]`)
    );
    this.locators.set(
      'VAT Registration number',
      new UserFriendlyLocator('xpath', `//*[@id="vatRegNum"]`)
    );
    this.locators.set(
      'VAT registration number: I do not have a registration number',
      new UserFriendlyLocator('xpath', `//*[@id="vatRegNumReason"]`)
    );
    this.locators.set(
      'Address line 1',
      new UserFriendlyLocator('xpath', `//*[@id="address.buildingAndStreet"]`)
    );
    this.locators.set(
      'Postal Code or ZIP',
      new UserFriendlyLocator('xpath', `//*[@id="address.postCode"]`)
    );
    this.locators.set(
      'Given names',
      new UserFriendlyLocator('xpath', `//*[@id="details.firstName"]`)
    );
    this.locators.set(
      'Last name',
      new UserFriendlyLocator('xpath', `//*[@id="details.lastName"]`)
    );
    this.locators.set(
      'country code GR for first phone',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),' GR (30)')])[1]`)
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
      'phone number 1',
      new UserFriendlyLocator('xpath', `//*[@id="phone1-phone-number"]`)
    );
    this.locators.set(
      'Primary contact work address is the same as the Account Holder address',
      new UserFriendlyLocator('xpath', `//*[@id="work-address-checkbox"]`)
    );
    this.locators.set(
      'What is their position in the company?',
      new UserFriendlyLocator('xpath', `//*[@id="positionInCompany"]`)
    );
    this.locators.set(
      'I confirm that the primary contact is aged 18 or over',
      new UserFriendlyLocator('xpath', `//*[@id="isOverEighteen"]`)
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
      'VAT registration number: Reason',
      new UserFriendlyLocator('xpath', `//*[@id="vat.noVatJustification"]`)
    );
    this.locators.set(
      'Company registration number: Reason',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="details.regNum.noRegistrationNumJustification"]`
      )
    );
    this.locators.set(
      'Enter account holder ID',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Enter account holder ID ']`
      )
    );
    this.locators.set(
      'Available typeahead Organisation 2',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Organisation 2')]`)
    );
    this.locators.set(
      'account holder ID',
      new UserFriendlyLocator('xpath', `//input[@placeholder='Search by ID']`)
    );
    this.locators.set(
      'Add a new organisation as account holder',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Add a new organisation as account holder ']`
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
