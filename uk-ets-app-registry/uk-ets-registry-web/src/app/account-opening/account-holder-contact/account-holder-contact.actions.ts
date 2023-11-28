import { Action, createAction, props } from '@ngrx/store';
import { AccountHolderContact } from '@shared/model/account/account-holder-contact';

export enum AccountHolderContactActionTypes {
  REMOVE_ACCOUNT_HOLDER_CONTACT = '[Account Holder Contact Wizard] Remove account holder contact',
  COMPLETE_WIZARD = '[Account Holder Contact Wizard] Complete',
  DELETE_ACCOUNT_HOLDER_CONTACT = '[Account Holder Contact Wizard] Delete account holder contact',
  SET_CURRENT_INDEX = '[Account Holder Contact Wizard] Set index',
  ACCOUNT_HOLDER_CONTACT_NEXT_PAGE = '[Account Holder Contact Wizard] Continue',
  SET_VIEW_STATE = '[Account Holder Contact Wizard] View state',
  FETCH_ACCOUNT_HOLDER_CONTACTS = '[Account Holder Contact Wizard] Fetch account holder contacts',
  POPULATE_ACCOUNT_HOLDER_CONTACTS = '[Account Holder Contact Wizard] Populate account holder contacts'
}

export const removeAccountHolderContact = createAction(
  AccountHolderContactActionTypes.REMOVE_ACCOUNT_HOLDER_CONTACT
);

export const completeWizard = createAction(
  AccountHolderContactActionTypes.COMPLETE_WIZARD,
  props<{ contactType: string }>()
);
export const deleteAccountHolderContact = createAction(
  AccountHolderContactActionTypes.DELETE_ACCOUNT_HOLDER_CONTACT,
  props<{ contactType: string }>()
);

export const nextPage = createAction(
  AccountHolderContactActionTypes.ACCOUNT_HOLDER_CONTACT_NEXT_PAGE,
  props<{
    accountHolderContact: AccountHolderContact;
    contactType: string;
  }>()
);

export const setViewState = createAction(
  AccountHolderContactActionTypes.SET_VIEW_STATE,
  props<{
    view: boolean;
  }>()
);

export const fetchAccountHolderContacts = createAction(
  AccountHolderContactActionTypes.FETCH_ACCOUNT_HOLDER_CONTACTS,
  props<{
    accountHolderId: string;
  }>()
);

export const populateAccountHolderContacts = createAction(
  AccountHolderContactActionTypes.POPULATE_ACCOUNT_HOLDER_CONTACTS,
  props<{ accountHolderContacts: AccountHolderContact[] }>()
);

export class FetchAccountHolderContacts implements Action {
  readonly type = AccountHolderContactActionTypes.FETCH_ACCOUNT_HOLDER_CONTACTS;
  constructor(readonly accountHolderId: string) {}
}

export const setSameAddress = createAction(
  '[Account Holder Contact Wizard] Set work address same as Account Holder address',
  props<{ sameAddress: boolean; contactType: string }>()
);
