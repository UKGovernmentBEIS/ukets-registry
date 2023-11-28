import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack, errors } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { UserDetailsUpdateWizardPathsModel } from '@user-update/model';
import { Observable } from 'rxjs';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { selectAllCountries } from '@shared/shared.selector';
import { IUser } from '@shared/user';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  cancelClicked,
  setPersonalDetailsRequest,
} from '@user-update/action/user-details-update.action';
import {
  selectIsLoadedFromMyProfilePage,
  selectUserDetailsUpdateInfo,
} from '@user-update/reducers';
import { isAdmin } from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-update-user-personal-details-container',
  template: `<div
      appScreenReaderPageAnnounce
      [pageTitle]="'Update the personal details'"
    ></div>
    <app-personal-details-input
      [caption]="'Request to update the user details'"
      [heading]="
        (isMyProfilePage$ | async)
          ? 'Update the personal details'
          : 'Update the user personal details'
      "
      [isRequestUpdateProcess]="true"
      [isMyProfilePage]="isMyProfilePage$ | async"
      [countries]="countries$ | async"
      [user]="user$ | async"
      [isRequestFromAdmin]="isAdmin$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-personal-details-input
    ><app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateUserPersonalDetailsContainerComponent implements OnInit {
  countries$: Observable<IUkOfficialCountry[]>;
  user$: Observable<IUser>;
  isMyProfilePage$: Observable<boolean>;
  isAdmin$: Observable<boolean>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/user-details/${this.route.snapshot.paramMap.get(
          'urid'
        )}/${UserDetailsUpdateWizardPathsModel.BASE_PATH}`,
        extras: { skipLocationChange: true },
      })
    );

    this.countries$ = this.store.select(selectAllCountries);
    this.user$ = this.store.select(selectUserDetailsUpdateInfo);
    this.isMyProfilePage$ = this.store.select(selectIsLoadedFromMyProfilePage);
    this.isAdmin$ = this.store.select(isAdmin);
  }

  onContinue(value: IUser): void {
    this.store.dispatch(setPersonalDetailsRequest({ userDetails: value }));
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
