import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Store } from '@ngrx/store';
import {
  approvalRequired,
  selectAcquiringAccountInfoOrEnrichedUserDefinedAccountInfo,
  selectAllocationType,
  selectAllocationYear,
  selectCalculatedTransactionTypeDescription,
  selectedTransactionBlocks,
  selectEnrichedReturnExcessAllocationTransactionSummaryForSigning,
  selectEnrichedTransactionSummaryForSigning,
  selectExcessAmount,
  selectITLNotification,
  selectTransactionReference,
  selectTransactionType,
  selectTransferringAccountInfo,
} from '@transaction-proposal/reducers';
import {
  AccountInfo,
  AcquiringAccountInfo,
  ProposedTransactionType,
  ReturnExcessAllocationTransactionSummary,
  TransactionBlockSummary,
  TransactionSummary,
} from '@shared/model/transaction';
import {
  selectTransactionApprovalRole,
  selectTransferringAccountInfoFromAccount,
} from '@account-management/account/account-details/account.selector';
import { ActivatedRoute, Router } from '@angular/router';
import { TransactionProposalActions } from '@transaction-proposal/actions';
import { TransactionProposalRoutePaths } from '@transaction-proposal/model';
import { map } from 'rxjs/operators';
import {
  cancelClicked,
  goBackButtonInCheckDetailsPage,
} from '@transaction-proposal/actions/transaction-proposal.actions';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { ItlNotification } from '@shared/model/transaction/itl-notification';
import { ExcessAmountPerAllocationAccount } from '@transaction-proposal/model/transaction-proposal-model';
import { selectErrorSummary } from '@registry-web/shared/shared.selector';
import { ErrorSummary } from '@registry-web/shared/error-summary';

@Component({
  selector: 'app-check-transaction-details-container',
  template: `
    <app-check-transaction-details
      [transferringAccountInfo]="transferringAccountInfo$ | async"
      [reversedTransferringAccountInfo]="
        reversedTransferringAccountInfo$ | async
      "
      [itlNotification]="itlNotification$ | async"
      [calculatedTransactionTypeDescription]="
        calculatedTransactionTypeDescription$ | async
      "
      [transactionBlocks]="transactionBlocks$ | async"
      [acquiringAccountInfo]="acquiringAccountInfo$ | async"
      [transactionSummary]="selectedTransactionSummaryForSigning$ | async"
      [returnExcessAllocationTransactionSummary]="
        selectedEnrichedTransactionSummaryForSigning$ | async
      "
      [totalOverAllocatedQuantity]="selectedTotalOverAllocatedQuantity$ | async"
      [proposedTransactionType]="transactionType$ | async"
      [approvalRequired]="approvalRequired$ | async"
      [approvalRole]="approvalRole$ | async"
      [isAdmin]="isAdmin$ | async"
      [allocationYear]="allocationYear$ | async"
      [allocationType]="allocationType$ | async"
      [transactionReference]="transactionReference$ | async"
      [errorSummary]="errorSummary$ | async"
      (transactionChecked)="onContinue($event)"
      (navigateToEmitter)="onStepBack($event)"
    ></app-check-transaction-details>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckTransactionDetailsContainerComponent
  implements OnInit, OnDestroy
{
  private readonly destroy$ = new Subject<void>();

  calculatedTransactionTypeDescription$: Observable<string>;
  transferringAccountInfo$: Observable<AccountInfo>;
  reversedTransferringAccountInfo$: Observable<AccountInfo>;
  transactionBlocks$: Observable<TransactionBlockSummary[]>;
  acquiringAccountInfo$: Observable<AcquiringAccountInfo>;
  transactionType$: Observable<ProposedTransactionType>;
  selectedTransactionSummaryForSigning$: Observable<TransactionSummary>;
  approvalRequired$: Observable<boolean>;
  approvalRole$: Observable<string>;
  isAdmin$: Observable<boolean>;
  isEtsTransaction: boolean;
  itlNotification$: Observable<ItlNotification>;
  allocationYear$: Observable<number>;
  allocationType$: Observable<string>;
  transactionReference$: Observable<string>;
  selectedEnrichedTransactionSummaryForSigning$: Observable<ReturnExcessAllocationTransactionSummary>;
  selectedTotalOverAllocatedQuantity$: Observable<ExcessAmountPerAllocationAccount>;
  errorSummary$: Observable<ErrorSummary>;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.isAdmin$ = this.store.select(isAdmin);
    this.transactionType$ = this.store.select(selectTransactionType);
    this.itlNotification$ = this.store.select(selectITLNotification);
    this.store.dispatch(goBackButtonInCheckDetailsPage());
    this.calculatedTransactionTypeDescription$ = this.store.select(
      selectCalculatedTransactionTypeDescription
    );
    this.reversedTransferringAccountInfo$ = this.store.select(
      selectTransferringAccountInfo
    );
    //TODO: Remove account selectors from transaction proposal (UKETS-4581)
    this.transferringAccountInfo$ = this.store.select(
      selectTransferringAccountInfoFromAccount
    );
    this.transactionBlocks$ = this.store.select(selectedTransactionBlocks);
    this.acquiringAccountInfo$ = this.store.select(
      selectAcquiringAccountInfoOrEnrichedUserDefinedAccountInfo
    );
    this.selectedTransactionSummaryForSigning$ = this.store.select(
      selectEnrichedTransactionSummaryForSigning
    );
    this.selectedEnrichedTransactionSummaryForSigning$ = this.store.select(
      selectEnrichedReturnExcessAllocationTransactionSummaryForSigning
    );
    this.allocationYear$ = this.store.select(selectAllocationYear);
    this.allocationType$ = this.store.select(selectAllocationType);
    this.approvalRequired$ = this.store.select(approvalRequired);
    //TODO: Remove account selectors from transaction proposal (UKETS-4581)
    this.approvalRole$ = this.store.select(selectTransactionApprovalRole);
    this.transactionReference$ = this.store.select(selectTransactionReference);
    this.selectedTotalOverAllocatedQuantity$ =
      this.store.select(selectExcessAmount);
    this.errorSummary$ = this.store.select(selectErrorSummary);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onContinue($event: { comment: string; otpCode: string }) {
    this.store.dispatch(
      TransactionProposalActions.setCommentAndOtpCode({
        comment: $event.comment,
        otpCode: $event.otpCode,
      })
    );
  }

  onStepBack(step: TransactionProposalRoutePaths) {
    switch (step) {
      case TransactionProposalRoutePaths['select-transaction-type']:
        this.store.dispatch(
          TransactionProposalActions.navigateToSelectTransactionType()
        );
        break;
      case TransactionProposalRoutePaths['select-unit-types-quantity']:
        this.store.dispatch(
          TransactionProposalActions.navigateToSelectUnitTypesAndQuantity()
        );
        break;
      case TransactionProposalRoutePaths['specify-acquiring-account']:
        this.store.dispatch(
          TransactionProposalActions.navigateToSpecifyAcquiringAccount()
        );
        break;
      case TransactionProposalRoutePaths['set-transaction-reference']:
        this.store.dispatch(
          TransactionProposalActions.navigateToTransactionReferencePage()
        );
        break;
    }
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
