import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { DomainEvent } from '@shared/model/event';
import { Store } from '@ngrx/store';
import { selectAccountHistory } from '@account-management/account/account-details/account.selector';
import { navigateToUserProfile } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import { isAdmin } from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-history-and-comments-container',
  template: `
    <app-domain-events
      [domainEvents]="accountHistory$ | async"
      [isAdmin]="isAdmin$ | async"
      (navigate)="navigateToUserPage($event)"
    ></app-domain-events>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HistoryAndCommentsContainerComponent implements OnInit {
  accountHistory$: Observable<DomainEvent[]>;
  isAdmin$: Observable<boolean>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.accountHistory$ = this.store.select(selectAccountHistory);
    this.isAdmin$ = this.store.select(isAdmin);
  }

  navigateToUserPage(urid: string) {
    const goBackRoute = this.route.snapshot['_routerState'].url;
    const userProfileRoute = '/user-details/' + urid;
    this.store.dispatch(
      navigateToUserProfile({ goBackRoute, userProfileRoute })
    );
  }
}
