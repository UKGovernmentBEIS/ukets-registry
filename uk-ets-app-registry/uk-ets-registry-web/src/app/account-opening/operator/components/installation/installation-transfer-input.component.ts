import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { HttpParams } from '@angular/common/http';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  InstallationActivityType,
  InstallationSearchResult,
  InstallationTransfer,
  OperatorType,
} from '@shared/model/account';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { getOptionsFromMap } from '@shared/shared.util';
import { regulatorMap } from '@account-management/account-list/account-list.model';
import { UkRegistryValidators } from '@shared/validation';
import { AccountOpeningOperatorActions } from '@account-opening/operator/actions';
import { AccountHolder } from '@shared/model/account/account-holder';

@Component({
  selector: 'app-installation-transfer-input',
  templateUrl: './installation-transfer-input.component.html',
})
export class InstallationTransferInputComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() installation: InstallationTransfer;
  @Input() title: string;
  @Input() headerTitle: string;
  @Input() accountHolder: AccountHolder;
  @Output()
  readonly installationTransferEmitter = new EventEmitter<{
    installationTransfer: InstallationTransfer;
    acquiringAccountHolderIdentifier: number;
  }>();
  activityTypes = InstallationActivityType;
  regulatorOptions: Option[] = getOptionsFromMap(regulatorMap);
  requestParams: HttpParams = new HttpParams();
  searchByIdRequestUrl: string;

  protected getFormModel() {
    return {
      identifier: [
        '',
        {
          validators: [
            Validators.required,
            UkRegistryValidators.isPositiveNumberWithoutDecimals,
          ],
        },
      ],
      name: ['', Validators.required],
      permit: this.formBuilder.group({
        id: ['', Validators.required],
      }),
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      identifier: {
        required: 'Enter the installation ID',
        invalid:
          'The installation ID must be a positive number without decimal places',
      },
      name: {
        required: 'Enter the installation name',
      },
      id: {
        required: 'Enter the permit ID',
      },
    };
  }
  protected doSubmit() {
    const updateObject = this.formGroup.getRawValue() as InstallationTransfer;
    updateObject.type = OperatorType.INSTALLATION_TRANSFER;
    this.installationTransferEmitter.emit({
      installationTransfer: updateObject,
      acquiringAccountHolderIdentifier: this.accountHolder.id,
    });
  }

  constructor(protected formBuilder: UntypedFormBuilder, private store: Store) {
    super();
    this.searchByIdRequestUrl = '/accounts.get.candidate-installation-transfer';
  }

  ngOnInit(): void {
    super.ngOnInit();

    const existingAccountHolder =
      this.accountHolder !== undefined && this.accountHolder.id !== undefined;
    if (existingAccountHolder) {
      this.requestParams = new HttpParams().set(
        'excludeAccountHolderIdentifier',
        this.accountHolder.id
      );
    }
  }

  activityTypeOptions(): Option[] {
    return Object.keys(this.activityTypes)
      .sort((a, b) => (this.activityTypes[a] > this.activityTypes[b] ? 1 : -1))
      .map((c) => ({ label: this.activityTypes[c], value: c }));
  }

  onSelectFromSearch(result: InstallationSearchResult) {
    const operator: InstallationTransfer = Object.assign({});
    operator.identifier = result.identifier;
    operator.name = result.installationName;
    operator.permit = { id: result.permitIdentifier };
    this.installation = operator;
    this.store.dispatch(
      AccountOpeningOperatorActions.initialPermitId({
        permitID: result.permitIdentifier,
      })
    );
  }

  operatorResultFormatter(result: InstallationTransfer) {
    return result;
  }

  onContinue() {
    // this.logAllErrors();
    this.onSubmit();
  }
}
