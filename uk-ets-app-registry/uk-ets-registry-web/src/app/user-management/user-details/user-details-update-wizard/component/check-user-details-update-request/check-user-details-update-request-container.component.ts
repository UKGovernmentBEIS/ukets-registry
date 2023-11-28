import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack, errors } from '@shared/shared.action';
import {
  UserDetailsUpdateWizardPathsModel,
  UserUpdateDetailsType,
} from '@user-update/model';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import {
  selectCurrentUserDetailsInfo,
  selectIsLoadedFromMyProfilePage,
  selectUserDetailsUpdateInfo,
  selectUserDetailsUpdateType,
} from '@user-update/reducers';
import { Observable } from 'rxjs';
import { IUser } from '@shared/user';
import { UserDetailsUpdateActions } from '@user-update/action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  cancelClicked,
  submitUpdateRequest,
} from '@user-update/action/user-details-update.action';
import { selectAllCountries } from '@shared/shared.selector';
import { IUkOfficialCountry } from '@shared/countries/country.interface';

@Component({
  selector: 'app-check-user-details-update-request-container',
  template: `<app-check-user-details-update-request
    [currentUserDetails]="currentUserDetails$ | async"
    [newUserDetails]="newUserDetails$ | async"
    [userUpdateDetailsType]="userUpdateDetailsType$ | async"
    [countries]="countries$ | async"
    [isMyProfilePage]="isMyProfilePage$ | async"
    (navigateToEmitter)="navigateTo($event)"
    (cancelEmitter)="onCancel()"
    (submitRequest)="onSubmit($event)"
    (errorDetails)="onError($event)"
  ></app-check-user-details-update-request>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckUserDetailsUpdateRequestContainerComponent implements OnInit {
  newUserDetails$: Observable<IUser>;
  currentUserDetails$: Observable<IUser>;
  userUpdateDetailsType$: Observable<UserUpdateDetailsType>;
  countries$: Observable<IUkOfficialCountry[]>;
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
          UserDetailsUpdateWizardPathsModel.WORK_DETAILS
        }`,
        extras: { skipLocationChange: true },
      })
    );
    this.newUserDetails$ = this.store.select(selectUserDetailsUpdateInfo);
    this.currentUserDetails$ = this.store.select(selectCurrentUserDetailsInfo);
    this.userUpdateDetailsType$ = this.store.select(
      selectUserDetailsUpdateType
    );
    this.countries$ = this.store.select(selectAllCountries);
    this.isMyProfilePage$ = this.store.select(selectIsLoadedFromMyProfilePage);
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  navigateTo(routePath: string) {
    this.store.dispatch(
      UserDetailsUpdateActions.navigateTo({
        route: `/user-details/${this.route.snapshot.paramMap.get(
          'urid'
        )}/${routePath}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onSubmit(value: IUser) {
    this.store.dispatch(submitUpdateRequest({ userDetails: value }));
  }

  onError(value: ErrorDetail) {
    const summary: ErrorSummary = {
      errors: [value],
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
