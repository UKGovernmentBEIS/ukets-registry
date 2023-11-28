import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { FileBase } from '@shared/model/file';
import { canGoBack, errors, navigateTo } from '@shared/shared.action';
import { Observable } from 'rxjs';
import {
  cancelClicked,
  clearEmissionsTableUpload,
  submitEmissionsTableRequest,
} from '@emissions-table/store/actions';
import { selectEmissionsTableFile } from '@emissions-table/store/reducers';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { selectErrorDetailByErrorId } from '@shared/shared.selector';

@Component({
  selector: 'app-check-request-and-submit-container',
  template: `
    <app-check-request-and-submit
      [fileHeader]="fileHeader$ | async"
      [serverSideErrorDetails]="errorDetails$ | async"
      (navigateBackEmitter)="onStepBack($event)"
      (emissionsTableSubmitted)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-check-request-and-submit>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckRequestAndSubmitContainerComponent implements OnInit {
  fileHeader$: Observable<FileBase>;
  errorDetails$: Observable<ErrorDetail[]>;

  constructor(private activatedRoute: ActivatedRoute, private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/ets-administration/emissions-table`,
      })
    );
    this.fileHeader$ = this.store.select(selectEmissionsTableFile);
    this.errorDetails$ = this.store.select(selectErrorDetailByErrorId, {
      errorId: 'INVALID_OTP_CODE',
    });
  }

  onStepBack(path: string): void {
    this.store.dispatch(clearEmissionsTableUpload());
    this.store.dispatch(
      navigateTo({
        route: `/ets-administration/${path}`,
      })
    );
  }

  onContinue(otp: string): void {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.store.dispatch(submitEmissionsTableRequest({ otp }));
  }

  onCancel(): void {
    this.store.dispatch(
      cancelClicked({ route: this.activatedRoute.snapshot['_routerState'].url })
    );
  }

  onError(details: ErrorDetail[]): void {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
