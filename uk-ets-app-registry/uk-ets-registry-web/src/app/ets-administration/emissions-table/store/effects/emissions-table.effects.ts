import { Actions, createEffect, ofType } from '@ngrx/effects';
import { EmissionsTableService } from '@emissions-table/services/emissions-table.service';
import { Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import {
  catchError,
  exhaustMap,
  map,
  mergeMap,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { HttpErrorResponse, HttpEvent } from '@angular/common/http';
import { errors, navigateTo } from '@shared/shared.action';
import { of } from 'rxjs';
import { ApiErrorHandlingService } from '@shared/services';
import { Injectable } from '@angular/core';
import {
  processSelectedFileError,
  uploadEmissionsTableFileSuccess,
  uploadSelectedEmissionsTableFile,
} from '@shared/file/actions/file-upload-api.actions';
import { UploadStatus } from '@shared/model/file';
import { requestUploadSelectedEmissionsTableFile } from '@shared/file/actions/file-upload-form.actions';
import { SharedEffects } from '@shared/shared.effect';
import {
  cancelClicked,
  cancelEmissionsTableUpload,
  downloadErrorsCSV,
  submitEmissionsTableRequest,
  submitEmissionsTableRequestSuccess,
} from '@emissions-table/store/actions/emissions-table.actions';
import { selectEmissionsTableFile } from '@emissions-table/store/reducers';
import { ExportFileService } from '@shared/export-file/export-file.service';

@Injectable()
export class EmissionsTableEffects {
  constructor(
    private emissionsTableService: EmissionsTableService,
    private exportFileService: ExportFileService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private router: Router,
    private store: Store,
    private sharedEffect: SharedEffects
  ) {}

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  startUploadSelectedEmissionsTable$ = createEffect(() =>
    this.actions$.pipe(
      ofType(requestUploadSelectedEmissionsTableFile),
      map((action) =>
        uploadSelectedEmissionsTableFile({
          file: action.file,
        })
      )
    )
  );

  uploadSelectedEmissionsTable$ = createEffect(() =>
    this.actions$.pipe(
      ofType(uploadSelectedEmissionsTableFile),
      exhaustMap((action) =>
        this.emissionsTableService
          .uploadSelectedEmissionsTable(action.file)
          .pipe(
            mergeMap((event: HttpEvent<any>) => {
              return this.sharedEffect.manageFileUpload(event);
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                processSelectedFileError({
                  status: UploadStatus.Failed,
                }),
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          )
      )
    )
  );

  submitUploadedEmissionsTable$ = createEffect(() =>
    this.actions$.pipe(
      ofType(submitEmissionsTableRequest),
      withLatestFrom(this.store.pipe(select(selectEmissionsTableFile))),
      exhaustMap(([action, fileHeader]) => {
        return this.emissionsTableService
          .submitSelectedEmissionsTable({ otp: action.otp, fileHeader })
          .pipe(
            map((response) => {
              return submitEmissionsTableRequestSuccess({
                requestId: response,
              });
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
    )
  );

  navigateToRequestUploaded$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(uploadEmissionsTableFileSuccess),
      map(() =>
        navigateTo({
          route: `/ets-administration/emissions-table/check-request-and-submit`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  navigateToRequestSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitEmissionsTableRequestSuccess),
      map(() =>
        navigateTo({
          route: `/ets-administration/emissions-table/request-submitted`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelEmissionsTableUpload$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(cancelEmissionsTableUpload),
        map(() =>
          this.router.navigate(['/ets-administration/emissions-table'], {
            skipLocationChange: true,
          })
        )
      );
    },
    { dispatch: false }
  );

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(cancelClicked),
      map((action) =>
        navigateTo({
          route: `/ets-administration/emissions-table/cancel`,
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true,
          },
        })
      )
    );
  });

  // TODO: there is an error here, we set dispatch false but in case of error we dispatch action
  downloadReportFile$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(downloadErrorsCSV),
        mergeMap((action) =>
          this.emissionsTableService.downloadErrorsCSV(action.fileId).pipe(
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
}
