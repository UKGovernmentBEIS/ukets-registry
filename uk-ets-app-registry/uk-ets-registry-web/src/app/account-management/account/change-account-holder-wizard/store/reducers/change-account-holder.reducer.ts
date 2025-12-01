import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  AccountHolder,
  AccountHolderContact,
  AccountHolderSelectionType,
  AccountHolderType,
  Individual,
  IndividualDetails,
  Organisation,
} from '@shared/model/account';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';

export const changeAccountHolderFeatureKey = 'change-account-holder-wizard';

export interface ChangeAccountHolderWizardState {
  acquiringAccountHolder: AccountHolder;
  accountHolderList: AccountHolder[];
  accountHolderSelectionType: AccountHolderSelectionType;
  acquiringAccountHolderContact: AccountHolderContact;
  isPrimaryAddressSameAsHolder: boolean;
  isAccountHolderOrphan: boolean;
  accountHolderDelete: boolean;
  backlinkToOverview: boolean;
  submittedRequestIdentifier: string;
}

export const initialState: ChangeAccountHolderWizardState = {
  acquiringAccountHolder: {
    id: null,
    type: null,
    details: null,
    address: null,
  },
  accountHolderList: [],
  accountHolderSelectionType: null,
  acquiringAccountHolderContact: {
    id: null,
    new: null,
    details: null,
    positionInCompany: null,
    address: null,
    phoneNumber: null,
    emailAddress: null,
  },
  isPrimaryAddressSameAsHolder: false,
  isAccountHolderOrphan: null,
  accountHolderDelete: null,
  backlinkToOverview: false,
  submittedRequestIdentifier: null,
};

const changeAccountHolderWizardReducer = createReducer(
  initialState,
  mutableOn(
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_TYPE,
    (state, { holderType }) => {
      if (state.acquiringAccountHolder.type === holderType) {
        return;
      }

      state.accountHolderSelectionType = null;
      state.acquiringAccountHolder = getEmptyAccountHolderWithType(holderType);
      state.acquiringAccountHolderContact =
        initialState.acquiringAccountHolderContact;
      state.isPrimaryAddressSameAsHolder =
        initialState.isPrimaryAddressSameAsHolder;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_SELECTION_TYPE,
    (state, { selectionType, id }) => {
      if (
        state.accountHolderSelectionType === selectionType &&
        state.acquiringAccountHolder.id === id
      ) {
        return;
      }

      state.accountHolderSelectionType = selectionType;
      state.acquiringAccountHolder = getEmptyAccountHolderWithType(
        state.acquiringAccountHolder.type
      );
      state.acquiringAccountHolderContact =
        initialState.acquiringAccountHolderContact;
      state.isPrimaryAddressSameAsHolder =
        initialState.isPrimaryAddressSameAsHolder;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_INDIVIDUAL_DETAILS,
    (state, { details }) => {
      const individualDetails = state.acquiringAccountHolder
        .details as IndividualDetails;
      individualDetails.name = `${details.firstName} ${details.lastName}`;
      individualDetails.firstName = details.firstName;
      individualDetails.lastName = details.lastName;
      individualDetails.countryOfBirth = details.countryOfBirth;
      individualDetails.isOverEighteen = details.isOverEighteen;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_INDIVIDUAL_CONTACT_DETAILS,
    (state, { individualContactDetails }) => {
      const individual = state.acquiringAccountHolder as Individual;
      individual.address = individualContactDetails.address;
      individual.phoneNumber = individualContactDetails.phoneNumber;
      individual.emailAddress = individualContactDetails.emailAddress;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_ORGANISATION_DETAILS,
    (state, { details }) => {
      state.acquiringAccountHolder.details = {
        name: details.name,
        registrationNumber: details.registrationNumber,
        noRegistrationNumJustification: details.noRegistrationNumJustification,
      };
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_ORGANISATION_ADDRESS,
    (state, { address }) => {
      state.acquiringAccountHolder.address = address;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_CONTACT_DETAILS,
    (state, { contact }) => {
      state.acquiringAccountHolderContact.details = contact.details;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_CONTACT_ADDRESS,
    (state, { contact }) => {
      state.acquiringAccountHolderContact.id = contact.id;
      state.acquiringAccountHolderContact.address = contact.address;
      state.acquiringAccountHolderContact.emailAddress = contact.emailAddress;
      state.acquiringAccountHolderContact.phoneNumber = contact.phoneNumber;
      state.acquiringAccountHolderContact.positionInCompany =
        contact.positionInCompany;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.POPULATE_ACCOUNT_HOLDER_LIST,
    (state, { list }) => {
      state.accountHolderList = list;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.POPULATE_ACQUIRING_ACCOUNT_HOLDER,
    (state, { accountHolder }) => {
      state.acquiringAccountHolder = accountHolder;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.POPULATE_ACQUIRING_ACCOUNT_HOLDER_PRIMARY_CONTACT,
    (state, { accountHolderContact }) => {
      state.acquiringAccountHolderContact = accountHolderContact;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.CLEAR_ACCOUNT_HOLDER_CHANGE_REQUEST,
    (state) => resetState(state)
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.SET_SAME_ADDRESS_PRIMARY_CONTACT,
    (state, { sameAddress }) => {
      state.isPrimaryAddressSameAsHolder = sameAddress;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.RESOLVE_IS_ACCOUNT_HOLDER_ORPHAN,
    (state, { isAccountHolderOrphan }) => {
      state.isAccountHolderOrphan = isAccountHolderOrphan;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.SET_DELETE_ORPHAN_ACCOUNT_HOLDER,
    (state, { accountHolderDelete }) => {
      state.accountHolderDelete = accountHolderDelete;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.SUBMIT_CHANGE_ACCOUNT_HOLDER_REQUEST_SUCCESS,
    (state, { requestId }) => {
      state.submittedRequestIdentifier = requestId;
    }
  ),
  mutableOn(ChangeAccountHolderWizardActions.INIT_OVERVIEW, (state) => {
    state.backlinkToOverview = true;
  }),
  mutableOn(
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_TYPE,
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_SELECTION_TYPE,
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_INDIVIDUAL_DETAILS,
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_INDIVIDUAL_CONTACT_DETAILS,
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_ORGANISATION_DETAILS,
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_ORGANISATION_ADDRESS,
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_CONTACT_DETAILS,
    ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_CONTACT_ADDRESS,
    ChangeAccountHolderWizardActions.SET_DELETE_ORPHAN_ACCOUNT_HOLDER,
    ChangeAccountHolderWizardActions.SET_SAME_ADDRESS_PRIMARY_CONTACT,
    (state) => {
      state.backlinkToOverview = false;
    }
  )
);

export function reducer(
  state: ChangeAccountHolderWizardState | undefined,
  action: Action
) {
  return changeAccountHolderWizardReducer(state, action);
}

function resetState(state: ChangeAccountHolderWizardState) {
  state.acquiringAccountHolder = initialState.acquiringAccountHolder;
  state.acquiringAccountHolderContact =
    initialState.acquiringAccountHolderContact;
  state.accountHolderList = initialState.accountHolderList;
  state.accountHolderSelectionType = initialState.accountHolderSelectionType;
  state.isPrimaryAddressSameAsHolder =
    initialState.isPrimaryAddressSameAsHolder;
  state.isAccountHolderOrphan = initialState.isAccountHolderOrphan;
  state.accountHolderDelete = initialState.accountHolderDelete;
  state.backlinkToOverview = initialState.backlinkToOverview;
  state.submittedRequestIdentifier = initialState.submittedRequestIdentifier;
}

function getEmptyAccountHolderWithType(
  accountHolderType: AccountHolderType
): AccountHolder {
  const baseAccountHolder = {
    id: null,
    type: accountHolderType,
    details: null,
    address: {
      buildingAndStreet: null,
      buildingAndStreet2: null,
      buildingAndStreet3: null,
      postCode: null,
      townOrCity: null,
      stateOrProvince: null,
      country: null,
    },
  };

  if (accountHolderType === AccountHolderType.INDIVIDUAL) {
    const individual: Individual = {
      ...baseAccountHolder,
      details: {
        name: null,
        firstName: null,
        lastName: null,
        birthDate: null,
        countryOfBirth: null,
        isOverEighteen: false,
      },
      phoneNumber: null,
      emailAddress: null,
    };
    return individual;
  }
  if (accountHolderType === AccountHolderType.ORGANISATION) {
    const organisation: Organisation = {
      ...baseAccountHolder,
      details: {
        name: null,
        registrationNumber: null,
        noRegistrationNumJustification: null,
      },
    };
    return organisation;
  }

  return baseAccountHolder;
}
