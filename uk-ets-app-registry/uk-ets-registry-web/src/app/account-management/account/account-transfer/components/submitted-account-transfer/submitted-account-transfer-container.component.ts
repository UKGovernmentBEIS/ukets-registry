import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@registry-web/shared/shared.action';
import { Observable } from 'rxjs';
import { selectSubmittedAccountTransferRequestIdentifier } from '@account-transfer/store/reducers';

@Component({
  selector: 'app-submitted-account-transfer-container',
  template: `
    <app-account-transfer-request-submitted
      [submittedIdentifier]="submittedIdentifier$ | async"
      [accountId]="accountId"
    ></app-account-transfer-request-submitted>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedAccountTransferContainerComponent implements OnInit {
  submittedIdentifier$: Observable<string>;
  accountId: string;
  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.accountId = this.route.snapshot.paramMap.get('accountId');
    this.submittedIdentifier$ = this.store.select(
      selectSubmittedAccountTransferRequestIdentifier
    );
  }
}
