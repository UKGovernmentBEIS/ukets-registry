import { verifyBeforeAndAfterActionDispatched } from '../../../../testing/helpers/reducer.test.helper';
import { reducer } from './email-change.reducer';
import { navigateToEmailChangeWizard } from '@email-change/action/email-change.actions';
import { EmailChange } from '@email-change/model/email-change.model';

describe('email change reducer', () => {
  it('should update the urid when prepareWizard is dispatched', () => {
    const uridValue = 'UK12345';
    verifyBeforeAndAfterActionDispatched<EmailChange>(
      reducer,
      {
        urid: null,
        newEmail: null,
        callerUrl: null
      },
      state => {
        expect(state.urid).toBeFalsy();
      },
      navigateToEmailChangeWizard({
        urid: uridValue,
        caller: { route: null }
      }),
      state => {
        expect(state.urid).toBe(uridValue);
      }
    );
  });
});
