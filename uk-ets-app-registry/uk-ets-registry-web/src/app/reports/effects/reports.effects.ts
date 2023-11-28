import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  catchError,
  exhaustMap,
  filter,
  map,
  mapTo,
  mergeMap,
  switchMap,
  takeUntil,
  withLatestFrom,
} from 'rxjs/operators';
import { interval, of, timer } from 'rxjs';

import { errors } from '@shared/shared.action';
import { ApiErrorHandlingService } from '@shared/services';
import { ExportFileService } from '@shared/export-file/export-file.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ReportsApiService, UsersBasicInfoApiService } from '@reports/services';
import { select, Store } from '@ngrx/store';
import { selectUrl } from '@registry-web/shared/router.selector';
import { ROUTER_NAVIGATED } from '@ngrx/router-store';
import {
  selectIsReportSuccess,
  selectReportRequestingUsers,
  selectReportRole,
} from '@reports/selectors';
import { isAuthenticated, selectUrid } from '@registry-web/auth/auth.selector';
import {
  clearReportState,
  createReportRequest,
  createReportRequestSuccess,
  downloadReport,
  loadReports,
  loadReportsSuccess,
  loadReportTypes,
  loadReportTypesSuccess,
  loadReportUsersSuccess,
  triggerLoadReportsTimeoutTimer,
  deactivateLoadReportsTimeoutTimer,
} from '@reports/actions';
import { showTimeoutDialog } from '@timeout/store/timeout.actions';
import { selectSessionExpirationNotificationOffset } from '@shared/shared.selector';
import { TimeoutBannerService } from '@timeout/service/timeout-banner.service';

@Injectable()
export class ReportsEffects {
  loadReports$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadReports),
      withLatestFrom(
        this.store.select(selectUrid),
        this.store.select(selectReportRole)
      ),
      switchMap(([, urid, role]) =>
        this.reportsApiService.loadReports(urid, role).pipe(
          map((reports) => loadReportsSuccess({ reports })),
          catchError((error) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                ),
              })
            )
          )
        )
      )
    )
  );

  triggerLoadReportsTimeoutTimer$ = createEffect(() =>
    this.actions$.pipe(
      ofType(triggerLoadReportsTimeoutTimer),
      withLatestFrom(
        this.store.select(selectSessionExpirationNotificationOffset)
      ),
      switchMap(([action, offset]) => {
        return timer(
          this.timeoutBannerService.getExpirationPeriod() -
            Number(offset) * 1000
        ).pipe(
          takeUntil(
            this.actions$.pipe(ofType(deactivateLoadReportsTimeoutTimer))
          ),
          mapTo(showTimeoutDialog())
        );
      })
    )
  );

  loadReportUsers$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadReportsSuccess),
      filter((action) => action.reports && action.reports.length > 0),
      withLatestFrom(this.store.pipe(select(selectReportRequestingUsers))),
      switchMap(([_, urids]) => {
        return this.userBasicInfoApiService.loadReportUsers(urids).pipe(
          map((users) =>
            loadReportUsersSuccess({
              users: users,
            })
          ),
          catchError((error) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                ),
              })
            )
          )
        );
      })
    )
  );

  createReportRequest$ = createEffect(() =>
    this.actions$.pipe(
      ofType(createReportRequest),
      withLatestFrom(this.store.select(selectReportRole)),
      exhaustMap(([action, role]) =>
        this.reportsApiService
          .requestReport({
            type: action.request.type,
            requestingRole: role,
            queryInfo: action.request.queryInfo,
          })
          .pipe(
            map((response) => createReportRequestSuccess({ response })),
            catchError((error) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          )
      )
    )
  );

  // TODO: there is an error here, we set dispatch false but in case of error we dispatch action
  downloadReportFile$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(downloadReport),
        mergeMap((action) =>
          this.reportsApiService.downloadReport(action.reportId).pipe(
            map((result) =>
              this.exportFileService.export(
                result.body,
                this.exportFileService.getContentDispositionFilename(
                  result.headers.get('Content-Disposition')
                )
              )
            ),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          )
        )
      ),
    { dispatch: false }
  );

  pollDownloads$ = createEffect(() =>
    interval(5000).pipe(
      withLatestFrom(
        this.store.select(selectUrl),
        this.store.select(isAuthenticated)
      ),
      filter(
        ([_, route, isAuthenticated]) =>
          route?.includes('/reports/downloads') && isAuthenticated
      ),
      mapTo(loadReports())
    )
  );

  clearReportsState$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ROUTER_NAVIGATED),
      withLatestFrom(this.store.pipe(select(selectIsReportSuccess))),
      filter(([action, reportSuccess]) => reportSuccess != null),
      map(() => clearReportState())
    )
  );

  loadReportTypes$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadReportTypes),
      withLatestFrom(this.store.select(selectReportRole)),
      switchMap(([, role]) =>
        this.reportsApiService.loadEligibleReportTypes(role).pipe(
          map((reportTypes) => loadReportTypesSuccess({ reportTypes })),
          catchError((error) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                ),
              })
            )
          )
        )
      )
    )
  );

  constructor(
    private actions$: Actions,
    private reportsApiService: ReportsApiService,
    private userBasicInfoApiService: UsersBasicInfoApiService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private exportFileService: ExportFileService,
    private readonly timeoutBannerService: TimeoutBannerService,
    private store: Store
  ) {}
}
