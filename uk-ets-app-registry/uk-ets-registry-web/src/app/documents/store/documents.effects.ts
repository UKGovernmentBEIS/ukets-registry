import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { ApiErrorHandlingService } from '@registry-web/shared/services';
import { errors } from '@registry-web/shared/shared.action';
import { switchMap, map, catchError, of, tap, mergeMap } from 'rxjs';
import { DocumentsApiService } from '../service/documents-api.service';
import * as DocumentActions from './documents.actions';
import { DocumentsWizardPath } from '../models/documents-wizard-path.model';
import { ExportFileService } from '@registry-web/shared/export-file/export-file.service';

@Injectable()
export class DocumentsEffects {
  constructor(
    private actions$: Actions,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private store: Store,
    private documentsApiService: DocumentsApiService,
    private exportFileService: ExportFileService
  ) {}

  navigateToUpdateDocumentsWizard$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(DocumentActions.navigateToUpdateDocumentsWizard),
        tap(() =>
          this.router.navigate([`${DocumentsWizardPath.BASE_PATH}`], {
            skipLocationChange: true,
          })
        )
      );
    },
    { dispatch: false }
  );

  fetchCategories$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(DocumentActions.fetchCategories),
      mergeMap(() => {
        return this.documentsApiService.fetchDocumentCategories().pipe(
          map((response) => {
            return DocumentActions.fetchCategoriesSuccess({
              categories: response,
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
    );
  });

  fetchDocumentFile$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(DocumentActions.fetchDocumentFile),
        mergeMap((action: { documentId: number }) => {
          return this.documentsApiService
            .fetchDocumentFile(action.documentId)
            .pipe(
              map((result) => {
                this.exportFileService.export(
                  result.body,
                  this.exportFileService.getContentDispositionFilename(
                    result.headers.get('Content-Disposition')
                  )
                );
              }),
              catchError((error: HttpErrorResponse) =>
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
      );
    },
    { dispatch: false }
  );
}
