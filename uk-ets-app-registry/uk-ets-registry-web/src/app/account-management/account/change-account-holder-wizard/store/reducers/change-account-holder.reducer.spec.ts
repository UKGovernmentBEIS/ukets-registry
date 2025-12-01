import {
  reducer,
  initialState,
  ChangeAccountHolderWizardState,
} from '@change-account-holder-wizard/store/reducers';
import { AccountHolderType } from '@shared/model/account';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import { expect } from '@jest/globals';

describe('Change Account Holder reducer', () => {
  it('sets the account holder type', () => {
    const beforeSetAccountHolderTypeState = reducer(initialState, {} as any);
    expect(
      beforeSetAccountHolderTypeState.acquiringAccountHolder.type
    ).toBeNull();

    const loadAction = ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_TYPE(
      {
        holderType: AccountHolderType.INDIVIDUAL,
      }
    );
    const afterSetAccountHolderTypeState = reducer(initialState, loadAction);
    expect(afterSetAccountHolderTypeState.acquiringAccountHolder.type).toEqual(
      AccountHolderType.INDIVIDUAL
    );
  });

  it('clears the state', () => {
    const nonEmptyState: ChangeAccountHolderWizardState = {
      acquiringAccountHolder: {
        id: 32,
        type: AccountHolderType.INDIVIDUAL,
        details: {
          name: 'Bruce Lee',
          firstName: 'Bruce',
          lastName: 'Lee',
          birthDate: {
            day: '27',
            month: '11',
            year: '1940',
          },
          countryOfBirth: 'Hong Kong',
          isOverEighteen: true,
        },
        address: null,
      },
      isPrimaryAddressSameAsHolder: true,
    };
    const beforeClearAccountHolderChangeRequestState = reducer(
      nonEmptyState,
      {} as any
    );
    expect(
      beforeClearAccountHolderChangeRequestState.acquiringAccountHolder
    ).toBeTruthy();
    expect(
      beforeClearAccountHolderChangeRequestState.isPrimaryAddressSameAsHolder
    ).toBeTruthy();

    const clearAccountHolderChangeRequestAction =
      ChangeAccountHolderWizardActions.CLEAR_ACCOUNT_HOLDER_CHANGE_REQUEST();
    const afterClearAccountHolderChangeRequestState = reducer(
      initialState,
      clearAccountHolderChangeRequestAction
    );
    expect(
      afterClearAccountHolderChangeRequestState.acquiringAccountHolder
    ).toStrictEqual({ address: null, details: null, id: null, type: null });
    expect(
      afterClearAccountHolderChangeRequestState.isPrimaryAddressSameAsHolder
    ).toBeFalsy();
  });
});
