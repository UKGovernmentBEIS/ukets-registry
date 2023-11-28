import { verifyBeforeAndAfterActionDispatched } from '../../../../testing/helpers/reducer.test.helper';
import { EnrolledUser } from '@registry-web/registry-administration/authority-setting/model';
import {
  AuthoritySettingState,
  reducer
} from '@registry-web/registry-administration/authority-setting/reducer/authority-setting.reducer';
import {
  cancelAuthoritySetting,
  clearAuthoritySettingState,
  fetchEnrolledUserSuccess,
  setUserAsAuthoritySuccess
} from '@registry-web/registry-administration/authority-setting/action';
import { Action } from '@ngrx/store';

describe('authority setting reducer', () => {
  function verifyClearState(action: Action) {
    const fetchedEnrolledUser: EnrolledUser = {
      userId: 'UK123123',
      firstName: 'first',
      lastName: 'last',
      email: 'email',
      knownAs: 'knownAs'
    };

    verifyBeforeAndAfterActionDispatched<AuthoritySettingState>(
      reducer,
      { enrolledUser: fetchedEnrolledUser },
      state => {
        expect(state.enrolledUser).toBe(fetchedEnrolledUser);
      },
      action,
      state => {
        expect(state.enrolledUser).toBeFalsy();
      }
    );
  }

  it('should update the enrolled user when fetchEnrolledUserSuccess is dispatched', () => {
    const fetchedEnrolledUser: EnrolledUser = {
      userId: 'UK123123',
      firstName: 'first',
      lastName: 'last',
      email: 'email',
      knownAs: 'knownAs'
    };

    verifyBeforeAndAfterActionDispatched<AuthoritySettingState>(
      reducer,
      { enrolledUser: null },
      state => {
        expect(state.enrolledUser).toBeFalsy();
      },
      fetchEnrolledUserSuccess({
        enrolledUser: fetchedEnrolledUser
      }),
      state => {
        expect(state.enrolledUser).toBe(fetchedEnrolledUser);
      }
    );
  });

  it('should clear the state', () => {
    verifyClearState(setUserAsAuthoritySuccess());
    verifyClearState(cancelAuthoritySetting());
    verifyClearState(clearAuthoritySettingState());
  });
});
