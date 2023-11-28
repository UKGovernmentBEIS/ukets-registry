import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import {
  cancelClicked,
  setNewUserDetailsInfoSuccess,
} from '@user-update/action/user-details-update.action';
import { IUser } from '@shared/user';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { UserDetailsUpdateWizardPathsModel } from '@user-update/model';
import { Observable } from 'rxjs';
import { selectUserDetailsUpdateInfo } from '@user-update/reducers';

@Component({
  selector: 'app-update-user-memorable-phrase-container',
  template: `
    <app-memorable-phrase
      [user]="user$ | async"
      [caption]="'Request to update the user details'"
      [header]="'Update the memorable phrase'"
      [isRequestUpdateProcess]="true"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-memorable-phrase>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateUserMemorablePhraseContainerComponent implements OnInit {
  user$: Observable<IUser>;

  constructor(private store: Store, private route: ActivatedRoute) {}

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

    this.user$ = this.store.select(selectUserDetailsUpdateInfo);
  }

  onContinue(value: IUser): void {
    this.store.dispatch(setNewUserDetailsInfoSuccess({ userDetails: value }));
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
