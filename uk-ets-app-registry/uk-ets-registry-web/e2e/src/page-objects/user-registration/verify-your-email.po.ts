/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

import { KnowsThePage } from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheVerifyYourEmailPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.EMAIL_VERIFY;
    this.setUpTestData();
  }

  getData(): Map<string, Promise<string>> {
    return new Map<string, Promise<string>>();
  }

  setUpTestData() {}
}
