import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheRequestDocumentsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.REQUEST_DOCUMENTS;
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
      'Cancel request',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Cancel request')])[2]`
      )
    );
    this.locators.set(
      'Go back to the user details',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Go back to the user details')]`
      )
    );
    this.locators.set(
      'Submit request',
      new UserFriendlyLocator(
        'xpath',
        KnowsThePage.LOCATOR_XPATH_SUBMIT_REQUEST
      )
    );
    this.locators.set(
      'continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE_2)
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
      'Add another document',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Add another document')]`
      )
    );
    this.locators.set(
      'Go back to the account details',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Go back to the account details')]`
      )
    );
    this.locators.set(
      'Task Details User Page main content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/ng-component/app-user-details-container/app-user-details/div/div/div`
      )
    );
    this.locators.set(
      'Page main content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-documents-container/app-check-documents-request/div`
      )
    );
    // define document textboxes by class locator because in this specific page, id values change after add/remove elements click
    this.locators.set(
      'Document name 1',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-input ng-untouched ng-pristine ng-valid')])[1]`
      )
    );
    this.locators.set(
      'Document name 2',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-input ng-untouched ng-pristine ng-valid')])[2]`
      )
    );
    this.locators.set(
      'Document name 3',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(@class, 'govuk-input ng-untouched ng-pristine ng-valid')])[3]`
      )
    );
    this.locators.set(
      'Comment',
      new UserFriendlyLocator('xpath', `//*[@id="comment"]`)
    );
    this.locators.set(
      'AuthorisedRepresentative1',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'AuthorisedRepresentative1')]`
      )
    );
    this.locators.set(
      'Recipient dropdown list',
      new UserFriendlyLocator('xpath', `//*[@id="recipient"]`)
    );
    this.locators.set(
      'Remove',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Remove')]`)
    );
    this.locators.set(
      'Remove Nr. 1',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Remove')])[1]`)
    );
    this.locators.set(
      'Remove Nr. 2',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Remove')])[2]`)
    );
    this.locators.set(
      'Remove Nr. 3',
      new UserFriendlyLocator('xpath', `(//*[contains(text(),'Remove')])[3]`)
    );
    this.locators.set(
      'Last signed in value',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/ng-component/app-user-details-container/app-user-details/div/div/div/div[2]/dl/div/dd`
      )
    );
    this.locators.set(
      'Authorised Representative in accounts content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/ng-component/app-user-details-container/app-user-details/div/div/div/app-ar-in-accounts/div`
      )
    );
    this.locators.set(
      'Identification documentation content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/ng-component/app-user-details-container/app-user-details/div/div/div/app-identification-documentation/div`
      )
    );
    this.locators.set(
      'Registration details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/ng-component/app-user-details-container/app-user-details/div/div/div/app-registration-details/div`
      )
    );
    this.locators.set(
      'Personal details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/ng-component/app-user-details-container/app-user-details/div/div/div/app-personal-details/div`
      )
    );
    this.locators.set(
      'Work contact details content',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/ng-component/app-user-details-container/app-user-details/div/div/div/app-work-contact-details/div`
      )
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
