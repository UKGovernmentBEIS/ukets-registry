import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { navigateTo } from '@report-publication/actions';

@Component({
  selector: 'app-cancel-publication-file-upload-request-container',
  template: `
    <app-cancel-publication-file-upload-request
      (cancelRequest)="onCancel()"
    ></app-cancel-publication-file-upload-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelPublicationFileUploadRequestContainerComponent {
  constructor(private store: Store) {}

  onCancel() {
    this.store.dispatch(navigateTo({}));
  }
}
