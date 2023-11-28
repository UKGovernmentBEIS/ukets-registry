import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheViewRegistryActivationPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.REGISTRY_ACTIVATION;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
    );
    this.locators.set(
      'Back',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_BACK)
    );
    this.locators.set(
      'Textbox number 1',
      new UserFriendlyLocator('xpath', `//*[@id="input-1"]`)
    );
    this.locators.set(
      'Textbox number 2',
      new UserFriendlyLocator('xpath', `//*[@id="input-2"]`)
    );
    this.locators.set(
      'Textbox number 3',
      new UserFriendlyLocator('xpath', `//*[@id="input-3"]`)
    );
    this.locators.set(
      'Textbox number 4',
      new UserFriendlyLocator('xpath', `//*[@id="input-4"]`)
    );
    this.locators.set(
      'Textbox number 5',
      new UserFriendlyLocator('xpath', `//*[@id="input-5"]`)
    );
    this.locators.set(
      'Continue',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-registry-activation/div/div/div/div/form/button`
      )
    );
    this.locators.set(
      'I have not received the code link',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'I have not received the code')]`
      )
    );
    this.locators.set(
      `There's a problem with my code`,
      new UserFriendlyLocator(
        'xpath',
        '//*[@id="main-content"]/div[2]/div/app-registry-activation/div/div/div/div/form/details/summary/span'
      )
    );
    this.locators.set(
      'Registry activation code is expired link',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Registry activation code is expired')]`
      )
    );
    this.locators.set(
      'Request a new registry activation code by not received the code',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-registry-activation/div/div/form/fieldset/details[1]/div/a`
      )
    );
    this.locators.set(
      'Request a new registry activation code',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Request a new registry activation code')]`
      )
    );
    this.locators.set(
      'Request a new registry activation code by code expired',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-registry-activation/div/div/form/fieldset/details[2]/div/a`
      )
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
