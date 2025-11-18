import { Component, computed, input } from '@angular/core';
import { Account } from '@registry-web/shared/model/account';

@Component({
  selector: 'app-account-contacts',
  templateUrl: './account-contacts.component.html',
})
export class AccountContactsComponent {
  readonly account = input.required<Account>();
  readonly isSeniorOrJuniorAdmin = input<boolean>(false);

  readonly metsContacts = computed(() => this.account()?.metsContacts || []);
  readonly registryContacts = computed(
    () => this.account()?.registryContacts || []
  );

  onSendInvitation() {}
}
