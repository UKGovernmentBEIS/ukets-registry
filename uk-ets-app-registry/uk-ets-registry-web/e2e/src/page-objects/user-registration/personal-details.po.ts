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

export class KnowsThePersonalDetailsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.PERSONAL_DETAILS;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpLocators() {
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
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

    this.locators.set(
      'Country Greece',
      new UserFriendlyLocator('xpath', `//*[@id="country"]/option[65]`)
    );
  }

  setUpTestData() {
    this.testData
      .set('firstName', 'Pink')
      .set('lastName', 'Floyd')
      .set('alsoKnownAs', '')
      .set('buildingAndStreet', 'Abbey Road 77')
      .set('buildingAndStreetOptional', '')
      .set('postCode', '56566')
      .set('townOrCity', 'London')
      .set('stateOrProvince', 'London State')
      .set('country', 'United Kingdom')
      .set('permit-day', '01')
      .set('permit-month', '02')
      .set('permit-year', '1970')
      .set('countryOfBirth', 'United Kingdom');
  }

  getData(): Map<string, Promise<string>> {
    return new Map<string, Promise<string>>()
      .set('firstName', this.getValue('firstName'))
      .set('lastName', this.getValue('lastName'))
      .set('alsoKnownAs', this.getValue('alsoKnownAs'))
      .set('buildingAndStreet', this.getValue('buildingAndStreet'))
      .set(
        'buildingAndStreetOptional',
        this.getValue('buildingAndStreetOptional')
      )
      .set('postCode', this.getValue('postCode'))
      .set('townOrCity', this.getValue('townOrCity'))
      .set('stateOrProvince', this.getValue('stateOrProvince'))
      .set('country', this.getValue('country'))
      .set('permit-day', this.getValue('permit-day'))
      .set('permit-month', this.getValue('permit-month'))
      .set('permit-year', this.getValue('permit-year'))
      .set('countryOfBirth', this.getValue('countryOfBirth'));
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
      // console.log(`No need for awaitElement.`);
    } else {
      await this.awaitElement(by.xpath(`//*[@id="permit-day"]`));
      await this.awaitElement(by.xpath(`//*[@id="permit-month"]`));
      await this.awaitElement(by.xpath(`//*[@id="permit-year"]`));
      await this.awaitElement(by.xpath(`//*[@id="country"]/option[65]`));
    }
  }
}
