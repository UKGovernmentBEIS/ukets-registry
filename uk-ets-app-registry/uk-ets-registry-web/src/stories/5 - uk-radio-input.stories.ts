import {
  FormControl,
  FormGroup,
  FormGroupDirective,
  NgControl,
  ReactiveFormsModule
} from '@angular/forms';
import { moduleMetadata } from '@storybook/angular';
import { UKSelectPhoneComponent } from '@shared/form-controls/uk-select-phone';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { formRadioGroupInfo } from './test-data';

export default {
  title: 'UKRadioInputComponent',
  component: UKSelectPhoneComponent,
  decorators: [
    moduleMetadata({
      declarations: [UkRadioInputComponent],
      imports: [ReactiveFormsModule],
      providers: [FormGroupDirective, NgControl]
    })
  ]
};

const formGroup = new FormGroup(
  {
    allocationYears: new FormControl('')
  },
  { updateOn: 'submit' }
);

export const Default = () => ({
  template: `
<form [formGroup]="formGroup">
   <app-uk-radio-input
        controlName="allocationYears"
        [formRadioGroupInfo]="formRadioGroupInfo"
   ></app-uk-radio-input>
  <button class="govuk-button" data-module="govuk-button" type="submit">
    Continue
  </button>
</form>
`,
  props: {
    formRadioGroupInfo,
    formGroup
  }
});
