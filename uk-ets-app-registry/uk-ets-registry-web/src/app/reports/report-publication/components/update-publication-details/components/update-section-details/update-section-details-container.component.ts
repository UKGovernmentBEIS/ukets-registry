import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBackInWizards, navigateTo } from '@report-publication/actions';
import { Observable } from 'rxjs';
import { Section } from '@report-publication/model';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { clearErrors, errors } from '@shared/shared.action';
import { submitSectionDetails } from '@report-publication/components/update-publication-details/actions/update-publication-details.actions';
import { selectUpdatedSectionDetails } from '@report-publication/components/update-publication-details/reducers/update-publication-details.selector';

@Component({
  selector: 'app-update-section-details-container',
  template: `<app-update-section-details
      [sectionDetails]="sectionDetails$ | async"
      (errorDetails)="onError($event)"
      (emitter)="onContinue($event)"
    ></app-update-section-details
    ><app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateSectionDetailsContainerComponent implements OnInit {
  sectionDetails$: Observable<Section>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBackInWizards({}));
    this.sectionDetails$ = this.store.select(selectUpdatedSectionDetails);
  }

  onContinue(event: Section) {
    this.store.dispatch(clearErrors());
    this.store.dispatch(submitSectionDetails({ sectionDetails: event }));
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      canGoBackInWizards({ specifyBackLink: '/update-publication-details' })
    );
    this.store.dispatch(
      navigateTo({ specifyLink: '/update-publication-details/cancel' })
    );
  }
}
