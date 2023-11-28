import { Actions, createEffect, ofType } from '@ngrx/effects';
import { AllocationTableService } from '@allocation-table/services/allocation-table.service';
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
import {
  cancelAllocationTableWizard,
  cancelClicked,
  downloadAllocationTableErrorsCSV,
  submitAllocationTableRequest,
  submitAllocationTableRequestSuccess,
} from '@allocation-table/actions/allocation-table.actions';
import { HttpErrorResponse, HttpEvent } from '@angular/common/http';
import { errors, navigateTo } from '@shared/shared.action';
import { of } from 'rxjs';
import { ApiErrorHandlingService } from '@shared/services';
import { Injectable } from '@angular/core';
import { selectAllocationTableFile } from '@allocation-table/reducers/allocation-table.selector';
import {
  processSelectedFileError,
  uploadAllocationTableFileSuccess,
  uploadSelectedAllocationTableFile,
} from '@shared/file/actions/file-upload-api.actions';
import { UploadStatus } from '@shared/model/file';
import { requestUploadSelectedAllocationTableFile } from '@shared/file/actions/file-upload-form.actions';
import { SharedEffects } from '@shared/shared.effect';
import { ExportFileService } from '@shared/export-file/export-file.service';

@Injectable()
export class AllocationTableEffects {
  constructor(
    private allocationTableService: AllocationTableService,
    private exportFileService: ExportFileService,
    private actions$: Actions,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
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

  startUploadSelectedAllocationTable$ = createEffect(() =>
    this.actions$.pipe(
      ofType(requestUploadSelectedAllocationTableFile),
      map((action) =>
        uploadSelectedAllocationTableFile({
          file: action.file,
        })
      )
    )
  );

  uploadSelectedAllocationTable$ = createEffect(() =>
    this.actions$.pipe(
      ofType(uploadSelectedAllocationTableFile),
      exhaustMap((action) =>
        this.allocationTableService
          .uploadSelectedAllocationTable(action.file)
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

  submitUploadedAllocationTable$ = createEffect(() =>
    this.actions$.pipe(
      ofType(submitAllocationTableRequest),
      withLatestFrom(this.store.pipe(select(selectAllocationTableFile))),
      switchMap(([, fileHeader]) => {
        return this.allocationTableService
          .submitSelectedAllocationTable(fileHeader)
          .pipe(
            map((response) => {
              return submitAllocationTableRequestSuccess({
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

  navigateToRequestSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitAllocationTableRequestSuccess),
      map(() =>
        navigateTo({
          route: `/ets-administration/allocation-table/request-submitted`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  navigateToRequestUploaded$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(uploadAllocationTableFileSuccess),
      map(() =>
        navigateTo({
          route: `/ets-administration/allocation-table/check-request-and-submit`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelUploadAllocationTableWizard$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(cancelAllocationTableWizard),
        map(() =>
          this.router.navigate(['/ets-administration/allocation-table'], {
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
          route: `/ets-administration/allocation-table/cancel`,
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true,
          },
        })
      )
    );
  });

  downloadAllocationTableErrorFile$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(downloadAllocationTableErrorsCSV),
        mergeMap((action) =>
          this.allocationTableService.downloadErrorsCSV(action.fileId).pipe(
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
