import { Component, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router, ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { selectAccount } from '@account-management/account/account-details/account.selector';
import { canGoBack } from '@shared/shared.action';
import {
  ChangeContactInvitationWizardActions,
  selectSelectedMetsContactsForDisplay,
  selectSelectedRegistryContactsForDisplay,
} from '@change-contact-invitation-wizard/store';
import { CHANGE_CONTACT_INVITATION_BASE_PATH } from '@change-contact-invitation-wizard/change-contact-invitation-wizard-routing.module';

@Component({
  selector: 'app-change-contact-invitation-overview-container',
  templateUrl: './change-contact-invitation-overview-container.component.html',
  styles: ``,
})
export class ChangeContactInvitationOverviewContainerComponent
  implements OnInit
{
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(Store);

  readonly account = toSignal(this.store.select(selectAccount));
  readonly metsContacts = toSignal(
    this.store.select(selectSelectedMetsContactsForDisplay)
  );
  readonly registryContacts = toSignal(
    this.store.select(selectSelectedRegistryContactsForDisplay)
  );

  ngOnInit() {
    this.store.dispatch(ChangeContactInvitationWizardActions.initOverview());
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get('accountId')}/${CHANGE_CONTACT_INVITATION_BASE_PATH}/select-contacts`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onSubmit() {
    this.store.dispatch(ChangeContactInvitationWizardActions.submitRequest());
  }

  navigateToSelectContacts() {
    this.store.dispatch(
      ChangeContactInvitationWizardActions.navigateTo({
        step: 'select-contacts',
      })
    );
  }

  onCancel() {
    const route = this.router.url;
    this.store.dispatch(ChangeContactInvitationWizardActions.cancel({ route }));
  }
}
