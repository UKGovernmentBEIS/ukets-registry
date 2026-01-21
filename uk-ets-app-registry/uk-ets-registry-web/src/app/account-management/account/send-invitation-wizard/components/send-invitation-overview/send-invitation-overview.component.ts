import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@registry-web/shared/shared.action';
import {
  selectSelectedMetsContacts,
  selectSelectedRegistryContacts,
  SendInvitationActions,
} from '@send-invitation-wizard/store';
import {
  SEND_INVITATION_BASE_PATH,
  SendInvitationWizardPaths,
} from '@send-invitation-wizard/send-invitation-wizard.helpers';
import { AccountContactsTablesComponent } from '@registry-web/shared/components/account/account-contacts-tables';
import { toSignal } from '@angular/core/rxjs-interop';
import { selectAccount } from '@registry-web/account-management/account/account-details/account.selector';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  selector: 'app-send-invitation-overview',
  templateUrl: './send-invitation-overview.component.html',
  standalone: true,
  imports: [AccountContactsTablesComponent, RouterLink, SharedModule],
})
export class SendInvitationOverviewComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(Store);

  readonly account = toSignal(this.store.select(selectAccount));
  readonly metsContacts = toSignal(
    this.store.select(selectSelectedMetsContacts)
  );
  readonly registryContacts = toSignal(
    this.store.select(selectSelectedRegistryContacts)
  );

  ngOnInit() {
    this.store.dispatch(SendInvitationActions.INIT_OVERVIEW());

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get('accountId')}/${SEND_INVITATION_BASE_PATH}/${SendInvitationWizardPaths.SELECT_CONTACTS}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onSubmit() {
    this.store.dispatch(SendInvitationActions.SUBMIT_REQUEST());
  }

  navigateToSelectContacts() {
    this.store.dispatch(
      SendInvitationActions.NAVIGATE_TO({
        step: SendInvitationWizardPaths.SELECT_CONTACTS,
      })
    );
  }

  onCancel() {
    const route = this.router.url;
    this.store.dispatch(SendInvitationActions.CANCEL({ route }));
  }
}
