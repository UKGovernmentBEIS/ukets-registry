import { inject } from '@angular/core';
import {
  CanActivateFn,
  CanDeactivateFn,
  createUrlTreeFromSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { map } from 'rxjs';
import { selectAccount } from '@account-management/account/account-details/account.selector';
import {
  selectSendInvitationWizardState,
  SendInvitationActions,
} from '@send-invitation-wizard/store';
import { MenuItemEnum } from '@account-management/account/account-details/model';
import {
  isWizardCompleted,
  SendInvitationWizardPaths,
} from '@send-invitation-wizard/send-invitation-wizard.helpers';

export const clearSendInvitationGuard: CanDeactivateFn<unknown> = () => {
  inject(Store).dispatch(SendInvitationActions.CLEAR_REQUEST());
  return true;
};

export const canActivateSendInvitation: CanActivateFn = (route) => {
  const account$ = inject(Store).select(selectAccount);
  return account$.pipe(
    map(
      (account) =>
        !!(account?.accountHolder?.id && account?.identifier) ||
        createUrlTreeFromSnapshot(route, [`../`], {
          selectedSideMenu: MenuItemEnum.CONTACTS,
        })
    )
  );
};

export const canActivateSendInvitationOverview: CanActivateFn = (route) => {
  const state$ = inject(Store).select(selectSendInvitationWizardState);
  return state$.pipe(
    map(
      (state) =>
        isWizardCompleted(state) ||
        createUrlTreeFromSnapshot(route, [
          `../${SendInvitationWizardPaths.SELECT_CONTACTS}`,
        ])
    )
  );
};
