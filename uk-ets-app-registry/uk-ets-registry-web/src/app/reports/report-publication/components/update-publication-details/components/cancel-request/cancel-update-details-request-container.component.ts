import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { navigateTo } from '@report-publication/actions';

@Component({
  selector: 'app-cancel-update-details-request-container',
  template: `
    <app-cancel-update-details-request
      (cancelRequest)="onCancel()"
    ></app-cancel-update-details-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelUpdateDetailsRequestContainerComponent {
  constructor(private store: Store) {}

  onCancel() {
    this.store.dispatch(navigateTo({}));
  }
}
