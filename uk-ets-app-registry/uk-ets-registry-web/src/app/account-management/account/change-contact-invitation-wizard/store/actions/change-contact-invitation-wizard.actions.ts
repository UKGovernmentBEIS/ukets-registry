import { NavigationExtras } from '@angular/router';
import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { MultiSelectedItem } from '@shared/components/multi-select-table';
import { MetsContact, RegistryContact } from '@shared/model/account';

export const ChangeContactInvitationWizardActions = createActionGroup({
  source: 'ChangeContactInvitationWizard',
  events: {
    'Navigate to': props<{
      step: string;
      extras?: NavigationExtras;
    }>(),
    'Set Selected Contacts': props<{
      metsContactsWithSelectState: MultiSelectedItem<MetsContact>[];
      registryContactsWithSelectState: MultiSelectedItem<RegistryContact>[];
    }>(),
    Cancel: props<{ route: string }>(),
    'Cancel Request': emptyProps(),
    'Complete Selected Contacts': emptyProps(),
    'Clear Request': emptyProps(),
    'Submit Request': emptyProps(),
    'Submit Request Success': emptyProps(),
    'Submit Request Failure': props<{
      error: unknown;
    }>(),
    'Init Overview': emptyProps(),
  },
});
