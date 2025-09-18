import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import { IUser } from '@shared/user';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { selectHasWorkMobilePhone, selectUser } from '../registration.selector';
import {
  hasWorkMobilePhoneChange,
  mobileNumberVerificationStatusChange,
  updateUserWorkDetails,
} from '../registration.actions';
import { MobileNumberVerificationStatus } from '@registry-web/shared/form-controls/uk-select-phone';

@Component({
  selector: 'app-work-details-container',
  template: `
    <app-work-details-input
      [caption]="'Create a registry sign in'"
      [heading]="'Your work details'"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      [user]="user$ | async"
      [hasWorkMobilePhone]="hasWorkMobilePhone$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
      (hasWorkMobilePhoneChange)="onHasWorkMobilePhoneChange($event)"
      (mobileNumberVerificationStatusChange)="
        onMobileNumberVerificationStatusChange($event)
      "
    />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkDetailsContainerComponent implements OnInit {
  countryCodes$ = this.store.select(selectCountryCodes);
  countries$ = this.store.select(selectAllCountries);
  user$ = this.store.select(selectUser);
  hasWorkMobilePhone$ = this.store.select(selectHasWorkMobilePhone);

  readonly nextRoute = '/registration/memorable-phrase';
  readonly previousRoute = '/registration/personal-details';

  constructor(private store: Store, private _router: Router) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: this.previousRoute }));
    this.store.dispatch(clearErrors());
  }

  onContinue(value: IUser): void {
    this.store.dispatch(
      updateUserWorkDetails({
        user: value,
      })
    );
    this._router.navigate([this.nextRoute], {
      skipLocationChange: false,
    });
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = { errors: details };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onHasWorkMobilePhoneChange(hasWorkMobilePhone: boolean) {
    this.store.dispatch(hasWorkMobilePhoneChange({ hasWorkMobilePhone }));
  }

  onMobileNumberVerificationStatusChange(
    mobileNumberVerificationStatus: MobileNumberVerificationStatus
  ) {
    this.store.dispatch(
      mobileNumberVerificationStatusChange({ mobileNumberVerificationStatus })
    );
  }
}
