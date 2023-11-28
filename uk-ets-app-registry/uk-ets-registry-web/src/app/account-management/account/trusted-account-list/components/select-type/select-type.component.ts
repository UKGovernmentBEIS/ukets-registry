import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { ActivatedRoute } from '@angular/router';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { TrustedAccountListUpdateType } from '@trusted-account-list/model';

@Component({
  selector: 'app-select-type',
  templateUrl: './select-type.component.html',
})
export class SelectTypeComponent extends UkFormComponent implements OnInit {
  @Output()
  readonly selectUpdateType = new EventEmitter<TrustedAccountListUpdateType>();
  @Output() readonly cancel = new EventEmitter<string>();
  @Input()
  updateType: TrustedAccountListUpdateType;
  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(
    private route: ActivatedRoute,
    protected formBuilder: UntypedFormBuilder
  ) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: 'Select type of update',
      radioGroupHeadingCaption: 'Request to update the trusted account list',
      radioGroupHint: 'Select one option',
      key: 'updateType',
      options: [
        {
          label: 'Add account',
          value: TrustedAccountListUpdateType.ADD,
          enabled: true,
        },
        {
          label: 'Remove account(s)',
          value: TrustedAccountListUpdateType.REMOVE,
          enabled: true,
        },
      ],
    };
  }

  onContinue() {
    this.onSubmit();
  }

  onCancel() {
    this.cancel.emit(this.route.snapshot['_routerState'].url);
  }

  protected doSubmit() {
    this.selectUpdateType.emit(this.formGroup.get('updateType').value);
  }

  protected getFormModel(): any {
    return {
      updateType: [this.updateType, Validators.required],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      updateType: {
        required: 'Select a type of update',
      },
    };
  }
}
