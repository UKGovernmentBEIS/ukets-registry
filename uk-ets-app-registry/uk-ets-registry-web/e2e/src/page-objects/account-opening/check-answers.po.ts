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
import { browser, by } from 'protractor';

export class KnowsTheCheckAnswersPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.CHECK_ANSWERS;
    this.setUpTestData();
    this.setUpLocators();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }

  setUpLocators() {
    this.locators.set(
      'Confirm and submit',
      new UserFriendlyLocator(
        'xpath',
        `//button[text()=' Confirm and submit ']`
      )
    );

    this.locators.set(
      'Upper Back',
      new UserFriendlyLocator('xpath', `//*[contains(text(),'Back')]`)
    );

    this.locators.set(
      'Information is incomplete or wrong Back',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/div/p[2]/a`
      )
    );
    this.locators.set(
      'Authorised Representative1',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'Authorised Representative1')])[1]`
      )
    );
    this.locators.set(
      'REGISTERED USER',
      new UserFriendlyLocator(
        'xpath',
        `(//*[contains(text(),'REGISTERED USER')])[1]`
      )
    );
    // ----------------------------------------------------------------------------------------------------
    // section Default rules
    this.locators.set(
      'Default rules for the trusted account list',
      new UserFriendlyLocator(
        'xpath',
        `//*[.=' Default rules for the trusted account list ']`
      )
    );

    this.locators.set(
      'Default rules for the trusted account list: second authorised representative necessary approval',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[3]/div/dl/div[1]/dd`
      )
    );

    this.locators.set(
      'Default rules for the trusted account list: Are transfers to accounts not on the trusted account list allowed',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[3]/div/dl/div[2]/dd`
      )
    );

    // ----------------------------------------------------------------------------------------------------
    // Links with primary contact section:
    this.locators.set(
      'primary contact',
      new UserFriendlyLocator('xpath', `//*[.=' Primary contact(s) ']`)
    );
    this.locators.set(
      'Primary Contact link',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div//app-account-holder-contact-summary/div/details/summary/span`
      )
    );
    this.locators.set(
      'primary contact Number 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/summary/span`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/div[2]/div/div/h2`
      )
    );
    this.locators.set(
      'Primary Contact: Personal Details: First and middle names',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[1]/div[1]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Personal Details: Last name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[1]/div[2]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Personal Details: I confirm that the primary contact is aged 18 or over',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[1]/div[4]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Company position',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[2]/div[1]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Work address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[2]/div[2]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Town or city',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[2]/div[3]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Country',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[2]/div[4]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Postal Code or ZIP',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[2]/div[5]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Work phone number 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[2]/div[6]/dd`
      )
    );
    this.locators.set(
      'Primary Contact: Address and contact details: Email address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/app-account-holder-contacts-details/details/app-account-holder-contact-summary/div/details/div/dl[2]/div[7]/dd`
      )
    );

    // ----------------------------------------------------------------------------------------------------
    // Links with authorised representative section:
    this.locators.set(
      'Authorised representatives',
      new UserFriendlyLocator('xpath', `//*[.='Authorised representatives']`)
    );

    this.locators.set(
      'Aircraft operator',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/summary/span`
      )
    );

    this.locators.set(
      'Aircraft operator: Monitoring plan ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[1]/dd`
      )
    );
    this.locators.set(
      'Aircraft operator: Monitoring plan first year of applicability',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[2]/dd`
      )
    );
    this.locators.set(
      'Aircraft operator: Regulator',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[3]/dd`
      )
    );
    this.locators.set(
      'Aircraft operator: First year of verified emission submission',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[4]/dd`
      )
    );

    this.locators.set(
      'Aircraft operator: First year of verification',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[4]/dd`
      )
    );

    this.locators.set(
      'Authorised representatives label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[5]/div/details[1]/summary/span`
      )
    );

    this.locators.set(
      'Authorised representative label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[1]/summary/span`
      )
    );

    this.locators.set(
      'Authorised representative: Name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[5]/div/details/div/dl[2]/div[1]/dd`
      )
    );

    this.locators.set(
      'Authorised representative: User ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[5]/div/details/div/dl[2]/div[2]/dd`
      )
    );

    this.locators.set(
      'Authorised representative: Permissions',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[5]/div/details/div/dl[4]/div/dd`
      )
    );

    this.locators.set(
      'Authorised representative Number 1 label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[1]/summary/span`
      )
    );

    this.locators.set(
      'Authorised representative Number 1: Name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[1]/div/dl/div/dd`
      )
    );

    this.locators.set(
      'Authorised representative Number 1: User ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[1]/div/dl/div[2]/dd`
      )
    );

    this.locators.set(
      'Authorised representative Number 1: Permissions',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[1]/div/dl/div[3]/dd`
      )
    );

    this.locators.set(
      'Authorised representative Number 2 label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[2]/summary/span`
      )
    );

    this.locators.set(
      'Authorised representative Number 2: Name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[2]/div/dl/div/dd`
      )
    );

    this.locators.set(
      'Authorised representative Number 2: User ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[2]/div/dl/div[2]/dd`
      )
    );

    this.locators.set(
      'Authorised representative Number 2: Permissions',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/details[2]/div/dl/div[3]/dd`
      )
    );

    this.locators.set(
      'Account holder',
      new UserFriendlyLocator('xpath', `//*[.=' Account Holder ']`)
    );

    this.locators.set(
      'Installation',
      new UserFriendlyLocator('xpath', `//*[.='Installation']`)
    );
    this.locators.set(
      'Installation details: Installation name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[1]/dd`
      )
    );
    this.locators.set(
      'Installation details: Installation activity type',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[2]/dd`
      )
    );
    this.locators.set(
      'Installation details: Permit ID',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[3]/dd`
      )
    );
    this.locators.set(
      'Installation details: Permit entry into force',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[4]/dd`
      )
    );
    this.locators.set(
      'Installation details: Regulator',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[5]/dd`
      )
    );
    this.locators.set(
      'Installation details: First year of verified emission submission',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[4]/div/dl/div[6]/dd`
      )
    );

    // ----------------------------------------------------------------------------------------------------
    // Links with individual details section:
    this.locators.set(
      'Individual details: Name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[2]/div[1]/dd`
      )
    );
    this.locators.set(
      'Individual details: Year of birth',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[2]/div[2]/dd`
      )
    );
    this.locators.set(
      'Individual details: Country of birth',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[2]/div[3]/dd`
      )
    );
    this.locators.set(
      'Individual Address and contact details: Residential address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[4]/div[1]/dd`
      )
    );
    this.locators.set(
      'Individual Address and contact details: Town or city',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[4]/div[2]/dd`
      )
    );
    this.locators.set(
      'Individual Address and contact details: Country',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[4]/div[3]/dd`
      )
    );
    this.locators.set(
      'Individual Address and contact details: Postal Code or ZIP',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[4]/div[4]/dd`
      )
    );
    this.locators.set(
      'Individual Address and contact details: Phone number 1',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[4]/div[5]/dd`
      )
    );
    this.locators.set(
      'Individual Address and contact details: Email address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[4]/div[6]/dd`
      )
    );

    // ----------------------------------------------------------------------------------------------------
    // Links with Organisation details section:
    this.locators.set(
      'Organisation details: Name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="accordion-with-summary-sections-account-holder"]/dl[1]/div[1]/dd`
      )
    );

    this.locators.set(
      'Organisation details: Registration number',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="accordion-with-summary-sections-account-holder"]/dl[1]/div[2]/dd`
      )
    );

    this.locators.set(
      'Organisation details: VAT registration number',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="accordion-with-summary-sections-account-holder"]/dl[1]/div[4]/dd`
      )
    );

    this.locators.set(
      'Organisation details: VAT registration number with country code',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div/div/details[1]/div/dl[2]/div[4]/dd`
      )
    );

    this.locators.set(
      'Organisation Address and contact details: Organisation address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="accordion-with-summary-sections-account-holder"]/dl[2]/div/dd/text()[1]`
      )
    );

    this.locators.set(
      'Organisation Address and contact details: Town or city',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="accordion-with-summary-sections-account-holder"]/dl[2]/div/dd/text()[2]`
      )
    );

    this.locators.set(
      'Organisation Address and contact details: Country',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="accordion-with-summary-sections-account-holder"]/dl[2]/div/dd/text()[3]`
      )
    );

    this.locators.set(
      'Account details',
      new UserFriendlyLocator('xpath', `//*[.=' Account details ']`)
    );

    this.locators.set(
      'Account details: Account type',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/details[2]/div/dl[1]/div[1]/dd[1]`
      )
    );
    this.locators.set(
      'Account details: Account name',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/details[2]/div/dl/div[2]/dd`
      )
    );
    this.locators.set(
      'Account details: Address label',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/details[2]/div/div/div/div/h2`
      )
    );
    this.locators.set(
      'Account details: Billing Address: Address',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/details[2]/div/dl[2]/div[1]/dd`
      )
    );
    this.locators.set(
      'Account details: Billing Address: Town or city',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/details[2]/div/dl[2]/div[2]/dd`
      )
    );
    this.locators.set(
      'Account details: Billing Address: Country',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/details[2]/div/dl[2]/div[3]/dd`
      )
    );
    this.locators.set(
      'Account details: Billing Address: Postal Code or ZIP',
      new UserFriendlyLocator(
        'xpath',
        `//*[@id="main-content"]/div[2]/div/app-check-answers/div[2]/div/details[2]/div/dl[2]/div[4]/dd`
      )
    );
  }

  async waitForMe() {
    if (await browser.waitForAngularEnabled()) {
    } else {
      await this.awaitElement(
        by.xpath(`//button[text()=' Confirm and submit ']`)
      );
    }
  }

  async clickLink(linkText: string) {
    this.webElementActionApply(null, this.locators.get(linkText), 'click');
  }

  async clickButton(buttonText: string) {
    this.webElementActionApply(null, this.locators.get(buttonText), 'click');
  }
}
