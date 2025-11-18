import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UntypedFormBuilder, UntypedFormControl } from '@angular/forms';
import {
  DropdownGroup,
  groupedStatusOptions,
  Installation,
  InstallationActivityType,
} from '@shared/model/account';
import { ErrorDetail } from '@shared/error-summary';

@Component({
  selector: 'app-installation-activity-type',
  templateUrl: './installation-activity-type.component.html',
  styleUrls: ['./installation-activity-type.component.css'],
})
export class InstallationActivityTypeComponent
  extends UkFormComponent
  implements OnInit
{
  formRadioGroupInfo: FormRadioGroupInfo;

  activityTypes: DropdownGroup[] = groupedStatusOptions;

  @Input()
  operator: Installation;

  @Input() title: string;
  @Input() headerTitle: string;

  @Output()
  readonly installationTypeEmitter = new EventEmitter<Installation>();

  @Output() readonly cancelEmitter = new EventEmitter();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected getFormModel() {
    const model: any = {};
    this.activityTypes.map((activityGroup) => {
      activityGroup.options.map((activity) => {
        if (this.operator.activityTypes?.includes(activity.value)) {
          model[activity.value] = [true];
        } else {
          model[activity.value] = [false];
        }
      });
    });
    return model;
  }

  ngOnInit() {
    super.ngOnInit();
  }

  onContinue() {
    if (this.getSelectedActivityTypes().length === 0) {
      this.errorDetails.emit([
        new ErrorDetail(null, 'Enter the  Regulated activity.'),
      ]);
    } else {
      this.onSubmit();
    }
  }

  onCancel() {
    this.cancelEmitter.emit();
  }

  protected doSubmit() {
    this.installationTypeEmitter.emit({
      ...this.operator,
      activityTypes: this.getSelectedActivityTypes(),
    });
  }

  getSelectedActivityTypes(): InstallationActivityType[] {
    const selectedItems: InstallationActivityType[] = [];
    Object.keys(this.formGroup.controls).forEach((key) => {
      const item = this.formGroup.get(key) as UntypedFormControl;
      if (item.value === true) {
        selectedItems.push(key as InstallationActivityType);
      }
    });
    return selectedItems;
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {};
  }
}
