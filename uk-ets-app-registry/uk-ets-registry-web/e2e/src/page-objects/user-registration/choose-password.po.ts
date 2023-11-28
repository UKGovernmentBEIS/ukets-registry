import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheChoosePasswordPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.CHOOSE_PWD;
    this.setUpTestData();
    this.setUpLocators();
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

  setUpLocators() {
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );

    this.locators.set(
      'Continue',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_CONTINUE)
    );
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }
}
