import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import {
  ProposedTransactionType,
  TransactionBlockSummary,
  TransactionBlockSummaryResult,
} from '@shared/model/transaction';
import { Observable } from 'rxjs';
import {
  selectedTransactionBlocks,
  selectIsETSTransaction,
  selectITLNotification,
  selectToBeReplacedUnitsAccountParts,
  selectTotalExcessAmount,
  selectTransactionType,
  transactionBlockSummaryResult,
} from '@transaction-proposal/reducers';
import { Store } from '@ngrx/store';
import { errors } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import {
  SelectUnitTypesActions,
  TransactionProposalActions,
} from '@transaction-proposal/actions';
import { cancelClicked } from '@transaction-proposal/actions/transaction-proposal.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { ItlNotification } from '@shared/model/transaction/itl-notification';
import { UserDefinedAccountParts } from '@registry-web/shared/model/account';

@Component({
  selector: 'app-select-unit-types-and-quantity-container',
  template: `
    <app-select-unit-types-and-quantity
      *ngIf="(isEtsTransaction$ | async) === false"
      [transactionType]="transactionType$ | async"
      [itlNotification]="itlNotification$ | async"
      [transactionBlockSummaries]="
        (transactionBlockSummaryResult$ | async).result
      "
      [selectedTransactionBlockSummaries]="
        selectedTransactionBlockSummaries$ | async
      "
      [toBeReplacedUnitsAccountParts]="toBeReplacedUnitsAccountParts$ | async"
      (selectedSummaries)="onSelectSummaries($event)"
      (errorDetails)="onError($event)"
    ></app-select-unit-types-and-quantity>
    <app-specify-quantity
      *ngIf="isEtsTransaction$ | async"
      [transactionType]="transactionType$ | async"
      [itlNotification]="itlNotification$ | async"
      [transactionBlockSummaries]="
        (transactionBlockSummaryResult$ | async).result
      "
      [selectedTransactionBlockSummaries]="
        selectedTransactionBlockSummaries$ | async
      "
      [excessAmount]="excessAmount | async"
      (selectedSummaries)="onSelectSummaries($event)"
    ></app-specify-quantity>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectUnitTypesAndQuantityContainerComponent implements OnInit {
  transactionType$: Observable<ProposedTransactionType>;
  transactionBlockSummaryResult$: Observable<TransactionBlockSummaryResult>;
  selectedTransactionBlockSummaries$: Observable<TransactionBlockSummary[]>;
  toBeReplacedUnitsAccountParts$: Observable<UserDefinedAccountParts>;
  isEtsTransaction$: Observable<boolean>;
  itlNotification$: Observable<ItlNotification>;
  excessAmount: Observable<number>;
  selectedTotalOverAllocatedQuantity$: Observable<number>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      TransactionProposalActions.goBackButtonInSpecifyUnitTypesAndQuantity()
    );
    this.toBeReplacedUnitsAccountParts$ = this.store.select(
      selectToBeReplacedUnitsAccountParts
    );
    this.excessAmount = this.store.select(selectTotalExcessAmount);
    this.isEtsTransaction$ = this.store.select(selectIsETSTransaction);
    this.transactionType$ = this.store.select(selectTransactionType);
    this.itlNotification$ = this.store.select(selectITLNotification);
    this.transactionBlockSummaryResult$ = this.store.select(
      transactionBlockSummaryResult
    );
    this.selectedTransactionBlockSummaries$ = this.store.select(
      selectedTransactionBlocks
    );
  }

  onSelectSummaries({
    selectedTransactionBlockSummaries,
    clearNextStepsInWizard,
    toBeReplacedUnitsHoldingAccountParts,
  }) {
    this.store.dispatch(
      SelectUnitTypesActions.setSelectedBlockSummaries({
        selectedTransactionBlockSummaries,
        clearNextStepsInWizard,
        toBeReplacedUnitsHoldingAccountParts,
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
