import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  cancelClicked,
  goBackButtonInSetTransactionReferencePage,
  setTransactionReference,
} from '@transaction-proposal/actions/transaction-proposal.actions';
import {
  selectTransactionReference,
  selectTransactionTypeLabel,
} from '@transaction-proposal/reducers';

@Component({
  selector: 'app-set-transaction-reference-container',
  template: `
    <app-set-transaction-reference
      [reference]="transactionReference$ | async"
      [title]="transactionTypeLabel$ | async"
      (setTransactionReference)="onContinue($event)"
    ></app-set-transaction-reference>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SetTransactionReferenceContainerComponent implements OnInit {
  transactionTypeLabel$: Observable<string>;
  transactionReference$: Observable<string>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(goBackButtonInSetTransactionReferencePage());
    this.transactionTypeLabel$ = this.store.select(selectTransactionTypeLabel);
    this.transactionReference$ = this.store.select(selectTransactionReference);
  }

  onContinue(value: string) {
    this.store.dispatch(
      setTransactionReference({
        reference: value,
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
