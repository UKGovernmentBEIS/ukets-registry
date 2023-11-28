import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import {
  FormControl,
  FormGroup,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { moduleMetadata } from '@storybook/angular';
import { UkProtoFormSelectComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-select/uk-proto-form-select.component';

export default {
  title: 'UkProtoFormSelectComponent',
  component: UkProtoFormSelectComponent,
  decorators: [
    moduleMetadata({
      imports: [ReactiveFormsModule],
      providers: [FormGroupDirective],
    }),
  ],
};

const defaultOptions: Option[] = [
  {
    label: 'option 1',
    value: 'opt-1',
  },
  {
    label: 'option 2',
    value: 'opt-2',
  },
];

const formGroup = new FormGroup({
  default: new FormControl(),
});

export const Default = () => ({
  component: UkProtoFormSelectComponent,
  props: {
    options: defaultOptions,
    formControl: formGroup.get('default'),
    hint: 'Select one',
  },
});

export const BoldHint = () => ({
  component: UkProtoFormSelectComponent,
  props: {
    options: defaultOptions,
    formControl: formGroup.get('default'),
    hint: 'Select one',
    hintClass: 'govuk-heading-m',
  },
});
