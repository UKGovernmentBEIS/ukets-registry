import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { canGoBack } from '@shared/shared.action';
import { selectExcludeBillingRemarksForm } from '@account-management/account/account-details/account.selector';
import { cancelExcludeBilling, submitExcludeBilling } from '../account.actions';

@Component({
  selector: 'app-exclude-billing-container',
  template: `
    <app-exclude-billing-form
      [remarks]="remarks$ | async"
      (remarksOutput)="onContinue($event)"
    >
    </app-exclude-billing-form>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExcludeBillingContainerComponent implements OnInit {
  remarks$: Observable<string>;

  constructor(private store: Store, private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.remarks$ = this.store.select(selectExcludeBillingRemarksForm);

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get(
          'accountId'
        )}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onContinue(remarks: string) {
    this.store.dispatch(submitExcludeBilling({ remarks }));
  }

  onCancel() {
    this.store.dispatch(cancelExcludeBilling());
  }
}
