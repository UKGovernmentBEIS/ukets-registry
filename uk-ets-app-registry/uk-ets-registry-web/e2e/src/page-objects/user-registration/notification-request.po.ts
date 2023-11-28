import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheNotificationRequestPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.NOTIFICATION_REQUEST;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }
  setUpLocators() {
    this.locators.set(
      'Ad-hoc Notification',
      new UserFriendlyLocator(
        'xpath',
        `//input[@type="radio"][following-sibling::*[1]/text()=' Ad-hoc Notification ']`
      )
    );

    this.locators.set(
      'Scheduled Date',
      new UserFriendlyLocator('xpath', `//*[@id="scheduledDate"]`)
    );

    this.locators.set(
      'today',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Today')]`)
    );

    this.locators.set(
      '11:30pm',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'11:30pm')]`)
    );

    this.locators.set(
      'title',
      new UserFriendlyLocator('xpath', `//*[@id="notificationSubject"]`)
    );

    this.locators.set(
      'content',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(@class, 'ql-editor ql-blank')]`
      )
    );

    this.locators.set(
      'Schedule',
      new UserFriendlyLocator('xpath', `//*[@id="submit"]`)
    );

    this.locators.set(
      'Go back to notification list',
      new UserFriendlyLocator('xpath', `//*[@id="back"]`)
    );

    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
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
