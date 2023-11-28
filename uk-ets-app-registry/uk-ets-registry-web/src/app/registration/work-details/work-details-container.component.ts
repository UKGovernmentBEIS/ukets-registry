import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import { IUser } from '@shared/user';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  selectSameAddress,
  selectSameEmail,
  selectUser,
} from '../registration.selector';
import {
  sameAddress,
  sameEmail,
  updateUserWorkDetails,
} from '../registration.actions';
import { CountryCodeModel } from '@shared/countries/country-code.model';

@Component({
  selector: 'app-work-details-container',
  template: `
    <app-work-details-input
      [caption]="'Create a registry sign in'"
      [heading]="'Your work details'"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      [user]="user$ | async"
      [sameAddress]="sameAddress$ | async"
      [sameEmail]="sameEmail$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
      (copyHomeAddressToWorkAddress)="onAddressCheckChange($event)"
      (copyHomeEmailToWorkEmail)="onEmailCheckChange($event)"
    >
    </app-work-details-input>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkDetailsContainerComponent implements OnInit {
  countryCodes$: Observable<CountryCodeModel[]>;
  countries$: Observable<IUkOfficialCountry[]>;
  user$: Observable<IUser>;

  sameEmail$: Observable<boolean>;
  sameAddress$: Observable<boolean>;

  readonly nextRoute = '/registration/memorable-phrase';
  readonly previousRoute = '/registration/personal-details';

  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: this.previousRoute }));
    this.store.dispatch(clearErrors());
    this.countryCodes$ = this.store.select(selectCountryCodes);
    this.countries$ = this.store.select(selectAllCountries);
    this.user$ = this.store.select(selectUser);
    this.sameEmail$ = this.store.select(selectSameEmail);
    this.sameAddress$ = this.store.select(selectSameAddress);
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
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onEmailCheckChange(event) {
    this.store.dispatch(
      sameEmail({
        sameEmail: event,
      })
    );
  }

  onAddressCheckChange(event) {
    this.store.dispatch(
      sameAddress({
        sameAddress: event,
      })
    );
  }
}
