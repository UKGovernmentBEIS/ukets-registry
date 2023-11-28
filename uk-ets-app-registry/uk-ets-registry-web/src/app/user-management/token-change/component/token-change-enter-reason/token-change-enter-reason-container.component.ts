import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { TokenChangeState } from '@user-management/token-change/reducer';
import {
  actionLoadData,
  actionSubmitReason
} from '@user-management/token-change/action/token-change.actions';
import { selectState } from '@user-management/token-change/reducer/token-change.selectors';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';

@Component({
  selector: 'app-token-change-enter-reason-container',
  template: `
    <app-token-change-enter-reason
      [state]="tokenChangeState$ | async"
      (submitReason)="onSubmitReason($event)"
      (errorDetails)="onError($event)"
    ></app-token-change-enter-reason>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TokenChangeEnterReasonContainerComponent implements OnInit {
  tokenChangeState$: Observable<TokenChangeState>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(actionLoadData());
    this.tokenChangeState$ = this.store.select(selectState);
  }

  onSubmitReason(reason: string) {
    this.store.dispatch(actionSubmitReason({ reason }));
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
