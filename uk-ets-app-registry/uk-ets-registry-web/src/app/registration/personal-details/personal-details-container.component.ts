import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
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
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-personal-details-input>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PersonalDetailsContainerComponent implements OnInit {
  countries$: Observable<IUkOfficialCountry[]>;
  user$: Observable<IUser>;

  readonly nextRoute = '/registration/work-details';

  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.store.dispatch(clearErrors());
    this.countries$ = this.store.select(selectAllCountries);
    this.user$ = this.store.select(selectUser);
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
