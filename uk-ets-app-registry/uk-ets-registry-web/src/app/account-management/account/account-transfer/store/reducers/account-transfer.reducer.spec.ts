import { reducer, initialState, AccountTransferState } from '.';
import {
  clearAccountTransferRequest,
  setAccountTransferType,
  setAcquiringAccountHolderAddress,
  setAcquiringAccountHolderPrimaryContactDetails,
  setAcquiringAccountHolderPrimaryContactWorkDetails,
  setAcquiringAccountHolderDetails,
  setPrimaryContactWorkAddressSameAsAccountHolderAddress,
  submitUpdateRequestSuccess,
} from '@account-transfer/store/actions/account-transfer.actions';
import { AccountHolderType } from '@registry-web/shared/model/account';
import {
  AcquiringAccountHolderContactDetails,
  AcquiringAccountHolderContactWorkDetails,
  AcquiringOrganisationAddress,
  AcquiringOrganisationDetails,
  SelectedAccountTransferType,
} from '@account-transfer/model';

describe('Account Transfer reducer', () => {
  it('sets the update type', () => {
    const beforeSetAccountTransferTypeActionState = reducer(
      initialState,
      {} as any
    );
    expect(beforeSetAccountTransferTypeActionState.updateType).toBeNull();
    const selectedAccountTransferType: SelectedAccountTransferType = {
      selectedUpdateType: 'ACCOUNT_TRANSFER_TO_EXISTING_HOLDER',
      selectedExistingAccountHolder: {
        identifier: 5,
        type: AccountHolderType.ORGANISATION,
        name: 'Organization name',
        firstName: 'Your First Name',
        lastName: 'Your Last Name',
      },
    };
    const setAccountTransferTypeAction = setAccountTransferType({
      selectedAccountTransferType,
    });
    const afterSetAccountTransferActionState = reducer(
      initialState,
      setAccountTransferTypeAction
    );
    expect(afterSetAccountTransferActionState.updateType).toEqual(
      'ACCOUNT_TRANSFER_TO_EXISTING_HOLDER'
    );
    expect(
      afterSetAccountTransferActionState.acquiringAccountHolder.id
    ).toEqual(5);
  });

  it('sets the acquiring account holder details', () => {
    const beforeSetAcquiringAccountHolderDetailsState = reducer(
      initialState,
      {} as any
    );
    expect(
      beforeSetAcquiringAccountHolderDetailsState.acquiringAccountHolder
    ).toBeNull();
    const acquiringOrganisationDetails: AcquiringOrganisationDetails = {
      name: 'NRB Group UK',
      registrationNumber: '',
      noRegistrationNumJustification: 'Non UK based',
    };
    const setAcquiringAccountHolderDetailsAction =
      setAcquiringAccountHolderDetails({
        acquiringOrganisationDetails,
      });
    const afterSetAcquiringAccountHolderDetailsState = reducer(
      initialState,
      setAcquiringAccountHolderDetailsAction
    );
    expect(
      afterSetAcquiringAccountHolderDetailsState.acquiringAccountHolder.details
    ).toMatchObject({
      name: 'NRB Group UK',
      registrationNumber: '',
      noRegistrationNumJustification: 'Non UK based',
    });
  });

  it('overwrites the acquiring account holder details (e.g. wizard navigation)', () => {
    //This action expects to have already have set the AcquiringAccountHolderContactDetails
    const initialOverwritesTheAcquiringAccountHolderDetailsState: AccountTransferState =
      {
        updateType: null,
        acquiringAccountHolder: {
          id: null,
          type: AccountHolderType.ORGANISATION,
          details: {
            name: 'Another name',
            registrationNumber: '978700',
            noRegistrationNumJustification: '',
          },
          address: null,
        },
        acquiringAccountHolderContactInfo: {
          primaryContact: null,
          alternativeContact: null,
        },
        submittedRequestIdentifier: null,
      };
    const beforeSetAcquiringAccountHolderDetailsState = reducer(
      initialOverwritesTheAcquiringAccountHolderDetailsState,
      {} as any
    );
    expect(
      beforeSetAcquiringAccountHolderDetailsState.acquiringAccountHolder.details
        .name
    ).toEqual(
      initialOverwritesTheAcquiringAccountHolderDetailsState
        .acquiringAccountHolder.details.name
    );
    expect(
      beforeSetAcquiringAccountHolderDetailsState.acquiringAccountHolder.details
    ).toEqual(
      initialOverwritesTheAcquiringAccountHolderDetailsState
        .acquiringAccountHolder.details
    );
    const acquiringOrganisationDetails: AcquiringOrganisationDetails = {
      name: 'NRB Group UK',
      registrationNumber: '',
      noRegistrationNumJustification: 'Non UK based',
    };
    const setAcquiringAccountHolderDetailsAction =
      setAcquiringAccountHolderDetails({
        acquiringOrganisationDetails,
      });
    const afterSetAcquiringAccountHolderDetailsState = reducer(
      initialOverwritesTheAcquiringAccountHolderDetailsState,
      setAcquiringAccountHolderDetailsAction
    );
    expect(
      afterSetAcquiringAccountHolderDetailsState.acquiringAccountHolder.details
    ).toMatchObject({
      name: 'NRB Group UK',
      registrationNumber: '',
      noRegistrationNumJustification: 'Non UK based',
    });
  });

  it('sets the acquiring account holder address', () => {
    const beforeSetAcquiringAccountHolderAddressState = reducer(
      initialState,
      {} as any
    );
    expect(
      beforeSetAcquiringAccountHolderAddressState.acquiringAccountHolder
    ).toBeNull();
    const acquiringOrganisationAddress: AcquiringOrganisationAddress = {
      address: {
        buildingAndStreet: 'Ethnikis Antistasis',
        buildingAndStreet2: '',
        buildingAndStreet3: '',
        postCode: '11236',
        townOrCity: 'Athens',
        stateOrProvince: 'Attica',
        country: '',
      },
    };
    const setAcquiringAccountHolderAddressAction =
      setAcquiringAccountHolderAddress({
        acquiringOrganisationAddress,
      });
    const afterSetAcquiringAccountHolderAddressState = reducer(
      initialState,
      setAcquiringAccountHolderAddressAction
    );
    expect(
      afterSetAcquiringAccountHolderAddressState.acquiringAccountHolder.address
    ).toMatchObject(acquiringOrganisationAddress.address);
  });

  it('sets the acquiring account primary contact details', () => {
    const beforeSetAcquiringAccountHolderContactDetailsState = reducer(
      initialState,
      {} as any
    );
    expect(
      beforeSetAcquiringAccountHolderContactDetailsState.acquiringAccountHolderContactInfo
    ).toBeNull();
    const acquiringAccountHolderContactDetails: AcquiringAccountHolderContactDetails =
      {
        details: {
          firstName: 'UK ETS',
          lastName: 'Full stack developer',
          aka: 'FSD',
          birthDate: null,
          isOverEighteen: true,
        },
      };
    const setAcquiringAccountHolderContactDetailsAction =
      setAcquiringAccountHolderPrimaryContactDetails({
        acquiringAccountHolderContactDetails,
      });
    const afterSetAcquiringAccountHolderContactDetailsState = reducer(
      initialState,
      setAcquiringAccountHolderContactDetailsAction
    );
    expect(
      afterSetAcquiringAccountHolderContactDetailsState
        .acquiringAccountHolderContactInfo.primaryContact.details
    ).toMatchObject(acquiringAccountHolderContactDetails.details);
  });

  it('sets the acquiring account primary contact work details', () => {
    //This action expects to have already have set the AcquiringAccountHolderContactDetails
    const initialPrimaryContactWorkDetailsState: AccountTransferState = {
      updateType: null,
      acquiringAccountHolder: null,
      acquiringAccountHolderContactInfo: {
        primaryContact: {
          id: null,
          new: true,
          details: null,
          positionInCompany: null,
          address: null,
          phoneNumber: null,
          emailAddress: null,
        },
        alternativeContact: null,
      },
      submittedRequestIdentifier: null,
    };
    const beforeSetAcquiringAccountHolderContactWorkDetailsState = reducer(
      initialPrimaryContactWorkDetailsState,
      {} as any
    );
    expect(
      beforeSetAcquiringAccountHolderContactWorkDetailsState
        .acquiringAccountHolderContactInfo.primaryContact.positionInCompany
    ).toBeNull();
    expect(
      beforeSetAcquiringAccountHolderContactWorkDetailsState
        .acquiringAccountHolderContactInfo.primaryContact.address
    ).toBeNull();
    expect(
      beforeSetAcquiringAccountHolderContactWorkDetailsState
        .acquiringAccountHolderContactInfo.primaryContact.phoneNumber
    ).toBeNull();
    expect(
      beforeSetAcquiringAccountHolderContactWorkDetailsState
        .acquiringAccountHolderContactInfo.primaryContact.emailAddress
    ).toBeNull();
    const acquiringAccountHolderContactWorkDetails: AcquiringAccountHolderContactWorkDetails =
      {
        positionInCompany: 'CEO',
        address: {
          buildingAndStreet: 'Street 1',
          buildingAndStreet2: 'Street 2',
          buildingAndStreet3: 'Street 3',
          townOrCity: 'Athens',
          stateOrProvince: 'Attica',
          country: 'GR',
          postCode: '18876',
        },
        phoneNumber: {
          countryCode1: 'UK',
          phoneNumber1: '466773777',
          countryCode2: '',
          phoneNumber2: '',
        },
        emailAddress: {
          emailAddress: 'zzz@test.com',
          emailAddressConfirmation: 'zzz@test.com',
        },
      };
    const setAcquiringAccountHolderContactDetailsAction =
      setAcquiringAccountHolderPrimaryContactWorkDetails({
        acquiringAccountHolderContactWorkDetails,
      });
    const afterSetAcquiringAccountHolderContactWorkDetailsState = reducer(
      initialPrimaryContactWorkDetailsState,
      setAcquiringAccountHolderContactDetailsAction
    );
    expect(
      afterSetAcquiringAccountHolderContactWorkDetailsState
        .acquiringAccountHolderContactInfo.primaryContact.positionInCompany
    ).toEqual(acquiringAccountHolderContactWorkDetails.positionInCompany);
    expect(
      afterSetAcquiringAccountHolderContactWorkDetailsState
        .acquiringAccountHolderContactInfo.primaryContact.address
    ).toMatchObject(acquiringAccountHolderContactWorkDetails.address);
    expect(
      afterSetAcquiringAccountHolderContactWorkDetailsState
        .acquiringAccountHolderContactInfo.primaryContact.phoneNumber
    ).toMatchObject(acquiringAccountHolderContactWorkDetails.phoneNumber);
    expect(
      afterSetAcquiringAccountHolderContactWorkDetailsState
        .acquiringAccountHolderContactInfo.primaryContact.emailAddress
    ).toMatchObject(acquiringAccountHolderContactWorkDetails.emailAddress);
  });

  it('sets the request identifier', () => {
    const beforeSetAccountTransferTypeActionState = reducer(
      initialState,
      {} as any
    );
    expect(
      beforeSetAccountTransferTypeActionState.submittedRequestIdentifier
    ).toBeNull();

    const setSubmitUpdateRequestSuccessAction = submitUpdateRequestSuccess({
      requestId: 'UK75545',
    });
    const afterSetAccountTransferActionState = reducer(
      initialState,
      setSubmitUpdateRequestSuccessAction
    );
    expect(
      afterSetAccountTransferActionState.submittedRequestIdentifier
    ).toEqual('UK75545');
  });

  it('sets the primary contact Work Address Same as Account Holder Address', () => {
    //This action expects to have already have set the AcquiringAccountHolderContactDetails
    const initialPrimaryContactWorkAddressSameAsAccountHolderAddressState: AccountTransferState =
      {
        updateType: null,
        acquiringAccountHolder: null,
        acquiringAccountHolderContactInfo: {
          primaryContact: {
            id: null,
            new: true,
            details: null,
            positionInCompany: null,
            address: null,
            phoneNumber: null,
            emailAddress: null,
          },
          alternativeContact: null,
          isPrimaryAddressSameAsHolder: false,
        },
        submittedRequestIdentifier: null,
      };
    const beforeSetPrimaryContactWorkAddressSameAsAccountHolderAddressState =
      reducer(
        initialPrimaryContactWorkAddressSameAsAccountHolderAddressState,
        {} as any
      );
    expect(
      beforeSetPrimaryContactWorkAddressSameAsAccountHolderAddressState
        .acquiringAccountHolderContactInfo.isPrimaryAddressSameAsHolder
    ).toBeFalsy();

    const setPrimaryContactWorkAddressSameAsAccountHolderAddressAction =
      setPrimaryContactWorkAddressSameAsAccountHolderAddress({
        primaryContactWorkAddressSameAsAccountHolderAddress: true,
      });
    const afterSetPrimaryContactWorkAddressSameAsAccountHolderAddressState =
      reducer(
        initialPrimaryContactWorkAddressSameAsAccountHolderAddressState,
        setPrimaryContactWorkAddressSameAsAccountHolderAddressAction
      );
    expect(
      afterSetPrimaryContactWorkAddressSameAsAccountHolderAddressState
        .acquiringAccountHolderContactInfo.isPrimaryAddressSameAsHolder
    ).toBeTruthy();
  });

  it('clears the state', () => {
    const nonEmptyState: AccountTransferState = {
      updateType: 'ACCOUNT_TRANSFER_TO_CREATED_HOLDER',
      acquiringAccountHolder: {
        id: 5,
        type: AccountHolderType.ORGANISATION,
        details: null,
        address: null,
      },
      acquiringAccountHolderContactInfo: {
        primaryContact: {
          id: 5,
          new: false,
          details: {
            firstName: 'Primary contact first name',
            lastName: 'Primary contact last name',
            aka: '',
            birthDate: null,
            isOverEighteen: true,
          },
          positionInCompany: '',
          address: {
            buildingAndStreet: '',
            buildingAndStreet2: '',
            buildingAndStreet3: '',
            townOrCity: '',
            stateOrProvince: '',
            country: '',
            postCode: '',
          },
          phoneNumber: {
            countryCode1: '',
            phoneNumber1: '',
            countryCode2: '',
            phoneNumber2: '',
          },
          emailAddress: {
            emailAddress: '',
            emailAddressConfirmation: '',
          },
        },
        alternativeContact: null,
      },
      submittedRequestIdentifier: 'UK789065',
    };

    const beforeClearAccountTransferState = reducer(nonEmptyState, {} as any);
    expect(beforeClearAccountTransferState.updateType).toBeTruthy();
    expect(beforeClearAccountTransferState.acquiringAccountHolder).toBeTruthy();
    expect(
      beforeClearAccountTransferState.acquiringAccountHolderContactInfo
    ).toBeTruthy();

    const clearAccountTransferAction = clearAccountTransferRequest();
    const afterClearAccountTransferState = reducer(
      initialState,
      clearAccountTransferAction
    );
    expect(afterClearAccountTransferState.updateType).toBeNull();
    expect(afterClearAccountTransferState.acquiringAccountHolder).toBeNull();
    expect(
      afterClearAccountTransferState.acquiringAccountHolderContactInfo
    ).toBeNull();
  });
});
