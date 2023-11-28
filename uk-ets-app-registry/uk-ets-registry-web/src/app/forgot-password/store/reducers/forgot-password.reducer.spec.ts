import { reducer, initialState } from '.';
import {
  requestResetPasswordEmail,
  resetPasswordSuccess,
  validateTokenSuccess
} from '../actions/forgot-password.actions';

describe('Forgot Password reducer', () => {
  it('sets the reset password email', () => {
    const beforeRequestResetPasswordEmail = reducer(initialState, {} as any);
    expect(beforeRequestResetPasswordEmail.email).toBeNull();

    const requestResetPasswordEmailAction = requestResetPasswordEmail({
      email: 'iwantto@reset.pwd'
    });
    const afterRequestResetPasswordEmailState = reducer(
      initialState,
      requestResetPasswordEmailAction
    );
    expect(afterRequestResetPasswordEmailState.email).toEqual(
      'iwantto@reset.pwd'
    );
  });

  it('sets the reset password token', () => {
    const beforeValidateTokenSuccess = reducer(initialState, {} as any);
    expect(beforeValidateTokenSuccess.token).toBeNull();

    const requestResetPasswordEmailAction = validateTokenSuccess({
      token:
        'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJcInp6enp6MTExMUB0ZXN0Lm' +
        'NvbVwiIiwiYXVkIjoiIHVrLWV0cy13ZWItYXBwIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zd' +
        'Do4MDkxL2F1dGgiLCJleHAiOjE2MDA0MjUzOTAsImlhdCI6MTYwMDQyMTc5MH0.7glZUeaI' +
        'dWkUQaKv0anNgxpXkOYsQhCrJBw7JNoPmJ4'
    });
    const afterValidateTokenSuccessState = reducer(
      initialState,
      requestResetPasswordEmailAction
    );
    expect(afterValidateTokenSuccessState.token).toEqual(
      'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJcInp6enp6MTExMUB' +
        '0ZXN0LmNvbVwiIiwiYXVkIjoiIHVrLWV0cy13ZWItYXBwIiwiaXNzIjoiaHR0cDovL2xvY2Fs' +
        'aG9zdDo4MDkxL2F1dGgiLCJleHAiOjE2MDA0MjUzOTAsImlhdCI6MTYwMDQyMTc5MH0.7glZU' +
        'eaIdWkUQaKv0anNgxpXkOYsQhCrJBw7JNoPmJ4'
    );
  });

  it('sets the reset password email after passwd reset', () => {
    const beforeResetPassword = reducer(initialState, {} as any);
    expect(beforeResetPassword.email).toBeNull();

    const resetPasswordAction = resetPasswordSuccess({
      email: 'iwantto@reset.pwd',
      success: true
    });
    const afterResetPasswordState = reducer(initialState, resetPasswordAction);
    expect(afterResetPasswordState.email).toEqual('iwantto@reset.pwd');
  });
});
