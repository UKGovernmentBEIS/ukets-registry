import { NavigationExtras } from '@angular/router';
import { createActionGroup, emptyProps, props } from '@ngrx/store';

export const AutoClaimWizardActions = createActionGroup({
  source: 'AutoClaimWizard',
  events: {
    'Submit Request': emptyProps(),
    'Cancel AutoClaimWizard': props<{ route: string }>(),
    'Cancel Request': emptyProps(),
    'Navigate to': props<{
      step: string;
      extras?: NavigationExtras;
    }>(),
  },
});
