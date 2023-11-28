import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import { TalTransactionRulesActions } from '@tal-transaction-rules/actions';

@Component({
  selector: 'app-cancel-update-request-container',
  template: `
    <app-cancel-update-request
      updateRequestText="transaction rules update"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CancelUpdateRequestContainerComponent implements OnInit {
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
    this.store.dispatch(
      TalTransactionRulesActions.cancelTalTransactionRulesUpdateRequest()
    );
  }
}
