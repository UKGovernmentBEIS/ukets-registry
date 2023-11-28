import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import {
  TransactionBlock,
  TransactionDetails,
  TransactionResponse,
} from '@transaction-management/model';
import { Store } from '@ngrx/store';
import {
  selectIsETSTransaction,
  selectTransaction,
  selectTransactionBlock,
  selectTransactionEventHistory,
  selectTransactionReference,
  selectTransactionResponse,
  selectTransactionType,
} from '@transaction-management/transaction-details/transaction-details.selector';
import { DomainEvent } from '@shared/model/event';
import { TransactionDetailsActions } from '@transaction-management/transaction-details/actions';
import { canGoBack, navigateToUserProfile } from '@shared/shared.action';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { ActivatedRoute } from '@angular/router';
import { GoBackNavigationExtras } from '@shared/back-button';
import {
  selectGoBackToListNavigationExtras,
  selectGoBackToListRoute,
} from '@shared/shared.selector';
import { selectIsReportSuccess } from '@reports/selectors';

@Component({
  selector: 'app-transaction-details-container',
  templateUrl: './transaction-details-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: [],
})
export class TransactionDetailsContainerComponent implements OnInit {
  eventHistory$: Observable<DomainEvent[]>;
  transactionDetails$: Observable<TransactionDetails>;
  transactionResponses$: Observable<Array<TransactionResponse>>;
  transactionBlocks$: Observable<Array<TransactionBlock>>;
  transactionType$: Observable<string>;
  isEtsTransaction$: Observable<boolean>;
  transactionIdentifier: string;
  transactionReference$: Observable<string>;
  isAdmin$: Observable<boolean>;
  isReportSuccess$: Observable<boolean>;
  goBackToListRoute$: Observable<string>;
  goBackToListNavigationExtras$: Observable<GoBackNavigationExtras>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.transactionDetails$ = this.store.select(selectTransaction);
    this.eventHistory$ = this.store.select(selectTransactionEventHistory);
    this.transactionResponses$ = this.store.select(selectTransactionResponse);
    this.transactionBlocks$ = this.store.select(selectTransactionBlock);
    this.transactionType$ = this.store.select(selectTransactionType);
    this.isEtsTransaction$ = this.store.select(selectIsETSTransaction);
    this.transactionReference$ = this.store.select(selectTransactionReference);
    this.isAdmin$ = this.store.select(isAdmin);
    this.isReportSuccess$ = this.store.select(selectIsReportSuccess);
    this.goBackToListRoute$ = this.store.select(selectGoBackToListRoute);

    this.goBackToListNavigationExtras$ = this.store.select(
      selectGoBackToListNavigationExtras
    );
  }

  navigateToManuallyCancel($event: string) {
    this.store.dispatch(
      TransactionDetailsActions.navigateToCancelTransaction()
    );
  }

  navigateToUserPage(urid: string) {
    const goBackRoute = this.route.snapshot['_routerState'].url;
    const userProfileRoute = '/user-details/' + urid;
    this.store.dispatch(
      navigateToUserProfile({ goBackRoute, userProfileRoute })
    );
  }
}
