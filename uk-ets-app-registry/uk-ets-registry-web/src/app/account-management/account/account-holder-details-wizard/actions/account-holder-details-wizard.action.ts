import { createAction, props } from '@ngrx/store';
import { NavigationExtras, Params } from '@angular/router';
import {
  AccountHolderContactChanged,
  AccountHolderDetailsType,
  AccountHolderInfoChanged,
  AccountHolderUpdate,
} from '@account-management/account/account-holder-details-wizard/model';

export const navigateTo = createAction(
  '[Account Holder details request update] Navigate to',
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const cancelClicked = createAction(
  '[Account Holder details request update] Cancel clicked',
  props<{ route: string }>()
);

export const cancelAccountHolderDetailsUpdateRequest = createAction(
  '[Account Holder details request update] Cancel update request'
);

export const clearAccountHolderDetailsUpdateRequest = createAction(
  '[Account Holder details request update] Clear update request'
);

export const setRequestUpdateType = createAction(
  '[Account Holder details request update] Set request update type',
  props<{ updateType: AccountHolderDetailsType }>()
);

export const setAccountHolderDetails = createAction(
  '[Account Holder details request update] Set account holder details values',
  props<{
    accountHolderInfoChanged: AccountHolderInfoChanged;
  }>()
);

export const setAccountHolderAddress = createAction(
  '[Account Holder details request update] Set account holder new address',
  props<{
    accountHolderInfoChanged: AccountHolderInfoChanged;
  }>()
);

export const setAccountHolderContactDetails = createAction(
  '[Account Holder details request update] Set account holder contact values',
  props<{
    accountHolderContactChanged: AccountHolderContactChanged;
    contactType: string;
  }>()
);

export const setAccountHolderContactWorkDetails = createAction(
  '[Account Holder details request update] Set account holder contact work values',
  props<{
    accountHolderContactChanged: AccountHolderContactChanged;
  }>()
);

export const submitUpdateRequest = createAction(
  '[Account Holder details request update] Submit update request',
  props<{
    accountHolderUpdateValues: AccountHolderUpdate;
  }>()
);

export const submitUpdateRequestSuccess = createAction(
  '[Account Holder details request update] Submit update request success',
  props<{ requestId: string }>()
);

export const setSameAddressPrimaryContact = createAction(
  '[Account Holder details request update] Set Primary work address same as Account Holder address',
  props<{ sameAddress: boolean }>()
);

export const setSameAddressAlternativePrimaryContact = createAction(
  '[Account Holder details request update] Set Alternative Primary work address same as Account Holder address',
  props<{ sameAddress: boolean }>()
);
