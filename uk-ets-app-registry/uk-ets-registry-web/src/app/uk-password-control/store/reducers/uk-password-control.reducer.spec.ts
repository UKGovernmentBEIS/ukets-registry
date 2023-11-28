import * as PasswordStrengthActions from '@uk-password-control/store/actions';
import {
  reducer,
  initialState,
  PasswordStrengthState,
} from '@uk-password-control/store/reducers';
import { Score } from '@uk-password-control/model';
import { clearPasswordStrength } from '@uk-password-control/store/actions';

describe('UK Password Control reducer', () => {
  const result: Score = 4;

  it('sets the calculated score', () => {
    const beforeFetchLoadAndShowState = reducer(initialState, {} as any);
    expect(beforeFetchLoadAndShowState.score).toBeNull();

    const loadAction = PasswordStrengthActions.loadPasswordScoreSuccess({
      score: result,
    });
    const afterLoadState = reducer(initialState, loadAction);
    expect(afterLoadState.score).toEqual(result);
  });

  it('clears the state', () => {
    const nonEmptyState: PasswordStrengthState = {
      score: 4,
    };
    const beforeClearPasswordStrengthState = reducer(nonEmptyState, {} as any);
    expect(beforeClearPasswordStrengthState.score).toBeTruthy();

    const clearPasswordStrengthAction = clearPasswordStrength();
    const afterClearAccountStatusState = reducer(
      initialState,
      clearPasswordStrengthAction
    );
    expect(afterClearAccountStatusState.score).toBeNull();
  });
});
