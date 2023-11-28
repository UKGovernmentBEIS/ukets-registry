import {
  FormControl,
  FormGroup,
  FormGroupDirective,
  NgControl,
  ReactiveFormsModule,
} from '@angular/forms';
import { moduleMetadata } from '@storybook/angular';
import { UKSelectPhoneComponent } from '@shared/form-controls/uk-select-phone';

export default {
  title: 'UKSelectPhoneComponent',
  component: UKSelectPhoneComponent,
  decorators: [
    moduleMetadata({
      declarations: [UKSelectPhoneComponent],
      imports: [ReactiveFormsModule],
      providers: [FormGroupDirective, NgControl],
    }),
  ],
};

const formGroup = new FormGroup(
  {
    phone1: new FormControl(''),
  },
  { updateOn: 'submit' }
);

export const Default = () => ({
  template: `
<form [formGroup]="formGroup">
  <app-uk-phone-select
    [phoneInfo]="_phoneInfo1"
    [countryCodes]="countryCodes"
    [showErrors]="true"
    [phoneNumberLabel]="'Phone number 1'"
    formControlName="phone1"
  ></app-uk-phone-select>
  <button class="govuk-button" data-module="govuk-button" type="submit" id="continue">
    Continue
  </button>
</form>
`,
  props: {
    formGroup: formGroup,
    _phoneInfo1: {},
    countryCodes: [
      { region: 'UK', code: 44 },
      { region: 'GR', code: 30 },
    ],
  },
});
