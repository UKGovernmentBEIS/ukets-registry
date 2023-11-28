import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheAccOpenReqRegAccount extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.ACCOUNT_OPENING_REQUEST_NEW_REGISTRY_ACCOUNT;
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
      'Start now',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Start now')]`)
    );
    this.locators.set(
      'Request to open a registry header title',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div/div/legend/h1`
      )
    );
    // -------------------------------------------------------------------------------
    // section labels
    this.locators.set(
      'Fill in the account details label name',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Fill in the account details ']/a`
      )
    );
    this.locators.set(
      'Configure the transaction rules label name',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Configure the transaction rules ']/a`
      )
    );
    this.locators.set(
      'Fill in the authorised representatives label name',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Fill in the authorised representatives ']/a`
      )
    );
    this.locators.set(
      'Fill in the aircraft operator details label name',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Fill in the aircraft operator details ']/a`
      )
    );
    this.locators.set(
      'Fill in the installation details label name',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Fill in the installation details ']/a`
      )
    );
    this.locators.set(
      'Approval of second authorised representative label',
      new UserFriendlyLocator('css', `app-transaction-rules-link a`)
    );
    // -------------------------------------------------------------------------------
    // section rows:
    this.locators.set(
      'Section 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' 1. Add the account holder ']/ancestor::div[1]`
      )
    );
    this.locators.set(
      'Section 2 trading account',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' 2. Add the ets - trading account details ']/ancestor::div[1]`
      )
    );
    this.locators.set(
      'Section 2 person holding account',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' 2. Add the kp - person holding account details ']/ancestor::div[1]`
      )
    );
    this.locators.set(
      'Section 2 operator holding account',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' 2. Add the ets - operator holding account details ']/ancestor::div[1]`
      )
    );
    this.locators.set(
      'Section 2 aircraft operator holding account',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' 2. Add the ets - aircraft operator holding account details ']/ancestor::div[1]`
      )
    );
    this.locators.set(
      'Section 3',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' 3. Set up rules for transactions ']/ancestor::div[1]`
      )
    );
    this.locators.set(
      'Section 4',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' 4. Add authorised representatives ']/ancestor::div[1]`
      )
    );
    this.locators.set(
      'Section 4 installation information',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' 4. Add the installation information ']/ancestor::div[1]`
      )
    );
    this.locators.set(
      'Section 4 aircraft operator',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' 4. Add the aircraft operator ']/ancestor::div[1]`
      )
    );
    this.locators.set(
      'Section 5',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' 5. Check and submit your request ']`
      )
    );
    this.locators.set(
      'Section 5 authorised representatives',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' 5. Add authorised representatives ']/ancestor::div[1]`
      )
    );
    this.locators.set(
      'Section 6',
      new UserFriendlyLocator('xpath', `//*[.=' 1. Add the account holder ']`)
    );
    this.locators.set(
      'Section 6 Check and submit',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' 6. Check and submit your request ']`
      )
    );

    this.locators.set(
      'Open all',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Open all')]`)
    );

    this.locators.set(
      'check your answers before submit content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]`
      )
    );

    // -------------------------------------------------------------------------------
    this.locators.set(
      'Account holder',
      new UserFriendlyLocator('xpath', `//*[.=' Account Holder ']`)
    );
    this.locators.set(
      'Account holder details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div[2]/div/form/div/div/dl[1]/div[2]/dd/a`
      )
    );
    this.locators.set(
      'Account details',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Account details')]`
      )
    );

    this.locators.set(
      'Account details label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div/div/form/div/div/dl[2]/div[2]/dd/a`
      )
    );

    this.locators.set(
      'Add the primary contact',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div/div/form/div/div/dl[1]/div[3]/dd/a`
      )
    );

    this.locators.set(
      'primary contact text',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div[2]/div/form/div/div/dl[1]/div[3]/dd/a`
      )
    );

    this.locators.set(
      '(alternative) primary contact',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., '(alternative primary contact)')]/app-account-holder-contacts/div/a[2]`
      )
    );

    this.locators.set(
      'Specify the authorised representatives',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div/div/form/dl[8]/div/dt/span`
      )
    );

    this.locators.set(
      'Installation information',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div/div/form/div/div/dl[4]/div[2]/dd/a`
      )
    );

    this.locators.set(
      'Installation information label',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'4. Add the installation information')]/../../div[2]/dd/a`
      )
    );

    this.locators.set(
      'Cancel',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div/div/a`
      )
    );

    this.locators.set(
      'Add the (alternative) primary contact',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Add the (alternative) primary contact')]/app-account-holder-contacts/div/div/button`
      )
    );

    this.locators.set(
      'Specified legal representative 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div/div/form/dl[2]/div[2]/dt[1]/a[1]`
      )
    );

    this.locators.set(
      'Specified legal representative 2',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div/div/form/dl[2]/div[2]/dt[1]/a[2]`
      )
    );

    this.locators.set(
      'Choose the transaction rules',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Choose the transaction rules ']/a`
      )
    );

    this.locators.set(
      'Configure the transaction rules label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div/div/form/dl[5]/div/dt/a`
      )
    );

    this.locators.set(
      'About the trusted account list',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' About the trusted account list ']/span`
      )
    );

    this.locators.set(
      'Authorised Representative details',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Authorised Representative details')]`
      )
    );

    this.locators.set(
      'Add an Authorised Representative',
      new UserFriendlyLocator(
        'xpath',
        `//button[text()=' Add an Authorised Representative ']`
      )
    );

    this.locators.set(
      'Aircraft Operator details label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div/div/form/div/div/dl[4]/div[2]/dd/a`
      )
    );

    this.locators.set(
      'Aircraft Operator details',
      new UserFriendlyLocator('xpath', `//*[.=' Aircraft Operator details ']/a`)
    );

    this.locators.set(
      'Authorised representatives label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div[2]/div/form/div/div/dl[4]/div[3]/dd`
      )
    );

    this.locators.set(
      'Authorised representatives label 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div/div/form/div/div/dl[4]/div[3]/dd/a`
      )
    );

    this.locators.set(
      'Authorised representatives label 2',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div/div/form/div/div/dl[4]/div[4]/dd/a`
      )
    );

    this.locators.set(
      'Authorised representatives label 3',
      new UserFriendlyLocator(
        'xpath',
        `//span[contains(., " 4. Add Authorised Representatives ")]/ancestor::dl/following-sibling::dl/div/dt/p[3]/a`
      )
    );

    this.locators.set(
      'Authorised representatives label 4',
      new UserFriendlyLocator(
        'xpath',
        `//span[contains(., " 4. Add Authorised Representatives ")]/ancestor::dl/following-sibling::dl/div/dt/p[4]/a`
      )
    );

    this.locators.set(
      'Authorised representatives label 5',
      new UserFriendlyLocator(
        'xpath',
        `//span[contains(., " 4. Add Authorised Representatives ")]/ancestor::dl/following-sibling::dl/div/dt/p[5]/a`
      )
    );

    this.locators.set(
      'Authorised representatives label 6',
      new UserFriendlyLocator(
        'xpath',
        `//span[contains(., " 4. Add Authorised Representatives ")]/ancestor::dl/following-sibling::dl/div/dt/p[6]/a`
      )
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Organisation',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Organisation ']`
      )
    );
    this.locators.set(
      'Individual',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Individual ']`
      )
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE_2)
    );
    this.locators.set(
      'Add a new individual',
      new UserFriendlyLocator('xpath', `//*[@id='new-account-holder']`)
    );
    this.locators.set(
      'Add a new organisation',
      new UserFriendlyLocator('xpath', `//*[@id='new-account-holder']`)
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'New individual label',
      new UserFriendlyLocator('xpath', `//*[.=' New individual ']`)
    );
    this.locators.set(
      'Add a new organisation label',
      new UserFriendlyLocator('xpath', `//*[.=' Add a new organisation ']`)
    );
    this.locators.set(
      'From the list below',
      new UserFriendlyLocator('xpath', `//*[@id='existing-dropdown']`)
    );
    this.locators.set(
      'Change Personal Details',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_CHANGE_OCCURRENCE_1
      )
    );
    this.locators.set(
      'Change Address and contact details',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_CHANGE_OCCURRENCE_2
      )
    );
    this.locators.set(
      'Change address and contact details legal representative',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-overview/div/div/dl[3]/div/dd/a`
      )
    );
    this.locators.set(
      'Full name',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Full name')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Fill Full name',
      new UserFriendlyLocator('xpath', `//*[@id="details.firstName"]`)
    );
    this.locators.set(
      'Fill Last name',
      new UserFriendlyLocator('xpath', `//*[@id="details.lastName"]`)
    );

    this.locators.set(
      'Fill Year of birth',
      new UserFriendlyLocator('xpath', `//*[@id="permit-year"]`)
    );

    this.locators.set(
      'Fill Month of birth',
      new UserFriendlyLocator('xpath', `//*[@id="permit-month"]`)
    );

    this.locators.set(
      'Fill Day of birth',
      new UserFriendlyLocator('xpath', `//*[@id="permit-day"]`)
    );
    this.locators.set(
      'Year of birth',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Year of birth')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Country of birth',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Country of birth')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Home address',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Home address')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'First and middle names',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'First and middle names')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Date of birth',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Date of birth')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Postal Code or ZIP',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Postal Code or ZIP')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Phone number 1',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Phone number 1')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Email address',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Email address')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Please select',
      new UserFriendlyLocator('xpath', `//*[@id="selection-from-dropdown"]`)
    );
    this.locators.set(
      'From the list below label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-account-holder-selection-container/app-account-holder-selection/form/div/div/div/fieldset/div[1]/label`
      )
    );
    this.locators.set(
      'Search by name textbox',
      new UserFriendlyLocator('xpath', `//*[@id="typeahead-basic"]`)
    );
    this.locators.set(
      'Search by name',
      new UserFriendlyLocator('xpath', `//*[@id="existing-search"]`)
    );
    this.locators.set(
      'Search by name label',
      new UserFriendlyLocator('xpath', `//*[.=' Search by name ']`)
    );
    this.locators.set(
      'List options',
      new UserFriendlyLocator('xpath', `//*[@id="selection-from-dropdown"]`)
    );
    this.locators.set(
      'Individual 1',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Individual 1')]`)
    );
    this.locators.set(
      'Individual 2',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Individual 2')]`)
    );
    this.locators.set(
      'Available typeahead Individual 2',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Individual 2')])[2]`
      )
    );
    this.locators.set(
      'Available typeahead Organisation 1',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Organisation 1')])[2]`
      )
    );
    this.locators.set(
      'Organisation 1',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Organisation 1')]`)
    );
    // fill account holder organisation details
    this.locators.set(
      'Organisation name',
      new UserFriendlyLocator('xpath', `//*[@id="details.name"]`)
    );
    this.locators.set(
      'Fill Registration number',
      new UserFriendlyLocator('xpath', `//*[@id="regNum"]`)
    );
    this.locators.set(
      'Fill VAT Registration number',
      new UserFriendlyLocator('xpath', `//*[@id="vatRegNum"]`)
    );
    this.locators.set(
      'Organisation registration number',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="details.regNum.registrationNumber"]`
      )
    );
    this.locators.set(
      'Organisation Reason for not providing a registration number',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="details.noRegistrationNumJustification"]`
      )
    );
    this.locators.set(
      'VAT registration number with country code',
      new UserFriendlyLocator('xpath', `//*[@id="vat.vatRegistrationNumber"]`)
    );
    this.locators.set(
      'Reason for not providing a VAT registration number',
      new UserFriendlyLocator('xpath', `//*[@id="vat.noVatJustification"]`)
    );
    this.locators.set(
      'Line 1',
      new UserFriendlyLocator('xpath', `//*[@id="address.buildingAndStreet"]`)
    );
    this.locators.set(
      'Line 2',
      new UserFriendlyLocator('xpath', `//*[@id="address.buildingAndStreet2"]`)
    );
    this.locators.set(
      'Line 3',
      new UserFriendlyLocator('xpath', `//*[@id="address.buildingAndStreet3"]`)
    );
    this.locators.set(
      'Fill Town or city',
      new UserFriendlyLocator('xpath', `//*[@id="address.townOrCity"]`)
    );
    this.locators.set(
      'Country United Kingdom',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'United Kingdom')]`)
    );
    this.locators.set(
      'Postal Code or ZIP',
      new UserFriendlyLocator('xpath', `//*[@id="address.postCode"]`)
    );
    this.locators.set(
      'Change Organisation details',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_CHANGE_OCCURRENCE_1
      )
    );
    this.locators.set(
      'Name',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Name')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Billing address is the same as account holder address',
      new UserFriendlyLocator('xpath', `//*[@id="address-checkbox"]`)
    );
    this.locators.set(
      'Registration number',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Registration number')]/following-sibling::dd[1]`
      )
    );

    this.locators.set(
      'VAT registration number',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'VAT registration number')]/following-sibling::dd[1]`
      )
    );

    this.locators.set(
      'Reason for not providing a VAT registration number',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Reason for not providing a VAT registration number')]/following-sibling::dd[1]`
      )
    );

    this.locators.set(
      'Organisation address',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Organisation address')]/following-sibling::dd[1]`
      )
    );

    this.locators.set(
      'Town or city',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Town or city')]/following-sibling::dd[1]`
      )
    );

    this.locators.set(
      'Country',
      new UserFriendlyLocator('xpath', `//dt[text()='Country']/../dd`)
    );

    this.locators.set(
      'Postal Code or ZIP',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Postal Code or ZIP')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Operator Holding Account',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Operator Holding Account ']`
      )
    );
    this.locators.set(
      'Aircraft Operator Holding Account',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Aircraft Operator Holding Account ']`
      )
    );
    this.locators.set(
      'Trading Account',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Trading Account ']`
      )
    );
    this.locators.set(
      'Person Holding Account',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Person Holding Account ']`
      )
    );
    this.locators.set(
      'First name',
      new UserFriendlyLocator('xpath', `//*[@id="details.firstName"]`)
    );
    this.locators.set(
      'Last name',
      new UserFriendlyLocator('xpath', `//*[@id="details.lastName"]`)
    );
    this.locators.set(
      'I confirm that the primary contact is aged 18 or over',
      new UserFriendlyLocator('xpath', `//*[@id="isOverEighteen"]`)
    );
    this.locators.set(
      'I confirm that the account holder is aged 18 or over',
      new UserFriendlyLocator('xpath', `//*[@id="isOverEighteen"]`)
    );
    this.locators.set(
      'Day of birth',
      new UserFriendlyLocator('xpath', `//*[@id="permit-day"]`)
    );
    this.locators.set(
      'Month of birth',
      new UserFriendlyLocator('xpath', `//*[@id="permit-month"]`)
    );
    this.locators.set(
      'Year of birth',
      new UserFriendlyLocator('xpath', `//*[@id="permit-year"]`)
    );
    // Account holder contact address
    this.locators.set(
      'Position',
      new UserFriendlyLocator('xpath', `//*[@id="positionInCompany"]`)
    );
    this.locators.set(
      'Country United Kingdom',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'United Kingdom')]`)
    );
    this.locators.set(
      'Fill Postal Code or ZIP',
      new UserFriendlyLocator('xpath', `//*[@id="address.postCode"]`)
    );
    this.locators.set(
      'Country code 1: GR (30)',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'GR (30)')])[1]`)
    );
    this.locators.set(
      'Country code 1: UK (44)',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'UK (44)')])[1]`)
    );
    this.locators.set(
      'Country code 2: GR (30)',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'GR (30)')])[2]`)
    );
    this.locators.set(
      'Country code 2: UK (44)',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'UK (44)')])[2]`)
    );
    this.locators.set(
      'Fill Phone number 1',
      new UserFriendlyLocator('xpath', `//*[@id="phone1-phone-number"]`)
    );
    this.locators.set(
      'Fill Phone number 2',
      new UserFriendlyLocator('xpath', `//*[@id="phone2-phone-number"]`)
    );
    this.locators.set(
      'Fill Email address',
      new UserFriendlyLocator('xpath', `//*[@id="emailAddress.emailAddress"]`)
    );
    this.locators.set(
      'Fill Re-type email address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="emailAddress.emailAddressConfirmation"]`
      )
    );
    this.locators.set(
      'Work address',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Address')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Edit',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_EDIT)
    );
    this.locators.set(
      'Delete',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_DELETE)
    );
    this.locators.set(
      'Work phone number 1',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Phone number 1')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Account name',
      new UserFriendlyLocator('xpath', `//*[@id="name"]`)
    );
    this.locators.set(
      'Change',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Change')]`)
    );
    this.locators.set(
      'Address',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Address')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Account type',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Account type')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Account name text',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Account name')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Yes',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_YES)
    );
    this.locators.set(
      'No',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_NO)
    );
    this.locators.set(
      'Do you want a second authorised representative to approve transfers of units to a trusted account change',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_CHANGE_OCCURRENCE_1
      )
    );
    this.locators.set(
      'Do you want to allow transfers of units to accounts that are not on the trusted list change',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_CHANGE_OCCURRENCE_2
      )
    );

    this.locators.set(
      'Do you want a second authorised representative to approve transfers of units to a trusted account',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),' No ') or contains(text(),' Yes')])[1]`
      )
    );
    this.locators.set(
      'Do you want to allow transfers of units to accounts that are not on the trusted list',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),' No ') or contains(text(),' Yes')])[2]`
      )
    );
    this.locators.set(
      'Is this an installation transfer: Yes',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Yes ']`
      )
    );
    this.locators.set(
      'Is this an installation transfer: No',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' No ']`
      )
    );
    this.locators.set(
      'Installation name',
      new UserFriendlyLocator('xpath', `//*[@id="name"]`)
    );
    this.locators.set(
      'Installation Id',
      new UserFriendlyLocator('xpath', `//*[@id="identifier"]`)
    );
    this.locators.set(
      'Permit ID',
      new UserFriendlyLocator('xpath', `//*[@id="permit.id"]`)
    );
    this.locators.set(
      'Permit entry into force: Day',
      new UserFriendlyLocator('xpath', `//*[@id="permit-day"]`)
    );
    this.locators.set(
      'Permit entry into force: Month',
      new UserFriendlyLocator('xpath', `//*[@id="permit-month"]`)
    );
    this.locators.set(
      'Permit entry into force: Year',
      new UserFriendlyLocator('xpath', `//*[@id="permit-year"]`)
    );
    this.locators.set(
      'First year of verified emission submission',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="yearsOfVerifiedEmission.firstYear"]`
      )
    );
    this.locators.set(
      'Regulator',
      new UserFriendlyLocator('xpath', `//*[@id="regulator"]`)
    );
    this.locators.set(
      'Regulator: EA',
      new UserFriendlyLocator('xpath', `//*[.=' EA ']`)
    );
    this.locators.set(
      'Regulator: NRW',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'NRW')]`)
    );
    this.locators.set(
      'Regulator: SEPA',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'SEPA')]`)
    );
    this.locators.set(
      'Regulator: DAERA',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'DAERA')]`)
    );
    this.locators.set(
      'Regulator: BEIS-OPRED',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'BEIS-OPRED')]`)
    );
    this.locators.set(
      'Installation activity type',
      new UserFriendlyLocator('xpath', `//*[@id="activityType"]`)
    );
    this.locators.set(
      'Installation activity type: Combustion of fuels',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Combustion of fuels')]`
      )
    );
    this.locators.set(
      'Installation activity type: Capture of greenhouse gases from other installations',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Capture of greenhouse gases from other installations')]`
      )
    );
    this.locators.set(
      'Change aircraft operator details',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Change')]`)
    );
    this.locators.set(
      'Fill Monitoring plan ID',
      new UserFriendlyLocator('xpath', `//*[@id="monitoringPlan.id"]`)
    );
    this.locators.set(
      'Monitoring plan ID',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Monitoring Plan ID')]/following-sibling::dd[1]`
      )
    );
    this.locators.set(
      'Change installation details',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_CHANGE_OCCURRENCE_1
      )
    );
    this.locators.set(
      'Permit entry into force',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-operator-overview-container/app-operator-overview/div[2]/div/app-overview-installation/app-summary-list/dl/div[4]/dd`
      )
    );
    this.locators.set(
      'Installation name text',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(), 'Installation name')]/../../dd`
      )
    );
    this.locators.set(
      'Permit ID text',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(), 'Permit ID')]/../../dd`
      )
    );
    this.locators.set(
      'Permit entry into force text',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(), 'Permit entry into force')]/../../dd`
      )
    );
    this.locators.set(
      'Regulator text',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(), 'Regulator')]/../../dd`
      )
    );
    this.locators.set(
      'First year of verified emission submission text',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(), 'First year of verified emission submission')]/../../dd`
      )
    );

    this.locators.set(
      'Country code GR (30)',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'GR (30)')])[1]`)
    );
    this.locators.set(
      'Country 2 code GR (30)',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'GR (30)')])[2]`)
    );
    this.locators.set(
      'Country code UK (44)',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'UK (44)')])[1]`)
    );
    this.locators.set(
      'Country 2 code UK (44)',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'UK (44)')])[2]`)
    );

    this.locators.set(
      'Select from the list',
      new UserFriendlyLocator('xpath', `//*[@id="selectCurrentAr"]`)
    );
    this.locators.set(
      'By User ID',
      new UserFriendlyLocator('xpath', `//*[@id="existing-by-id"]`)
    );
    this.locators.set(
      'Authorised Representative1',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Authorised Representative1')]`
      )
    );
    this.locators.set(
      'Authorised Representative2',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Authorised Representative2')]`
      )
    );
    this.locators.set(
      'Authorised Representative3',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Authorised Representative3')]`
      )
    );
    this.locators.set(
      'Authorised Representative4',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Authorised Representative4')]`
      )
    );
    this.locators.set(
      'Authorised Representative5',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Authorised Representative5')]`
      )
    );
    this.locators.set(
      'Authorised Representative6',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Authorised Representative6')]`
      )
    );
    this.locators.set(
      'User registered',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'USER REGISTERED')]`
      )
    );
    this.locators.set(
      'User ID',
      new UserFriendlyLocator('xpath', `//*[@id="selection-by-id"]`)
    );

    this.locators.set(
      'User',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'(User ID:')]`)
    );
    this.locators.set(
      'Rules for authorised representatives',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Rules for authorised representatives ']`
      )
    );
    this.locators.set(
      'Initiate and approve transactions',
      new UserFriendlyLocator('xpath', `//*[@id="access-rights-0"]`)
    );
    this.locators.set(
      'Approve transfers',
      new UserFriendlyLocator('xpath', `//*[@id="access-rights-1"]`)
    );
    this.locators.set(
      'Initiate transfers',
      new UserFriendlyLocator('xpath', `//*[@id="access-rights-2"]`)
    );
    this.locators.set(
      'Read only',
      new UserFriendlyLocator('xpath', `//*[@id="access-rights-3"]`)
    );
    this.locators.set(
      'Rule 1 description',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-access-rights-container/app-access-rights/div/div/form/details/div/ul/li[1]`
      )
    );
    this.locators.set(
      'Rule 2 description',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-access-rights-container/app-access-rights/div/div/form/details/div/ul/li[2]`
      )
    );
    this.locators.set(
      'Rule 3 description',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-access-rights-container/app-access-rights/div/div/form/details/div/ul/li[3]`
      )
    );
    this.locators.set(
      'Rule 4 description',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-access-rights-container/app-access-rights/div/div/form/details/div/ul/li[4]`
      )
    );
    this.locators.set(
      'Permissions available options',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="arAccessRight-label"]/fieldset/div`
      )
    );

    this.locators.set(
      'Afghanistan',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Afghanistan')]`)
    );

    this.locators.set(
      'Zimbabwe',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Zimbabwe')]`)
    );

    this.locators.set(
      'Country Greece',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Greece')]`)
    );

    this.locators.set(
      'Change Permissions',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Change')]`)
    );

    this.locators.set(
      'Last name text',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Last name')]/following-sibling::dd[1]`
      )
    );

    this.locators.set(
      'User ID text',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'User ID')]/following-sibling::dd[1]`
      )
    );

    this.locators.set(
      'Permissions description',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., 'Permissions')]/following-sibling::dd[1]`
      )
    );

    this.locators.set(
      'Organisation details: Name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="accordion-with-summary-sections-account-holder"]/dl[1]/div[1]/dd`
      )
    );

    this.locators.set(
      'Organisation details: Registration number',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="accordion-with-summary-sections-account-holder"]/dl[1]/div[2]/dd`
      )
    );

    this.locators.set(
      'Organisation details: VAT registration number',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="accordion-with-summary-sections-account-holder"]/dl[1]/div[4]/dd`
      )
    );

    this.locators.set(
      'Organisation details: VAT registration number with country code',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[2]/div[4]/dd`
      )
    );

    this.locators.set(
      'Organisation Address and contact details: Organisation address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="accordion-with-summary-sections-account-holder"]/dl[2]/div/dd/text()[2]`
      )
    );

    this.locators.set(
      'Organisation Address and contact details: Town or city',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[2]/div[2]/dd`
      )
    );

    this.locators.set(
      'Organisation Address and contact details: Country',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="accordion-with-summary-sections-account-holder"]/dl[2]/div/dd/text()[3]`
      )
    );

    this.locators.set(
      'Account details: Account type',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/details[2]/div/dl[1]/div[1]/dd[1]`
      )
    );
    this.locators.set(
      'Account details: Account name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/details[2]/div/dl/div[2]/dd`
      )
    );
    this.locators.set(
      'Account details: Address label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/details[2]/div/div/div/div/h2`
      )
    );
    this.locators.set(
      'Account details: Billing Address: Address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/details[2]/div/dl[2]/div[1]/dd`
      )
    );
    this.locators.set(
      'Account details: Billing Address: Town or city',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/details[2]/div/dl[2]/div[2]/dd`
      )
    );
    this.locators.set(
      'Account details: Billing Address: Country',
      new UserFriendlyLocator(
        'xpath',
        `(//dt[contains(., "Country")])[3]/../dd`
      )
    );
    this.locators.set(
      'Account details: Billing Address: Postal Code or ZIP',
      new UserFriendlyLocator(
        'xpath',
        `(//dt[contains(., "Postal Code or ZIP")])[3]/../dd`
      )
    );
    this.locators.set(
      'Individual details: Name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[2]/div[1]/dd`
      )
    );
    this.locators.set(
      'Individual details: Year of birth',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[2]/div[2]/dd`
      )
    );
    this.locators.set(
      'Individual details: Country of birth',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[2]/div[3]/dd`
      )
    );
    this.locators.set(
      'Individual Address and contact details: Residential address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[4]/div[1]/dd`
      )
    );
    this.locators.set(
      'Individual Address and contact details: Town or city',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[4]/div[2]/dd`
      )
    );
    this.locators.set(
      'Individual Address and contact details: Country',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[4]/div[3]/dd`
      )
    );
    this.locators.set(
      'Individual Address and contact details: Postal Code or ZIP',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[4]/div[4]/dd`
      )
    );
    this.locators.set(
      'Individual Address and contact details: Phone number 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[4]/div[5]/dd`
      )
    );
    this.locators.set(
      'Individual Address and contact details: Email address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[4]/div[6]/dd`
      )
    );
    this.locators.set(
      'Installation',
      new UserFriendlyLocator('xpath', `//*[.='Installation']`)
    );
    this.locators.set(
      'Installation details: Installation name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[1]/dd`
      )
    );
    this.locators.set(
      'Installation details: Installation activity type',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[2]/dd`
      )
    );
    this.locators.set(
      'Installation details: Permit ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[3]/dd`
      )
    );
    this.locators.set(
      'Installation details: Permit entry into force',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[4]/dd`
      )
    );
    this.locators.set(
      'Installation details: Regulator',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[5]/dd`
      )
    );
    this.locators.set(
      'Installation details: First year of verified emission submission',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[6]/dd`
      )
    );
    this.locators.set(
      'Authorised representatives',
      new UserFriendlyLocator('xpath', `//*[.='Authorised representatives']`)
    );

    this.locators.set(
      'Aircraft operator',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/summary/span`
      )
    );

    this.locators.set(
      'Aircraft operator: Monitoring plan ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[1]/dd`
      )
    );
    this.locators.set(
      'Aircraft operator: Monitoring plan first year of applicability',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[2]/dd`
      )
    );
    this.locators.set(
      'Aircraft operator: Regulator',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[3]/dd`
      )
    );
    this.locators.set(
      'Aircraft operator: First year of verified emission submission',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[4]/dd`
      )
    );

    this.locators.set(
      'Aircraft operator: First year of verification',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[4]/dd`
      )
    );

    this.locators.set(
      'Authorised representatives label check answers',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[5]/div/details[1]/summary/span`
      )
    );

    this.locators.set(
      'Authorised representative label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[1]/summary/span`
      )
    );

    this.locators.set(
      'Authorised representative: Name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[5]/div/details/div/dl[2]/div[1]/dd`
      )
    );

    this.locators.set(
      'Authorised representative: User ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[5]/div/details/div/dl[2]/div[2]/dd`
      )
    );

    this.locators.set(
      'Authorised representative: Permissions',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[5]/div/details/div/dl[4]/div/dd`
      )
    );

    this.locators.set(
      'Authorised representative Number 1 label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[1]/summary/span`
      )
    );

    this.locators.set(
      'Authorised representative Number 1: Name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[1]/div/dl/div/dd`
      )
    );

    this.locators.set(
      'Authorised representative Number 1: User ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[1]/div/dl/div[2]/dd`
      )
    );

    this.locators.set(
      'Authorised representative Number 1: Permissions',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[1]/div/dl/div[3]/dd`
      )
    );

    this.locators.set(
      'Authorised representative Number 2 label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[2]/summary/span`
      )
    );

    this.locators.set(
      'Authorised representative Number 2: Name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[2]/div/dl/div/dd`
      )
    );

    this.locators.set(
      'Authorised representative Number 2: User ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[2]/div/dl/div[2]/dd`
      )
    );

    this.locators.set(
      'Authorised representative Number 2: Permissions',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[2]/div/dl/div[3]/dd`
      )
    );
    this.locators.set(
      'Confirm and submit',
      new UserFriendlyLocator(
        'xpath',
        `//button[text()=' Confirm and submit ']`
      )
    );

    this.locators.set(
      'Upper Back',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Back')]`)
    );

    this.locators.set(
      'Information is incomplete or wrong Back',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/div/p[2]/a`
      )
    );
    this.locators.set(
      'Authorised Representative1',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Authorised Representative1')])[1]`
      )
    );
    this.locators.set(
      'REGISTERED USER',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'REGISTERED USER')])[1]`
      )
    );
    // ----------------------------------------------------------------------------------------------------
    // section Default rules
    this.locators.set(
      'Default rules for the trusted account list',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Default rules for the trusted account list ']`
      )
    );

    this.locators.set(
      'Default rules for the trusted account list: second authorised representative necessary approval',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[3]/div/dl/div[1]/dd`
      )
    );

    this.locators.set(
      'Default rules for the trusted account list: Are transfers to accounts not on the trusted account list allowed',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[3]/div/dl/div[2]/dd`
      )
    );

    // ----------------------------------------------------------------------------------------------------
    // Links with primary contact section:
    this.locators.set(
      'primary contact',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),' Primary contact(s) ')]`
      )
    );
    this.locators.set(
      'Primary Contact link',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-account-holder-contact-summary/div/details/summary/span`
      )
    );
    this.locators.set(
      'primary contact Number 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/summary/span`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/div[2]/div/div/h2`
      )
    );
    this.locators.set(
      'Primary Contact: Personal Details: First and middle names',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[1]/div[1]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Personal Details: Last name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[1]/div[2]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Personal Details: I confirm that the primary contact is aged 18 or over',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[1]/div[4]/dd`
      )
    );
    this.locators.set(
      'Account Holder: Individual Details: I confirm that the account holder is aged 18 or over',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-individual-details-container/app-account-holder-individual-details/div[2]/div/form/div/div[4]`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Company position',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[2]/div[1]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Work address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[2]/div[2]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Town or city',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[2]/div[3]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Country',
      new UserFriendlyLocator(
        'xpath',
        `(//dt[contains(., "Country")])[2]/../dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Postal Code or ZIP',
      new UserFriendlyLocator(
        'xpath',
        `(//dt[contains(., "Postal Code or ZIP")])[2]/../dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Work phone number 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[2]/div[6]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Email address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[2]/div[7]/dd`
      )
    );

    this.locators.set(
      'Title',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-confirmation/div/div/div/h1`
      )
    );
    this.locators.set(
      'Request ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-confirmation/div/div/div/div/strong`
      )
    );
    this.locators.set(
      'Accounts',
      new UserFriendlyLocator('xpath', `//*[.=' Accounts ']/a`)
    );
    this.locators.set(
      'Tasks',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Tasks')]`)
    );

    this.locators.set(
      'Check your answers: Organisation details: Name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-overview/div[2]/div/dl[2]/div[1]/dd`
      )
    );

    this.locators.set(
      'Check your answers: Organisation details: Registration number',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-overview/div[2]/div/dl[2]/div[2]/dd`
      )
    );

    this.locators.set(
      'Check your answers: Organisation details: VAT registration number',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-overview/div[2]/div/dl[2]/div[3]/dd`
      )
    );

    this.locators.set(
      'Check your answers: Organisation details: Organisation address',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., "Organisation address")]/../dd`
      )
    );

    this.locators.set(
      'Check your answers: Organisation details: Town or city',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., "Town or city")]/../dd`
      )
    );

    this.locators.set(
      'Check your answers: Organisation details: Postal Code or ZIP',
      new UserFriendlyLocator(
        'xpath',
        `//dt[contains(., "Postal Code or ZIP")]/../dd`
      )
    );

    this.locators.set(
      'Check your answers: Organisation details: Country',
      new UserFriendlyLocator('xpath', `//dt[contains(., "Country")]/../dd`)
    );

    this.locators.set(
      'Page title',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-main-wizard/div[2]/div/form/div/div/div[1]/h1`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//*[contains(text(),'Start now')]`));
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
