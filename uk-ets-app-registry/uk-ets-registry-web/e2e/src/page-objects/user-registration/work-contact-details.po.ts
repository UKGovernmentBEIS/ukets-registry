/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

import { by, element, browser } from 'protractor';
import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheWorkContactDetailsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.WORK_CONTACT_DETAILS;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {
    this.testData
      .set('workPhone-country-code', 'UK')
      .set('workPhone-phone-number', '(020) 1234 1234')
      .set('workEmailAddress', 'test@test.gov.uk')
      .set('workBuildingAndStreet', 'Down street, 42')
      .set('workBuildingAndStreetOptional', '')
      .set('workBuildingAndStreetOptional2', '')
      .set('workPostCode', '45556')
      .set('workTownOrCity', 'London')
      .set('workStateOrProvince', 'London State')
      .set('workCountry', 'UK');
  }

  getData(): Map<string, Promise<string>> {
    return new Map<string, Promise<string>>()
      .set('workPhone-country-code', this.getValue('workPhone-country-code'))
      .set('workPhone-phone-number', this.getValue('workPhone-phone-number'))
      .set('workBuildingAndStreet', this.getValue('workBuildingAndStreet'))
      .set(
        'workBuildingAndStreetOptional',
        this.getValue('workBuildingAndStreetOptional')
      )
      .set(
        'workBuildingAndStreetOptional2',
        this.getValue('workBuildingAndStreetOptional2')
      )
      .set('workPostCode', this.getValue('workPostCode'))
      .set('workTownOrCity', this.getValue('workTownOrCity'))
      .set('workStateOrProvince', this.getValue('workStateOrProvince'))
      .set('workCountry', this.getValue('workCountry'));
  }

  setUpLocators() {
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
      // console.log(`No need for awaitElement.`);
    } else {
      await this.awaitElement(by.xpath(`//*[@id="workEmailAddress"]`));
    }
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }
}
