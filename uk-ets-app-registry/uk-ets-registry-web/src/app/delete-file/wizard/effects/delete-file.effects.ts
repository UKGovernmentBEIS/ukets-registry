import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { ApiErrorHandlingService } from '@registry-web/shared/services';
import { DeleteFileService } from '@delete-file/wizard/services/delete-file.service';
import { Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { catchError, map, mergeMap, withLatestFrom } from 'rxjs/operators';
import { errors, navigateTo } from '@registry-web/shared/shared.action';
import {
  enterDeleteFileWizard,
  setDeleteFileName,
  submitDeleteFile,
} from '@delete-file/wizard/actions/delete-file.actions';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { selectOriginatingPath } from '@registry-web/delete-file/wizard/reducers/delete-file.selector';

@Injectable()
export class DeleteFileEffects {
  constructor(
    private actions$: Actions,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private deleteFileService: DeleteFileService,
    private router: Router,
    private store: Store
  ) {}

  enterDeleteFileWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(enterDeleteFileWizard),
      map(() =>
        navigateTo({
          route: `/delete-file`,
        })
      )
    );
  });

  submitDeleteFile$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitDeleteFile),
      mergeMap((action) => {
        return this.deleteFileService
          .submitDeleteFile(
            action.id,
            action.fileId,
            action.documentsRequestType
          )
          .pipe(
            map((result) => setDeleteFileName({ fileName: action.fileName })),
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

  navigateToDeleteSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(setDeleteFileName),
      withLatestFrom(this.store.pipe(select(selectOriginatingPath))),
      map(([, path]) =>
        navigateTo({
          route: path,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });
}
