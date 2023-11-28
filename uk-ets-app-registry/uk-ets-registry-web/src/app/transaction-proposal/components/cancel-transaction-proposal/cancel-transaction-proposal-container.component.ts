import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import { TransactionProposalActions } from '@transaction-proposal/actions';

@Component({
  selector: 'app-cancel-transaction-proposal-container',
  template: `
    <app-cancel-transaction-proposal
      (cancelProposal)="onCancel()"
    ></app-cancel-transaction-proposal>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CancelTransactionProposalContainerComponent implements OnInit {
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
  }

  onCancel() {
    this.store.dispatch(TransactionProposalActions.cancelTransactionProposal());
  }
}
