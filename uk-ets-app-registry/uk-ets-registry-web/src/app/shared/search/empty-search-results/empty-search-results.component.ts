import { Component } from '@angular/core';

@Component({
  selector: 'app-empty-search-results',
  template: `
    <p
      id="noResultsFoundId"
      class="govuk-body govuk-!-font-weight-bold"
      style="text-align: center"
    >
      There are no matching results.
    </p>
  `,
})
export class EmptySearchResultsComponent {}
