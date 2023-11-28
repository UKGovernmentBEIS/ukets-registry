import { Reconciliation } from '@shared/model/reconciliation-model';
import { verifyBeforeAndAfterActionDispatched } from '../../../../testing/helpers/reducer.test.helper';
import {
  ReconciliationState,
  reducer,
} from '@reconciliation-administration/reducers/reconciliation.reducer';
import { updateLatestReconciliation } from '@reconciliation-administration/actions/reconciliation.actions';

describe('Reconciliation reducer', () => {
  it(`should update the stored reconciliation with the latest reconciliation
  that updateReconciliation action carries when the last is dispatched`, () => {
    const storedReconciliation: Reconciliation = {
      identifier: 12345,
      created: '10/08/2020 11:00',
      updated: '10/08/2020 11:00',
      status: 'INITIATED',
    };
    const updatedReconciliation = {
      identifier: 12345,
      created: '10/08/2020 11:00',
      updated: '10/08/2020 11:10',
      status: 'COMPLETED',
    };
    const updatedStatus = 'COMPLETED';
    verifyBeforeAndAfterActionDispatched<ReconciliationState>(
      reducer,
      {
        lastStartedReconciliation: storedReconciliation,
      },
      (state) => {
        expect(state.lastStartedReconciliation.status).toBe(
          storedReconciliation.status
        );
      },
      updateLatestReconciliation({ reconciliation: updatedReconciliation }),
      (state) => {
        expect(state.lastStartedReconciliation).toBeTruthy();
        expect(state.lastStartedReconciliation.status).toBe(
          updatedReconciliation.status
        );
        expect(state.lastStartedReconciliation.updated).toBe(
          updatedReconciliation.updated
        );
      }
    );
  });
});
