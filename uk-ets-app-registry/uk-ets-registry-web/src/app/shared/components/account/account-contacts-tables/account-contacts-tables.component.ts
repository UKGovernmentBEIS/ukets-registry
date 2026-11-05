import { Component, input } from '@angular/core';
import {
  claimInvitationStatusMap,
  MetsContact,
  RegistryContact,
} from '@shared/model/account';
import {
  MetsContactOperatorTypePipe,
  MetsContactTypePipe,
  RegistryContactTypePipe,
} from '@shared/pipes';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-account-contacts-tables',
  templateUrl: './account-contacts-tables.component.html',
  standalone: true,
  imports: [
    SharedModule,
    MetsContactTypePipe,
    MetsContactOperatorTypePipe,
    RegistryContactTypePipe,
  ],
})
export class AccountContactsTablesComponent {
  claimInvitationStatusMap = claimInvitationStatusMap;

  readonly metsContacts = input.required<MetsContact[]>();
  readonly registryContacts = input.required<RegistryContact[]>();
}
