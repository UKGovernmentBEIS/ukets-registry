import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectSubmittedRequestIdentifier } from '@trusted-account-list/reducers';
import { Observable } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import { isAdmin } from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-request-submitted-container',
  template: `
    <app-request-submitted
      [submittedIdentifier]="submittedIdentifier$ | async"
      [accountId]="accountId"
      [isAdmin]="isAdmin$ | async"
    ></app-request-submitted>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestSubmittedContainerComponent implements OnInit {
  submittedIdentifier$: Observable<string>;
  accountId: string;
  isAdmin$: Observable<boolean>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.isAdmin$ = this.store.select(isAdmin);
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.accountId = this.route.snapshot.paramMap.get('accountId');
    this.submittedIdentifier$ = this.store.select(
      selectSubmittedRequestIdentifier
    );
  }
}
