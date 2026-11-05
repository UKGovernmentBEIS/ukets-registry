import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';

@Component({
  selector: 'app-confirm-change-contact-invitation-container',
  template: `
    <app-request-submitted
      [confirmationMessageTitle]="'The invitation status has been changed'"
      [accountId]="accountId"
    />
  `,
  styles: ``,
})
export class ConfirmChangeContactInvitationContainerComponent
  implements OnInit
{
  private readonly store = inject(Store);
  private readonly route = inject(ActivatedRoute);

  accountId: string;

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.accountId = this.route.snapshot.paramMap.get('accountId');
  }
}
