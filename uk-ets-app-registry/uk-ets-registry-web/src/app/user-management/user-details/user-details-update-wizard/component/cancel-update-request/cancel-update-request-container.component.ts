import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import { UserDetailsUpdateActions } from '@user-update/action';
import { selectUserDetailsUpdateType } from '@user-update/reducers';
import { UserUpdateDetailsType } from '@user-update/model';

@Component({
  selector: 'app-cancel-user-update-request-container',
  template: `<div
      appScreenReaderPageAnnounce
      [pageTitle]="'Cancel user details update'"
    ></div>
    <app-cancel-update-request
      *ngIf="(userUpdateDetailsType$ | async) === 'DEACTIVATE_USER'"
      [updateRequestText]="'user deactivation'"
      [pageTitle]="'user'"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
    <app-cancel-update-request
      *ngIf="(userUpdateDetailsType$ | async) !== 'DEACTIVATE_USER'"
      [updateRequestText]="'user details update'"
      [pageTitle]="'user'"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelUpdateRequestContainerComponent implements OnInit {
  goBackRoute: string;
  userUpdateDetailsType$: Observable<UserUpdateDetailsType>;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.userUpdateDetailsType$ = this.store.select(
      selectUserDetailsUpdateType
    );
    this.route.queryParams.subscribe((params) => {
      this.goBackRoute = params.goBackRoute;
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.goBackRoute,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel() {
    this.store.dispatch(UserDetailsUpdateActions.cancelUserUpdateRequest());
  }
}
