import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { checkPasswordRequested } from '../registration.actions';
import { Observable } from 'rxjs';
import { Configuration } from '@shared/configuration/configuration.interface';
import { selectConfigurationRegistry } from '@shared/shared.selector';

@Component({
  selector: 'app-choose-password-container',
  template: `
    <app-choose-password
      [configuration]="configuration$ | async"
      (choosePasswordRequest)="onSubmitChoosePasswordRequest($event)"
      (errorDetails)="onError($event)"
    />
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChoosePasswordContainerComponent implements OnInit {
  readonly previousRoute = '/registration/memorable-phrase';
  configuration$: Observable<Configuration[]>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: this.previousRoute }));
    this.store.dispatch(clearErrors());
    this.configuration$ = this.store.select(selectConfigurationRegistry);
  }

  onSubmitChoosePasswordRequest(request: { newPasswd: string }) {
    this.store.dispatch(
      checkPasswordRequested({
        password: request.newPasswd,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
