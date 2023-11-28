import { verifyBeforeAndAfterActionDispatched } from '../../../../../testing/helpers/reducer.test.helper';
import { AllocationStatusState, reducer } from './allocation-status.reducer';
import {
  continueToUpdateRequestVerification,
  fetchAllocationStatus,
  fetchAllocationStatusSuccess,
  updateAllocationStatusSuccess
} from '@account-management/account/allocation-status/actions/allocation-status.actions';
import {
  AccountAllocationStatus,
  UpdateAllocationStatusRequest
} from '@account-management/account/allocation-status/model';
import { AllocationStatus } from '@shared/model/account';

describe('Allocation status reducer', () => {
  it('should turn the annualAllocationStatusesLoaded flag to false when the fetchAllocationStatus is dispatched', () => {
    verifyBeforeAndAfterActionDispatched<AllocationStatusState>(
      reducer,
      {
        annualAllocationStatusesLoaded: true,
        updatedAccountId: null,
        updateRequest: null,
        annualAllocationStatuses: null
      },
      state => {
        expect(state.annualAllocationStatusesLoaded).toBeTruthy();
      },
      fetchAllocationStatus({ accountId: '1234' }),
      state => {
        expect(state.annualAllocationStatusesLoaded).toBeFalsy();
      }
    );
  });

  it(`should store the annual allocation statuses and turn the annualAllocationStatusesLoaded flag to true
  when the fetchAllocationStatusSuccess is dispatched`, () => {
    const allocationStatus: AccountAllocationStatus = {
      2023: AllocationStatus.WITHHELD
    };

    verifyBeforeAndAfterActionDispatched<AllocationStatusState>(
      reducer,
      {
        annualAllocationStatusesLoaded: false,
        updatedAccountId: null,
        updateRequest: null,
        annualAllocationStatuses: null
      },
      state => {
        expect(state.annualAllocationStatuses).toBeFalsy();
        expect(state.annualAllocationStatusesLoaded).toBeFalsy();
      },
      fetchAllocationStatusSuccess({ allocationStatus }),
      state => {
        expect(state.annualAllocationStatusesLoaded).toBeTruthy();
        expect(state.annualAllocationStatuses).toBeTruthy();
        expect(state.annualAllocationStatuses).toBe(allocationStatus);
      }
    );
  });

  it('should store the allocation status update request when the continueToUpdateRequestVerification is dispatched', () => {
    const request: UpdateAllocationStatusRequest = {
      justification: 'test test test',
      changedStatus: {
        2021: AllocationStatus.ALLOWED
      }
    };
    verifyBeforeAndAfterActionDispatched(
      reducer,
      {
        annualAllocationStatuses: null,
        updatedAccountId: '21321',
        annualAllocationStatusesLoaded: true,
        updateRequest: null
      },
      state => {
        expect(state.updateRequest).toBeFalsy();
      },
      continueToUpdateRequestVerification({
        updateAllocationStatusRequest: request
      }),
      state => {
        expect(state.updateRequest).toBe(request);
      }
    );
  });

  it(`should store the id of the account which allocation status has been updated.`, () => {
    const updatedAccountId = '2132';
    verifyBeforeAndAfterActionDispatched(
      reducer,
      {
        updateRequest: null,
        annualAllocationStatusesLoaded: false,
        annualAllocationStatuses: null,
        updatedAccountId: null
      },
      state => {
        expect(state.updatedAccountId).toBeFalsy();
      },
      updateAllocationStatusSuccess({ updatedAccountId }),
      state => {
        expect(state.updatedAccountId).toBe(updatedAccountId);
      }
    );
  });
});
