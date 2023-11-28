/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

import {
  browser,
  by,
  element,
  ElementFinder,
  ExpectedConditions,
  protractor,
  until,
  WebDriver,
} from 'protractor';
import { AppRoutesPerScreenUtil } from './app.routes-per-screen.util';
import { RegistryScreen } from './screens';
import { delay } from './step.util';
import { Locator } from 'selenium-webdriver';
import { assert } from 'chai';

const EC = protractor.ExpectedConditions;

export class UserFriendlyLocator {
  constructor(
    private type:
      | 'xpath'
      | 'id'
      | 'css'
      | 'class'
      | 'tag'
      | 'partialLinkText'
      | 'partialButtonText'
      | 'buttonText'
      | 'linkText',
    private searchString: string
  ) {}

  by() {
    return this.type === 'xpath'
      ? by.xpath(this.searchString)
      : this.type === 'css'
      ? by.css(this.searchString)
      : this.type === 'class'
      ? by.className(this.searchString)
      : this.type === 'id'
      ? by.id(this.searchString)
      : this.type === 'tag'
      ? by.tagName(this.searchString)
      : this.type === 'linkText'
      ? by.linkText(this.searchString)
      : this.type === 'partialLinkText'
      ? by.partialLinkText(this.searchString)
      : undefined;
  }

  async element(driver: WebDriver) {
    return driver.findElement(this.by());
  }
}

export class KnowsTheLocators {
  private textToIdLocators: Map<string, UserFriendlyLocator> = new Map();

  set(text: string, locator: UserFriendlyLocator) {
    this.textToIdLocators.set(text, locator);
  }

  get(text: string): UserFriendlyLocator {
    return this.textToIdLocators.get(text);
  }

  all() {
    return Array.from(this.textToIdLocators.values());
  }
}

export abstract class KnowsThePage {
  public static readonly LOCATOR_XPATH_BACK = `//*[.='Back']/a`;
  public static readonly LOCATOR_XPATH_bACK = `//*[.=' Back ']/a`;
  public static readonly LOCATOR_XPATH_BACK_TO_ACCOUNT_LIST = `//*[contains(text(),'Back to account list')]`;
  public static readonly LOCATOR_XPATH_SIGN_OUT = `//*[.='Sign out']/a`;
  public static readonly LOCATOR_XPATH_GENERATE_CURRENT_REPORT = `//*[contains(text(),'Generate current report')]`;
  public static readonly LOCATOR_XPATH_EDIT = `//*[.=' Edit ']`;
  public static readonly LOCATOR_XPATH_CONTINUE = `//*[.=' Continue ']`;
  public static readonly LOCATOR_XPATH_CONTINUE_2 = `//*[contains(text(),'Continue')]`;
  public static readonly LOCATOR_XPATH_CANCEL = `//*[.='Cancel']/a`;
  public static readonly LOCATOR_XPATH_CANCEL_REQUEST = `//*[.=' Cancel request ']`;
  public static readonly LOCATOR_XPATH_UK_ETS_HOMEPAGE = `//*[contains(text(),'UK ETS Homepage')]`;
  public static readonly LOCATOR_XPATH_SEARCH = `//*[.=' Search ']`;
  public static readonly LOCATOR_XPATH_CLEAR = `//*[.=' Clear ']`;
  public static readonly LOCATOR_XPATH_DELETE = `//*[.=' Delete ']`;
  public static readonly LOCATOR_XPATH_SHOW_FILTERS = `//*[contains(text(),'Show filters')]`;
  public static readonly LOCATOR_XPATH_HIDE_FILTERS = `//*[contains(text(),'Hide filters')]`;
  public static readonly LOCATOR_XPATH_COMMENT = `//*[@id="comment"]`;
  public static readonly LOCATOR_XPATH_CHANGE_OCCURRENCE_1 = `(//*[contains(text(),'Change')])[1]`;
  public static readonly LOCATOR_XPATH_CHANGE_OCCURRENCE_2 = `(//*[contains(text(),'Change')])[2]`;
  public static readonly LOCATOR_XPATH_YES = `//*[@id="yes"]`;
  public static readonly LOCATOR_XPATH_NO = `//*[@id="no"]`;
  public static readonly LOCATOR_XPATH_SUBMIT_REQUEST = `//*[contains(text(),' Submit request ')]`;
  public static readonly LOCATOR_XPATH_SUBMIT = `//*[contains(text(),' Submit ')]`;
  public static readonly LOCATOR_XPATH_SUBMIT_2 = `//*[contains(text(),'Submit')]`;
  public static readonly LOCATOR_XPATH_OTP = `//*[@id="otpCode"]`;
  public static readonly LOCATOR_XPATH_OTP_2 = `//*[@id="otp"]`;
  public static readonly LOCATOR_XPATH_SIGN_IN = `//*[@id="sign-in"]`;
  public static readonly LOCATOR_XPATH_SIGN_IN_2 = `//*[contains(@class, 'govuk-header__link uk_ets_sign_in_out_link govuk-header__link--sign-custom') and contains(text(),'Sign in')]`;
  public static readonly LOCATOR_XPATH_SIGN_IN_3 = `//*[contains(@class, 'govuk-button govuk-button--start') and contains(text(),'Sign in')]`;
  public static readonly LOCATOR_XPATH_SIGN_OUT_2 = `//*[@id="sign-out"]`;
  public static readonly LOCATOR_XPATH_MENU_ACCOUNTS = `//*[.=' Accounts ']/a`;
  public static readonly LOCATOR_XPATH_MENU_TASKS = `//*[.=' Tasks ']/a`;
  public static readonly LOCATOR_XPATH_MENU_TRANSACTIONS = `//*[.=' Transactions ']/a`;
  public static readonly LOCATOR_XPATH_MENU_USER_ADMINISTRATION = `//*[.=' User Administration ']/a`;
  public static readonly LOCATOR_ID_QR_CODE_IMAGE = `kc-totp-secret-qr-code`;
  public static readonly LOCATOR_ID_REQUEST_UPDATE = `//*[contains(text(),'Request Update') or contains(text(),'Request update')]`;
  public static readonly LOCATOR_XPATH_UPDATE_USER_DETAILS_RADIO_BUTTON = `//input[@type="radio"][following-sibling::*[contains(text(),'user detail')]]`;
  public static readonly TIMEOUT = 150 * 1000;
  public static readonly ELEMENT_TIMEOUT = 5 * 1000;
  public static readonly DEFAULT_SIGN_IN_PASSWORD = 'stkuy!gh34#$%dgf#$dfJHGjh';

  public static requestId: any;
  public static userId = '';
  public static governmentAccountHolderId = '';
  public static idValueInScreenName = '';
  public static users2fa = new Map<string, any>();

  backspaces: Array<string> = Array.of(
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE,
    protractor.Key.BACK_SPACE
  );

  name: RegistryScreen;

  testData: Map<string, string> = new Map();
  snapshotData: Map<string, Promise<string>> = new Map();
  locators: KnowsTheLocators = new KnowsTheLocators();

  public static async setIdValueInScreenName(idValueInScreenName: string) {
    this.idValueInScreenName = idValueInScreenName;
    console.log(
      `Entered setIdValueInScreenName method. idValueInScreenName: '${this.idValueInScreenName}'.`
    );
  }

  public static getIdValueInScreenName() {
    return this.idValueInScreenName;
  }

  public static async setRequestId(requestId: string) {
    this.requestId = requestId;
    console.log(`Entered setRequestId method. RequestId: '${this.requestId}'.`);
  }

  public static async setUserId(userId: string) {
    this.userId = userId;
    console.log(`Entered setUserId method. Variable: '${this.userId}'.`);
  }

  public static getRequestId() {
    return this.requestId;
  }

  public static getUserId() {
    return this.userId;
  }

  public static async setGovernmentAccountHolderId(
    governmentAccountHolderId: string
  ) {
    this.governmentAccountHolderId = governmentAccountHolderId;
  }

  public static getGovernmentAccountHolderId() {
    return this.governmentAccountHolderId;
  }

  protected constructor(public readonly developedWithAngular = true) {}

  abstract setUpTestData();

  async navigateTo(screenName: string) {
    if (screenName) {
      await browser.get(
        browser.baseUrl + AppRoutesPerScreenUtil.get(screenName)
      );
    } else {
      throw new Error(`Cannot navigate into screen: '${screenName}'.`);
    }
  }

  public async getCurrentUrl() {
    if (this.developedWithAngular && browser.waitForAngularEnabled()) {
      return browser.getCurrentUrl();
    } else {
      return browser.driver.getCurrentUrl();
    }
  }

  async setInputTextById(id: string, value: string) {
    await this.awaitElement(by.id(id));
    const inputText = await element(by.id(id));
    await inputText.sendKeys(value);
  }

  async fillExcluding(excluding: string[] = []) {
    for (const [key, value] of this.testData) {
      if (excluding.includes(key)) {
        continue;
      }
      await this.setInputTextById(key, value);
    }
  }

  async fillWith(newTestData: Map<string, string>) {
    this.testData.clear();
    for (const [key, value] of newTestData) {
      this.testData.set(key, value);
    }
    await this.fillExcluding();
  }

  getButton(buttonText: string): ElementFinder {
    const button = element(by.buttonText(buttonText));
    browser.wait(ExpectedConditions.presenceOf(button), KnowsThePage.TIMEOUT);
    return button;
  }

  getLink(linkText: string): ElementFinder {
    const link = element(by.linkText(linkText));
    browser.wait(ExpectedConditions.presenceOf(link), KnowsThePage.TIMEOUT);
    return link;
  }

  async webElementActionApply(
    sendValue: string,
    locator: UserFriendlyLocator,
    actionOnWebElement:
      | 'click'
      | 'sendKeys'
      | 'getText'
      | 'clear'
      | 'isDisplayed'
      | 'isEnabled'
      | 'getRadioButtonSelectionStatus'
      | 'getDropdownSelectionStatus'
      | 'getCheckBoxButtonSelectionStatus'
      | 'check'
      | 'uncheck'
      | 'select'
  ) {
    try {
      if (!locator) {
        console.log(`Undefined locator: '${locator.by}'.`);
        return;
      }
    } catch (exception) {
      console.log(
        `locator exception: actionOnWebElement: '${actionOnWebElement}', sendValue: '${sendValue}'.`
      );
      return;
    }

    await this.awaitElement(locator.by());
    let elements = null;
    let returnValue = null;
    const totalTries = 3;
    let tries = 1;

    await Promise.all([delay(100)]);

    while (tries < totalTries && returnValue === null) {
      try {
        if (
          this.developedWithAngular &&
          (await browser.waitForAngularEnabled())
        ) {
        } else {
          elements = await browser.driver.findElements(locator.by());
        }

        if (actionOnWebElement === 'click') {
          returnValue = await elements[0].click();
        } else if (actionOnWebElement === 'select') {
          returnValue = await elements[0].click();
        } else if (actionOnWebElement === 'sendKeys') {
          returnValue = await elements[0].sendKeys(sendValue);
        } else if (actionOnWebElement === 'getText') {
          returnValue =
            (await elements[0].getText()) !== ''
              ? elements[0].getText()
              : await elements[0].getAttribute('value');
        } else if (actionOnWebElement === 'clear') {
          returnValue = await elements[0].clear();
        } else if (actionOnWebElement === 'isDisplayed') {
          returnValue = await elements[0].isDisplayed();
        } else if (actionOnWebElement === 'isEnabled') {
          returnValue = await elements[0].isEnabled();
        } else if (actionOnWebElement === 'getDropdownSelectionStatus') {
          returnValue = await elements[0].getAttribute('value');
        } else if (actionOnWebElement === 'getCheckBoxButtonSelectionStatus') {
          returnValue = (await elements[0].isSelected())
            ? 'selected'
            : 'not selected';
        } else if (actionOnWebElement === 'getRadioButtonSelectionStatus') {
          returnValue = (await elements[0].isSelected())
            ? 'selected'
            : 'not selected';
        } else if (
          actionOnWebElement === 'check' ||
          actionOnWebElement === 'uncheck'
        ) {
          returnValue = await elements[0].click();
        } else {
          assert.fail(
            `Extend this method or use a valid argument. Currently the value actionOnWebElement: '${actionOnWebElement}' is used.`
          );
        }
        tries++;
        await Promise.all([delay(1000)]);
        if (returnValue === null) {
          if (
            actionOnWebElement === 'click' ||
            actionOnWebElement === 'clear' ||
            actionOnWebElement === 'sendKeys' ||
            actionOnWebElement === 'check' ||
            actionOnWebElement === 'uncheck'
          ) {
            return;
          }
        }
      } catch (ex) {
        console.log(
          `exception while trying to apply web element action in webElementActionApply for time '${tries}'.`
        );
        tries++;
        await Promise.all([delay(1000)]);
      }
    }
    return returnValue;
  }

  async clickButton(buttonText: string) {
    console.log(`Entered clickButton in knows the page`);
    let button: any;
    await browser.waitForAngularEnabled();
    let err = false;
    console.log(`Waited in clickButton in knows the page`);

    try {
      if (
        this.developedWithAngular &&
        (await browser.waitForAngularEnabled())
      ) {
        console.log(
          `About to locate element by partialButtonText '${buttonText}'.`
        );
        button = await element(by.partialButtonText(buttonText));
        console.log(`Located.`);
      } else {
        console.log(`About to locate element by className.`);
        const elements = await browser.driver.findElements(
          by.className('govuk-button')
        );
        console.log(`Located.`);
        button = elements.find(
          async (el) => buttonText === (await el.getText())
        );
        console.log(`Button found.`);
      }
      console.log(`About to executeScript arguments[0].scrollIntoView().`);
      await browser.driver.executeScript(
        'arguments[0].scrollIntoView();',
        button
      );
    } catch (e) {
      err = true;

      const currentPageSource = await browser.driver.getPageSource();

      console.log(
        `Could not click on button '${buttonText}'. Exception: '${e}'.` +
          `Current page source is: '\n\n${currentPageSource}\n\n'.`
      );
    }

    assert.isTrue(!err, `Error in clickButton`);
    return button.click();
  }

  async clickLink(linkText: string) {
    let link: any;
    if (this.developedWithAngular && (await browser.waitForAngularEnabled())) {
      link = await element(by.linkText(linkText));
    } else {
      await this.awaitElement(by.className('uk_ets_sign_in_out_link'));
      const elements = await browser.driver.findElements(
        by.className('uk_ets_sign_in_out_link')
      );
      link = elements.find(async (el) => linkText === (await el.getText()));
    }
    await browser.driver.executeScript('arguments[0].scrollIntoView();', link);
    return link.click();
  }

  abstract getData(): Map<string, Promise<string>>;

  async awaitElement(locator: Locator) {
    const isPresent = EC.presenceOf(element(locator));

    try {
      let done: any;
      if (await browser.waitForAngularEnabled()) {
        await Promise.all([
          browser.wait(isPresent, KnowsThePage.ELEMENT_TIMEOUT),
          browser.wait(
            until.elementsLocated(locator),
            KnowsThePage.TIMEOUT,
            locator.toString()
          ),
        ]).then((res) => (done = res[1]));
      } else {
        await Promise.all([
          browser.driver.wait(isPresent, KnowsThePage.ELEMENT_TIMEOUT),
          browser.driver.wait(
            until.elementsLocated(locator),
            KnowsThePage.TIMEOUT,
            locator.toString()
          ),
        ]).then((res) => (done = res[1]));
      }
      return done;
    } catch (error) {
      console.log(`Locator not found '${locator}'.`);
      
      const htmlPageSource = await browser.getPageSource();
      console.log(`htmlPageSource: '${htmlPageSource}'.`);
      
      await Promise.all([delay(500)]);
    }

    console.log(`Cannot locate: '${locator.toString()}'.`);
  }

  async getValue(id: string, type: string = 'text') {
    let valueElement = element(by.id(id));
    if (type === 'select') {
      valueElement = valueElement.element(by.css('option:checked'));
    }
    if (type === 'element-with-span') {
      valueElement = valueElement.element(by.tagName('span'));
    }
    let textElement = valueElement.getText();
    if (type === 'input') {
      textElement = valueElement.getAttribute('value');
    }
    return await textElement.then().catch((reason) => {
      throw new Error('Could not get the text of the element: ' + reason);
    });
  }

  async getErrorSummary(errorMessageToLocate: string) {
    return element(by.xpath(`//div[@class="govuk-error-message"]`)).getText();
  }

  async getErrorDetail(errorId: string) {
    return element(by.id(errorId)).getText();
  }
}
