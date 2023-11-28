import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, ValidatorFn, Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  AccountTransferType,
  SelectedAccountTransferType,
} from '@account-transfer/model';
import { AccountHolderType } from '@shared/model/account';
import { HttpParams } from '@angular/common/http';
import { ErrorDetail } from '@shared/error-summary';
import { accountHolderResultFormatter } from '@shared/shared.util';
import { AccountHolderTypeAheadSearchResult } from '@registry-web/account-shared/model';

@Component({
  selector: 'app-select-account-transfer-acquiring-account-holder',
  templateUrl:
    './select-account-transfer-acquiring-account-holder.component.html',
  styles: [],
})
export class SelectAccountTransferAcquiringAccountHolderComponent
  extends UkFormComponent
  implements OnInit
{
  requestParams: HttpParams;

  @Input()
  updateType: AccountTransferType;
  @Input()
  searchByIdentifierRequestUrl: string;
  @Input()
  selectedAcquiringAccountHolderIdentifier: number;
  @Input()
  currentAccountHolderIdentifier: number;
  @Output()
  readonly selectedAccountTransferType = new EventEmitter<SelectedAccountTransferType>();
  @Output()
  readonly errorDetails = new EventEmitter<ErrorDetail[]>();
  selectedExistingAccountHolder: AccountHolderTypeAheadSearchResult;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.requestParams = new HttpParams().set(
      'types',
      `${AccountHolderType.INDIVIDUAL},${AccountHolderType.ORGANISATION}`
    );
  }

  protected doSubmit(): void {
    this.selectedAccountTransferType.emit({
      selectedUpdateType: this.formGroup.get('accountTransferType').value,
      selectedExistingAccountHolder: this.selectedExistingAccountHolder,
    });
  }

  protected getFormModel(): any {
    return {
      accountTransferType: [
        this.updateType,
        [Validators.required, this.ahSelectionValidator()],
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      accountTransferType: {
        required: 'Select the account transfer type.',
        idError:
          'The account holder does not exist. Enter a valid account holder ID.',
        sameAh: 'You cannot transfer the account to the same Account Holder.',
      },
    };
  }

  onContinue(): void {
    // since the type-ahead is not a form control we need somehow to trigger the custom validator when the value changes
    this.formGroup.get('accountTransferType').updateValueAndValidity();
    this.onSubmit();
  }

  accountHolderResultFormatter(
    item: AccountHolderTypeAheadSearchResult
  ): string {
    return accountHolderResultFormatter(item);
  }

  onSelectFromSearch(
    selectedAccountHolder: AccountHolderTypeAheadSearchResult
  ): void {
    this.selectedExistingAccountHolder = selectedAccountHolder;
  }

  onSelectAccountTransferType(type): void {
    this.updateType = type;
  }

  showAccountTransferTypeError(): boolean {
    return (
      !this.formGroup.get('accountTransferType').valid &&
      !!this.formGroup.get('accountTransferType').errors &&
      !!this.validationErrorMessage.accountTransferType
    );
  }

  ahSelectionValidator(): ValidatorFn {
    return () => {
      if (
        this.updateType === 'ACCOUNT_TRANSFER_TO_EXISTING_HOLDER' &&
        !this.selectedExistingAccountHolder
      ) {
        return { idError: true };
      } else if (
        this.selectedExistingAccountHolder &&
        this.selectedExistingAccountHolder.identifier ===
          this.currentAccountHolderIdentifier
      ) {
        return { sameAh: true };
      }
      return null;
    };
  }
}
