import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { Section } from '@report-publication/model';
import { canGoBackInWizards, navigateTo } from '@report-publication/actions';
import { selectSection } from '@report-publication/selectors';
import { selectUpdatedDetailsForCheckAndSubmit } from '@report-publication/components/update-publication-details/reducers/update-publication-details.selector';
import { submitUpdateRequest } from '@report-publication/components/update-publication-details/actions/update-publication-details.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';

@Component({
  selector: 'app-check-your-answers-container',
  template: `<app-check-your-answers
      [sectionDetails]="sectionDetails$ | async"
      [updatedDetails]="updatedDetails$ | async"
      (navigateToEmitter)="navigateTo($event)"
      (submitRequest)="submitChanges()"
      (errorDetails)="onError($event)"
    >
    </app-check-your-answers
    ><app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckYourAnswersContainerComponent implements OnInit {
  sectionDetails$: Observable<Section>;
  updatedDetails$: Observable<any>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBackInWizards({
        specifyBackLink: '/update-publication-details/update-scheduler-details',
      })
    );
    this.sectionDetails$ = this.store.select(selectSection);
    this.updatedDetails$ = this.store.select(
      selectUpdatedDetailsForCheckAndSubmit(false)
    );
  }

  navigateTo(routePath: string) {
    this.store.dispatch(
      navigateTo({
        specifyLink: routePath,
        extras: { skipLocationChange: true },
      })
    );
  }

  submitChanges() {
    this.store.dispatch(submitUpdateRequest());
  }

  onCancel() {
    this.store.dispatch(
      canGoBackInWizards({
        specifyBackLink: '/update-publication-details/check-and-submit',
      })
    );
    this.store.dispatch(
      navigateTo({ specifyLink: '/update-publication-details/cancel' })
    );
  }

  onError(value: ErrorDetail) {
    const summary: ErrorSummary = {
      errors: [value],
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
