import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { cancelEmissionsTableUpload } from '@emissions-table/store/actions';

@Component({
  selector: 'app-cancel-emissions-table-upload-container',
  template: `
    <app-cancel-update-request
      updateRequestText="emissions table upload"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelEmissionsTableUploadContainerComponent implements OnInit {
  goBackRoute: string;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.goBackRoute = params.goBackRoute;
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.goBackRoute,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel(): void {
    this.store.dispatch(cancelEmissionsTableUpload());
  }
}
