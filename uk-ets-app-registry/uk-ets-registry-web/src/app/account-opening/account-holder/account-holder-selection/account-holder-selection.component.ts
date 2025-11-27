import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AuthModel } from '../../../auth/auth.model';
import { HttpParams } from '@angular/common/http';
import {
  AccountHolder,
  AccountHolderSelectionType,
  AccountHolderType,
} from '@shared/model/account/account-holder';
import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { ErrorDetail } from '@shared/error-summary';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { accountHolderResultFormatter } from '@shared/shared.util';
import { AccountHolderTypeAheadSearchResult } from '@registry-web/account-shared/model';

@Component({
  selector: 'app-account-holder-selection',
  templateUrl: './account-holder-selection.component.html',
})
export class AccountHolderSelectionComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  accountHolder: AccountHolder;

  @Input()
  accountHolderList: AccountHolder[];

  @Input()
  accountHolderType: string;

  @Input()
  accountHolderSelectionType: AccountHolderSelectionType;

  @Input()
  loggedinUser: AuthModel;

  @Input()
  searchByNameRequestUrl: string;

  @Output() readonly output = new EventEmitter<any>();

  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  accountHolderTypes = AccountHolderType;
  accountHolderTypeText: string;
  accountHolderSelectionTypes = AccountHolderSelectionType;
  selectedIdFromSearch: number;
  requestParams: HttpParams;
  validationErrorMessage: ValidationErrors = {};

  accountHolderResultFormatter(
    item: AccountHolderTypeAheadSearchResult
  ): string {
    return accountHolderResultFormatter(item);
  }

  ngOnInit() {
    super.ngOnInit();
    this.accountHolderTypeText =
      this.accountHolderType === AccountHolderType.ORGANISATION
        ? 'organisation'
        : 'individual';
    this.requestParams = new HttpParams().set('type', this.accountHolderType);
  }

  onSelectFromSearch(selectedIdFromSearch: AccountHolderTypeAheadSearchResult) {
    this.selectedIdFromSearch = selectedIdFromSearch.identifier;
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      accountHolderSelection: {
        required: 'Select the account holder.',
        error: 'The name you entered could not be found.',
      },
      selectedIdFromList: {
        error: 'Please select an option.',
      },
    };
  }

  getFormModel(): any {
    return {
      accountHolderSelection: ['', Validators.required],
      selectedIdFromList: [''],
    };
  }

  getErrors(): string[] {
    return Object.keys(this.validationErrorMessage).map(
      (key) => this.validationErrorMessage[key]
    );
  }

  onError(control: AbstractControl) {
    control.setValidators([this.errorValidator()]);
    control.updateValueAndValidity();

    this.showErrors = true;
    this.validationErrorMessage = this.genericValidator.processMessages(
      this.formGroup
    );
    this.errorDetails.emit(
      this.genericValidator.mapMessagesToErrorDetails(
        this.validationErrorMessage
      )
    );
  }

  onSuccess() {
    this.output.emit({
      selectedIdFromSearch: this.selectedIdFromSearch,
      selectedIdFromList: this.formGroup.get('selectedIdFromList').value,
      accountHolderSelectionType: this.accountHolderSelectionType,
      accountHolderType: this.accountHolderType,
    });
  }

  protected doSubmit() {
    const selectedIdFromListForm = this.formGroup.get('selectedIdFromList');

    if (
      this.accountHolderSelectionType ===
        AccountHolderSelectionType.FROM_LIST &&
      (selectedIdFromListForm.value === '' ||
        selectedIdFromListForm.value === 'null')
    ) {
      this.onError(selectedIdFromListForm);
    } else if (
      this.accountHolderSelectionType ===
        AccountHolderSelectionType.FROM_SEARCH &&
      !this.selectedIdFromSearch
    ) {
      this.onError(this.formGroup.get('accountHolderSelection'));
    } else {
      this.onSuccess();
    }
  }

  onContinue() {
    super.onSubmit();
    this.resetValidators();
  }

  resetValidators() {
    const selectedIdFromListForm = this.formGroup.get('selectedIdFromList');
    selectedIdFromListForm.setValidators([]);
    selectedIdFromListForm.updateValueAndValidity();

    const accountHolderSelectionForm = this.formGroup.get(
      'accountHolderSelection'
    );
    accountHolderSelectionForm.setValidators([Validators.required]);
    accountHolderSelectionForm.updateValueAndValidity();

    this.formGroup.updateValueAndValidity();
    this.formGroup.markAllAsTouched();
  }

  errorValidator(): ValidatorFn {
    return () => {
      return { error: true };
    };
  }
}
