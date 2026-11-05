import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { take } from 'rxjs';
import { ChangeContactInvitationWizardActions } from '@change-contact-invitation-wizard/store';

@Component({
  selector: 'app-cancel-change-contact-invitation-container',
  template: `
    <app-cancel-update-request
      notification="Are you sure you want to cancel the change invitation status request and return back to the account page?"
      (cancelRequest)="onCancel()"
    />
  `,
  styles: ``,
})
export class CancelChangeContactInvitationContainerComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.route.queryParams.pipe(take(1)).subscribe((params) => {
      this.store.dispatch(
        canGoBack({
          goBackRoute: params.goBackRoute,
          extras: { skipLocationChange: true },
        })
      );
    });
  }

  onCancel(): void {
    this.store.dispatch(ChangeContactInvitationWizardActions.cancelRequest());
  }
}
