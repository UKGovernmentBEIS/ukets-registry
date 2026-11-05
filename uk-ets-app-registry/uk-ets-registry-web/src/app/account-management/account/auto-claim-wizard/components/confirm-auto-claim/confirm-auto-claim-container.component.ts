import { Component, OnInit } from '@angular/core';
import { MenuItemEnum } from '@account-management/account/account-details/model/account-side-menu.model';
import { canGoBack, clearErrors } from '@shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { AutoClaimWizardActions } from '@auto-claim-wizard/store';

@Component({
  selector: 'app-confirm-auto-claim-container',
  template: `
    <div
      appScreenReaderPageAnnounce
      pageTitle="Are you sure you want to change the auto-send invitation status?"
    ></div>
    <h1 class="govuk-heading-l">
      Are you sure you want to change the auto-send invitation status?
    </h1>
    <p class="govuk-body">
      You will still be able to manually send individual invitations to the
      contacts of this account.
    </p>
    <button
      id="submit"
      class="govuk-button"
      data-module="govuk-button"
      (click)="onContinue()"
    >
      Yes, change status
    </button>
    <app-cancel-request-link (goToCancelScreen)="onCancel()" />
  `,
  styles: ``,
})
export class ConfirmAutoClaimContainerComponent implements OnInit {
  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    const goBackRoute = `/account/${this.activatedRoute.snapshot.paramMap.get('accountId')}`;
    this.store.dispatch(
      canGoBack({
        goBackRoute,
        extras: {
          queryParams: {
            selectedSideMenu: MenuItemEnum.CONTACTS,
          },
        },
      })
    );
  }

  onContinue() {
    this.store.dispatch(AutoClaimWizardActions.submitRequest());
  }

  onCancel() {
    const route = this._router.url;
    this.store.dispatch(
      AutoClaimWizardActions.cancelAutoClaimWizard({ route })
    );
  }
}
