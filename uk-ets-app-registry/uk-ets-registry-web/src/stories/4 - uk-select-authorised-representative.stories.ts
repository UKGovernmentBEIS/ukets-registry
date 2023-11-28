import {
  FormGroupDirective,
  NgControl,
  ReactiveFormsModule,
} from '@angular/forms';
import { moduleMetadata } from '@storybook/angular';
import { UkSelectAuthorisedRepresentativeComponent } from '@shared/form-controls/uk-select-authorised-representative/uk-select-authorised-representative.component';
import { arFormGroupData, authorisedRepresentativesData } from './test-data';
import { AddRepresentativeComponent } from '@authorised-representatives/components';
import { action } from '@storybook/addon-actions';
import { ProtectPipe } from '@shared/pipes';

export default {
  title: 'UkSelectAuthorisedRepresentativeComponent',
  component: UkSelectAuthorisedRepresentativeComponent,
  decorators: [
    moduleMetadata({
      declarations: [
        UkSelectAuthorisedRepresentativeComponent,
        AddRepresentativeComponent,
        ProtectPipe,
      ],
      imports: [ReactiveFormsModule],
      providers: [FormGroupDirective, NgControl],
    }),
  ],
};

export const Default = () => ({
  template: `
<form [formGroup]="formGroup">
  <app-uk-select-authorised-representative
    formControlName="selectAr"
    [authorisedRepresentatives]="authorisedRepresentatives"
    [showErrors]="true"
  >
  </app-uk-select-authorised-representative>
  <button class="govuk-button" data-module="govuk-button" type="submit" id="continue">
    Continue
  </button>
</form>
`,
  props: {
    formGroup: arFormGroupData(),
    authorisedRepresentatives: authorisedRepresentativesData,
  },
});

export const WithHintAndHintClass = () => ({
  template: `
<form [formGroup]="formGroup">
  <app-uk-select-authorised-representative
    formControlName="selectAr"
    [authorisedRepresentatives]="authorisedRepresentatives"
    [hint]="hint"
    [hintClass]="hintClass"
    [showErrors]="true"
  >
  </app-uk-select-authorised-representative>
  <button class="govuk-button" data-module="govuk-button" type="submit" id="continue">
    Continue
  </button>
</form>
`,
  props: {
    formGroup: arFormGroupData(),
    authorisedRepresentatives: authorisedRepresentativesData,
    hint: 'with the one bellow',
    hintClass: 'govuk-heading-m',
  },
});

export const WithUkFormParent = () => ({
  template: `
<app-add-representative
  [authorisedRepresentatives]="authorisedRepresentatives"
  (selectAuthorizedRepresentative)="
    onAuthorizedRepresentativeSelected($event)
  "
  (errorDetails)="onError($event)"
></app-add-representative>
  `,
  props: {
    authorisedRepresentatives: authorisedRepresentativesData,
    onAuthorizedRepresentativeSelected: action('Continue button clicked'),
    onError: action('Error details'),
  },
});

export const OnlyUserIdInput = () => ({
  template: `
<form [formGroup]="formGroup">
  <app-uk-select-authorised-representative
    formControlName="selectAr"
    [authorisedRepresentatives]="authorisedRepresentatives"
    [showErrors]="true"
    showOnlyUserIdInput="true"
    hint="User ID"
    hintClass="govuk-heading-s"
  >
  </app-uk-select-authorised-representative>
  <button class="govuk-button" data-module="govuk-button" type="submit" id="continue">
    Continue
  </button>
</form>
`,
  props: {
    formGroup: arFormGroupData(),
    authorisedRepresentatives: authorisedRepresentativesData,
  },
});
