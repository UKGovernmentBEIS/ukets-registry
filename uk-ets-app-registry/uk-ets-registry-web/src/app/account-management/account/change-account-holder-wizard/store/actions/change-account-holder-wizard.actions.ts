import { NavigationExtras, Params } from '@angular/router';
import { createAction, props } from '@ngrx/store';
import {
  AccountHolder,
  AccountHolderAddress,
  AccountHolderContact,
  AccountHolderSelectionType,
  AccountHolderType,
  IndividualContactDetails,
  IndividualDetails,
  OrganisationDetails,
} from '@shared/model/account';
import { ChangeAccountHolderWizardPaths } from '@change-account-holder-wizard/model';

export const ChangeAccountHolderWizardActions = {
  NAVIGATE_TO: createAction(
    '[Change Account Holder Wizard] Navigate to',
    props<{ step: ChangeAccountHolderWizardPaths; extras?: NavigationExtras }>()
  ),

  RESOLVE_IS_ACCOUNT_HOLDER_ORPHAN: createAction(
    '[Change Account Holder Wizard] Resolve Is account holder orphan',
    props<{ isAccountHolderOrphan: boolean }>()
  ),

  SET_ACCOUNT_HOLDER_TYPE: createAction(
    '[Change Account Holder Wizard] Set account holder type',
    props<{ holderType: AccountHolderType }>()
  ),
  SET_ACCOUNT_HOLDER_SELECTION_TYPE: createAction(
    '[Change Account Holder Wizard] Set account holder selection type',
    props<{ selectionType: AccountHolderSelectionType; id?: number }>()
  ),
  SET_ACCOUNT_HOLDER_INDIVIDUAL_DETAILS: createAction(
    '[Change Account Holder Wizard] Set account holder individual details',
    props<{
      details: Pick<
        IndividualDetails,
        'firstName' | 'lastName' | 'countryOfBirth' | 'isOverEighteen'
      >;
    }>()
  ),
  SET_ACCOUNT_HOLDER_INDIVIDUAL_CONTACT_DETAILS: createAction(
    '[Change Account Holder Wizard] Set account holder individual contact details',
    props<{ individualContactDetails: IndividualContactDetails }>()
  ),
  SET_ACCOUNT_HOLDER_ORGANISATION_DETAILS: createAction(
    '[Change Account Holder Wizard] Set account holder organisation details',
    props<{ details: OrganisationDetails }>()
  ),
  SET_ACCOUNT_HOLDER_ORGANISATION_ADDRESS: createAction(
    '[Change Account Holder Wizard] Set account holder organisation address',
    props<{ address: AccountHolderAddress }>()
  ),
  SET_ACCOUNT_HOLDER_CONTACT_DETAILS: createAction(
    '[Change Account Holder Wizard] Set account holder contact details',
    props<{ contact: AccountHolderContact }>()
  ),
  SET_ACCOUNT_HOLDER_CONTACT_ADDRESS: createAction(
    '[Change Account Holder Wizard] Set account holder contact address',
    props<{ contact: AccountHolderContact }>()
  ),
  SET_SAME_ADDRESS_PRIMARY_CONTACT: createAction(
    '[Change Account Holder Wizard] Set Primary work address same as Account Holder address',
    props<{ sameAddress: boolean }>()
  ),
  SET_DELETE_ORPHAN_ACCOUNT_HOLDER: createAction(
    '[Change Account Holder Wizard] Set delete orphan account holder',
    props<{ accountHolderDelete: boolean }>()
  ),

  CANCEL: createAction(
    '[Change Account Holder Wizard] Cancel clicked',
    props<{ route: string }>()
  ),
  CANCEL_CHANGE_ACCOUNT_HOLDER_REQUEST: createAction(
    '[Change Account Holder Wizard] Cancel change account holder request'
  ),

  FETCH_ACCOUNT_HOLDER_LIST: createAction(
    '[Change Account Holder Wizard] Fetch list',
    props<{ holderType: string }>()
  ),
  POPULATE_ACCOUNT_HOLDER_LIST: createAction(
    '[Change Account Holder Wizard] Populate list',
    props<{ list: AccountHolder[] }>()
  ),
  POPULATE_ACQUIRING_ACCOUNT_HOLDER: createAction(
    '[Change Account Holder Wizard] Populate acquiring account holder',
    props<{ accountHolder: AccountHolder }>()
  ),
  POPULATE_ACQUIRING_ACCOUNT_HOLDER_PRIMARY_CONTACT: createAction(
    '[Change Account Holder Wizard] Populate acquiring account holder contact',
    props<{ accountHolderContact: AccountHolderContact }>()
  ),
  CLEAN_ACCOUNT_HOLDER_LIST: createAction(
    '[Change Account Holder Wizard] Clean list'
  ),
  FETCH_ACCOUNT_HOLDER_BY_ID: createAction(
    '[Change Account Holder Wizard] Fetch by ID',
    props<{ id: number }>()
  ),

  INIT_OVERVIEW: createAction(
    '[Change Account Holder Wizard] Initialized Overview page'
  ),

  CLEAR_ACCOUNT_HOLDER_CHANGE_REQUEST: createAction(
    '[Change Account Holder Wizard] Clear account holder change request'
  ),
  SUBMIT_CHANGE_ACCOUNT_HOLDER_REQUEST: createAction(
    '[Change Account Holder Wizard] Submit update request'
  ),
  SUBMIT_CHANGE_ACCOUNT_HOLDER_REQUEST_SUCCESS: createAction(
    '[Change Account Holder Wizard] Submit update request success',
    props<{ requestId: string }>()
  ),
};
