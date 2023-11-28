import { Component, Input } from '@angular/core';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { navigateTo } from '@shared/shared.action';
import { GoBackNavigationExtras } from '@shared/back-button';
import { SearchMode } from '@shared/resolvers/search.resolver';
import { KeycloakUser, userStatusMap } from '@shared/user';
import { UserDetailsUpdateWizardPathsModel } from '@user-update/model';

@Component({
  selector: 'app-user-header',
  templateUrl: './user-header.component.html',
  styleUrls: ['../../../shared/sub-headers/styles/sub-header.scss'],
})
export class UserHeaderComponent {
  @Input()
  user: KeycloakUser;
  @Input()
  userHeaderActionsVisibility: boolean;
  @Input()
  userHeaderVisibility: boolean;
  @Input()
  showBackToList: boolean;
  @Input()
  showRequestUpdate: boolean;
  @Input()
  goBackToListRoute: string;
  @Input()
  goBackToListNavigationExtras: GoBackNavigationExtras;

  readonly userStatusMap = userStatusMap;
  private readonly userDetailsPath = 'user-details';
  searchMode = SearchMode;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  get headerActionsVisibility() {
    return this.user.attributes.state[0] !== 'DEACTIVATION_PENDING' &&
      this.user.attributes.state[0] !== 'DEACTIVATED'
      ? this.userHeaderActionsVisibility
      : false;
  }

  get requestUpdate() {
    return this.user.attributes.state[0] !== 'DEACTIVATION_PENDING' &&
      this.user.attributes.state[0] !== 'DEACTIVATED'
      ? this.showRequestUpdate
      : false;
  }

  goToUpdateUserStatus(): void {
    this.router.navigate([`${this.userDetailsPath}`, this.urid, 'status']);
  }

  goToUpdateUserDetails(): void {
    this.router.navigate([
      `${this.userDetailsPath}`,
      this.urid,
      UserDetailsUpdateWizardPathsModel.BASE_PATH,
    ]);
  }

  goBack(event) {
    event.preventDefault();
    const extras: NavigationExtras = {
      skipLocationChange: this.goBackToListNavigationExtras?.skipLocationChange,
      queryParams: this.goBackToListNavigationExtras?.queryParams,
    };
    this.store.dispatch(
      navigateTo({
        route: this.goBackToListRoute,
        extras,
        queryParams: this.goBackToListNavigationExtras?.queryParams,
      })
    );
  }

  private get urid() {
    return this.activatedRoute.snapshot.params['urid'];
  }
}
