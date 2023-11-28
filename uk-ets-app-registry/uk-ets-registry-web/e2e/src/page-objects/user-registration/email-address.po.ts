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

export class KnowsTheEmailAddressPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.EMAIL_ADDRESS;
    this.setUpTestData();
    this.setUpLocators();
  }

  getData(): Map<string, Promise<string>> {
    return new Map<string, Promise<string>>().set(
      'email',
      this.getValue('email')
    );
  }

  setUpTestData() {
    this.testData.set('email', 'test@test.com');
  }

  setUpLocators() {
    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
  }
}
