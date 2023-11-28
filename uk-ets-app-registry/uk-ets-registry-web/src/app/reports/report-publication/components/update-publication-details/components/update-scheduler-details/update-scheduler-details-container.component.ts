import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBackInWizards, navigateTo } from '@report-publication/actions';
import { Observable } from 'rxjs';
import { Section } from '@report-publication/model';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { clearErrors, errors } from '@shared/shared.action';
import { submitPublicationDetails } from '@report-publication/components/update-publication-details/actions/update-publication-details.actions';
import { selectUpdatedPublicationDetails } from '@report-publication/components/update-publication-details/reducers/update-publication-details.selector';

@Component({
  selector: 'app-update-scheduler-details-container',
  template: ` <app-update-scheduler-details
      [sectionDetails]="sectionDetails$ | async"
      (errorDetails)="onError($event)"
      (emitter)="onSubmit($event)"
    >
    </app-update-scheduler-details
    ><app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateSchedulerDetailsContainerComponent implements OnInit {
  sectionDetails$: Observable<Section>;
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBackInWizards({ specifyBackLink: '/update-publication-details' })
    );
    this.sectionDetails$ = this.store.select(selectUpdatedPublicationDetails);
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onSubmit(sectionData: Section) {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      submitPublicationDetails({ publicationDetails: sectionData })
    );
  }

  onCancel() {
    this.store.dispatch(
      canGoBackInWizards({
        specifyBackLink: '/update-publication-details/update-scheduler-details',
      })
    );
    this.store.dispatch(
      navigateTo({ specifyLink: '/update-publication-details/cancel' })
    );
  }
}
