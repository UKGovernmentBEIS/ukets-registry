/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

import { KnowsThePage } from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheRegisterPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.REGISTER;
    this.setUpTestData();
  }

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpTestData() {}
}
