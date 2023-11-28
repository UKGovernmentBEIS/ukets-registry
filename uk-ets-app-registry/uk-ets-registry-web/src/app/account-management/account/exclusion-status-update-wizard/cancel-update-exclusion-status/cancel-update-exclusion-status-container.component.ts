import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@registry-web/shared/shared.action';
import { Observable } from 'rxjs';
import { UpdateExclusionStatusActions } from '../actions';
import { Operator } from '@shared/model/account';

@Component({
  selector: 'app-cancel-update-exclusion-status-container',
  template: `<div
      appScreenReaderPageAnnounce
      [pageTitle]="'Cancel update exclusion status'"
    ></div>
    <app-cancel-update-request
      [updateRequestText]="'exclusion status'"
      [pageTitle]="'account'"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelUpdateExclusionStatusContainerComponent implements OnInit {
  goBackRoute: string;
  currentOperatorInfo$: Observable<Operator>;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
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

  onCancel() {
    this.store.dispatch(
      UpdateExclusionStatusActions.cancelUpdateExclusionStatus()
    );
  }
}
