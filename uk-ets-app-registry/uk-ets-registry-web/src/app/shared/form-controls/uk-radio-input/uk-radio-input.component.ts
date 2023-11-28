import { Component, Input } from '@angular/core';
import {
  FormRadioGroupInfo,
  FormRadioOption,
} from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UkProtoFormComponent } from '@shared/form-controls/uk-proto-form.component';
import { ControlContainer, FormGroupDirective } from '@angular/forms';

@Component({
  selector: 'app-uk-radio-input',
  templateUrl: './uk-radio-input.component.html',
  styleUrls: ['./uk-radio-input.component.scss'],
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkRadioInputComponent extends UkProtoFormComponent {
  @Input() formRadioGroupInfo: FormRadioGroupInfo;

  @Input()
  selectedType;

  @Input() separatorIndex?: number;

  selectedValue(option: FormRadioOption) {
    this.selectedType = option.value?.type;
  }
}
