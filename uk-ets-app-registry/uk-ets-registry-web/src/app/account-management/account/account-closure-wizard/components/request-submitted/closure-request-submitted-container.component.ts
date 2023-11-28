import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import { selectSubmittedRequestIdentifier } from '@account-management/account/account-closure-wizard/reducers';

@Component({
  selector: 'app-account-closure-request-submitted-container',
  template: `<div
      appScreenReaderPageAnnounce
      [pageTitle]="'Request submitted page'"
    ></div>
    <app-account-closure-request-submitted
      [submittedIdentifier]="submittedIdentifier$ | async"
      [accountId]="accountId"
    ></app-account-closure-request-submitted> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ClosureRequestSubmittedContainerComponent implements OnInit {
  submittedIdentifier$: Observable<string>;
  accountId: string;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.accountId = this.route.snapshot.paramMap.get('accountId');
    this.submittedIdentifier$ = this.store.select(
      selectSubmittedRequestIdentifier
    );
  }
}
