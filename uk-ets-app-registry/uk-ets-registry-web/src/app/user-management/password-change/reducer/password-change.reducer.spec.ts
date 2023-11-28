import { verifyBeforeAndAfterActionDispatched } from '../../../../testing/helpers/reducer.test.helper';
import { navigateToPasswordChangeWizard } from '@user-management/password-change/action/password-change.actions';
import { reducer } from '.';

describe('passwordChangeReducer', () => {
  it('should update the email when navigateToPasswordChangeWizard is dispatched', () => {
    const value = 'test@test.com';
    verifyBeforeAndAfterActionDispatched<any>(
      reducer,
      {
        email: null,
        newPassword: null
      },
      state => {
        expect(state.email).toBeFalsy();
      },
      navigateToPasswordChangeWizard({
        email: value
      }),
      state => {
        expect(state.email).toBe(value);
      }
    );
  });
});
