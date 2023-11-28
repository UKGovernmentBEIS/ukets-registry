import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-guidance-modification-date',
  template: `<hr
      class="govuk-section-break govuk-section-break--m govuk-section-break--visible"
    />

    <ul class="govuk-list">
      <li class="govuk-body-s">Published {{ published }}</li>
      <li class="govuk-body-s">Last updated {{ lastUpdated }}</li>
      <li class="govuk-body-s">From: {{ from }}</li>
    </ul>

    <hr class="govuk-section-break govuk-section-break--m" />`,
})
export class GuidanceModificationDateComponent {
  @Input() published = '23 March 2021';
  @Input() lastUpdated = '23 March 2021';
  @Input() from = 'UK Emissions Trading Registry';
}
