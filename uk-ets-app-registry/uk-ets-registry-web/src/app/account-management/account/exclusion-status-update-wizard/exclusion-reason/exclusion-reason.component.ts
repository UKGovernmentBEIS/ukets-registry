import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  UntypedFormGroup,
  UntypedFormBuilder,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { VerifiedEmissions } from '@registry-web/account-shared/model';
import { UkFormComponent } from '@registry-web/shared/form-controls/uk-form.component';
import { FormRadioGroupInfo } from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';
import { SharedModule } from '@registry-web/shared/shared.module';
import {
  UkValidationMessageHandler,
  UkRegistryValidators,
} from '@registry-web/shared/validation';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, SharedModule],
  selector: 'app-exclusion-reason',
  templateUrl: './exclusion-reason.component.html',
})
export class ExclusionReasonComponent
  extends UkFormComponent
  implements OnInit
{
  @Output()
  readonly submitReason = new EventEmitter<boolean>();
  @Input()
  exclusionReason: string;
  @Input()
  year: number;
  @Input()
  emissionEntries: VerifiedEmissions[];
  genericValidator: UkValidationMessageHandler;
  validationErrorMessage: { [key: string]: string } = {};
  validationMessages: { [key: string]: { [key: string]: string } };
  formGroup: UntypedFormGroup;
  formRadioGroupInfo: FormRadioGroupInfo;
  excluded: string;
  validationMessage: string;
  hasEmissions: boolean;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();

    this.hasEmissions = this.emissionEntries?.some(
      (e) =>
        e.year === this.year &&
        e.reportableEmissions != null &&
        e.reportableEmissions !== 'Excluded'
    );
  }

  protected doSubmit() {
    this.submitReason.emit(this.formGroup.get('exclusionReason').value);
  }

  protected getFormModel(): any {
    return {
      exclusionReason: [
        this.exclusionReason,
        {
          validators: [Validators.required],
          updateOn: 'change',
        },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      exclusionReason: {
        required: 'Please enter a reason.',
      },
    };
  }
}
