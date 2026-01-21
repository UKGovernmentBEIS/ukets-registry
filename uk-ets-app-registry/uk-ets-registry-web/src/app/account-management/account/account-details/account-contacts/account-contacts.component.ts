import { Component, computed, inject, input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Account } from '@registry-web/shared/model/account';
import { SEND_INVITATION_BASE_PATH } from '@send-invitation-wizard/send-invitation-wizard.helpers';

@Component({
  selector: 'app-account-contacts',
  templateUrl: './account-contacts.component.html',
})
export class AccountContactsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly account = input.required<Account>();
  readonly isSeniorOrJuniorAdmin = input<boolean>(false);

  readonly metsContacts = computed(() => this.account()?.metsContacts || []);
  readonly registryContacts = computed(
    () => this.account()?.registryContacts || []
  );

  onSendInvitation() {
    this.router.navigate([SEND_INVITATION_BASE_PATH], {
      relativeTo: this.activatedRoute,
    });
  }
}
