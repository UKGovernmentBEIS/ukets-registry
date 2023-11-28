import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { canGoBack } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import {
  selectAccountFullIdentifier,
  selectTrustedAccountDescriptionOrFullIdentifier,
} from '@account-management/account/account-details/account.selector';
import { cancelPendingActivationRequested } from '@trusted-account-list/actions/trusted-account-list.actions';

@Component({
  selector: 'app-cancel-pending-activation-container',
  template: `<app-cancel-pending-activation
    (cancelAddition)="cancel()"
    [description]="description$ | async"
  >
  </app-cancel-pending-activation>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelPendingActivationContainerComponent
  implements OnInit, OnDestroy
{
  private subscription: Subscription;
  description$: Observable<string>;
  accountId: string;
  trustedAccountFullIdentifier: string;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.accountId = this.route.snapshot.paramMap.get('accountId');
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.accountId}`,
      })
    );
    this.description$ = this.store.select(
      selectTrustedAccountDescriptionOrFullIdentifier
    );
  }

  cancel() {
    this.subscription = this.store
      .select(selectAccountFullIdentifier)
      .subscribe((trustedAccountFullIdentifier) => {
        this.trustedAccountFullIdentifier = trustedAccountFullIdentifier;
      });

    this.store.dispatch(
      cancelPendingActivationRequested({
        accountIdentifier: this.accountId,
        trustedAccountFullIdentifier: this.trustedAccountFullIdentifier,
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }
}
