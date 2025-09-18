import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectRecoveryMethodsChangeState } from '../../store/recovery-methods-change.selectors';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import { UpdateRecoveryEmailRequest } from '../../recovery-methods-change.models';
import { recoveryMethodsActions } from '../../store/recovery-methods-change.actions';
import { Router } from '@angular/router';
import { injectDestroy } from '@registry-web/shared/utils/inject-destroy';
import { takeUntil, tap } from 'rxjs';
import { selectErrorDetails } from '@registry-web/shared/shared.selector';

@Component({
  selector: 'app-update-recovery-email-form-container',
  template: `
    <app-update-recovery-email-form
      [state]="state$ | async"
      [serverSideErrorDetails]="errorDetails$ | async"
      (submitRequest)="onSubmitRequest($event)"
      (errorDetails)="onError($event)"
    />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateRecoveryEmailFormContainerComponent {
  private readonly destroy$ = injectDestroy();

  state$ = this.store.select(selectRecoveryMethodsChangeState);
  errorDetails$ = this.store.select(selectErrorDetails);

  constructor(
    private store: Store,
    private router: Router
  ) {
    this.state$
      .pipe(
        tap((state) =>
          this.store.dispatch(
            canGoBack({ goBackRoute: state.originCaller?.route })
          )
        ),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  onSubmitRequest(request: UpdateRecoveryEmailRequest) {
    this.store.dispatch(
      recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL({
        request,
        caller: { route: this.router.url },
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const errorSummary: ErrorSummary = { errors: details };
    this.store.dispatch(errors({ errorSummary }));
  }
}
