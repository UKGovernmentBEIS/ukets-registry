import { Component, OnInit } from '@angular/core';
import { EnrolledUser } from '@authority-setting/model';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { selectEnrolledUser } from '@authority-setting/reducer/authority-setting.selector';
import {
  removeUserFromAuthorityUsers,
  setUserAsAuthority
} from '@authority-setting/action';
import { canGoBack, navigateTo } from '@shared/shared.action';
import { AuthoritySettingRoutePathsModel } from '@authority-setting/model/authority-setting-route-paths.model';

@Component({
  selector: 'app-check-authority-user-container',
  template: `
    <app-check-authority-user
      [enrolledUser]="enrolledUser$ | async"
      (setUserAsAuthority)="updateUserAsAuthority($event)"
      (removeUserFromAuthorityUsers)="removeUserFromAuthorities($event)"
      (changeUser)="onChangeUser()"
    ></app-check-authority-user>
  `
})
export class CheckAuthorityUserContainerComponent implements OnInit {
  enrolledUser$: Observable<EnrolledUser>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.enrolledUser$ = this.store.select(selectEnrolledUser);
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/${AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION}/${AuthoritySettingRoutePathsModel.BASE_PAGE}`
      })
    );
  }

  updateUserAsAuthority(urid: string) {
    this.store.dispatch(
      setUserAsAuthority({
        urid
      })
    );
  }

  removeUserFromAuthorities(urid: string) {
    this.store.dispatch(
      removeUserFromAuthorityUsers({
        urid
      })
    );
  }

  onChangeUser() {
    this.store.dispatch(
      navigateTo({
        route: `/${AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION}/${AuthoritySettingRoutePathsModel.BASE_PAGE}`
      })
    );
  }
}
