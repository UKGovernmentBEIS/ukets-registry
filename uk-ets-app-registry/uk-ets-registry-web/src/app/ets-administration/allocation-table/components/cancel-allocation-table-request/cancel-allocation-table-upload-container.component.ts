import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import { cancelAllocationTableWizard } from '@allocation-table/actions/allocation-table.actions';

@Component({
  selector: 'app-cancel-allocation-table-upload-container',
  template: `
    <app-cancel-update-request
      updateRequestText="allocation table upload"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CancelAllocationTableUploadContainerComponent implements OnInit {
  goBackRoute: string;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.goBackRoute = params.goBackRoute;
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.goBackRoute,
        extras: { skipLocationChange: true }
      })
    );
  }

  onCancel() {
    this.store.dispatch(cancelAllocationTableWizard());
  }
}
