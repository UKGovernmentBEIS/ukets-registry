import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { navigateToCancellation } from '@authority-setting/action';

@Component({
  selector: 'app-update-allocation-status-wizard',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <router-outlet></router-outlet>
        <app-cancel-request-link (goToCancelScreen)="onCancel()">
        </app-cancel-request-link>
      </div>
    </div>
  `
})
export class SetAuthorityUserWizardComponent {
  constructor(private store: Store) {}

  onCancel() {
    this.store.dispatch(navigateToCancellation());
  }
}
