import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import {
  selectCallerExtras,
  selectCallerRoute,
  selectCrcDetails,
  selectCrcSubmitted,
  UserCrcActions,
} from '@user-crc/store';
import { combineLatest, Observable, take } from 'rxjs';
import { Store } from '@ngrx/store';
import { CrcDetails, UserCrcUpdateRequest } from '@user-crc/model';
import { selectUserDetails } from '@user-management/user-details/store/reducers';
import { canGoBack, errors } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-user-crc-form-container',
  template: `<app-feature-header-wrapper>
      <app-user-header
        [user]="user$ | async"
        [userHeaderVisibility]="true"
        [userHeaderActionsVisibility]="false"
        [showBackToList]="false"
        [showRequestUpdate]="false"
      >
      </app-user-header>
    </app-feature-header-wrapper>
    <app-user-crc-form
      [crc]="crcSubmitted$ | async"
      [crcDetails]="crcDetails$ | async"
      (crcDetailsOutput)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-user-crc-form> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserCrcFormContainerComponent implements OnInit {
  user$ = this.store.select(selectUserDetails);
  crcSubmitted$ = this.store.select(selectCrcSubmitted);
  crcDetails$: Observable<CrcDetails> = this.store.select(
    selectCrcDetails
  ) as Observable<CrcDetails>;
  callerRoute$: Observable<string> = this.store.select(selectCallerRoute);
  callerExtras$: Observable<unknown> = this.store.select(selectCallerExtras);

  constructor(private store: Store) {}

  ngOnInit() {
    combineLatest([this.callerRoute$, this.callerExtras$])
      .pipe(take(1))
      .subscribe(([route, extras]) => {
        this.store.dispatch(
          canGoBack({
            goBackRoute: route,
            extras: extras,
          })
        );
      });
  }

  onContinue(value: UserCrcUpdateRequest) {
    this.store.dispatch(
      UserCrcActions.requestUserCrcUpdate({
        request: value,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
