import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@registry-web/shared/shared.action';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  selector: 'app-send-invitation-request-submitted-container',
  template: `
    <app-request-submitted
      [confirmationMessageTitle]="'The invitation has been sent'"
      [accountId]="accountId"
    />
  `,
  imports: [SharedModule],
  standalone: true,
})
export class RequestSubmittedContainerComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly route = inject(ActivatedRoute);

  accountId: string;

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.accountId = this.route.snapshot.paramMap.get('accountId');
  }
}
