import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent } from '@angular/common/http';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { requestUploadSelectedPublicationFile } from '@shared/file/actions/file-upload-form.actions';
import {
  catchError,
  exhaustMap,
  map,
  mergeMap,
  withLatestFrom,
} from 'rxjs/operators';
import {
  processSelectedFileError,
  uploadReportPublicationFileSuccess,
  uploadSelectedPublicationReportFile,
} from '@shared/file/actions/file-upload-api.actions';
import { of } from 'rxjs';
import { UploadStatus } from '@shared/model/file';
import { errors, navigateTo } from '@shared/shared.action';
import { select, Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { ApiErrorHandlingService } from '@shared/services';
import {
  selectPublicationReportFile,
  selectPublicationReportFileYear,
} from '@reports/report-publication/components/upload-publication-file/reducers/upload-publication-file.selector';
import {
  cancelPublicationFileWizard,
  submitFileYear,
  submitPublicationFileRequest,
  submitPublicationFileRequestSuccess,
} from '@reports/report-publication/components/upload-publication-file/actions/upload-publication-file.actions';
import { selectCurrentActivatedRoute } from '@shared/shared.selector';
import { ReportPublicationService } from '@report-publication/services';
import { SharedEffects } from '@shared/shared.effect';
import {
  selectReportPublicationSectionId,
  selectSection,
} from '@registry-web/reports/report-publication/selectors';
import { DisplayType } from '@report-publication/model';

@Injectable()
export class UploadPublicationFileEffects {
  constructor(
    private publicationReportService: ReportPublicationService,
    private actions$: Actions,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private store: Store,
    private sharedEffects: SharedEffects
  ) {}

  startUploadSelectedPublicationReportFile$ = createEffect(() =>
    this.actions$.pipe(
      ofType(requestUploadSelectedPublicationFile),
      map((action) =>
        uploadSelectedPublicationReportFile({
          file: action.file,
        })
      )
    )
  );

  uploadSelectedPublicationReportFile$ = createEffect(() =>
    this.actions$.pipe(
      ofType(uploadSelectedPublicationReportFile),
      withLatestFrom(this.store.pipe(select(selectSection))),
      exhaustMap(([action, section]) =>
        this.publicationReportService
          .uploadSelectedPublicationReportFile(action.file, section.displayType)
          .pipe(
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

  submitUploadedPublicationReportFile$ = createEffect(() =>
    this.actions$.pipe(
      ofType(submitPublicationFileRequest),
      withLatestFrom(
        this.store.pipe(select(selectPublicationReportFile)),
        this.store.pipe(select(selectPublicationReportFileYear)),
        this.store.pipe(select(selectReportPublicationSectionId))
      ),
      exhaustMap(([, fileHeader, fileYear, id]) => {
        return this.publicationReportService
          .submitSelectedPublicationReportFile({
            ...fileHeader,
            year: fileYear,
            sectionId: id,
          })
          .pipe(
            map((response) => {
              return submitPublicationFileRequestSuccess();
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
      ofType(submitPublicationFileRequestSuccess),
      withLatestFrom(this.store.pipe(select(selectCurrentActivatedRoute))),
      map(([, snapshotUrl]) =>
        navigateTo({
          route: `${snapshotUrl}/upload-publication-file/publication-file-submitted`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  navigateToAddPublicationYear$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(uploadReportPublicationFileSuccess),
      withLatestFrom(
        this.store.pipe(select(selectCurrentActivatedRoute)),
        this.store.pipe(select(selectSection))
      ),
      map(([, snapshotUrl, section]) => {
        if (
          section.displayType === DisplayType.ONE_FILE ||
          section.displayType === DisplayType.MANY_FILES
        ) {
          return navigateTo({
            route: `${snapshotUrl}/upload-publication-file/check-answers-and-submit`,
            extras: {
              skipLocationChange: true,
            },
          });
        } else {
          return navigateTo({
            route: `${snapshotUrl}/upload-publication-file/add-publication-year`,
            extras: {
              skipLocationChange: true,
            },
          });
        }
      })
    );
  });

  navigateToCheckAnswers$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitFileYear),
      withLatestFrom(this.store.pipe(select(selectCurrentActivatedRoute))),
      map(([, snapshotUrl]) =>
        navigateTo({
          route: `${snapshotUrl}/upload-publication-file/check-answers-and-submit`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelUploadPublicationReportFileWizard$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(cancelPublicationFileWizard),
        withLatestFrom(this.store.pipe(select(selectCurrentActivatedRoute))),
        map(() =>
          this.router.navigate(['/upload-publication-file'], {
            skipLocationChange: true,
          })
        )
      );
    },
    { dispatch: false }
  );
}
