import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';
import { ResetPasswordRequest } from '../../model';
import {
  resetPassword,
  validateToken
} from '../../store/actions/forgot-password.actions';
import { selectConfigurationRegistry } from "@shared/shared.selector";
import { Observable } from "rxjs";
import { Configuration } from "@shared/configuration/configuration.interface";

@Component({
  selector: 'app-reset-password-container',
  template: `
    <app-reset-password
      (resetPasswordRequest)="onSubmitResetPasswordRequest($event)"
      [configuration]="configuration$ | async"
      (errorDetails)="onError($event)"
    >
    </app-reset-password>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ResetPasswordContainerComponent implements OnInit {
  token: string;
  configuration$: Observable<Configuration[]>;
  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.paramMap.get('token');
    this.configuration$ = this.store.select(selectConfigurationRegistry);
    this.store.dispatch(validateToken({ token: this.token }));
  }

  onSubmitResetPasswordRequest(request: { otp: string; newPasswd: string }) {
    this.store.dispatch(
      resetPassword({
        token: this.token,
        otp: request.otp,
        newPasswd: request.newPasswd
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
