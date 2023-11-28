import * as fromTokenChangeActions from './token-change.actions';

describe('Tests the change token actions', () => {
  it('Tests the submit reason action', () => {
    expect(fromTokenChangeActions.actionSubmitReason.type).toBe(
      '[Token Change Action] Submit reason'
    );
  });

  it('Tests the submit token action', () => {
    expect(fromTokenChangeActions.actionSubmitToken.type).toBe(
      '[Token Change Action] Submit one time password'
    );
  });

  it('Tests the submit proposal action', () => {
    expect(fromTokenChangeActions.actionSubmitProposal.type).toBe(
      '[Token Change Action] Propose token change'
    );
  });

  it('Tests the first navigation action', () => {
    expect(fromTokenChangeActions.actionNavigateToEnterReason.type).toBe(
      '[Token Change Action] Navigate to Enter Reason page'
    );
  });

  it('Tests the first navigation action', () => {
    expect(fromTokenChangeActions.actionNavigateToEnterCode.type).toBe(
      '[Token Change Action] Navigate to Enter Code page'
    );
  });

  it('Tests the first navigation action', () => {
    expect(fromTokenChangeActions.actionNavigateToVerification.type).toBe(
      '[Token Change Action] Navigate to Verification page'
    );
  });

  it('Tests the first navigation action', () => {
    expect(fromTokenChangeActions.actionValidateEmailToken.type).toBe(
      '[Token Change Action] Validate Email Token'
    );
  });

  it('Tests the first navigation action', () => {
    expect(fromTokenChangeActions.actionValidateEmailTokenSuccess.type).toBe(
      '[Token Change Action] Validate Email Token Success'
    );
  });

  it('Tests the first navigation action', () => {
    expect(fromTokenChangeActions.actionValidateEmailTokenFailure.type).toBe(
      '[Token Change Action] Validate Email Token Failure'
    );
  });
});
