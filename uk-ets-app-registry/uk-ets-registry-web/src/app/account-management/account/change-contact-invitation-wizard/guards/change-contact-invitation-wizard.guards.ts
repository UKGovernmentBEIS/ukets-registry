import { inject } from '@angular/core';
import { CanDeactivateFn } from '@angular/router';
import { Store } from '@ngrx/store';
import { ChangeContactInvitationWizardActions } from '@change-contact-invitation-wizard/store';

export const clearChangeContactInvitationGuard: CanDeactivateFn<
  boolean
> = () => {
  inject(Store).dispatch(ChangeContactInvitationWizardActions.clearRequest());
  return true;
};

// export const canActivateSendInvitation: CanActivateFn = (route) => {
//   const account$ = inject(Store).select(selectAccount);
//   return account$.pipe(
//     map(
//       (account) =>
//         !!(account?.accountHolder?.id && account?.identifier) ||
//         createUrlTreeFromSnapshot(route, [`../`], {
//           selectedSideMenu: MenuItemEnum.CONTACTS,
//         })
//     )
//   );
// };

// export const canActivateSendInvitationOverview: CanActivateFn = (route) => {
//   const state$ = inject(Store).select(selectSendInvitationWizardState);
//   return state$.pipe(
//     map(
//       (state) =>
//         isWizardCompleted(state) ||
//         createUrlTreeFromSnapshot(route, [
//           `../${SendInvitationWizardPaths.SELECT_CONTACTS}`,
//         ])
//     )
//   );
// };
