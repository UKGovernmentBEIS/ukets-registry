import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import { IUser } from '@shared/user';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack, errors } from '@shared/shared.action';
import { UserDetailsUpdateWizardPathsModel } from '@user-update/model';
import {
  cancelClicked,
  setWorkDetailsRequest,
} from '@user-update/action/user-details-update.action';
import {
  selectIsLoadedFromMyProfilePage,
  selectUserDetailsUpdateInfo,
} from '@user-update/reducers';

@Component({
  selector: 'app-update-user-work-details-container',
  template: `<div
      appScreenReaderPageAnnounce
      [pageTitle]="'Update the work contact details'"
    ></div>
    <app-work-details-input
      [caption]="'Request to update the user details'"
      [heading]="
        (isMyProfilePage$ | async)
          ? 'Update the work contact details'
          : 'Update the user work contact details'
      "
      [isRequestUpdateProcess]="true"
      [isMyProfilePage]="isMyProfilePage$ | async"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      [user]="user$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-work-details-input
    ><app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateUserWorkDetailsContainerComponent implements OnInit {
  countryCodes$: Observable<
    {
      region: string;
      code: string;
    }[]
  >;
  countries$: Observable<IUkOfficialCountry[]>;
  user$: Observable<IUser>;
  isMyProfilePage$: Observable<boolean>;

  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/user-details/${this.route.snapshot.paramMap.get(
          'urid'
        )}/${UserDetailsUpdateWizardPathsModel.BASE_PATH}/${
          UserDetailsUpdateWizardPathsModel.PERSONAL_DETAILS
        }`,
        extras: { skipLocationChange: true },
      })
    );
    this.countries$ = this.store.select(selectAllCountries);
    this.countryCodes$ = this.store.select(selectCountryCodes);
    this.user$ = this.store.select(selectUserDetailsUpdateInfo);
    this.isMyProfilePage$ = this.store.select(selectIsLoadedFromMyProfilePage);
  }

  onContinue(value: IUser): void {
    this.store.dispatch(setWorkDetailsRequest({ userDetails: value }));
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
