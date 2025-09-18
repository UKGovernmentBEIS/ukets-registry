import { TaskListState, taskListFeatureKey } from './task-list.reducer';
import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromAuth from './../../auth/auth.selector';
import { RequestType } from '@task-management/model';

const selectTaskListState =
  createFeatureSelector<TaskListState>(taskListFeatureKey);

export const selectSearchState = createSelector(
  selectTaskListState,
  (state) => state
);

export const areCriteriaExternal = createSelector(
  selectTaskListState,
  (state) => state.externalCriteria
);

export const selectPagination = createSelector(
  selectTaskListState,
  (state) => state.pagination
);

export const selectPageParameters = createSelector(
  selectTaskListState,
  (state) => state.pageParameters
);

export const selectSortParameters = createSelector(
  selectTaskListState,
  (state) => state.sortParameters
);

export const selectResults = createSelector(
  selectTaskListState,
  (state) => state.results
);

export const selectHideCriteria = createSelector(
  selectTaskListState,
  (state) => state.hideCriteria
);

export const selectShowAdvancedSearch = createSelector(
  selectTaskListState,
  (state) => state.showAdvancedSearch
);

export const selectResultsLoaded = createSelector(
  selectTaskListState,
  (state) => state.resultsLoaded
);

export const selectCriteria = createSelector(
  selectTaskListState,
  (state) => state.criteria
);

export const selectedTasks = createSelector(
  selectTaskListState,
  (state) => state.selectedTasks
);

export const selectSuccess = createSelector(
  selectTaskListState,
  (state) => state.bulkActionSuccess
);

/**
 * Filters out task types depending on the admin/non-admin and authority case.
 * TODO filter the task type picklist at the backend, see https://pmo.trasys.be/jira/browse/UKETS-2163.
 */
// eslint-disable-next-line ngrx/prefix-selectors-with-select
export const taskTypeOptions = createSelector(
  selectTaskListState,
  fromAuth.isAdmin,
  fromAuth.isAuthorityUser,
  (state, isAdmin, isAuthorityUser) => {
    return state?.taskTypeOptions
      .filter((t) =>
        !isAdmin
          ? isAuthorityUser
            ? t.value === RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST ||
              t.value === RequestType.TRANSACTION_REQUEST
            : isClaimableByAr(t.value)
          : t.value !== RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST
      )
      .sort((a, b) => (a.label > b.label ? 1 : -1));
  }
);

export const taskTypeOptionsAll = createSelector(
  selectTaskListState,
  (state) => state?.taskTypeOptions
);

export const accountTypeOptions = createSelector(
  selectTaskListState,
  (state) => state?.accountTypeOptions
);

export const selectAreTasksClaimed = (requestIds: string[]) =>
  createSelector(selectTaskListState, (state) =>
    state.results
      ?.filter((task) => requestIds.indexOf(task.requestId) !== -1)
      .some((task) => task.taskStatus === 'CLAIMED')
  );

function isClaimableByAr(request: string): boolean {
  return (
    request !== RequestType.ACCOUNT_OPENING_REQUEST &&
    request !== RequestType.ACCOUNT_TRANSFER &&
    request !== RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST &&
    request !==
      RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD &&
    request !== RequestType.REQUESTED_EMAIL_CHANGE &&
    request !== RequestType.LOST_TOKEN &&
    request !==
      RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST &&
    request !== RequestType.ACCOUNT_CLOSURE_REQUEST &&
    request !== RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST &&
    request !== RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST &&
    request !==
      RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE &&
    request !== RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST &&
    request !== RequestType.LOST_PASSWORD_AND_TOKEN &&
    request !== RequestType.CHANGE_TOKEN &&
    request !== RequestType.AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST &&
    request !== RequestType.AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST &&
    request !== RequestType.AIRCRAFT_OPERATOR_UPDATE_REQUEST &&
    request !== RequestType.MARITIME_OPERATOR_UPDATE_REQUEST &&
    request !== RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST &&
    request !== RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS &&
    request !==
      RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE &&
    request !== RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS &&
    request !== RequestType.TRANSACTION_RULES_UPDATE_REQUEST &&
    request !== RequestType.USER_DETAILS_UPDATE_REQUEST &&
    request !== RequestType.PRINT_ENROLMENT_LETTER_REQUEST &&
    request !== RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST &&
    request !== RequestType.ALLOCATION_REQUEST &&
    request !== RequestType.USER_DEACTIVATION_REQUEST &&
    request !== RequestType.EMISSIONS_TABLE_UPLOAD_REQUEST
  );
}
