import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { cancelRequestDocumentsConfirmed } from '@request-documents/wizard/actions';
import { canGoBack } from '@shared/shared.action';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';
import { Observable } from 'rxjs';
import { selectRequestDocumentsOrigin } from '@request-documents/wizard/reducers/request-document.selector';

@Component({
  selector: 'app-cancel-request-documents-container',
  template: `
    <app-cancel-request-documents
      [requestDocumentsOrigin]="requestDocumentsOrigin$ | async"
      (cancelProposal)="onCancel()"
    ></app-cancel-request-documents>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CancelRequestDocumentsContainerComponent implements OnInit {
  requestDocumentsOrigin$: Observable<RequestDocumentsOrigin>;
  goBackRoute: string;

  constructor(
    private store: Store,
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
    this.requestDocumentsOrigin$ = this.store.select(
      selectRequestDocumentsOrigin
    );
  }

  onCancel() {
    this.store.dispatch(cancelRequestDocumentsConfirmed());
  }
}
