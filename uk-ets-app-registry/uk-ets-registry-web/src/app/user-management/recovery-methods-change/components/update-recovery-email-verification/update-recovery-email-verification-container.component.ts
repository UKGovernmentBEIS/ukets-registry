import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectRecoveryMethodsChangeState } from '../../store/recovery-methods-change.selectors';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import {
  ResetRecoveryEmailStateRequest,
  UpdateRecoveryEmailVerificationRequest,
} from '../../recovery-methods-change.models';
import { recoveryMethodsActions } from '../../store/recovery-methods-change.actions';
import {
  selectErrorDetails,
  selectEtrAddress,
} from '@registry-web/shared/shared.selector';
import { takeUntil, tap } from 'rxjs';
import { injectDestroy } from '@registry-web/shared/utils/inject-destroy';

@Component({
  selector: 'app-update-recovery-email-verification-container',
  template: `
    <app-update-recovery-email-verification
      [state]="state$ | async"
      [etsRegistryHelpEmail]="etsRegistryHelpEmail$ | async"
      [serverSideErrorDetails]="errorDetails$ | async"
      (resendSecurityCode)="onResendSecurityCode()"
      (submitRequest)="onSubmitRequest($event)"
      (resetState)="onResetState($event)"
      (errorDetails)="onError($event)"
    />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateRecoveryEmailVerificationContainerComponent {
  private readonly destroy$ = injectDestroy();

  state$ = this.store.select(selectRecoveryMethodsChangeState);
  etsRegistryHelpEmail$ = this.store.select(selectEtrAddress);
  errorDetails$ = this.store.select(selectErrorDetails);

  constructor(private store: Store) {
    this.state$
      .pipe(
        tap((state) =>
          this.store.dispatch(canGoBack({ goBackRoute: state.caller?.route }))
        ),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  onResendSecurityCode() {
    this.store.dispatch(
      recoveryMethodsActions.REQUEST_RESEND_UPDATE_RECOVERY_EMAIL_SECURITY_CODE()
    );
  }

  onSubmitRequest(request: UpdateRecoveryEmailVerificationRequest) {
    this.store.dispatch(
      recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL_VERIFICATION({
        request,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const errorSummary: ErrorSummary = { errors: details };
    this.store.dispatch(errors({ errorSummary }));
  }

  onResetState(request: ResetRecoveryEmailStateRequest) {
    this.store.dispatch(
      recoveryMethodsActions.RESET_RECOVERY_EMAIL_FROM_STATE({
        request,
      })
    );
  }
}
