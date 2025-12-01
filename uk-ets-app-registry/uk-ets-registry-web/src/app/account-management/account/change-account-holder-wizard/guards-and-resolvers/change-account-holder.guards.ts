import { inject } from '@angular/core';
import {
  CanActivateFn,
  CanDeactivateFn,
  createUrlTreeFromSnapshot,
} from '@angular/router';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import { Store } from '@ngrx/store';
import {
  selectAcquiringAccountHolderType,
  selectChangeAccountHolderWizardState,
} from '@change-account-holder-wizard/store/selectors';
import { map } from 'rxjs';
import { ChangeAccountHolderWizardPaths } from '@change-account-holder-wizard/model';
import { selectAccount } from '@account-management/account/account-details/account.selector';
import {
  isExistingAccountHolder,
  isWizardCompleted,
} from '../change-account-holder.helpers';
import { AccountHolderType } from '@registry-web/shared/model/account';

export const clearChangeAccountHolderRequestGuard: CanDeactivateFn<unknown> = (
  component,
  currentRoute,
  currentState,
  nextState
) => {
  inject(Store).dispatch(
    ChangeAccountHolderWizardActions.CLEAR_ACCOUNT_HOLDER_CHANGE_REQUEST()
  );
  return true;
};

export const canActivateChangeAccountHolder: CanActivateFn = (route) => {
  const account$ = inject(Store).select(selectAccount);
  return account$.pipe(
    map((account) => {
      const hasAllowedAccountStatus =
        account?.accountDetails?.accountStatus &&
        account.accountDetails.accountStatus !== 'CLOSURE_PENDING' &&
        account.accountDetails.accountStatus !== 'CLOSED';

      return (
        (hasAllowedAccountStatus &&
          !!(account?.accountHolder?.id && account?.identifier)) ||
        createUrlTreeFromSnapshot(route, [`../`])
      );
    })
  );
};

export const canActivateSelectExistingOrAddNewStep: CanActivateFn = (route) => {
  const store = inject(Store);
  const accountHolderType$ = store.select(selectAcquiringAccountHolderType);

  return accountHolderType$.pipe(
    map(
      (accountHolderType) =>
        !!accountHolderType ||
        createUrlTreeFromSnapshot(route, [
          `../${ChangeAccountHolderWizardPaths.SELECT_TYPE}`,
        ])
    )
  );
};

export const canActivateChangeAccountHolderAddNewStep: CanActivateFn = (
  route
) => {
  const state$ = inject(Store).select(selectChangeAccountHolderWizardState);

  return state$.pipe(
    map((state) => {
      const path = route.routeConfig.path;
      const accountHolderType = state.acquiringAccountHolder.type;

      if (!accountHolderType) {
        return createUrlTreeFromSnapshot(route, [
          `../${ChangeAccountHolderWizardPaths.SELECT_TYPE}`,
        ]);
      }

      if (!state.accountHolderSelectionType) {
        return createUrlTreeFromSnapshot(route, [
          `../${ChangeAccountHolderWizardPaths.SELECT_EXISTING_OR_ADD_NEW}`,
        ]);
      }

      if (isExistingAccountHolder(state.accountHolderSelectionType)) {
        return createUrlTreeFromSnapshot(route, [
          `../${state.isAccountHolderOrphan ? ChangeAccountHolderWizardPaths.DELETE_ORPHAN_ACCOUNT_HOLDER : ChangeAccountHolderWizardPaths.OVERVIEW}`,
        ]);
      }

      if (
        (path === ChangeAccountHolderWizardPaths.INDIVIDUAL_DETAILS ||
          path === ChangeAccountHolderWizardPaths.INDIVIDUAL_CONTACT) &&
        accountHolderType === AccountHolderType.ORGANISATION
      ) {
        return createUrlTreeFromSnapshot(route, [
          `../${ChangeAccountHolderWizardPaths.ORGANISATION_DETAILS}`,
        ]);
      }

      if (
        (path === ChangeAccountHolderWizardPaths.ORGANISATION_DETAILS ||
          path === ChangeAccountHolderWizardPaths.ORGANISATION_ADDRESS) &&
        accountHolderType === AccountHolderType.INDIVIDUAL
      ) {
        return createUrlTreeFromSnapshot(route, [
          `../${ChangeAccountHolderWizardPaths.INDIVIDUAL_DETAILS}`,
        ]);
      }

      return true;
    })
  );
};

export const canActivateChangeAccountHolderDeleteOrphan: CanActivateFn = (
  route
) => {
  const state$ = inject(Store).select(selectChangeAccountHolderWizardState);

  return state$.pipe(
    map((state) => {
      if (!state.acquiringAccountHolder.type) {
        return createUrlTreeFromSnapshot(route, [
          `../${ChangeAccountHolderWizardPaths.SELECT_TYPE}`,
        ]);
      }

      if (!state.accountHolderSelectionType) {
        return createUrlTreeFromSnapshot(route, [
          `../${ChangeAccountHolderWizardPaths.SELECT_EXISTING_OR_ADD_NEW}`,
        ]);
      }

      return (
        state.isAccountHolderOrphan ||
        createUrlTreeFromSnapshot(route, [
          `../${ChangeAccountHolderWizardPaths.OVERVIEW}`,
        ])
      );
    })
  );
};

export const canActivateChangeAccountHolderOverview: CanActivateFn = (
  route
) => {
  const state$ = inject(Store).select(selectChangeAccountHolderWizardState);
  return state$.pipe(
    map(
      (state) =>
        isWizardCompleted(state) ||
        createUrlTreeFromSnapshot(route, [
          `../${ChangeAccountHolderWizardPaths.SELECT_TYPE}`,
        ])
    )
  );
};
