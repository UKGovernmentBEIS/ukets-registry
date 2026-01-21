import { Component, computed, input } from '@angular/core';
import {
  MetsContact,
  RegistryContact,
} from '@registry-web/shared/model/account';
import {
  MetsContactOperatorTypePipe,
  MetsContactTypePipe,
  RegistryContactTypePipe,
} from '@registry-web/shared/pipes';
import { SharedModule } from '@registry-web/shared/shared.module';

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
  readonly metsContacts = input.required<MetsContact[]>();
  readonly registryContacts = input.required<RegistryContact[]>();
}
