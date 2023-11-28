import { createAction, props } from '@ngrx/store';
import {
  AccountAllocationStatus,
  UpdateAllocationStatusRequest
} from '@allocation-status/model';

/**
 * This first action should be dispatched from the resolver. This action will be mapped to other actions from the effect
 * [fetchAllocationStatus, canGoBack]
 */
export const prepareWizard = createAction(
  `[Allocation Status Resolver]
Prepare wizard by preloading the allocation status and by preparing the go back url`,
  props<{ accountId: string }>()
);

/**
 * This action triggers the reducer to update the state (laoded = false) and then an effect should call
 * the underlying service to fetch the result. After that the effect
 * maps this action to the fetchAllocationStatusSuccess which carries the fetched results.
 */
export const fetchAllocationStatus = createAction(
  `[Allocation Status Effect]
Fetch the account allocation status / annual allocation statuses of the account`,
  props<{ accountId: string }>()
);

/**
 * This action is handled by the reducer which stores the results to the state.
 */
export const fetchAllocationStatusSuccess = createAction(
  '[Allocation Status Effect] ' +
    'Load the fetched allocation status / annual allocation statuses of the account to the store',
  props<{
    allocationStatus: AccountAllocationStatus;
  }>()
);

/**
 * This action is dispatched on continue button click of the fist page of the wizard.
 * This action is handled by the reducer first. The reducer stores the UpdateAllocationStatusRequest.
 * After reducer process, this action is handled by an effect which maps this action to the shared actions
 * [navigateToVerificationPage, canGoBack]
 */
export const continueToUpdateRequestVerification = createAction(
  `[Change Allocation Status Page Continue button]
  Continue to the check the update, sign and apply page`,
  props<{
    updateAllocationStatusRequest: UpdateAllocationStatusRequest;
  }>()
);

/**
 * This action is handled by an effect which maps this action to the navigateTo shared action with the proper route
 * /account/[accountId]/allocation-status/[Check the allocation Status change Page url]
 */
export const navigateToVerificationPage = createAction(
  '[Allocation Status Effect] Navigate to the check the update, sign and apply page'
);

/**
 * This action is dispatched on apply button click of the verification page.
 * It is handled by an effect which calls the underlying service and then maps it to the following actions
 * [updateAllocationStatusSuccess, navigateToAccountAllocation, resetAllocationStatusState]
 */
export const updateAllocationStatus = createAction(
  `[Allocation Status Verification Page Apply button]
Submit the update allocation status request`
);

/**
 * This action is handled by the reducer which stores the id of the account which allocation status has been updated.
 */
export const updateAllocationStatusSuccess = createAction(
  '[Allocation Status Effect] Update completed',
  props<{
    updatedAccountId: string;
  }>()
);

/**
 * This action is handled by an effect which maps it to the navigateTo shared action with route
 * /account/[accountId]/ and extras: { queryParams: { selectedSideMenu: 'Allocation status for UK allowances' } }
 */
export const navigateToAccountAllocation = createAction(
  '[Allocation Status Effect] Navigate back to the account details and to the account allocation page.'
);

/**
 * This action is handled by the reducer which resets the state to the initial state.
 */
export const resetAllocationStatusState = createAction(
  '[Allocation Status Effect] Reset the allocation status state'
);

/**
 * This action can be dispatched from every step of the wizard.
 * It is handled by an effect which maps it to the actions [canGoBack, navigateTo].
 */
export const navigateToCancel = createAction(
  '[Allocation Status Wizard] Click on Cancel link'
);

/**
 * This action is dispatched from the shared cancel component and an effect handles it.
 * The effect maps it with the actions [resetAllocationStatusState, navigateToAccountAllocation]
 */
export const cancel = createAction(
  '[Cancel Shared Component] Click on cancel button'
);
