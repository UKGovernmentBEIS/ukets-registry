import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { clearErrors, errors } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBackInWizards, navigateTo } from '@report-publication/actions';
import { submitFileYear } from '@report-publication/components/upload-publication-file/actions/upload-publication-file.actions';

@Component({
  selector: 'app-add-publication-year-container',
  template: `<app-add-publication-year
      (emitter)="submitFileYear($event)"
      (errorDetails)="onError($event)"
    >
    </app-add-publication-year>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddPublicationYearContainerComponent implements OnInit {
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBackInWizards({ specifyBackLink: '/upload-publication-file' })
    );
  }

  submitFileYear(fileYear: number) {
    this.store.dispatch(clearErrors());
    this.store.dispatch(submitFileYear({ fileYear: fileYear }));
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      canGoBackInWizards({
        specifyBackLink: '/upload-publication-file/add-publication-year',
      })
    );
    this.store.dispatch(
      navigateTo({ specifyLink: '/upload-publication-file/cancel' })
    );
  }
}
