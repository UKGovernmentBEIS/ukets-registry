import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { selectAllCountries } from '@shared/shared.selector';
import { IUser } from '@shared/user';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { updateUserHomeDetails } from '../registration.actions';
import { selectUser } from '../registration.selector';

@Component({
  selector: 'app-personal-details-container',
  template: `
    <app-personal-details-input
      [caption]="'Create a registry sign in'"
      [heading]="'Your details'"
      [countries]="countries$ | async"
      [user]="user$ | async"
      [showDifferentCountryLastFiveYears]="true"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PersonalDetailsContainerComponent implements OnInit {
  countries$ = this.store.select(selectAllCountries);
  user$ = this.store.select(selectUser);

  readonly nextRoute = '/registration/work-details';

  constructor(private store: Store, private _router: Router) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.store.dispatch(clearErrors());
  }

  onContinue(value: IUser): void {
    this.store.dispatch(
      updateUserHomeDetails({
        user: value,
      })
    );

    this._router.navigate([this.nextRoute], {
      skipLocationChange: false,
    });
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
