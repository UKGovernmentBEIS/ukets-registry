import {
  KnowsThePage,
  UserFriendlyLocator,
} from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';
import { browser, by } from 'protractor';

export class KnowsTheTaskCommentsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.TASK_COMMENTS;
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
      'Print letter with registry activation code label',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Print letter with registry activation code ']`
      )
    );
    this.locators.set(
      'History & comments label',
      new UserFriendlyLocator('xpath', `//*[.='History & comments']`)
    );
    // textbox
    this.locators.set(
      'comment area',
      new UserFriendlyLocator('xpath', KnowsThePage.LOCATOR_XPATH_COMMENT)
    );
    // link
    this.locators.set(
      'Add comment',
      new UserFriendlyLocator('xpath', `//*[.='Add comment']`)
    );
    // button
    this.locators.set(
      'Add Comment',
      new UserFriendlyLocator('xpath', `//*[.=' Add comment ']`)
    );
    // button
    this.locators.set(
      'Clear Comment',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/app-history-comments/form/details/div/fieldset/div/div/div/button[2]`
      )
    );
    this.locators.set(
      'comment table headers',
      new UserFriendlyLocator(
        'xpath',
        `//*[contains(@class,'govuk-table')]/thead`
      )
    );
    this.locators.set(
      'comment table results',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/app-history-comments/app-task-history-container/app-task-history/table/tbody`
      )
    );
    this.locators.set(
      'comment',
      new UserFriendlyLocator(
        'xpath',
        `//app-task-history/table/tbody/tr/td[5]`
      )
    );
    this.locators.set(
      'comment text result 1',
      new UserFriendlyLocator(
        'xpath',
        `//table[@aria-describedby="Domain events table"]//tbody//tr[1]/td[5]`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(by.xpath(`//*[.='History & comments']`));
    }
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
