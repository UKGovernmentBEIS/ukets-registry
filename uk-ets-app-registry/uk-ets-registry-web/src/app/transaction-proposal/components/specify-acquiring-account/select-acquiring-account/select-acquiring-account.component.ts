import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import {
  AccountInfo,
  AcquiringAccountInfo,
  CandidateAcquiringAccounts,
  ProposedTransactionType,
} from '@shared/model/transaction';
import { Account, UserDefinedAccountParts } from '@shared/model/account';

export const USER_DEFINED_ACCOUNT = 'user-defined-account';

@Component({
  selector: 'app-specify-acquiring-account',
  templateUrl: './select-acquiring-account.component.html',
})
export class SelectAcquiringAccountComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  transactionType: ProposedTransactionType;

  @Input()
  trustedAccountsResult: CandidateAcquiringAccounts;

  @Input()
  userDefinedAccountParts: UserDefinedAccountParts;

  @Input()
  selectedIdentifier: AccountInfo;

  @Input()
  isEtsTransaction: boolean;

  transfersOutsideTalAllowed: boolean;

  _transferringAccount: Account;

  @Output()
  readonly selectedAccountInfoEmitter = new EventEmitter<AcquiringAccountInfo>();

  @Output()
  readonly userDefinedAccountPartsEmitter = new EventEmitter<UserDefinedAccountParts>();

  USER_DEFINED_ACCOUNT: string = USER_DEFINED_ACCOUNT;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  @Input()
  set transferringAccount(transferringAccount: Account) {
    this._transferringAccount = transferringAccount;
    this.transfersOutsideTalAllowed =
      transferringAccount.trustedAccountListRules.rule2;
  }

  ngOnInit() {
    super.ngOnInit();

    if (this.selectedIdentifier) {
      this.acquiringAccountIdentifierControl.setValue(
        this.selectedIdentifier.fullIdentifier.toString()
      );
    }
    if (!this.showTrustedAccountOptions()) {
      this.acquiringAccountIdentifierControl.setValue(USER_DEFINED_ACCOUNT);
    }
    if (this.userDefinedAccountParts) {
      this.acquiringAccountIdentifierControl.setValue(USER_DEFINED_ACCOUNT);
      this.userDefinedCountryCodeControl.setValue(
        this.userDefinedAccountParts.userDefinedCountryCode
      );
      this.userDefinedAccountTypeControl.setValue(
        this.userDefinedAccountParts.userDefinedAccountType
      );
      this.userDefinedAccountIdControl.setValue(
        this.userDefinedAccountParts.userDefinedAccountId
      );
      this.userDefinedPeriodControl.setValue(
        this.userDefinedAccountParts.userDefinedPeriod
      );
      this.userDefinedCheckDigitsControl.setValue(
        this.userDefinedAccountParts.userDefinedCheckDigits
      );
    }
    this.acquiringAccountIdentifierControl.valueChanges.subscribe((value) => {
      if (value !== USER_DEFINED_ACCOUNT) {
        this.clearUserDefinedValuesAndValidators();
      }
    });
  }

  protected getFormModel(): any {
    return {
      acquiringAccountIdentifier: [
        '',
        { validators: Validators.required, updateOn: 'change' },
      ],
      userDefinedCountryCode: [''],
      userDefinedAccountType: [''],
      userDefinedAccountId: [''],
      userDefinedPeriod: [''],
      userDefinedCheckDigits: [''],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      acquiringAccountIdentifier: {
        required: 'Enter the account number.',
      },
    };
  }

  get acquiringAccountIdentifierControl() {
    return this.formGroup.get('acquiringAccountIdentifier');
  }

  get userDefinedCountryCodeControl() {
    return this.formGroup.get('userDefinedCountryCode');
  }

  get userDefinedAccountTypeControl() {
    return this.formGroup.get('userDefinedAccountType');
  }

  get userDefinedAccountIdControl() {
    return this.formGroup.get('userDefinedAccountId');
  }

  get userDefinedPeriodControl() {
    return this.formGroup.get('userDefinedPeriod');
  }

  get userDefinedCheckDigitsControl() {
    return this.formGroup.get('userDefinedCheckDigits');
  }

  private clearUserDefinedValuesAndValidators() {
    this.userDefinedCountryCodeControl.reset();
    this.userDefinedAccountTypeControl.reset();
    this.userDefinedAccountIdControl.reset();
    this.userDefinedPeriodControl.reset();
    this.userDefinedCheckDigitsControl.reset();
  }

  showTrustedAccountOptions(): boolean {
    return (
      this.trustedAccountsResult &&
      (this.atLeastOneTrustedAccountUnderTheSameAccountHolder() ||
        this.atLeastOneOtherTrustedAccount())
    );
  }

  atLeastOneTrustedAccountUnderTheSameAccountHolder(): boolean {
    return (
      this.trustedAccountsResult.trustedAccountsUnderTheSameHolder &&
      this.trustedAccountsResult.trustedAccountsUnderTheSameHolder.length > 0
    );
  }

  atLeastOneOtherTrustedAccount(): boolean {
    return (
      this.trustedAccountsResult &&
      this.trustedAccountsResult.otherTrustedAccounts &&
      this.trustedAccountsResult.otherTrustedAccounts.length > 0
    );
  }

  protected doSubmit() {
    if (this.acquiringAccountIdentifierControl.value === USER_DEFINED_ACCOUNT) {
      this.userDefinedAccountPartsEmitter.emit({
        userDefinedCountryCode:
          this.userDefinedCountryCodeControl.value.toUpperCase(),
        userDefinedAccountId: this.userDefinedAccountIdControl.value,
        userDefinedAccountType: this.userDefinedAccountTypeControl.value,
        userDefinedPeriod: this.userDefinedPeriodControl.value,
        userDefinedCheckDigits: this.userDefinedCheckDigitsControl.value,
      });
    } else {
      /**
       * This is a common control so look at both trusted list for the selected identifier.
       * The same identifier cannot exist in both lists
       */
      const selectedTrustedAcquiringAccountInfo: AcquiringAccountInfo =
        this.findTrustedAcquiringAccountInfo([
          ...this.trustedAccountsResult.trustedAccountsUnderTheSameHolder,
          ...this.trustedAccountsResult.otherTrustedAccounts,
        ]);
      this.selectedAccountInfoEmitter.emit(selectedTrustedAcquiringAccountInfo);
    }
  }

  findTrustedAcquiringAccountInfo(
    input: AcquiringAccountInfo[]
  ): AcquiringAccountInfo {
    return input.find(
      (info) =>
        info.fullIdentifier.toString() ===
        this.acquiringAccountIdentifierControl.value
    );
  }

  getAccountCode(): string {
    return this.isEtsTransaction ? 'UK' : 'GB';
  }
}
