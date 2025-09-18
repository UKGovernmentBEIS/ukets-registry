import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { select, Store } from '@ngrx/store';
import {
  selectAllocationType,
  selectCalculatedExcessAmountPerAllocationAccount,
  selectEnrichedReturnExcessAllocationTransactionSummaryForSigning,
  selectEnrichedTransactionSummaryForSigning,
  selectExcessAmount,
  selectITLNotification,
  selectITLNotificationId,
  selectTransactionType,
} from '@transaction-proposal/reducers';
import { TransactionProposalService } from '@transaction-proposal/services';
import { Router } from '@angular/router';
import {
  SelectUnitTypesActions,
  SpecifyAcquiringAccountActions,
  TransactionProposalActions,
} from '@transaction-proposal/actions';
import { map, mergeMap, tap, withLatestFrom } from 'rxjs/operators';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import {
  TRANSACTION_TYPES_VALUES,
  TransactionType,
} from '@shared/model/transaction';
import {
  canGoBack,
  navigateToTransactionProposal,
} from '@shared/shared.action';
import { selectCurrentActivatedRoute } from '@shared/shared.selector';
import { AccountActions } from '@account-management/account/account-details';
import { navigateOutsideTransactionProposal } from '@transaction-proposal/actions/transaction-proposal.actions';
import { TransactionDetailsActions } from '@transaction-management/transaction-details/actions';
import { AllocationType } from '@registry-web/shared/model/allocation';

/**
 * Manages transaction proposal wizard navigation routing side effects
 *
 */
@Injectable()
export class TransactionProposalWizardNavigationEffects {
  constructor(
    private transactionWizardApiService: TransactionProposalService,
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  navigateToSelectTransactionType$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.navigateToSelectTransactionType),
      map(() =>
        TransactionProposalActions.navigateTo({
          route: `/select-transaction-type`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  navigateToSpecifyUnitTypeAndQuantityWhen$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.setSelectedTransactionType),
      withLatestFrom(
        this.store.pipe(select(selectITLNotificationId)),
        this.store.pipe(select(selectTransactionType))
      ),
      map(([, itlNotificationId, transactionType]) => {
        if (
          TRANSACTION_TYPES_VALUES[transactionType.type]
            .hasTransactionITLNotificationId
        ) {
          if (
            (itlNotificationId === null ||
              itlNotificationId.toString() == '') &&
            !TRANSACTION_TYPES_VALUES[transactionType.type]
              .isMandatoryITLNotificationIdForTransaction
          ) {
            return TransactionProposalActions.navigateToSelectUnitTypesAndQuantity();
          } else {
            return TransactionProposalActions.validateITLNotificationId({
              itlNotificationId,
              transactionType: transactionType.type,
            });
          }
        } else {
          return TransactionProposalActions.navigateToSelectUnitTypesAndQuantity();
        }
      })
    );
  });

  navigateToSpecifyUnitTypeAndQuantity$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionProposalActions.navigateToSelectUnitTypesAndQuantity,
        AccountActions.prepareTransactionStateForReturnOfExcess
      ),
      withLatestFrom(
        this.store.pipe(select(selectAccountId)),
        this.store.pipe(select(selectTransactionType)),
        this.store.pipe(select(selectITLNotification))
      ),
      map(([, accountId, transactionType, itlNotification]) =>
        SelectUnitTypesActions.getTransactionBlockSummariesResult({
          accountId,
          transactionType: transactionType.type,
          itlNotificationIdentifier: itlNotification?.notificationIdentifier,
        })
      )
    );
  });

  navigateAfterSettingTransactionBlocks$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(SelectUnitTypesActions.validateSelectedBlockSummariesSuccess),
      concatLatestFrom(() => [
        this.store.select(selectTransactionType),
        this.store.select(selectITLNotification),
      ]),
      map(([, proposedTransactionType, itlNotification]) => {
        if (proposedTransactionType.skipAccountStep || itlNotification) {
          return SpecifyAcquiringAccountActions.populateAcquiringAccount();
        } else {
          return TransactionProposalActions.navigateToSpecifyAcquiringAccount();
        }
      })
    );
  });

  navigateAfterSettingTransactionBlocksNatAndNer$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        SelectUnitTypesActions.validateSelectedBlockSummariesNatAndNerSuccess
      ),
      concatLatestFrom(() => [
        this.store.select(selectTransactionType),
        this.store.select(selectITLNotification),
      ]),
      map(([, proposedTransactionType, itlNotification]) => {
        if (proposedTransactionType.skipAccountStep || itlNotification) {
          return SpecifyAcquiringAccountActions.populateExcessAllocationAcquiringAccounts();
        } else {
          return TransactionProposalActions.navigateToSpecifyAcquiringAccount();
        }
      })
    );
  });

  navigateToSpecifyAcquiringAccount$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.navigateToSpecifyAcquiringAccount),
      map(() =>
        TransactionProposalActions.navigateTo({
          route: `/specify-acquiring-account`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  navigateToTransactionReferencePage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.navigateToTransactionReferencePage),
      map(() =>
        TransactionProposalActions.navigateTo({
          route: `/set-transaction-reference`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  goBackButtonInSetTransactionReferencePage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionProposalActions.goBackButtonInSetTransactionReferencePage
      ),
      withLatestFrom(
        this.store.pipe(select(selectTransactionType)),
        this.store.pipe(select(selectITLNotification)),
        this.store.pipe(select(selectCurrentActivatedRoute))
      ),
      map(([, transactionType, itlNotification, snapshotUrl]) => {
        let specifyBackLink = '';
        if (transactionType.isReversal === true) {
          return canGoBack({
            goBackRoute: snapshotUrl,
            extras: {
              skipLocationChange: true,
            },
          });
        }
        if (transactionType.skipAccountStep === true || itlNotification) {
          specifyBackLink = '/select-unit-types-quantity';
        } else {
          specifyBackLink = '/specify-acquiring-account';
        }
        return canGoBack({
          goBackRoute: snapshotUrl + '/transactions/' + specifyBackLink,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  goBackButtonInCheckDetailsPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.goBackButtonInCheckDetailsPage),
      withLatestFrom(
        this.store.pipe(select(selectTransactionType)),
        this.store.pipe(select(selectITLNotification)),
        this.store.pipe(select(selectCurrentActivatedRoute))
      ),
      map(([, transactionType, itlNotification, snapshotUrl]) => {
        return canGoBack({
          goBackRoute: snapshotUrl + '/transactions/set-transaction-reference', // + specifyBackLink,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  goBackButtonInSpecifyUnitTypesAndQuantity$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionProposalActions.goBackButtonInSpecifyUnitTypesAndQuantity
      ),
      withLatestFrom(
        this.store.pipe(select(selectTransactionType)),
        this.store.pipe(select(selectCurrentActivatedRoute))
      ),
      map(([, transactionType, snapshotUrl]) => {
        let specifyBackLink = snapshotUrl;
        if (transactionType.type !== TransactionType.ExcessAllocation) {
          specifyBackLink =
            snapshotUrl + '/transactions/select-transaction-type';
        }
        return canGoBack({
          goBackRoute: specifyBackLink,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateToSelectUnitTypesQuantitySuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(SelectUnitTypesActions.getTransactionBlockSummaryResultSuccess),
      map(() => {
        return TransactionProposalActions.navigateTo({
          route: `/select-unit-types-quantity`,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateToTransactionReferenceWhen$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        SpecifyAcquiringAccountActions.validateTrustedAccountSuccess,
        SpecifyAcquiringAccountActions.populateAcquiringAccountSuccess,
        SpecifyAcquiringAccountActions.populateExcessAllocationAcquiringAccountsSuccess,
        TransactionDetailsActions.prepareTransactionProposalStateForReversalSuccess
      ),

      map(() => {
        return TransactionProposalActions.navigateTo({
          route: `/set-transaction-reference`,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateToCheckAndSignWhen$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionProposalActions.setTransactionReference
        /*SpecifyAcquiringAccountActions.validateTrustedAccountSuccess,
        SpecifyAcquiringAccountActions.populateAcquiringAccountSuccess,
        TransactionDetailsActions.prepareTransactionProposalStateForReversalSuccess*/
      ),
      withLatestFrom(
        this.store.pipe(select(selectEnrichedTransactionSummaryForSigning)),
        this.store.pipe(
          select(
            selectEnrichedReturnExcessAllocationTransactionSummaryForSigning
          )
        ),
        this.store.pipe(select(selectTransactionType)),
        this.store.pipe(
          select(selectCalculatedExcessAmountPerAllocationAccount)
        )
      ),
      map(
        ([
          ,
          enrichedTransactionSummaryForSigning,
          enrichedReturnExcessAllocationTransactionSummaryForSigning,
          proposedTransactionType,
          excessAmount,
        ]) => {
          if (
            enrichedTransactionSummaryForSigning.identifier ||
            enrichedTransactionSummaryForSigning.identifier
          ) {
            return TransactionProposalActions.navigateToCheckTransactionDetails();
          } /*else if(enrichedTransactionSummaryForSigning.identifier) {
            return TransactionProposalActions.navigateToCheckExcessAllocationTransactionDetails();
          }*/ else if (
            excessAmount != null &&
            excessAmount.returnToNewEntrantsReserveAccount != null &&
            excessAmount.returnToAllocationAccountAmount != null &&
            excessAmount.returnToAllocationAccountAmount > 0 &&
            excessAmount.returnToNewEntrantsReserveAccount > 0
          ) {
            return TransactionProposalActions.enrichReturnExcessAllocationProposalForSigning();
          } else {
            return TransactionProposalActions.enrichProposalForSigning();
          }
        }
      )
    );
  });

  enrichForSigningDone$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionProposalActions.enrichProposalForSigningSuccess,
        TransactionProposalActions.enrichReturnExcessAllocationProposalForSigningSuccess
      ),
      map((props) => {
        if (
          props.transactionSummary.type === TransactionType.ExcessAllocation
        ) {
          return TransactionProposalActions.navigateToCheckExcessAllocationTransactionDetails();
        }
        return TransactionProposalActions.navigateToCheckTransactionDetails();
      })
    );
  });

  navigateToCheckAndSign$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionProposalActions.navigateToCheckTransactionDetails,
        TransactionProposalActions.navigateToCheckExcessAllocationTransactionDetails
      ),
      map(() =>
        TransactionProposalActions.navigateTo({
          route: `/check-transaction-details`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  // navigateToCheckExcessAllocationAndSign$ = createEffect(() => {
  //   return this.actions$.pipe(
  //     ofType(
  //       TransactionProposalActions.navigateToCheckExcessAllocationTransactionDetails
  //     ),
  //     map(() =>
  //       TransactionProposalActions.navigateTo({
  //         route: `/check-excess-allocation-transaction-details`,
  //         extras: {
  //           skipLocationChange: true,
  //         },
  //       })
  //     )
  //   );
  // });

  navigateToProposalSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionProposalActions.submitProposalSuccess,
        TransactionProposalActions.submitReturnExcessAllocationProposalSuccess
      ),
      map(() =>
        TransactionProposalActions.navigateTo({
          route: `/proposal-submitted`,
          extras: { skipLocationChange: true },
        })
      )
    );
  });

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.cancelClicked),
      map((action) =>
        TransactionProposalActions.navigateTo({
          route: `/cancel`,
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelTransactionProposal$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.cancelTransactionProposal),
      withLatestFrom(this.store.select(selectCurrentActivatedRoute)),
      mergeMap(([, snapshotUrl]) => [
        TransactionProposalActions.clearTransactionProposal(),
        TransactionProposalActions.navigateOutsideTransactionProposal({
          route: snapshotUrl,
        }),
      ])
    );
  });

  navigateToTransactionProposal$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(navigateToTransactionProposal),
        tap((action) =>
          this.router.navigate([action.routeSnapshotUrl + '/transactions'])
        )
      );
    },
    { dispatch: false }
  );

  navigateOutsideTransactionProposal$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(navigateOutsideTransactionProposal),
        withLatestFrom(this.store.select(selectCurrentActivatedRoute)),
        tap(([action, snapshotUrl]) => {
          this.router.navigate([snapshotUrl], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TransactionProposalActions.navigateTo),
        withLatestFrom(this.store.select(selectCurrentActivatedRoute)),
        tap(([action, snapshotUrl]) => {
          const url = snapshotUrl + '/transactions' + action.route;
          this.router.navigate([url], action.extras);
        })
      );
    },
    { dispatch: false }
  );
}
