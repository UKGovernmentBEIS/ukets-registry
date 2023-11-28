import { createAction, props } from '@ngrx/store';
import { NavigationExtras, Params } from '@angular/router';
import { AccountAllocation, AccountDetails } from '@shared/model/account';
import { AllocationStatus } from '@account-management/account-list/account-list.model';

export const navigateTo = createAction(
  '[Account Closure Wizard] Navigate to',
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const setClosureComment = createAction(
  '[Account Closure Wizard] Set closure comment',
  props<{ closureComment: string }>()
);

export const setClosureCommentSuccess = createAction(
  '[Account Closure Wizard] Set closure comment success',
  props<{ accountDetails: AccountDetails; closureComment: string }>()
);

export const fetchAccountPendingAllocationTaskExistsForAccountClosure =
  createAction(
    '[Account Closure Wizard] Fetch Pending Allocation Task',
    props<{ accountId: string }>()
  );

export const fetchAccountPendingAllocationTaskExistsForAccountClosureSuccess =
  createAction(
    '[Account Closure Wizard] Fetch Pending Allocation Task Success',
    props<{ pendingAllocationTaskExists: boolean }>()
  );

export const fetchAccountAllocationForAccountClosure = createAction(
  '[Account Closure Wizard] Fetch allocation',
  props<{ accountId: string }>()
);

export const fetchAccountAllocationForAccountClosureSuccess = createAction(
  '[Account Closure Wizard] Fetch allocation Success',
  props<{ allocation: AccountAllocation }>()
);

export const submitClosureRequest = createAction(
  '[Account Closure Wizard] Submit closure request',
  props<{
    allocationClassification: AllocationStatus;
    noActiveAR: boolean;
    closureComment: string;
  }>()
);

export const submitClosureRequestSuccess = createAction(
  '[Account Closure Wizard] Submit closure request success',
  props<{ requestId: string }>()
);

export const cancelClicked = createAction(
  '[Account Closure Wizard] Cancel clicked',
  props<{ route: string }>()
);

export const cancelAccountClosureRequest = createAction(
  '[Account Closure Wizard] Cancel closure request'
);

export const clearAccountClosureState = createAction(
  '[Account Closure Wizard] Clear state'
);
