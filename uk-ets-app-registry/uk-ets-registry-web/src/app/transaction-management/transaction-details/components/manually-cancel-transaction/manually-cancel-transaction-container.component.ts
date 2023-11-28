import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack, errors } from '@shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import { selectTransaction } from '@transaction-management/transaction-details/transaction-details.selector';
import { Observable } from 'rxjs';
import { TransactionDetails } from '@transaction-management/model';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { TransactionDetailsActions } from '@transaction-management/transaction-details/actions';

@Component({
  selector: 'app-cancel-transaction-container',
  template: `
    <app-feature-header-wrapper>
      <app-transaction-header
        [transactionDetails]="transactionDetails$ | async"
        [hideCancelButton]="true"
        [hideBackButton]="true"
      >
      </app-transaction-header>
    </app-feature-header-wrapper>
    <app-manually-cancel-transaction
      (comment)="onCancelSubmit($event)"
      (errorDetails)="onError($event)"
    ></app-manually-cancel-transaction>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManuallyCancelTransactionContainerComponent implements OnInit {
  transactionDetails$: Observable<TransactionDetails>;
  goBackRoute: string;

  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.transactionDetails$ = this.store.select(selectTransaction);
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

  onCancelSubmit(comment: string) {
    this.store.dispatch(
      TransactionDetailsActions.cancelTransaction({ comment: comment })
    );
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
