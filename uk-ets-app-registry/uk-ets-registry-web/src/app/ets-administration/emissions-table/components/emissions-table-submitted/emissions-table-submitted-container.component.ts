import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { canGoBack } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { selectEmissionsTableRequestId } from '@emissions-table/store/reducers';

@Component({
  selector: 'app-emissions-table-submitted-container',
  template: `
    <app-request-submitted
      [confirmationMessageTitle]="'Request Submitted'"
      [confirmationMessageBody]="'Request ID'"
      [submittedIdentifier]="submittedIdentifier$ | async"
      [accountId]="accountId"
      [isAdmin]="isAdmin$ | async"
    ></app-request-submitted>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsTableSubmittedContainerComponent implements OnInit {
  submittedIdentifier$: Observable<string>;
  accountId: string;
  isAdmin$: Observable<boolean>;
  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.isAdmin$ = this.store.select(isAdmin);
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.accountId = this.route.snapshot.paramMap.get('accountId');
    this.submittedIdentifier$ = this.store.select(
      selectEmissionsTableRequestId
    );
  }
}
