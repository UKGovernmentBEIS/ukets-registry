import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack, errors } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { Observable } from 'rxjs';
import {
  UserStatusActionOption,
  UserStatusActionState,
} from '@user-management/model';
import {
  cancelUserStatus,
  checkIfUserSuspendedByTheSystem,
  setSelectedUserStatusAndNavigateToConfirmAction,
} from '@user-management/user-details/user-status/store/actions/user-status.actions';
import {
  selectAllowedUserStatusActions,
  selectUserStatusAction,
  selectUserSuspendedByTheSystem,
} from '@user-management/user-details/user-status/store/reducers';

@Component({
  selector: 'app-select-user-status-action-container',
  template: `
    <app-select-user-status-action
      [allowedUserStatusActions]="allowedUserStatusActions$ | async"
      [userStatusAction]="userStatusAction$ | async"
      [userSuspendedByTheSystem]="userSuspendedByTheSystem$ | async"
      (cancelUserStatusAction)="onCancel()"
      (selectedUserStatusAction)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-select-user-status-action>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectUserStatusActionContainerComponent implements OnInit {
  allowedUserStatusActions$: Observable<UserStatusActionOption[]>;
  userStatusAction$: Observable<UserStatusActionState>;
  userSuspendedByTheSystem$: Observable<boolean>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.userStatusAction$ = this.store.select(selectUserStatusAction);
    this.allowedUserStatusActions$ = this.store.select(
      selectAllowedUserStatusActions
    );
    this.userSuspendedByTheSystem$ = this.store.select(
      selectUserSuspendedByTheSystem
    );
    this.store.dispatch(checkIfUserSuspendedByTheSystem());
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/user-details/${this.route.snapshot.paramMap.get(
          'urid'
        )}`,
      })
    );
  }

  onContinue(value: UserStatusActionState) {
    this.store.dispatch(
      setSelectedUserStatusAndNavigateToConfirmAction({
        userStatusAction: value,
      })
    );
  }

  onCancel() {
    this.store.dispatch(cancelUserStatus());
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
