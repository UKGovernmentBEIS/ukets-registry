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

export class KnowsTheRegisteredPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.REGISTERED;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {
    this.testData.set('user-id', '123456789123');
  }

  setUpLocators() {
    this.locators.set(
      'User ID',
      new UserFriendlyLocator('xpath', `//*[@id="user-id"]`)
    );
  }

  getData(): Map<string, Promise<string>> {
    try {
      return new Map<string, Promise<string>>().set(
        'user-id',
        this.getValue('user-id')
      );
    } catch (e) {
      throw e;
    }
  }
}
