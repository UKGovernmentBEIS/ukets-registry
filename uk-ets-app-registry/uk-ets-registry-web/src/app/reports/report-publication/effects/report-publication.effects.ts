import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  exhaustMap,
  mergeMap,
  map,
  tap,
  withLatestFrom,
  catchError,
} from 'rxjs/operators';
import { select, Store } from '@ngrx/store';
import { selectCurrentActivatedRoute } from '@shared/shared.selector';
import { canGoBack } from '@shared/shared.action';
import { empty } from '@shared/shared.util';
import {
  canGoBackInWizards,
  downloadFile,
  getReportPublicationSection,
  getReportPublicationSectionSuccess,
  loadReportPublicationHistory,
  loadReportPublicationHistorySuccess,
  loadReportPublicationSections,
  loadReportPublicationSectionsSuccess,
  navigateTo,
  unpublishFile,
  unpublishFileSuccess,
} from '@report-publication/actions';
import { ReportPublicationService } from '@report-publication/services';
import { ApiErrorHandlingService } from '@registry-web/shared/services';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@registry-web/shared/shared.action';
import { Router } from '@angular/router';
import { selectPublicationFile } from '@report-publication/selectors';
import { selectReportPublicationSectionId } from '@report-publication/selectors';
import { of } from 'rxjs';
import { ExportFileService } from '@registry-web/shared/export-file/export-file.service';

@Injectable()
export class ReportPublicationEffects {
  constructor(
    private actions$: Actions,
    private reportPublicationService: ReportPublicationService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private exportFileService: ExportFileService,
    private store: Store,
    private router: Router
  ) {}

  loadReportPublicationSections$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(loadReportPublicationSections),
      mergeMap((action) =>
        this.reportPublicationService
          .loadReportPublicationSections(action.sectionType)
          .pipe(
            map((result) =>
              loadReportPublicationSectionsSuccess({
                sections: result,
              })
            ),
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

  navigateToBackLink$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(canGoBackInWizards),
      withLatestFrom(this.store.pipe(select(selectCurrentActivatedRoute))),
      map(([action, snapshotUrl]) => {
        return canGoBack({
          goBackRoute: `${snapshotUrl}${
            empty(action.specifyBackLink) ? '' : action.specifyBackLink
          }`,
          extras: {
            skipLocationChange: action.extras?.skipLocationChange,
          },
        });
      })
    );
  });

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(navigateTo),
        withLatestFrom(this.store.pipe(select(selectCurrentActivatedRoute))),
        tap(([action, snapshotUrl]) => {
          if (empty(action.specifyLink)) {
            this.router.navigate([snapshotUrl], action.extras);
          } else {
            this.router.navigate(
              [snapshotUrl + action.specifyLink],
              action.extras
            );
          }
        })
      );
    },
    { dispatch: false }
  );

  getReportPublicationSection$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(getReportPublicationSection),
      withLatestFrom(this.store.select(selectReportPublicationSectionId)),
      mergeMap(([action, id]) =>
        this.reportPublicationService.getReportPublicationSection(id).pipe(
          map((result) =>
            getReportPublicationSectionSuccess({
              section: result,
            })
          ),
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

  loadReportPublicationHistory$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(loadReportPublicationHistory),
      withLatestFrom(this.store.select(selectReportPublicationSectionId)),
      mergeMap(([action, id]) =>
        this.reportPublicationService
          .loadPublicationHistory(String(id), action.sortParameters)
          .pipe(
            map((result) =>
              loadReportPublicationHistorySuccess({
                publicationHistory: result,
              })
            ),
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

  unpublishFile$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(unpublishFile),
      withLatestFrom(this.store.select(selectPublicationFile)),
      exhaustMap(([, file]) =>
        this.reportPublicationService.unpublishFile(file).pipe(
          map(() => unpublishFileSuccess()),
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

  unpublishFileSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(unpublishFileSuccess),
      map(() => navigateTo({}))
    );
  });

  downloadFile$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(downloadFile),
        mergeMap((action) =>
          this.reportPublicationService.downloadFile(action.id).pipe(
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
