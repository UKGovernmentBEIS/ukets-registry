import { Injectable } from '@angular/core';
import { ReportPublicationService } from '@report-publication/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Router } from '@angular/router';
import { ApiErrorHandlingService } from '@shared/services';
import { select, Store } from '@ngrx/store';
import {
  submitPublicationDetails,
  submitSectionDetails,
  submitUpdateRequest,
  submitUpdateRequestSuccess,
} from '@report-publication/components/update-publication-details/actions/update-publication-details.actions';
import { catchError, exhaustMap, map, withLatestFrom } from 'rxjs/operators';
import * as ReportPublicationActions from '@report-publication/actions/report-publication.actions';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { errors, navigateTo } from '@shared/shared.action';
import { selectUpdatedDetailsForCheckAndSubmit } from '@report-publication/components/update-publication-details/reducers/update-publication-details.selector';
import { selectCurrentActivatedRoute } from '@shared/shared.selector';
import { selectReportPublicationSectionId } from '@registry-web/reports/report-publication/selectors';

@Injectable({ providedIn: 'root' })
export class UpdatePublicationDetailsEffects {
  constructor(
    private publicationReportService: ReportPublicationService,
    private actions$: Actions,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private store: Store
  ) {}

  submitSectionDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitSectionDetails),
      map(() =>
        ReportPublicationActions.navigateTo({
          specifyLink: '/update-publication-details/update-scheduler-details',
          extras: { skipLocationChange: true },
        })
      )
    );
  });

  submitPublicationDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitPublicationDetails),
      map(() =>
        ReportPublicationActions.navigateTo({
          specifyLink: '/update-publication-details/check-and-submit',
          extras: { skipLocationChange: true },
        })
      )
    );
  });

  submitUpdateRequest$ = createEffect(() =>
    this.actions$.pipe(
      ofType(submitUpdateRequest),
      withLatestFrom(
        this.store.pipe(select(selectUpdatedDetailsForCheckAndSubmit(true))),
        this.store.select(selectReportPublicationSectionId)
      ),
      exhaustMap(([, updatedPublicationDetails, id]) => {
        updatedPublicationDetails.id = id;
        return this.publicationReportService
          .submitUpdatedPublicationDetails(updatedPublicationDetails)
          .pipe(
            map((response) => {
              return submitUpdateRequestSuccess();
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
      ofType(submitUpdateRequestSuccess),
      withLatestFrom(this.store.pipe(select(selectCurrentActivatedRoute))),
      map(([, snapshotUrl]) =>
        navigateTo({
          route: `${snapshotUrl}/update-publication-details/publication-update-submitted`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });
}
