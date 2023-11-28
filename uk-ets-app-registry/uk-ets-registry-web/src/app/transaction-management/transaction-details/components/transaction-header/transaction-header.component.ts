import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TransactionDetails } from '@transaction-management/model';
import {
  transactionStatusMap,
  TransactionType,
  UnitType,
} from '@shared/model/transaction';
import { Store } from '@ngrx/store';
import {
  fetchTransactionDetailsReport,
  prepareTransactionProposalStateForReversal,
} from '@transaction-management/transaction-details/actions/transaction-details.actions';
import { ActivatedRoute, NavigationExtras } from '@angular/router';
import { getUrlIdentifier } from '@shared/shared.util';
import { TransactionAtrributesPipe } from '@shared/pipes';
import { SearchMode } from '@registry-web/shared/resolvers/search.resolver';
import { GoBackNavigationExtras } from '@shared/back-button';
import { navigateTo } from '@shared/shared.action';

@Component({
  selector: 'app-transaction-header',
  templateUrl: './transaction-header.component.html',
  styleUrls: ['../../../../shared/sub-headers/styles/sub-header.scss'],
})
export class TransactionHeaderComponent {
  transactionStatusMap = transactionStatusMap;

  @Input()
  transactionDetails: TransactionDetails;
  @Input()
  hideCancelButton = false;
  @Input()
  hideBackButton = false;
  @Input()
  hideDetailsReportButton = false;
  @Input()
  goBackToListRoute: string;
  @Input()
  goBackToListNavigationExtras: GoBackNavigationExtras;
  @Input()
  isReportSuccess: string;

  @Output() readonly cancelTransaction = new EventEmitter<string>();
  @Output() readonly transactionDetailsReport = new EventEmitter<string>();
  searchMode = SearchMode;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private atrributesPipe: TransactionAtrributesPipe
  ) {}

  get properReverseTransactionType() {
    switch (this.transactionDetails.type) {
      case 'AllocateAllowances':
        return TransactionType.ReverseAllocateAllowances;
      case 'SurrenderAllowances':
        return TransactionType.ReverseSurrenderAllowances;
      case 'DeletionOfAllowances':
        return TransactionType.ReverseDeletionOfAllowances;
    }
  }

  goBackToList(event) {
    event.preventDefault();
    const extras: NavigationExtras = {
      skipLocationChange: this.goBackToListNavigationExtras?.skipLocationChange,
      queryParams: this.goBackToListNavigationExtras?.queryParams,
    };
    this.store.dispatch(
      navigateTo({
        route: this.goBackToListRoute,
        extras,
        queryParams: this.goBackToListNavigationExtras?.queryParams,
      })
    );
  }

  generateTransactionDetailsReport(transactionIdentifier) {
    this.store.dispatch(
      fetchTransactionDetailsReport({ transactionIdentifier })
    );
  }

  reverseTransaction() {
    this.store.dispatch(
      prepareTransactionProposalStateForReversal({
        routeSnapshotUrl: getUrlIdentifier(
          this.activatedRoute.snapshot['_routerState'].url
        ),
        quantity: this.transactionDetails.quantity,
        blockType: UnitType.ALLOWANCE,
        transferringAccountIdentifier:
          this.transactionDetails.transferringAccountFullIdentifier,
        acquiringAccountIdentifier:
          this.transactionDetails.acquiringAccountFullIdentifier,
        transactionReversedIdentifier: this.transactionDetails.identifier,
        attributes: this.atrributesPipe.transform(
          this.transactionDetails.attributes,
          'AllocationYear'
        ),
        transactionType: this.properReverseTransactionType,
      })
    );
  }
}
