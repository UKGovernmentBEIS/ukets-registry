/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheCheckAnswersAndSubmitPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.CHECK_ANSWERS_AND_SUBMIT;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpLocators() {
    this.locators.set(
      'Submit',
      new UserFriendlyLocator('xpath', `//button[text()=' Submit ']`)
    );
    this.locators.set(
      'phone',
      new UserFriendlyLocator('xpath', `//*[@id="phone-number"]/span`)
    );
  }

  setUpTestData() {
    this.testData
      .set('password', KnowsThePage.DEFAULT_SIGN_IN_PASSWORD)
      .set('pconfirm', KnowsThePage.DEFAULT_SIGN_IN_PASSWORD);
  }

  getData(): Map<string, Promise<string>> {
    try {
      return new Map<string, Promise<string>>()
        .set('password', this.getValue('password'))
        .set('pconfirm', this.getValue('pconfirm'));
    } catch (e) {
      throw e;
    }
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//button[text()=' Submit ']`));
    }
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }
}
