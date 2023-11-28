import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { TransactionDetails } from '@transaction-management/model';
import { Store } from '@ngrx/store';
import { selectTransaction } from '@transaction-management/transaction-details/transaction-details.selector';
import { canGoBack } from '@shared/shared.action';

@Component({
  selector: 'app-request-submitted',
  template: `
    <app-transaction-cancelled-confirmation
      [transactionIdentifier]="(transactionDetails$ | async).identifier"
    ></app-transaction-cancelled-confirmation>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransactionCancelledConfirmationContainerComponent
  implements OnInit {
  transactionDetails$: Observable<TransactionDetails>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.transactionDetails$ = this.store.select(selectTransaction);
  }
}
