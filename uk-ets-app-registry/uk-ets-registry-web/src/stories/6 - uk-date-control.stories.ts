import {
  FormControl,
  FormGroup,
  FormGroupDirective,
  NgControl,
  ReactiveFormsModule,
} from '@angular/forms';
import { moduleMetadata } from '@storybook/angular';
import { UkDateControlComponent } from '@shared/form-controls/uk-date-control/uk-date-control.component';

export default {
  title: 'UkDateControlComponent',
  component: UkDateControlComponent,
  decorators: [
    moduleMetadata({
      declarations: [UkDateControlComponent],
      imports: [ReactiveFormsModule],
      providers: [FormGroupDirective, NgControl],
    }),
  ],
};

const formGroup = new FormGroup(
  {
    birthDate: new FormControl(''),
  },
  { updateOn: 'submit' }
);

export const Default = () => ({
  template: `
<form [formGroup]="formGroup">
  <app-uk-date-control
            [label]="'Date of birth'"
            [hint]="'For example, 31 3 1980'"
            [showErrors]="true"
            formControlName="birthDate"
            [validationErrorMessages]="validationMessages.birthDate"
          ></app-uk-date-control>
  <button class="govuk-button" data-module="govuk-button" type="submit" id="continue">
    Continue
  </button>
</form>
`,
  props: {
    formGroup,
    validationMessages: {},
  },
});
