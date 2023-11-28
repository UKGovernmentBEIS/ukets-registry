import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack, errors } from '@shared/shared.action';
import {
  UserDetailsUpdateWizardPathsModel,
  UserUpdateDetailsType,
} from '@user-update/model';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import {
  selectDeactivationComment,
  selectIsLoggedInUserSameAsDeactivated,
  selectUserDetailsUpdateType,
} from '@user-update/reducers';
import {
  cancelClicked,
  submitDeactivationRequest,
} from '@user-update/action/user-details-update.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { selectAllCountries } from '@shared/shared.selector';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { UserDetailsUpdateActions } from '@user-update/action';
import {
  selectARsInAccountDetails,
  selectEnrolmentKeyDetails,
  selectUserDetails,
} from '@user-management/user-details/store/reducers';
import { KeycloakUser } from '@shared/user';
import { ArInAccount, EnrolmentKey } from '@user-management/user-details/model';

@Component({
  selector: 'app-check-deactivation-details-container',
  template: ` <app-check-deactivation-details
      [userUpdateDetailsType]="userUpdateDetailsType$ | async"
      [countries]="countries$ | async"
      [comment]="deactivationComment$ | async"
      [userDetails]="userDetails$ | async"
      [enrolmentKeyDetails]="enrolmentKeyDetails$ | async"
      [isLoggedInUserSameAsDeactivated]="
        isLoggedInUserSameAsDeactivated$ | async
      "
      [arInAccounts]="arInAccounts$ | async"
      (submitRequest)="onSubmit($event)"
      (navigateToEmitter)="navigateTo($event)"
    ></app-check-deactivation-details>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckDeactivationDetailsContainerComponent implements OnInit {
  deactivationComment$: Observable<string>;
  userUpdateDetailsType$: Observable<UserUpdateDetailsType>;
  countries$: Observable<IUkOfficialCountry[]>;
  userDetails$: Observable<KeycloakUser>;
  enrolmentKeyDetails$: Observable<EnrolmentKey>;
  isLoggedInUserSameAsDeactivated$: Observable<boolean>;
  arInAccounts$: Observable<ArInAccount[]>;

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
          UserDetailsUpdateWizardPathsModel.DEACTIVATION_COMMENT
        }`,
        extras: { skipLocationChange: true },
      })
    );
    this.deactivationComment$ = this.store.select(selectDeactivationComment);
    this.userUpdateDetailsType$ = this.store.select(
      selectUserDetailsUpdateType
    );
    this.countries$ = this.store.select(selectAllCountries);
    this.userDetails$ = this.store.select(selectUserDetails);
    this.enrolmentKeyDetails$ = this.store.select(selectEnrolmentKeyDetails);
    this.isLoggedInUserSameAsDeactivated$ = this.store.select(
      selectIsLoggedInUserSameAsDeactivated
    );
    this.arInAccounts$ = this.store.select(selectARsInAccountDetails);
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
  onSubmit(comment: string) {
    this.store.dispatch(
      submitDeactivationRequest({ deactivationComment: comment })
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(value: ErrorDetail) {
    const summary: ErrorSummary = {
      errors: [value],
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
