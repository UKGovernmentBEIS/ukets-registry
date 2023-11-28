import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import {
  UntypedFormBuilder,
  UntypedFormControl,
  Validators,
} from '@angular/forms';
import { Installation, Operator, OperatorType } from '@shared/model/account';

@Component({
  selector: 'app-is-it-an-installation-transfer',
  templateUrl: './is-it-an-installation-transfer.component.html',
})
export class IsItAnInstallationTransferComponent
  extends UkFormComponent
  implements OnInit
{
  formRadioGroupInfo: FormRadioGroupInfo;

  @Input()
  operator: Operator;

  @Output()
  readonly installationTypeEmitter = new EventEmitter<Operator>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: 'Is this an installation transfer?',
      radioGroupHeadingCaption: 'Add the installation information',
      radioGroupHint: '',
      key: 'installationType',
      options: [
        {
          label: 'Yes',
          value: OperatorType.INSTALLATION_TRANSFER,
          enabled: true,
        },
        { label: 'No', value: OperatorType.INSTALLATION, enabled: true },
      ],
    };
  }

  onContinue() {
    this.onSubmit();
  }

  protected doSubmit() {
    this.installationTypeEmitter.emit({
      ...this.operator,
      type: this.installationTypeControl.value,
    });
  }

  get installationTypeControl(): UntypedFormControl {
    return this.formGroup.get('installationType') as UntypedFormControl;
  }

  protected getFormModel(): any {
    return {
      installationType: [OperatorType.INSTALLATION, Validators.required],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {};
  }
}
