import * as fromAHUpdateActions from './account-holder-details-wizard.action';

describe('Tests the account holder update actions', () => {
  it('Tests the navigation action', () => {
    expect(fromAHUpdateActions.navigateTo.type).toBe(
      '[Account Holder details request update] Navigate to'
    );
  });

  it('Tests the cancel clicked action', () => {
    expect(fromAHUpdateActions.cancelClicked.type).toBe(
      '[Account Holder details request update] Cancel clicked'
    );
  });

  it('Tests the cancel request action', () => {
    expect(
      fromAHUpdateActions.cancelAccountHolderDetailsUpdateRequest.type
    ).toBe('[Account Holder details request update] Cancel update request');
  });

  it('Tests the clear request action', () => {
    expect(
      fromAHUpdateActions.clearAccountHolderDetailsUpdateRequest.type
    ).toBe('[Account Holder details request update] Clear update request');
  });

  it('Tests the request update type action', () => {
    expect(fromAHUpdateActions.setRequestUpdateType.type).toBe(
      '[Account Holder details request update] Set request update type'
    );
  });

  it('Tests the account holder details action', () => {
    expect(fromAHUpdateActions.setAccountHolderDetails.type).toBe(
      '[Account Holder details request update] Set account holder details values'
    );
  });

  it('Tests the account holder address action', () => {
    expect(fromAHUpdateActions.setAccountHolderAddress.type).toBe(
      '[Account Holder details request update] Set account holder new address'
    );
  });

  it('Tests the account holder contact details action', () => {
    expect(fromAHUpdateActions.setAccountHolderContactDetails.type).toBe(
      '[Account Holder details request update] Set account holder contact values'
    );
  });

  it('Tests the account holder contact work details action', () => {
    expect(fromAHUpdateActions.setAccountHolderContactWorkDetails.type).toBe(
      '[Account Holder details request update] Set account holder contact work values'
    );
  });

  it('Tests the submit update request action', () => {
    expect(fromAHUpdateActions.submitUpdateRequest.type).toBe(
      '[Account Holder details request update] Submit update request'
    );
  });

  it('Tests the submit update request success action', () => {
    expect(fromAHUpdateActions.submitUpdateRequestSuccess.type).toBe(
      '[Account Holder details request update] Submit update request success'
    );
  });

  it('Tests the is same primary address action', () => {
    expect(fromAHUpdateActions.setSameAddressPrimaryContact.type).toBe(
      '[Account Holder details request update] Set Primary work address same as Account Holder address'
    );
  });

  it('Tests the is same alternative primary address action', () => {
    expect(
      fromAHUpdateActions.setSameAddressAlternativePrimaryContact.type
    ).toBe(
      '[Account Holder details request update] Set Alternative Primary work address same as Account Holder address'
    );
  });
});
