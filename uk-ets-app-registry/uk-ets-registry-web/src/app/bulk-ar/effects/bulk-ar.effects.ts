import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Router } from "@angular/router";
import { ApiErrorHandlingService } from "@shared/services";
import { select, Store } from "@ngrx/store";
import { Injectable } from "@angular/core";
import { errors, navigateTo } from "@shared/shared.action";
import { catchError, exhaustMap, map, mergeMap, switchMap, tap, withLatestFrom } from "rxjs/operators";
import { BulkArService } from "@registry-web/bulk-ar/services";
import { requestUploadSelectedBulkArFile } from "@shared/file/actions/file-upload-form.actions";
import {
  processSelectedFileError,
  uploadBulkARFileSuccess,
  uploadSelectedBulkArFile
} from "@shared/file/actions/file-upload-api.actions";
import { HttpErrorResponse, HttpEvent } from "@angular/common/http";
import { of } from "rxjs";
import { UploadStatus } from "@shared/model/file";
import {
  cancelBulkArWizard,
  cancelClicked,
  submitBulkArRequest,
  submitBulkArRequestSuccess
} from "@registry-web/bulk-ar/actions/bulk-ar.actions";
import { selectBulkArFile } from "@registry-web/bulk-ar/reducers";
import { SharedEffects } from "@shared/shared.effect";

@Injectable()
export class BulkArEffects {
  constructor(
    private bulkArService: BulkArService,
    private actions$: Actions,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private store: Store,
    private sharedEffects: SharedEffects
  ) {}

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras).then((r) => r);
        })
      );
    },
    { dispatch: false }
  );

  startUploadSelectedBulkAr$ = createEffect(() =>
    this.actions$.pipe(
      ofType(requestUploadSelectedBulkArFile),
      map((action) =>
        uploadSelectedBulkArFile({
          file: action.file,
        })
      )
    )
  );

  uploadSelectedBulkAr$ = createEffect(() =>
    this.actions$.pipe(
      ofType(uploadSelectedBulkArFile),
      exhaustMap((action) =>
        this.bulkArService.uploadSelectedBulkArFile(action.file).pipe(
          mergeMap((event: HttpEvent<any>) => {
            return this.sharedEffects.manageFileUpload(event);
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

  submitUploadedBulkAr$ = createEffect(() =>
    this.actions$.pipe(
      ofType(submitBulkArRequest),
      withLatestFrom(this.store.pipe(select(selectBulkArFile))),
      switchMap(([, fileHeader]) => {
        return this.bulkArService.submitSelectedBulkArFile(fileHeader).pipe(
          map((response) => {
            return submitBulkArRequestSuccess({
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
      ofType(submitBulkArRequestSuccess),
      map(() =>
        navigateTo({
          route: `/bulk-ar/request-submitted`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  navigateToRequestUploaded$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(uploadBulkARFileSuccess),
      map(() =>
        navigateTo({
          route: `/bulk-ar/check-request-and-submit`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelUploadBulkArWizard$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(cancelBulkArWizard),
        map(() =>
          this.router.navigate(['/bulk-ar'], {
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
          route: `/bulk-ar/cancel`,
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true,
          },
        })
      )
    );
  });
}
