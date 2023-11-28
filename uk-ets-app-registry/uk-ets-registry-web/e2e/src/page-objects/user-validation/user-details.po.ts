import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheUserDetailsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.USER_DETAILS;
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
      'Search',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SEARCH)
    );
    this.locators.set(
      'page main content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div`
      )
    );
    this.locators.set(
      'Email address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-registration-details/div/dl/div[2]/dd`
      )
    );
    this.locators.set(
      'Reason',
      new UserFriendlyLocator('xpath', `//*[@id="justification"]`)
    );
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Submit')]`)
    );
    this.locators.set(
      'User ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-registration-details/div/dl/div[3]/dd`
      )
    );
    this.locators.set(
      'User Status',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-registration-details/div/dl/div[4]/dd`
      )
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    this.locators.set(
      'Personal details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-personal-details/div`
      )
    );
    this.locators.set(
      'Work details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-work-contact-details/div[2]`
      )
    );
    this.locators.set(
      'user status',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'User ID: ')]/../../../../div[3]`
      )
    );
    this.locators.set(
      'No items added',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'No items added.')]`
      )
    );
    this.locators.set(
      'Request Update',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Request Update')]`)
    );
    this.locators.set(
      'Go back to the user details',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Go back to the user details')]`
      )
    );
    this.locators.set(
      'Request documents',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Request documents')]`
      )
    );
    this.locators.set(
      'Change status',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Change status')]`)
    );
    this.locators.set(
      'Back to user list',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Back to user list')]`
      )
    );
    this.locators.set(
      'Name or user ID textbox',
      new UserFriendlyLocator('xpath', `//*[@id="nameOrUserId"]`)
    );
    this.locators.set(
      'user basic information',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/div[2]/span`
      )
    );
    this.locators.set(
      'user basic status',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="feature-header"]/app-user-header/div/div/div[3]/app-govuk-tag/strong`
      )
    );
    this.locators.set(
      'user id information',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="feature-header"]/app-user-header/div/div/div[4]/div/div/div`
      )
    );
    this.locators.set(
      'user details left side',
      new UserFriendlyLocator('xpath', `//*[contains(text(),' User details ')]`)
    );
    this.locators.set(
      'user history and comments left side',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),' History and comments ')]`
      )
    );

    this.locators.set(
      'Last signed in date time value',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/div[2]/dl/div/dd`
      )
    );
    this.locators.set(
      'Authorised Representative in accounts',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/app-ar-in-accounts/div`
      )
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
        `//input[@type="radio"][following-sibling::*[1]/text()=' Deactivate user ']`
      )
    );
    this.locators.set(
      'Identification documentation',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/app-identification-documentation/div`
      )
    );
    this.locators.set(
      'Work contact details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/app-work-contact-details/div`
      )
    );
    this.locators.set(
      'Personal details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/app-personal-details/div`
      )
    );
    this.locators.set(
      'Registration details',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-user-details-container/app-user-details/div/div/div/div/app-registration-details/div`
      )
    );
    this.locators.set(
      'Who',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-user-history-and-comments-container/app-domain-events/table/tbody/tr[1]/td[2]`
      )
    );
    this.locators.set(
      'What',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-user-history-and-comments-container/app-domain-events/table/tbody/tr[1]/td[3]`
      )
    );
    this.locators.set(
      'Request ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-user-history-and-comments-container/app-domain-events/table/tbody/tr[1]/td[4]`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//*[.='User details']`));
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
