import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { UserDetailsUpdateWizardPathsModel } from '@user-update/model';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  cancelClicked,
  setDeactivationComment,
} from '@user-update/action/user-details-update.action';
import { Observable } from 'rxjs';
import { KeycloakUser } from '@shared/user';
import { selectUserDetails } from '@user-management/user-details/store/reducers';
import { selectDeactivationComment } from '@user-update/reducers';

@Component({
  selector: 'app-user-deactivation-comment-container',
  template: `<div
      appScreenReaderPageAnnounce
      [pageTitle]="'Explain why you are deactivating this user'"
    ></div>
    <app-user-deactivation-comment
      [_fullName]="user$ | async"
      [comment]="comment$ | async"
      (justificationComment)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-user-deactivation-comment>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserDeactivationCommentContainerComponent implements OnInit {
  constructor(private store: Store, private route: ActivatedRoute) {}

  user$: Observable<KeycloakUser>;
  comment$: Observable<string>;

  ngOnInit(): void {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/user-details/${this.route.snapshot.paramMap.get(
          'urid'
        )}/${UserDetailsUpdateWizardPathsModel.BASE_PATH}`,
        extras: { skipLocationChange: true },
      })
    );
    this.user$ = this.store.select(selectUserDetails);
    this.comment$ = this.store.select(selectDeactivationComment);
  }

  onContinue(comment: string) {
    this.store.dispatch(setDeactivationComment({ comment: comment }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
