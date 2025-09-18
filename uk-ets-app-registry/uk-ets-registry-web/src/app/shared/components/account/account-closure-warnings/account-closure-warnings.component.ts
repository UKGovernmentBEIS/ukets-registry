import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-account-closure-warnings',
  template: `<div class="govuk-warning-text">
    <span class="govuk-warning-text__icon" aria-hidden="true">!</span>
    <strong class="govuk-warning-text__text">
      <span class="govuk-govuk-visually-hidden-text__assistive">Warning</span>
      {{ label }}
    </strong>
  </div>`,
})
export class AccountClosureWarningsComponent {
  @Input()
  label: string;
}
