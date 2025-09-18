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
import { ExistingEmitterIdAsyncValidator, UkRegistryValidators } from '@shared/validation';
import { AccountOpeningOperatorActions } from '@account-opening/operator/actions';
import { AccountHolder } from '@shared/model/account/account-holder';
import { take } from 'rxjs/operators';

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
  @Input() isSeniorOrJuniorAdmin: boolean;

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
      emitter: this.formBuilder.group({
        emitterId: [
          this.installation?.emitterId,
          [Validators.required,Validators.pattern('^[a-zA-Z0-9-_]*$')],
          (control) => this.existingEmitterIdAsyncValidator.validateEmitterId(this.installation?.identifier)(control)
        ],
      }),
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
      emitterId: {
        required: 'Enter the Emitter ID',
        exists: 'This emitter ID is used by another account',
        pattern: 'The Emitter ID cannot contain any special characters'        
      },
      id: {
        required: 'Enter the permit ID',
      },
    };
  }

  protected doSubmit() {
    const updateObject:InstallationTransfer = {...this.formGroup.getRawValue(),
                                               type:OperatorType.INSTALLATION_TRANSFER,
                                               emitterId:this.formGroup.get('emitter.emitterId').value};
    this.installationTransferEmitter.emit({
      installationTransfer: updateObject,
      acquiringAccountHolderIdentifier: this.accountHolder.id,
    });
  }

  constructor(protected formBuilder: UntypedFormBuilder, private existingEmitterIdAsyncValidator:ExistingEmitterIdAsyncValidator,private store: Store) {
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
    operator.emitterId = result.emitterId;
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
    this.onSubmit();
  }

  onSubmit() {
    // this.logAllErrors();
    if (this.formGroup.pending) {
      this.formGroup.statusChanges.
       pipe(take(1)).
       subscribe(() => {
        super.onSubmit();
      });
    } else {
      super.onSubmit();
    }
  }
}
