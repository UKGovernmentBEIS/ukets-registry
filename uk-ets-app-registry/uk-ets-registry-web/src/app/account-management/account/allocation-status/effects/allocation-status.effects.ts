import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import { ApiErrorHandlingService } from '@shared/services';
import {
  cancel,
  continueToUpdateRequestVerification,
  fetchAllocationStatus,
  fetchAllocationStatusSuccess,
  navigateToAccountAllocation,
  navigateToCancel,
  navigateToVerificationPage,
  prepareWizard,
  resetAllocationStatusState,
  updateAllocationStatus,
  updateAllocationStatusSuccess,
} from '@allocation-status/actions/allocation-status.actions';
import {
  catchError,
  concatMap,
  exhaustMap,
  map,
  mergeMap,
  withLatestFrom,
} from 'rxjs/operators';
import { canGoBack, errors, navigateTo } from '@shared/shared.action';
import { MenuItemEnum } from '@registry-web/account-management/account/account-details/model/account-side-menu.model';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { AllocationStatusRoutePathsModel } from '@allocation-status/model/allocation-status-route-paths.model';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { AccountAllocationService } from '@account-management/service/account-allocation.service';
import { selectUpdateRequest } from '@allocation-status/reducers/allocation-status.selector';
import { getRouteFromArray } from '@shared/utils/router.utils';

@Injectable()
export class AllocationStatusEffect {
  constructor(
    private allocationStatusService: AccountAllocationService,
    private actions$: Actions,
    private store: Store,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private activatedRoute: ActivatedRoute
  ) {}

  prepareWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(prepareWizard),
      concatMap((action: { accountId: string }) => {
        return [
          fetchAllocationStatus(action),
          canGoBack({
            goBackRoute: `/account/${action.accountId}`,
            extras: {
              queryParams: {
                selectedSideMenu: MenuItemEnum.ALLOCATION,
              },
            },
          }),
        ];
      })
    );
  });

  fetchAllocationStatus$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fetchAllocationStatus),
      mergeMap((action: { accountId: string }) => {
        return this.allocationStatusService
          .fetchAllocationStatus(action.accountId)
          .pipe(
            map((result) => {
              return fetchAllocationStatusSuccess({ allocationStatus: result });
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          );
      })
    );
  });

  continueToUpdateRequestVerification$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(continueToUpdateRequestVerification),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      concatMap(([action, accountId]) => {
        return [
          canGoBack({
            goBackRoute: `/account/${accountId}/${AllocationStatusRoutePathsModel.ALLOCATION_STATUS}`,
          }),
          navigateToVerificationPage(),
        ];
      })
    );
  });

  navigateToVerificationPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToVerificationPage),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      concatMap(([action, accountId]) => {
        return [
          canGoBack({
            goBackRoute: `/account/${accountId}/${AllocationStatusRoutePathsModel.ALLOCATION_STATUS}`,
          }),
          navigateTo({
            route: getRouteFromArray([
              'account',
              accountId,
              AllocationStatusRoutePathsModel.ALLOCATION_STATUS,
              AllocationStatusRoutePathsModel.CHECK_UPDATE_REQUEST,
            ]),
          }),
        ];
      })
    );
  });

  updateAllocationStatus$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(updateAllocationStatus),
      withLatestFrom(
        this.store.pipe(select(selectAccountId)),
        this.store.pipe(select(selectUpdateRequest))
      ),
      exhaustMap(([action, accountId, updateRequest]) =>
        this.allocationStatusService
          .updateAllocationStatus(accountId, updateRequest)
          .pipe(
            concatMap((response: string) => {
              return [
                updateAllocationStatusSuccess({
                  updatedAccountId: response,
                }),
                navigateToAccountAllocation(),
              ];
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          )
      )
    );
  });

  navigateToAccountAllocation$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToAccountAllocation, cancel),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      concatMap(([action, accountId]) => {
        return [
          canGoBack({ goBackRoute: null }),
          navigateTo({
            route: `/account/${accountId}`,
            extras: {
              queryParams: {
                selectedSideMenu: MenuItemEnum.ALLOCATION,
              },
            },
          }),
          resetAllocationStatusState(),
        ];
      })
    );
  });

  navigateToCancel$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToCancel),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      concatMap(([action, accountId]) => [
        canGoBack({
          goBackRoute: this.activatedRoute.snapshot['_routerState'].url,
        }),
        navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AllocationStatusRoutePathsModel.ALLOCATION_STATUS,
            AllocationStatusRoutePathsModel.CANCEL_UPDATE_REQUEST,
          ]),
        }),
      ])
    );
  });
}
