import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  AccountAllocationStatus,
  UpdateAllocationStatusRequest,
} from '@allocation-status/model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  AbstractControl,
  UntypedFormBuilder,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import {
  ALLOCATION_STATUS_LABELS,
  AllocationStatus,
} from '@shared/model/account';
import { UkRegistryValidators } from '@shared/validation';

@Component({
  selector: 'app-update-allocation-status-form',
  templateUrl: './update-allocation-status-form.component.html',
})
export class UpdateAllocationStatusFormComponent extends UkFormComponent {
  @Input() accountAllocationStatus: AccountAllocationStatus;
  @Input() updateAllocationStatusRequest: UpdateAllocationStatusRequest;
  @Output()
  readonly updateAllocationStatus: EventEmitter<UpdateAllocationStatusRequest> =
    new EventEmitter<UpdateAllocationStatusRequest>();

  allocationStatusEnum = AllocationStatus;
  allocationStatusLabels = ALLOCATION_STATUS_LABELS;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected getFormModel(): any {
    const model: any = {};
    const initialJustification = this.updateAllocationStatusRequest
      ? this.updateAllocationStatusRequest.justification
      : '';
    model.justification = [
      initialJustification,
      {
        validators: [Validators.required],
        updateOn: 'change',
      },
    ];

    const allocationStatusFormModel: any = {};
    for (const year in this.accountAllocationStatus) {
      if (
        this.updateAllocationStatusRequest &&
        this.updateAllocationStatusRequest.changedStatus &&
        this.updateAllocationStatusRequest.changedStatus[year]
      ) {
        allocationStatusFormModel[year] = [
          this.updateAllocationStatusRequest.changedStatus[year],
        ];
      } else {
        allocationStatusFormModel[year] = [this.accountAllocationStatus[year]];
      }
    }
    model.status = this.formBuilder.group(allocationStatusFormModel, {
      validators: [
        UkRegistryValidators.allocationStatusShouldBeChanged(
          this.accountAllocationStatus,
          this.calculateChangedStatus
        ),
      ],
    });
    return model;
  }

  public calculateChangedStatus(
    currentStatus: AccountAllocationStatus,
    previousStatus: AccountAllocationStatus
  ): AccountAllocationStatus {
    let changedStatus: AccountAllocationStatus = null;
    for (const year in previousStatus) {
      if (previousStatus[year] !== currentStatus[year]) {
        if (!changedStatus) {
          changedStatus = {};
        }
        changedStatus[year] = currentStatus[year];
      }
    }
    return changedStatus;
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      status: {
        changeRequired: 'You should change the allocation status to continue.',
      },
      justification: {
        required: 'Explain why you are changing the status.',
      },
    };
  }

  protected doSubmit() {
    const changedStatus: AccountAllocationStatus = this.calculateChangedStatus(
      this.formGroup.value['status'],
      this.accountAllocationStatus
    );
    const updateRequest: UpdateAllocationStatusRequest = {
      justification: this.formGroup.value['justification'],
      changedStatus,
    };
    this.updateAllocationStatus.emit(updateRequest);
  }
}
