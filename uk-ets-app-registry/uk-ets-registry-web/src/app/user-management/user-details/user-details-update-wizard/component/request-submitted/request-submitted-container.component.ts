import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import {
  selectIsLoadedFromMyProfilePage,
  selectSubmittedRequestIdentifier,
  selectUserDetailsUpdateType,
} from '@user-update/reducers';
import { Logout } from '@registry-web/auth/auth.actions';
import { UserUpdateDetailsType } from '@user-update/model';

@Component({
  selector: 'app-request-user-details-update-submitted-container',
  template: `<div
      appScreenReaderPageAnnounce
      [pageTitle]="'Request submitted page'"
    ></div>
    <app-request-user-details-update-submitted
      *ngIf="(submittedIdentifier$ | async) !== null"
      [submittedIdentifier]="submittedIdentifier$ | async"
      [urid]="urid"
      [isMyProfilePage]="isMyProfilePage$ | async"
      [updateType]="updateType$ | async"
      (navigateToEmitter)="goToDashboard()"
    ></app-request-user-details-update-submitted>
    <app-request-minor-user-details-update-submitted
      *ngIf="
        (submittedIdentifier$ | async) === null &&
        (updateType$ | async) === 'UPDATE_USER_DETAILS'
      "
      [urid]="urid"
      [isMyProfilePage]="isMyProfilePage$ | async"
      (navigateToEmitter)="goToDashboard()"
    ></app-request-minor-user-details-update-submitted>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestSubmittedContainerComponent implements OnInit {
  submittedIdentifier$: Observable<string>;
  urid: string;
  isMyProfilePage$: Observable<boolean>;
  updateType$: Observable<UserUpdateDetailsType>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.urid = this.route.snapshot.paramMap.get('urid');
    this.submittedIdentifier$ = this.store.select(
      selectSubmittedRequestIdentifier
    );
    this.isMyProfilePage$ = this.store.select(selectIsLoadedFromMyProfilePage);
    this.updateType$ = this.store.select(selectUserDetailsUpdateType);
    console.log(this.urid);
    console.log(this.submittedIdentifier$);
  }

  goToDashboard() {
    this.logout();
  }

  logout() {
    this.store.dispatch(
      Logout({
        redirectUri: location.origin + '/dashboard',
      })
    );
  }
}
