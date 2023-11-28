import {
  AbstractControl,
  UntypedFormGroup,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';
import { dateIsValid, daysInMonth, emptyProp } from '../shared.util';
import {
  ARAccessRights,
  AuthorisedRepresentative,
  TrustedAccount,
} from '@shared/model/account';
import { AccountAllocationStatus } from '@allocation-status/model';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import dayjs from 'dayjs';

export class UkRegistryValidators {
  /**
   * Verifies that an input string is a valid positive number without decimals
   * @param control
   *    '{ invalid:true}' when the string can not be converted to number
   *    '{ invalid:true}' when the string is negative or zero
   *    '{ invalid:true}' when the string has decimals
   *
   */
  static isPositiveNumberWithoutDecimals(
    control: AbstractControl
  ): ValidationErrors | null {
    // user required validator to catch empty values. This validator
    if (!control.value || control.value === '') {
      return null;
    }
    const positiveNumber: number = +control.value;
    if (Number.isNaN(positiveNumber)) {
      return { invalid: true };
    }
    if (positiveNumber <= 0) {
      return { invalid: true };
    }
    if (positiveNumber % 1) {
      return { invalid: true };
    }
    return null;
  }

  /**
   * Verifies that the entered year is after
   * @param year e.g 2021
   * @returns
   *    '{ invalid:true}' when the year is invalid
   *    '{yearAfter:true}' when the form control year is not after the validators param year
   */
  static yearShouldBeAfter(year: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // user required validator to catch empty values. This validator
      if (!control.value || control.value === '') {
        return null;
      }
      const controlValueDate = new Date(control.value);
      // invalid date
      if (isNaN(controlValueDate.getTime())) {
        return { invalid: true };
      }
      return control.value >= year ? null : { yearAfter: true };
    };
  }

  /**
   * Verifies that the second field is greater or equal from the first
   * @param firstField e.g 'firstYear'
   * @param secondField e.g 'lastYear'
   * @param year e.g. 2021
   * @returns
   *    '{ invalid:true}' when the year is invalid
   *    '{yearAfter:true}' when the form control year is not after the validators param year
   *    '{notGreaterOrEqualThan:true}' when the form control firstField is not before the form control secondField
   */
  static greaterOrEqualThan(
    firstField: string,
    secondField: string,
    year: number
  ): ValidatorFn {
    return (fg: UntypedFormGroup): ValidationErrors | null => {
      fg.controls[firstField].setErrors(null);
      fg.controls[secondField].setErrors(null);

      const firstFieldValue = fg.controls[firstField].value;

      if (!firstFieldValue || firstFieldValue === '') {
        fg.controls[firstField].setErrors({ required: true });
        return { required: true };
      }

      const secondFieldValue = fg.controls[secondField].value;
      let secondFieldEmpty = false;
      if (!secondFieldValue || secondFieldValue === '') {
        secondFieldEmpty = true;
      }

      let secondFieldInvalidDate = false;
      if (!secondFieldEmpty) {
        const secondFieldValueDate = new Date(secondFieldValue);
        if (isNaN(secondFieldValueDate.getTime())) {
          fg.controls[secondField].setErrors({ invalid: true });
          secondFieldInvalidDate = true;
        }
      }

      const firstFieldValueDate = new Date(firstFieldValue);
      if (isNaN(firstFieldValueDate.getTime())) {
        fg.controls[firstField].setErrors({ invalid: true });
      }
      if (secondFieldInvalidDate || isNaN(firstFieldValueDate.getTime())) {
        return { invalid: true };
      }

      if (Number(firstFieldValue) < year) {
        fg.controls[firstField].setErrors({ yearAfter: true });
      }
      let secondFieldYearAfter = false;
      if (!secondFieldEmpty && Number(secondFieldValue) < year) {
        fg.controls[secondField].setErrors({ yearAfter: true });
        secondFieldYearAfter = true;
      }
      if (Number(firstFieldValue) < year || secondFieldYearAfter) {
        return {
          yearAfter: true,
        };
      }

      if (
        !secondFieldEmpty &&
        Number(secondFieldValue) < Number(firstFieldValue)
      ) {
        fg.controls[secondField].setErrors({ notGreaterOrEqualThan: true });
        return {
          notGreaterOrEqualThan: true,
        };
      }
      return null;
    };
  }

  static doFirstAndLastYearFieldChecks(
    firstYear: string,
    lastYear: string,
    minYear: number,
    maxYear: number
  ): ValidatorFn {
    return (fg: UntypedFormGroup): ValidationErrors | null => {
      fg.controls[firstYear].setErrors(null);
      fg.controls[lastYear].setErrors(null);
      const firstYearValue = fg.controls[firstYear].value;
      const lastYearValue = fg.controls[lastYear].value;
      const firstYearPresent = firstYearValue && firstYearValue !== '';
      const lastYearPresent = lastYearValue && lastYearValue !== '';
      if (!firstYearPresent) {
        return getTheProperMessageError(firstYear, { required: true });
      }
      if (!this.containsOnlyDigits(firstYearValue)) {
        return getTheProperMessageError(firstYear, { noLetters: true });
      }
      const yearBoundsResponse = this.doDateBoundsChecks(
        firstYearValue,
        minYear,
        maxYear
      );
      if (yearBoundsResponse !== null) {
        return getTheProperMessageError(firstYear, yearBoundsResponse);
      }
      if (lastYearPresent) {
        if (!this.containsOnlyDigits(lastYearValue)) {
          return getTheProperMessageError(lastYear, { noLetters: true });
        }
        const yearBoundsResponse = this.doDateBoundsChecks(
          lastYearValue,
          minYear,
          maxYear
        );
        if (yearBoundsResponse !== null) {
          return getTheProperMessageError(lastYear, yearBoundsResponse);
        }
        if (
          this.isFirstDateGreaterThanTheSecondOne(firstYearValue, lastYearValue)
        ) {
          return getTheProperMessageError(lastYear, {
            notGreaterOrEqualThan: true,
          });
        }
      }
      function getTheProperMessageError(f: string, error: any) {
        fg.controls[f].setErrors(error);
        return error;
      }
    };
  }

  static containsOnlyDigits(val: string) {
    return /^\d{4}$/.test(val);
  }
  static isFirstDateGreaterThanTheSecondOne(year1: string, year2: string) {
    return Number(year1) > Number(year2);
  }
  static doDateBoundsChecks(val: string, min: number, max: number) {
    if (Number(val) < min) {
      return { yearAfter: true };
    }
    if (Number(val) >= max) {
      return { yearBefore: true };
    }
    return null;
  }

  /**
   * Verifies that two  values match. Used for email verification for now
   * @param emailField this is the formControl name of the email field within the same formGroup
   * returns '{emailNotMatch: true}' when the two emails do not match
   */
  static emailVerificationMatcher(emailField: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.parent) {
        return null;
      }
      const emailControl = control.parent.get(emailField);
      const confirmControl = control;
      if (
        confirmControl.value === '' ||
        emailControl.value === confirmControl.value
      ) {
        return null;
      }
      return { emailNotMatch: true };
    };
  }

  /**
   * Verifies that the field is empty, or does not have value equal to sameValue
   * @param sameValue The value that the field is not permitted to have
   */
  static shouldBeEmptyOrNotEqual(sameValue: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.parent) {
        return null;
      }
      if (control.value === null || control.value === '') {
        return null;
      }
      const sameValueControl = control.parent.get(sameValue);
      return control.value === sameValueControl.value
        ? { notSameValue: true }
        : null;
    };
  }

  /**
   * Verifies that the field does not have value equal to notPermittedValue
   * @param notPermittedValue The value that the field is not permitted to have
   */
  static shouldNotBeEqual(notPermittedValue: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return control.value === notPermittedValue
        ? { notPermittedValue: true }
        : null;
    };
  }

  /**
   * Gived a formgroup this one checks that fields given are not both empty or both field
   * @param firstField name of the first control
   * @param secondField name of second control
   *    * @returns
   *    '{ bothEmpty:true}' when both form fields are empty
   *    '{ bothFilled:true}' when both form fields are filled
   */
  static fieldsBothEmptyOrBothFieldError(
    firstField: string,
    secondField: string
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const field1 = control.get(firstField).value;
      const field2 = control.get(secondField).value;
      if (field1 === '' && field2 === '') {
        return { bothEmpty: true };
      }
      if (field1 !== '' && field2 !== '') {
        return { bothFilled: true };
      }
      return null;
    };
  }

  /**
   * Verifies that the control value is not empty or its fields are all filled.
   * @param control The form control
   */
  static allFieldsRequired(control: AbstractControl): ValidationErrors | null {
    return !control.value || emptyProp(control.value)
      ? { allFieldsRequired: true }
      : null;
  }

  /**
   * Verifies that the number of the authorised representatives who have other than read-only access right
   * is smaller or equal than the {maxNumberOfARs} (default 5)
   * @param currentAR The value of the current AR that will be added/edited
   * @param authorisedRepresentatives The list of all ARs added so far
   * @param maxNumberOfARs The max number of ARs that is allowed
   */
  static numberOfARsNonReadOnlyAccess(
    currentAR: AuthorisedRepresentative,
    authorisedRepresentatives: AuthorisedRepresentative[],
    maxNumberOfARs: number
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value === '') {
        return null;
      }

      let numberOfNonReadAccess = 0;
      if (control.value !== ARAccessRights.READ_ONLY) {
        authorisedRepresentatives.forEach((ar) => {
          if (
            ar.right !== ARAccessRights.READ_ONLY &&
            ar.urid !== currentAR.urid
          ) {
            numberOfNonReadAccess++;
          }
        });
      }

      return numberOfNonReadAccess >= maxNumberOfARs
        ? { numberOfARsNonReadOnlyAccess: true }
        : null;
    };
  }

  /**
   * Verifies that all the text inputs of the activation code are not empty.
   * @param activationCodeInputs An array of input names of the registry activation code.
   */
  static registryActivationCodeAllInputsProvided(
    activationCodeInputs: Array<string>
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let isEmpty = false;
      activationCodeInputs.forEach((input) => {
        if (control.get(input).value === '') {
          isEmpty = true;
        }
      });
      return isEmpty ? { emptyActivationCode: true } : null;
    };
  }

  /**
   * Verifies that the account input is not already included in the trusted account list
   * or in the requested trusted account list
   * @param inputs The form input fields of the account
   * @param otherTrustedAccounts The list of trusted accounts with not the same account holder
   * @param sameAccountHolderTrustedAccounts The list of trusted accounts with the same account holder
   * @param pendingTrustedAccounts The list of trusted accounts with status pending
   */
  static isIncludedInTrustedAccountList(
    inputs: string[],
    otherTrustedAccounts: TrustedAccount[],
    sameAccountHolderTrustedAccounts: TrustedAccount[],
    pendingTrustedAccounts: TrustedAccount[]
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let accountFullIdentifier = '';
      inputs.forEach((input) => {
        if (control.value[input] !== '') {
          accountFullIdentifier += control.value[input] + '-';
        }
      });
      const account = accountFullIdentifier.substring(
        0,
        accountFullIdentifier.lastIndexOf('-')
      );
      const isIncludedInOther = otherTrustedAccounts
        .map((r) => {
          return r.accountFullIdentifier;
        })
        .includes(account);
      const isIncludedInSameHolder = sameAccountHolderTrustedAccounts
        .map((r) => {
          return r.accountFullIdentifier;
        })
        .includes(account);
      const isIncludedInPending = pendingTrustedAccounts
        .map((r) => {
          return r.accountFullIdentifier;
        })
        .includes(account);

      return isIncludedInOther
        ? { isInOtherTrustedAccounts: true }
        : isIncludedInSameHolder
        ? { isInSameAccountHolderTrustedAccounts: true }
        : isIncludedInPending
        ? { isInPendingTrustedAccounts: true }
        : null;
    };
  }

  /**
   * Verifies that the current changed allocation status is different from the initial loaded allocation status.
   * @param previousStatus The initial loaded allocation status
   * @param calculateChangedStatus The function which calculates the status change
   */
  static allocationStatusShouldBeChanged(
    previousStatus: AccountAllocationStatus,
    calculateChangedStatus: (
      currentStatus: AccountAllocationStatus,
      previousStatus: AccountAllocationStatus
    ) => AccountAllocationStatus
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const changedStatus: AccountAllocationStatus = calculateChangedStatus(
        control.value,
        previousStatus
      );
      return changedStatus
        ? null
        : {
            changeRequired: true,
          };
    };
  }

  /**
   * @description
   * Validates for equality a group of form control(s) values.
   *
   * @usageNotes
   *
   * ### Validate that all field values are equal
   *
   * ```typescript
   * FormGroup fg = formbuilder.group(
   * {
   *   controlA: new FormControl(''),
   *   controlB: new FormControl(''),
   * },
   * {
   *   validators: [UkRegistryValidators.equalValidator],
   * }
   *
   * console.log(control.errors); // {equal: true}
   * ```
   *
   * @returns An error map with the `equal` property
   * if the validation check fails, otherwise `null`.
   *
   * @see `updateValueAndValidity()`
   *
   */
  static equalValidator: ValidatorFn = (control) => {
    const [first, ...rest] = Object.keys(control.value || {});
    const valid = rest.every((v) => control.value[v] === control.value[first]);
    return valid ? null : { equal: true };
  };

  /**
   * Verifies that the changed input does not contain value with only white spaces.
   * @param errorKey The key error to return
   */
  static checkForOnlyWhiteSpaces(errorKey: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      let isEmpty = null;
      if (control.value && control.value.toString().trim() === '') {
        isEmpty = {};
        isEmpty[errorKey] = true;
      }
      return isEmpty;
    };
  }

  /**
   * Verifies that authorised representatives do not exceed the maximum number.
   * @param activeARs The number of active authorized representatives
   * @param pendingARAddRequests The number of pending requests for adding authorised representatives into the account
   * @param maxARsPermitted The maximum number of authorised representatives permitted
   */
  static checkForMaxARsExceeded(
    activeARs: number,
    pendingARAddRequests: number,
    maxARsPermitted: number
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // eslint-disable-next-line
      return control.value === AuthorisedRepresentativesUpdateType.ADD &&
        activeARs + pendingARAddRequests >= maxARsPermitted
        ? { maxARsExceeded: true }
        : null;
    };
  }

  /**
   * Verifies that the current changed exclusion status is different from the existing exclusion status.
   */
  static exclusionStatusShouldBeChanged(
    notPermittedValue: boolean
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return control.value === notPermittedValue
        ? { notPermittedValue: true }
        : null;
    };
  }

  static isValidYear(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // empty filter should not be validated
      if (!control.value || control.value === '') {
        return null;
      }
      // year should have 4 digits
      if (control.value?.length != 4) {
        return { invalidYear: true };
      }

      // check that input is a year
      const controlValueDate = new Date(control.value);
      if (isNaN(controlValueDate.getTime())) {
        return { invalidYear: true };
      }
      return null;
    };
  }

  static numberShouldBeBetween(from: number, to: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // empty filter should not be validated
      if (!control.value || control.value === '') {
        return null;
      }

      if (isNaN(control.value) || control.value < from || control.value > to) {
        return { invalidRange: true };
      }
      return null;
    };
  }

  static isValidDayOfMonth(d: number, m: number, y?: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (m > 0 && m <= 12 && d > 0 && d <= daysInMonth(m, y)) {
        return null;
      }
      return { invalidDayOfMonth: true };
    };
  }

  static dateFormatValidator(): ValidatorFn {
    return (control: AbstractControl) => {
      if (!control.value) {
        return null;
      } else {
        const validFormat: boolean = dateIsValid(control.value, 'YYYY-MM-DD');
        return validFormat ? null : { ngbDate: true };
      }
    };
  }

  static dateRangeValidator(fromField: string, toField: string): ValidatorFn {
    return (control: AbstractControl) => {
      if (
        (control.get(fromField).value === '' ||
          control.get(fromField).value === null) &&
        (control.get(toField).value === '' ||
          control.get(toField).value === null)
      ) {
        return null;
      } else {
        const to = dayjs(control.get(toField).value);
        const from = dayjs(control.get(fromField).value);
        const invalid = to.isBefore(from);
        return invalid ? { invalidDateRange: true } : null;
      }
    };
  }
}
