import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { navigateToCancel } from '@allocation-status/actions/allocation-status.actions';

@Component({
  selector: 'app-update-allocation-status-wizard',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <router-outlet></router-outlet>
        <app-cancel-request-link (goToCancelScreen)="onCancel()">
        </app-cancel-request-link>
      </div>
    </div>
  `
})
export class UpdateAllocationStatusWizardComponent {
  constructor(private store: Store) {}

  onCancel() {
    this.store.dispatch(navigateToCancel());
  }
}
