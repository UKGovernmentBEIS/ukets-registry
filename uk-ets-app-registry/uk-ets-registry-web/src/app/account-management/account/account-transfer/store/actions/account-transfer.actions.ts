import { NavigationExtras, Params } from '@angular/router';
import { createAction, props } from '@ngrx/store';
import {
  AcquiringAccountHolderContactDetails,
  AcquiringAccountHolderContactWorkDetails,
  AcquiringAccountHolderInfo,
  AcquiringOrganisationAddress,
  AcquiringOrganisationDetails,
  SelectedAccountTransferType,
} from '@account-transfer/model';
import {
  AccountHolder,
  AccountHolderContact,
  AccountHolderContactInfo,
  Organisation,
} from '@shared/model/account';

export const navigateTo = createAction(
  '[Account Transfer] Navigate to',
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const cancelClicked = createAction(
  '[Account Transfer] Cancel clicked',
  props<{ route: string }>()
);

export const cancelAccountTransferRequest = createAction(
  '[Account Transfer] Cancel account transfer request'
);

export const clearAccountTransferRequest = createAction(
  '[Account Transfer] Clear account transfer request'
);

export const setAccountTransferType = createAction(
  '[Account Transfer] Set account transfer type',
  props<{ selectedAccountTransferType: SelectedAccountTransferType }>()
);

export const setAcquiringAccountHolderDetails = createAction(
  '[Account Transfer] Set Acquiring Organisation Details values',
  props<{ acquiringOrganisationDetails: AcquiringOrganisationDetails }>()
);

export const setAcquiringAccountHolderAddress = createAction(
  '[Account Transfer] Set account holder new address',
  props<{
    acquiringOrganisationAddress: AcquiringOrganisationAddress;
  }>()
);

export const setAcquiringAccountHolderPrimaryContactDetails = createAction(
  '[Account Transfer] Set account holder contact values',
  props<{
    acquiringAccountHolderContactDetails: AcquiringAccountHolderContactDetails;
  }>()
);

export const setAcquiringAccountHolderPrimaryContactWorkDetails = createAction(
  '[Account Transfer] Set account holder contact work values',
  props<{
    acquiringAccountHolderContactWorkDetails: AcquiringAccountHolderContactWorkDetails;
  }>()
);

export const setPrimaryContactWorkAddressSameAsAccountHolderAddress = createAction(
  '[Account Transfer] Set Primary Contact work address same as Account Holder address',
  props<{ primaryContactWorkAddressSameAsAccountHolderAddress: boolean }>()
);

export const fetchLoadAndShowAcquiringAccountHolder = createAction(
  '[Account Transfer] Fetch, load and show acquiring account holder',
  props<{ identifier: number }>()
);

export const fetchAcquiringAccountHolderSuccess = createAction(
  '[Account Transfer] Successful fetch acquiring account holder',
  props<{ accountHolder: AccountHolder }>()
);

export const loadAcquiringAccountHolder = createAction(
  '[Account Transfer] Load acquiring account holder',
  props<{ accountHolder: AccountHolder }>()
);

export const fetchLoadAndShowAcquiringAccountHolderContacts = createAction(
  '[Account Transfer] Fetch, load and show acquiring account holder contacts',
  props<{ identifier: number }>()
);

export const fetchAcquiringAccountHolderContactsSuccess = createAction(
  '[Account Transfer] Successful fetch acquiring account holder contacts',
  props<{ accountHolderContactInfo: AccountHolderContactInfo }>()
);

export const loadAcquiringAccountHolderContacts = createAction(
  '[Account Transfer] Load acquiring account holder conacts',
  props<{ accountHolderContactInfo: AccountHolderContactInfo }>()
);

export const submitUpdateRequest = createAction(
  '[Account Transfer] Submit update request',
  props<{
    acquiringAccountHolderInfo: AcquiringAccountHolderInfo;
  }>()
);

export const submitUpdateRequestSuccess = createAction(
  '[Account Transfer] Submit update request success',
  props<{ requestId: string }>()
);
