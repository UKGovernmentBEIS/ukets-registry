import { KnowsThePage } from './knows-the-page.po';
import { assert } from 'chai';
import moment from 'moment';
import { browser } from 'protractor';
import fsExtra from '../../../node_modules/fs-extra';
import { getMapEntryValueAttributeByKey } from './user-2fa-map-utils';
import * as base32 from 'hi-base32';

/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

export class StepUtil {}

export function getCurrentYear() {
  console.log('Entering getCurrentYear');
  const currentYear = new Date().getFullYear().toString();
  console.log(`Exiting getCurrentYear with return value '${currentYear}'.`);
  return currentYear;
}

export async function clearBrowserData(browserType: any) {
  console.log('Entering clearBrowserData');
  if (!(browserType === undefined || browserType === null)) {
    await Promise.all([
      browser.executeScript(`window.sessionStorage.clear();`),
      browser.executeScript(`window.localStorage.clear();`),
      browser.manage().deleteAllCookies(),
    ]);
  }
  console.log('Exiting clearBrowserData');
}

export function convertBase32DataToAscii(base32data: string) {
  console.log(`Entered convertData with arguments data '${base32data}'.`);
  const convertedValue = base32.decode(base32data, true);
  console.log(`Exiting convertData with convertedValue '${convertedValue}'.`);
  return convertedValue;
}

export function isStrAlphanumericOnly(str: string) {
  return /^[a-zA-Z0-9 ]*$/.test(str);
}

export function convertToBoolean(input: any): boolean | undefined {
  try {
    return JSON.parse(input);
  } catch (e) {
    console.log(`Could not convertToBooleanDataType: '${input}'.`);
    return undefined;
  }
}

export function existsSubstringInArrayElements(substr: string, arr: any) {
  return arr.findIndex((element) => element.includes(substr)) !== -1;
}

export function millisecondsToMinutesRounded(number: any) {
  return Math.round((number / (1000 * 60) + Number.EPSILON) * 100) / 100;
}

export function isSubstringInsensitivelyIncludedIntoArrayItems(
  phrase: string,
  strArr: string[]
) {
  for (let str of strArr) {
    if (str.toLowerCase().includes(phrase.toLowerCase())) {
      return true;
    }
  }
  return false;
}

export function getRegistryRolesByGherkinName(role: string) {
  let registryRoles: string[] = [];

  switch (role) {
    case 'junior admin': {
      registryRoles = ['junior-registry-administrator'];
      break;
    }
    case 'senior admin': {
      registryRoles = ['senior-registry-administrator'];
      break;
    }
    case 'senior admin 2': {
      registryRoles = ['senior-registry-administrator'];
      break;
    }
    case 'read only admin': {
      registryRoles = ['readonly-administrator'];
      break;
    }
    case 'authority_1': {
      registryRoles = ['authority-user', 'authorized-representative'];
      break;
    }
    case 'authority_2': {
      registryRoles = ['authority-user', 'authorized-representative'];
      break;
    }
    case 'authority_3': {
      registryRoles = ['authority-user', 'authorized-representative'];
      break;
    }
    case 'authority_4': {
      registryRoles = ['authority-user', 'authorized-representative'];
      break;
    }
    // enrolled user who is authorized representative
    case 'authorized representative': {
      registryRoles = ['authorized-representative'];
      break;
    }
  }
  return registryRoles;
}

export function printApplicationConsoleLogErrors() {
  browser
    .manage()
    .logs()
    .get('browser')
    .then((browserLogs) => {
      if (browserLogs) {
        browserLogs.forEach((log) => {
          if (log.level.value > 900) {
            console.log(`Browser console error log: '${log.message}'.`);
          }
        });
      }
    });
}

export function jsonEscape(str) {
  return str
    .replace(/\n/g, '\\\\n')
    .replace(/\r/g, '\\\\r')
    .replace(/\t/g, '\\\\t');
}

export function getValidCurrentUrl(currentUrl: string) {
  let pageName = '';
  if (currentUrl.includes('emergency-password-otp-change/email-verify')) {
    pageName = 'emergency-password-otp-change/email-verify';
    console.log(`Setting '${currentUrl}' url page name to '${pageName}'.`);
    currentUrl = pageName;
  } else if (currentUrl.includes('registration/emailConfirm')) {
    pageName = 'registration/emailConfirm';
    console.log(`Setting '${currentUrl}' url page name to '${pageName}'.`);
    currentUrl = pageName;
  } else if (currentUrl.includes('email-change/confirmation')) {
    pageName = 'email-change/confirmation';
    console.log(`Setting '${currentUrl}' url page name to '${pageName}'.`);
    currentUrl = pageName;
  } else if (currentUrl.includes('forgot-password/reset-password/')) {
    pageName = 'forgot-password/reset-password';
    console.log(`Setting '${currentUrl}' url page name to '${pageName}'.`);
    currentUrl = pageName;
  } else if (currentUrl.includes('emergency-otp-change/email-verify')) {
    pageName = 'emergency-otp-change/email-verify';
    console.log(`Setting '${currentUrl}' url page name to '${pageName}'.`);
    currentUrl = pageName;
  } else if (currentUrl.includes('emergency-password-otp-change/init')) {
    pageName = 'emergency-password-otp-change/init';
    console.log(`Setting '${currentUrl}' url page name to '${pageName}'.`);
    currentUrl = pageName;
  } else if (currentUrl.includes('password-change/confirmation')) {
    pageName = 'password-change/confirmation';
    console.log(`Setting '${currentUrl}' url page name to '${pageName}'.`);
    currentUrl = pageName;
  } else if (
    currentUrl.includes('execution=UPDATE_PASSWORD&client_id=account')
  ) {
    pageName = 'enter-your-new-password';
    console.log(`Setting '${currentUrl}' url page name to '${pageName}'.`);
    currentUrl = pageName;
  } else if (currentUrl.includes('required-action?client_id=account&tab_id=')) {
    pageName = 'successfully-changed-credentials';
    console.log(`Setting '${currentUrl}' url page name to '${pageName}'.`);
    currentUrl = pageName;
  } else if (
    currentUrl.includes('auth/realms/uk-ets/login-actions/action-token?key=')
  ) {
    pageName = 'reset-your-credentials';
    console.log(`Setting '${currentUrl}' url page name to '${pageName}'.`);
    currentUrl = pageName;
  } else if (
    currentUrl.includes('auth/realms/uk-ets/login-actions/authenticate')
  ) {
    pageName = 'auth/realms/uk-ets/login-actions/authenticate';
    console.log(`Setting '${currentUrl}' url page name to '${pageName}'.`);
    currentUrl = pageName;
  } else if (currentUrl.includes('?')) {
    // cut postfixes such as "?mode=X" if any
    pageName = currentUrl.substring(0, currentUrl.indexOf('?'));
    console.log(`Setting '${currentUrl}' url page name to '${pageName}'.`);
    currentUrl = pageName;
  } else {
    // console.log(
    //   `currentUrl has valid pattern and it does not need any changes.`
    // );
  }

  // handle dynamically autogenerated sequential postgres identifier
  currentUrl = currentUrl
    // cut the prefix of base url and user postfix
    .replace(browser.baseUrl, '')
    .replace('/user_unregistered@test.com', '')
    .replace('101/0', '[itl-notification-identifier]')
    .replace('GB100000001', '[identifier]')
    // replace user id static data inserted of pattern UKxxxxxxx with [identifier] value
    .replace('UK977538690871', '[identifier]')
    .replace('UK689820232063', '[identifier]')
    .replace('UK88299344979', '[identifier]');

  // replace autogenerated user id values UKxxxxxxx with [identifier] value
  if (KnowsThePage.getUserId() != '') {
    currentUrl = currentUrl.replace(KnowsThePage.getUserId(), '[identifier]');
  }

  currentUrl = currentUrl
    // replace digits with underscores, keep only one underscore and replace underscore with [identifier]
    .replace(new RegExp('[0-9]', 'g'), '_')
    .replace(
      /_/g,
      (
        (i) => (m) =>
          !i++ ? m : ''
      )(0)
    )
    .replace('_', '[identifier]');

  return currentUrl;
}

export function isArrayIncludedToArray(arraySuperset, arraySubset) {
  if (arraySubset.length === 0) {
    return false;
  }
  return arraySubset.every(function isArrayIncluded(item) {
    return arraySuperset.indexOf(item) >= 0;
  });
}

export function removeAllSpacesLineBreaksAndTabsInString(str: string) {
  const mapChangesLinebreaks = new Map().set('\n', '');
  const mapChangesSpaces = new Map().set(' ', '');
  const mapChangesTabs = new Map().set('\t', '');

  mapChangesLinebreaks.forEach((value: string, key: string) => {
    str = str.split(key).join(value);
  });

  mapChangesSpaces.forEach((value: string, key: string) => {
    str = str.split(key).join(value);
  });

  mapChangesTabs.forEach((value: string, key: string) => {
    str = str.split(key).join(value);
  });

  // remove =CRLF
  str = str.replace(/=[\n\r]/g, '');

  // remove CRLF
  str = str.replace(/[\n\r]/g, '');

  return str;
}

export function replaceAllInString(
  str: string,
  replaceValues: Map<string, string>
) {
  replaceValues.forEach((value: string, key: string) => {
    str = str.split(key).join(value);
  });
  return str;
}

export function delay(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export function setDynamicGherkinValueToActualData(value: string) {
  // autogenerated value cannot be handled at BDD level, so the snippet below handles these values:
  // if the value is a relative date/datetime instead of hardcoded value
  if (value.includes('correct requestId')) {
    value = KnowsThePage.getRequestId();
  }
  if (value.includes('correct userId')) {
    value = KnowsThePage.getUserId();
  }
  if (value.includes('correct otp for user')) {
    // value = KnowsThePage.getVerificationOtpCode();
    const username = value.split(`correct otp for user `)[1];
    value = getMapEntryValueAttributeByKey(
      KnowsThePage.users2fa,
      username,
      'otp'
    );
  }
  if (value.includes('incorrect requestId')) {
    // incorrect request id should have the value of the correct one minus one
    // because technically this a common edge case defected functional area
    value = String(+KnowsThePage.getRequestId() - 1);
  }
  if (value.includes('email address name by current datetime')) {
    value =
      `emailprefix` +
      getCurrentMomentByFormat(0, `MM_DD_YYYY_h_mm_ss`) +
      `@email_infix.postfix`;
  }
  return value;
}

export function setMapElement(
  map: Map<any, any>,
  sendKey: string,
  sendValue: string
) {
  map.set(sendKey, sendValue);
}

export function getMapAttributeByAttribute(
  map: Map<any, any>,
  getAttribute: 'size' | 'key' | 'value',
  getByMerit: string,
  getByType: 'key' | 'value'
) {
  let returnItem: string;
  // return key by specific value
  if (getAttribute === 'key' && getByType === 'value') {
    returnItem = String(
      [...map.entries()]
        .filter(({ 1: value }) => value === getByMerit)
        .map(([key]) => key)
    );
    return returnItem;
  }
  // return value by specific key
  else if (getAttribute === 'value' && getByType === 'key') {
    for (const [key, value] of map) {
      if (key === getByMerit) {
        // return the value if found, return empty string if not found
        return value;
      }
    }
  } else {
    assert.fail(`Extend getMapAttributeByAttribute function.`);
  }
  return;
}

export function getISODateFormat(pattern: string) {
  let returnValue: string;

  // US English uses month-day-year order - example: 01/31/2020
  if (pattern === 'US English') {
    returnValue = 'en-US';
  }
  // British English uses day-month-year order - example: 31/01/2020
  else if (pattern === 'British English') {
    returnValue = 'en-GB';
  }
  // raise error
  else {
    assert.fail(
      `Failure in getISODateFormat method: Use a valid argument or extend this method.`
    );
  }

  // console.log(`Returning value '${returnValue}' from getISODateFormat method.`);
  return returnValue;
}

export function getCurrentMomentByFormat(
  subtractDays: number,
  formatPattern: string
) {
  // formatPattern argument value examples:
  // 'MMMM Do YYYY, h:mm:ss a' => returns e.g. February 25th 2020, 3:27:28 pm
  // 'dddd'                    => returns e.g. Tuesday
  // 'MMM Do YY'               => returns e.g. Feb 25th 20
  // 'YYYY [something] YYYY'   => returns e.g. 2020 something 2020
  //  empty string argument    => returns e.g. 2020-02-25T15:29:32+02:00
  // 'DD MMM YY, hh:mm A'      => returns e.g. 06 Apr 20, 05:43 PM
  // 'DD MMM YYYY'             => returns e.g. 06 Apr 2020

  let returnValue = moment()
    .subtract(subtractDays, 'days')
    .format(formatPattern);

  // trim the first character if it is zero:
  if (returnValue.charAt(0) === '0') {
    returnValue = returnValue.substr(1);
  }
  return returnValue;
}

export function getRandomString(
  numberOfDigits: number,
  charactersType:
    | 'all numbers'
    | 'all alphabet characters'
    | 'all alphabet uppercase characters and all numbers'
) {
  let str = '';
  // append to string
  for (let i = 0; i < numberOfDigits; i++) {
    let random = '';
    // numbers
    if (charactersType === 'all alphabet characters') {
      random = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
    }
    // characters
    else if (charactersType === 'all numbers') {
      random = '0123456789';
    }
    // uppercase characters and numbers
    else if (
      charactersType === 'all alphabet uppercase characters and all numbers'
    ) {
      random = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    } else {
      console.error(`Extend getRandomString function.`);
    }
    // selection based on a random index
    str = str + random.charAt(Math.floor(Math.random() * random.length));
  }
  return str;
}

export function getRelativeDate(datePhrase: string) {
  const dateTimeParsedElements = String(datePhrase).split('-');
  const currentDateTime = new Date();

  dateTimeParsedElements.forEach((element) => {
    if (element.includes('year') && element.includes('minus')) {
      currentDateTime.setFullYear(
        currentDateTime.getFullYear() -
          Number(/(?:minus)(.*)(?:year)/.exec(element)[1].trim())
      );
    }
    if (element.includes('year') && element.includes('plus')) {
      currentDateTime.setFullYear(
        currentDateTime.getFullYear() +
          Number(/(?:plus)(.*)(?:year)/.exec(element)[1].trim())
      );
    }
    if (element.includes('month') && element.includes('minus')) {
      currentDateTime.setMonth(
        currentDateTime.getMonth() -
          Number(/(?:plus)(.*)(?:month)/.exec(element)[1].trim())
      );
    }
    if (element.includes('month') && element.includes('plus')) {
      currentDateTime.setMonth(
        currentDateTime.getMonth() +
          Number(/(?:plus)(.*)(?:month)/.exec(element)[1].trim())
      );
    }
    if (element.includes('day') && element.includes('minus')) {
      currentDateTime.setDate(
        currentDateTime.getDate() -
          Number(/(?:plus)(.*)(?:day)/.exec(element)[1].trim())
      );
    }
    if (element.includes('day') && element.includes('plus')) {
      currentDateTime.setDate(
        currentDateTime.getDate() +
          Number(/(?:plus)(.*)(?:day)/.exec(element)[1].trim())
      );
    }
  });

  // example format to return: Wed Nov 28 1979 13:17:52 GMT+0200 (Eastern European Standard Time)
  let returnValue = null;

  if (datePhrase.includes('current date short format')) {
    returnValue = new Date().toLocaleDateString(
      getISODateFormat('British English')
    );
  } else {
    assert.fail(
      'Invalid returnValue: ' +
        returnValue +
        '. Use a valid one or extend this method.'
    );
  }
  return returnValue;
}
