/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

import { KnowsThePage } from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheStartRegistrationPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.START_REGISTRATION;
    this.setUpTestData();
  }

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpTestData() {}
}
