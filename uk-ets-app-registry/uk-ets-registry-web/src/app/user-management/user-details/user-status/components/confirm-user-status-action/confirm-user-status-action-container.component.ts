import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import {canGoBack, clearErrors, errors} from '@shared/shared.action';
import {
  cancelUserStatus,
  submitUserStatusAction
} from '@user-management/user-details/user-status/store/actions/user-status.actions';
import { ActivatedRoute } from '@angular/router';
import { selectUserStatusAction } from '@user-management/user-details/user-status/store/reducers';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { UserStatusActionState } from '@user-management/model';
import { selectUserStatus } from '@user-management/user-details/store/reducers';
import { UserStatus } from '@shared/user';
import {ErrorDetail, ErrorSummary} from "@shared/error-summary";

@Component({
  selector: 'app-confirm-user-status-action-container',
  template: `
    <app-confirm-user-status-action
      *ngIf="currentUserStatus$ | async as currentUserStatus"
      [currentUserStatus]="currentUserStatus"
      [userStatusAction]="userStatusAction$ | async"
      (cancelUserStatusAction)="onCancel()"
      (comment)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-confirm-user-status-action>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfirmUserStatusActionContainerComponent implements OnInit {
  currentUserStatus$: Observable<UserStatus>;
  userStatusAction$: Observable<UserStatusActionState>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/user-details/${this.route.snapshot.paramMap.get(
          'urid'
        )}/status`
      })
    );
    this.currentUserStatus$ = this.store.select(selectUserStatus);
    this.userStatusAction$ = this.store.select(selectUserStatusAction);
  }

  onContinue(commentValue: string) {
    this.store.dispatch(submitUserStatusAction({ comment: commentValue }));
  }

  onCancel() {
    this.store.dispatch(cancelUserStatus());
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
