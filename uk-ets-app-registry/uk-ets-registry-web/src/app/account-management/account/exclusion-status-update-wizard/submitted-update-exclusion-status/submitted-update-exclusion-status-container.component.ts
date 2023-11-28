import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors } from '@registry-web/shared/shared.action';

@Component({
  selector: 'app-submitted-update-exclusion-status-container',
  template: `<app-submit-success-change-description
    [backLinkMessage]="'Back to the account'"
    [backPath]="'/account/' + accountId"
    [message]="'The exclusion status has been updated'"
  ></app-submit-success-change-description>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedUpdateExclusionStatusContainerComponent
  implements OnInit {
  accountId: string;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit(): void {
    this.accountId = this.route.snapshot.paramMap.get('accountId');
    this.store.dispatch(clearErrors());
    this.store.dispatch(canGoBack({ goBackRoute: null }));
  }
}
