import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by, until } from 'protractor';

export class KnowsTheRegistryDashboardPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.REGISTRY_DASHBOARD;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  setUpLocators() {
    this.locators.set(
      'Sign out',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_OUT)
    );
    this.locators.set(
      'Account opening',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-dashboard/div/div/p/button[1]`
      )
    );
    this.locators.set(
      'Sign in',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_SIGN_IN)
    );
    this.locators.set(
      'Dashboard',
      new UserFriendlyLocator('xpath', `//*[@id="navigation"]/li/a`)
    );

    this.locators.set(
      'LIMITED access label',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'LIMITED')]`)
    );

    this.locators.set(
      'Request account',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Request account')]`
      )
    );
    this.locators.set(
      'User Administration',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'User Administration')]`
      )
    );
    this.locators.set(
      'Task List',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-dashboard/div/div/p/button[2]`
      )
    );
    this.locators.set(
      'Welcome label',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Welcome')]`)
    );
    this.locators.set(
      'Transactions',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Transactions')]`)
    );
    this.locators.set(
      'Tasks',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Tasks')]`)
    );
    this.locators.set(
      'KP Administration',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'KP Administration')]`
      )
    );
    this.locators.set(
      'ETS Administration',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'ETS Administration')]`
      )
    );
    this.locators.set(
      'Accounts',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Accounts')]`)
    );
    this.locators.set(
      'Reports',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Reports')]`)
    );
    this.locators.set(
      'Notifications',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Notifications')]`)
    );
    this.locators.set(
      'Home',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Home')]`)
    );
    this.locators.set(
      'Request account tab',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Request account')]`
      )
    );
    this.locators.set(
      'Registry activation code label',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Registry activation code')]`
      )
    );
    this.locators.set(
      'Enter registry activation code link',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Enter your Registry activation code')]`
      )
    );
    this.locators.set(
      'I have not received my Registry activation code',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'I have not received my Registry activation code')]`
      )
    );
    this.locators.set(
      'Request a new registry activation code link',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(text(),'Request a new registry activation code')]`
      )
    );
    this.locators.set(
      'My Profile tab',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'My profile')]`)
    );
    this.locators.set(
      'My Profile',
      new UserFriendlyLocator('xpath', `//*[.='My Profile']/a`)
    );
  }

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await browser.driver.findElement(by.xpath(`//*[@id="navigation"]/li/a`));
      await this.awaitElement(by.xpath(`//*[@id="navigation"]/li/a`));
    }
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
