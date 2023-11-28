import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import {
  Account,
  TrustedAccount,
  TrustedAccountStatus,
  UserDefinedAccountParts,
} from '@shared/model/account';

import { ErrorDetail } from '@shared/error-summary';
import { AddTrustedAccount } from '@trusted-account-list/model/add-trusted-account';
import {
  UkRegistryValidators,
  UkValidationMessageHandler,
} from '@shared/validation';

@Component({
  selector: 'app-add-account',
  templateUrl: './add-account.component.html',
})
export class AddAccountComponent implements OnInit {
  @Input()
  account: Account;

  @Input()
  userDefinedAccountParts: UserDefinedAccountParts;

  @Input()
  trustedAccountDescription: string;

  @Output()
  readonly addTrustedAccountEmitter = new EventEmitter<AddTrustedAccount>();

  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  trustedAccounts: TrustedAccount[];
  validationErrorMessage: ValidationErrors = {};
  genericValidator: UkValidationMessageHandler = new UkValidationMessageHandler(
    {}
  );

  formGroup: UntypedFormGroup = this.formBuilder.group(
    {
      accountDescription: [
        '',
        {
          validators: [Validators.required, Validators.minLength(3)],
          updateOn: 'change',
        },
      ],
      userDefinedCountryCode: [
        '',
        {
          validators: Validators.required,
        },
      ],
      userDefinedAccountType: [
        '',
        {
          validators: Validators.required,
        },
      ],
      userDefinedAccountId: [
        '',
        {
          validators: Validators.required,
        },
      ],
      userDefinedPeriod: [
        '',
        {
          validators: Validators.required,
        },
      ],
      userDefinedCheckDigits: [
        '',
        {
          validators: Validators.required,
        },
      ],
    },
    { updateOn: 'submit' }
  );

  constructor(protected formBuilder: UntypedFormBuilder) {}

  ngOnInit() {
    this.trustedAccounts = this.account
      ? this.account.trustedAccountList?.results
      : [];
    if (this.userDefinedAccountParts) {
      this.formGroup
        .get('userDefinedCountryCode')
        .setValue(this.userDefinedAccountParts.userDefinedCountryCode);
      this.formGroup
        .get('userDefinedAccountType')
        .setValue(this.userDefinedAccountParts.userDefinedAccountType);
      this.formGroup
        .get('userDefinedAccountId')
        .setValue(this.userDefinedAccountParts.userDefinedAccountId);
      this.formGroup
        .get('userDefinedPeriod')
        .setValue(this.userDefinedAccountParts.userDefinedPeriod);
      this.formGroup
        .get('userDefinedCheckDigits')
        .setValue(this.userDefinedAccountParts.userDefinedCheckDigits);
    }
    if (this.trustedAccountDescription) {
      this.formGroup
        .get('accountDescription')
        .setValue(this.trustedAccountDescription);
    }
  }

  getTrustedAccountsOfSameAccountHolder() {
    return this.trustedAccounts?.filter((ta) => ta.underSameAccountHolder);
  }

  getOtherActivatedTrustedAccounts() {
    return this.trustedAccounts?.filter(
      (ta) => ta.status === TrustedAccountStatus.ACTIVE
    );
  }

  getOtherPendingTrustedAccounts() {
    return this.trustedAccounts?.filter(
      (ta) =>
        !ta.underSameAccountHolder &&
        ta.status !== TrustedAccountStatus.ACTIVE &&
        ta.status !== TrustedAccountStatus.REJECTED
    );
  }

  onSubmit() {
    this.formGroup.setValidators([
      Validators.required,
      UkRegistryValidators.isIncludedInTrustedAccountList(
        [
          'userDefinedCountryCode',
          'userDefinedAccountType',
          'userDefinedAccountId',
          'userDefinedPeriod',
          'userDefinedCheckDigits',
        ],
        this.getOtherActivatedTrustedAccounts(),
        this.getTrustedAccountsOfSameAccountHolder(),
        this.getOtherPendingTrustedAccounts()
      ),
    ]);
    this.formGroup.updateValueAndValidity();
    this.formGroup.markAsTouched();
    if (this.formGroup.valid) {
      this.onSuccess();
    } else if (this.formHasErrors()) {
      this.onError();
    }
  }

  onSuccess() {
    const countryCode = this.formGroup.get('userDefinedCountryCode').value;
    const accountType = this.formGroup.get('userDefinedAccountType').value;
    const accountId = this.formGroup.get('userDefinedAccountId').value;
    const definedPeriod = this.formGroup.get('userDefinedPeriod').value;
    const checkDigits = this.formGroup.get('userDefinedCheckDigits').value;

    const addTrustedAccount: AddTrustedAccount = {
      account: {
        userDefinedCountryCode: countryCode.toUpperCase(),
        userDefinedAccountId: accountId,
        userDefinedAccountType: accountType,
        userDefinedPeriod: definedPeriod,
        userDefinedCheckDigits: checkDigits,
      },
      description: this.formGroup.get('accountDescription').value,
    };
    this.addTrustedAccountEmitter.emit(addTrustedAccount);
  }

  onError() {
    const errorMessageObj: any = {
      trustedAccountErrorMessage: null,
      accountDescription: null,
    };

    if (this.trustedAccountNumberHasRequiredErrors()) {
      errorMessageObj.trustedAccountErrorMessage =
        'Account Number is mandatory.';
    }
    if (this.formGroup.errors?.isInOtherTrustedAccounts) {
      errorMessageObj.trustedAccountErrorMessage =
        'The selected account has already been added to the Trusted Account List.';
    }
    if (this.formGroup.errors?.isInSameAccountHolderTrustedAccounts) {
      errorMessageObj.trustedAccountErrorMessage =
        'The specified account is under the same account holder and therefore part of the trusted account list.';
    }
    if (this.formGroup.errors?.isInPendingTrustedAccounts) {
      errorMessageObj.trustedAccountErrorMessage =
        'The specified account is part of a pending update request.';
    }
    if (this.formGroup.errors?.isInPendingTrustedAccounts) {
      errorMessageObj.trustedAccountErrorMessage =
        'The specified account is part of a pending update request.';
    }
    if (this.formGroup.get('accountDescription').errors) {
      errorMessageObj.accountDescription =
        this.getDescriptionValidationMessage();
    }

    this.generateErrorMessage(errorMessageObj);

    this.errorDetails.emit(
      this.genericValidator.mapMessagesToErrorDetails(
        this.validationErrorMessage
      )
    );
  }

  generateErrorMessage(value: any) {
    Object.assign(this.validationErrorMessage, value);
  }

  private getDescriptionValidationMessage(): string {
    if (this.formGroup.get('accountDescription').errors.required) {
      return 'Description is mandatory.';
    }

    if (this.formGroup.get('accountDescription').errors.minlength) {
      return 'Enter at least 3 characters in the "Description"';
    }
  }

  private formHasErrors(): boolean {
    return (
      this.formGroup.errors !== null ||
      this.formGroup.get('accountDescription').errors !== null ||
      this.trustedAccountNumberHasRequiredErrors()
    );
  }

  private trustedAccountNumberHasRequiredErrors(): boolean {
    return (
      this.formGroup.get('userDefinedCountryCode')?.errors?.required ||
      this.formGroup.get('userDefinedAccountType')?.errors?.required ||
      this.formGroup.get('userDefinedAccountId')?.errors?.required ||
      this.formGroup.get('userDefinedPeriod')?.errors?.required ||
      this.formGroup.get('userDefinedCheckDigits')?.errors?.required
    );
  }
}
