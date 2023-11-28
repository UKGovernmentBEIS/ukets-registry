import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheUserMyProfilePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.USER_MY_PROFILE;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'Cancel request',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_CANCEL_REQUEST
      )
    );
    this.locators.set(
      'Cancel',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CANCEL)
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SUBMIT)
    );
    this.locators.set(
      'Request update',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_ID_REQUEST_UPDATE)
    );
    this.locators.set(
      'Update user details',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_UPDATE_USER_DETAILS_RADIO_BUTTON
      )
    );
    this.locators.set(
      'Deactivate user',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Deactivate your Registry access ']`
      )
    );
    this.locators.set(
      'Reason',
      new UserFriendlyLocator('xpath', `//*[@id="justification"]`)
    );
    this.locators.set(
      'Address line 1',
      new UserFriendlyLocator('xpath', `//*[@id="workBuildingAndStreet"]`)
    );
    this.locators.set(
      'Date of birth: Day',
      new UserFriendlyLocator('xpath', `//*[@id="permit-day"]`)
    );
    this.locators.set(
      'Date of birth: Month',
      new UserFriendlyLocator('xpath', `//*[@id="permit-month"]`)
    );
    this.locators.set(
      'Date of birth: Year',
      new UserFriendlyLocator('xpath', `//*[@id="permit-year"]`)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator(
        'xpath',
        KnowsTheUserMyProfilePage.LOCATOR_XPATH_CONTINUE
      )
    );
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
    );
    this.locators.set(
      'Last signed in value',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/div[2]/dl/div/dd`
      )
    );
    this.locators.set(
      'Authorised Representative in accounts content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/app-ar-in-accounts/div`
      )
    );
    this.locators.set(
      'Identification documentation content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/app-identification-documentation/div`
      )
    );
    this.locators.set(
      'Registration details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/app-registration-details/div`
      )
    );
    this.locators.set(
      'Personal details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/app-personal-details/div`
      )
    );
    this.locators.set(
      'Work contact details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/app-work-contact-details/div`
      )
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Back to the user details',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Back to the user details')]`
      )
    );
    this.locators.set(
      'History and comments',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'History and comments')]`
      )
    );
    this.locators.set(
      'Change your email address',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Change your email address')]`
      )
    );
    this.locators.set(
      'Change your password',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Change your password')]`
      )
    );
    this.locators.set(
      'Change two factor authentication',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Change two factor authentication')]`
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
