import { NavigationExtras, Params } from '@angular/router';
import { createAction, props } from '@ngrx/store';
import {
  AccountHolder,
  AccountHolderAddress,
  AccountHolderContact,
  AccountHolderContactInfo,
  AccountHolderSelectionType,
  AccountHolderType,
  IndividualContactDetails,
  IndividualDetails,
  OrganisationDetails,
} from '@shared/model/account';

export enum ChangeAccountHolderActionTypes {
  NAVIGATE_TO = '[Change Account Holder Wizard] Navigate to',
  NAVIGATE_TO_ACCOUNT_HOLDER_DETAILS = '[Change Account Holder Wizard] Navigate to account holder details',

  SET_ACCOUNT_HOLDER_TYPE = '[Change Account Holder Wizard] - Set account holder type',
  SET_ACCOUNT_HOLDER_SELECTION_TYPE = '[Change Account Holder Wizard] - Set account holder selection type',
  SET_ACCOUNT_HOLDER_INDIVIDUAL_DETAILS = '[Change Account Holder Wizard] - Set account holder individual details',
  SET_ACCOUNT_HOLDER_INDIVIDUAL_CONTACT_DETAILS = '[Change Account Holder Wizard] - Set account holder individual contact details',
  SET_ACCOUNT_HOLDER_ORGANISATION_DETAILS = '[Change Account Holder Wizard] - Set account holder organisation details',
  SET_ACCOUNT_HOLDER_ORGANISATION_ADDRESS = '[Change Account Holder Wizard] - Set account holder organisation address',
  SET_ACCOUNT_HOLDER_CONTACT_DETAILS = '[Change Account Holder Wizard] - Set account holder contact details',
  SET_ACCOUNT_HOLDER_CONTACT_ADDRESS = '[Change Account Holder Wizard] - Set account holder contact address',

  CANCEL = '[Change Account Holder Wizard] Cancel clicked',
  CANCEL_CHANGE_ACCOUNT_HOLDER_REQUEST = '[Change Account Holder Wizard] Cancel change account holder request',
  COMPLETE_CHANGE_ACCOUNT_HOLDER = '[Change Account Holder Wizard] Complete Change Account Holder',
  ACCOUNT_HOLDER_NEXT_PAGE = '[Change Account Holder Wizard] - Continue',
  FETCH_ACCOUNT_HOLDER_LIST = '[Change Account Holder Wizard] - Fetch list',
  POPULATE_ACCOUNT_HOLDER_CONTACT_INFO = '[Change Account Holder Wizard] - Populate primary and alternative contacts',
  POPULATE_ACCOUNT_HOLDER_LIST = '[Change Account Holder Wizard] - Populate list',
  POPULATE_ACQUIRING_ACCOUNT_HOLDER = '[Change Account Holder Wizard] - Populate acquiring account holder',
  POPULATE_ACQUIRING_ACCOUNT_HOLDER_CONTACT = '[Change Account Holder Wizard] - Populate acquiring account holder contact',
  CLEAN_ACCOUNT_HOLDER_LIST = '[Change Account Holder Wizard] - Clean list',
  FETCH_ACCOUNT_HOLDER_BY_ID = '[Change Account Holder Wizard] - Fetch by ID',
}

export const navigateTo = createAction(
  ChangeAccountHolderActionTypes.NAVIGATE_TO,
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const navigateToAccountHolderDetails = createAction(
  ChangeAccountHolderActionTypes.NAVIGATE_TO_ACCOUNT_HOLDER_DETAILS
);
///////////////////////////////////////////////////////////////////////////////
export const setAccountHolderType = createAction(
  ChangeAccountHolderActionTypes.SET_ACCOUNT_HOLDER_TYPE,
  props<{ holderType: AccountHolderType }>()
);
export const setAccountHolderSelectionType = createAction(
  ChangeAccountHolderActionTypes.SET_ACCOUNT_HOLDER_SELECTION_TYPE,
  props<{ selectionType: AccountHolderSelectionType; id?: number }>()
);
export const setAccountHolderIndividualDetails = createAction(
  ChangeAccountHolderActionTypes.SET_ACCOUNT_HOLDER_INDIVIDUAL_DETAILS,
  props<{
    details: Pick<
      IndividualDetails,
      'firstName' | 'lastName' | 'countryOfBirth' | 'isOverEighteen'
    >;
  }>()
);
export const setAccountHolderIndividualContactDetails = createAction(
  ChangeAccountHolderActionTypes.SET_ACCOUNT_HOLDER_INDIVIDUAL_CONTACT_DETAILS,
  props<{ contactDetails: IndividualContactDetails }>()
);
export const setAccountHolderOrganisationDetails = createAction(
  ChangeAccountHolderActionTypes.SET_ACCOUNT_HOLDER_ORGANISATION_DETAILS,
  props<{ details: OrganisationDetails }>()
);
export const setAccountHolderOrganisationAddress = createAction(
  ChangeAccountHolderActionTypes.SET_ACCOUNT_HOLDER_ORGANISATION_ADDRESS,
  props<{ address: AccountHolderAddress }>()
);
export const setAccountHolderContactDetails = createAction(
  ChangeAccountHolderActionTypes.SET_ACCOUNT_HOLDER_CONTACT_DETAILS,
  props<{
    contact: AccountHolderContact;
  }>()
);
export const setAccountHolderContactAddress = createAction(
  ChangeAccountHolderActionTypes.SET_ACCOUNT_HOLDER_CONTACT_ADDRESS,
  props<{ contact: AccountHolderContact }>()
);

export const cancelClicked = createAction(
  ChangeAccountHolderActionTypes.CANCEL,
  props<{ route: string }>()
);
export const cancelChangeAccountHolderRequest = createAction(
  ChangeAccountHolderActionTypes.CANCEL_CHANGE_ACCOUNT_HOLDER_REQUEST
);
export const completeWizard = createAction(
  ChangeAccountHolderActionTypes.COMPLETE_CHANGE_ACCOUNT_HOLDER,
  props<{
    complete: boolean;
  }>()
);

export const nextPage = createAction(
  ChangeAccountHolderActionTypes.ACCOUNT_HOLDER_NEXT_PAGE,
  props<{ accountHolder: AccountHolder }>()
);
export const fetchAccountHolderList = createAction(
  ChangeAccountHolderActionTypes.FETCH_ACCOUNT_HOLDER_LIST,
  props<{ holderType: string }>()
);
export const populateAccountHolderList = createAction(
  ChangeAccountHolderActionTypes.POPULATE_ACCOUNT_HOLDER_LIST,
  props<{ list: AccountHolder[] }>()
);
export const populateAccountHolderContactInfo = createAction(
  ChangeAccountHolderActionTypes.POPULATE_ACCOUNT_HOLDER_CONTACT_INFO,
  props<{ accountHolderContactInfo: AccountHolderContactInfo }>()
);
export const populateAcquiringAccountHolder = createAction(
  ChangeAccountHolderActionTypes.POPULATE_ACQUIRING_ACCOUNT_HOLDER,
  props<{ accountHolder: AccountHolder }>()
);
export const populateAcquiringAccountHolderPrimaryContact = createAction(
  ChangeAccountHolderActionTypes.POPULATE_ACQUIRING_ACCOUNT_HOLDER_CONTACT,
  props<{ accountHolderContact: AccountHolderContact }>()
);
export const cleanAccountHolderList = createAction(
  ChangeAccountHolderActionTypes.CLEAN_ACCOUNT_HOLDER_LIST
);
export const fetchAccountHolderById = createAction(
  ChangeAccountHolderActionTypes.FETCH_ACCOUNT_HOLDER_BY_ID,
  props<{ id: number }>()
);

export const setAccountHolderDetails = createAction(
  '[Change Account Holder Wizard] Set account holder details values',
  props<{
    accountHolderInfoChanged: AccountHolder;
  }>()
);
export const setAccountHolderAddress = createAction(
  '[Change Account Holder Wizard] Set account holder new address',
  props<{
    accountHolderInfoChanged: AccountHolder;
  }>()
);
export const clearAccountHolderChangeRequest = createAction(
  '[Change Account Holder Wizard] Clear account holder change request'
);
export const submitChangeAccountHolderRequest = createAction(
  '[Change Account Holder Wizard] Submit update request'
);
export const submitChangeAccountHolderRequestSuccess = createAction(
  '[Change Account Holder Wizard] Submit update request success'
);
export const setSameAddressPrimaryContact = createAction(
  '[Change Account Holder Wizard] Set Primary work address same as Account Holder address',
  props<{ sameAddress: boolean }>()
);
