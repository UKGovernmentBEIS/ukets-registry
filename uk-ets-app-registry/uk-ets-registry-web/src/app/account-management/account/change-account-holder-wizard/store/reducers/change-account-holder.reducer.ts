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
  OrganisationDetails,
} from '@shared/model/account';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';

export const changeAccountHolderFeatureKey = 'change-account-holder-wizard';

export interface ChangeAccountHolderWizardState {
  acquiringAccountHolder: AccountHolder;
  accountHolderList: AccountHolder[];
  accountHolderSelectionType: AccountHolderSelectionType;
  accountHolderCompleted: boolean;
  acquiringAccountHolderContact: AccountHolderContact;
  isPrimaryAddressSameAsHolder: boolean;
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
  accountHolderCompleted: false,
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
};

const changeAccountHolderWizardReducer = createReducer(
  initialState,
  mutableOn(
    ChangeAccountHolderWizardActions.setAccountHolderType,
    (state, { holderType }) => {
      if (holderType === AccountHolderType.INDIVIDUAL) {
        state.acquiringAccountHolder = {
          id: null,
          type: holderType,
          details: {
            name: null,
            firstName: null,
            lastName: null,
            birthDate: null,
            countryOfBirth: null,
            isOverEighteen: false,
          },
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
      } else {
        state.acquiringAccountHolder = {
          id: null,
          type: holderType,
          details: {
            name: null,
            registrationNumber: null,
            noRegistrationNumJustification: null,
          },
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
      }
      state.accountHolderSelectionType = null;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.setAccountHolderSelectionType,
    (state, { selectionType, id }) => {
      state.accountHolderSelectionType = selectionType;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.setAccountHolderIndividualDetails,
    (state, { details }) => {
      const individualDetails = state.acquiringAccountHolder
        .details as IndividualDetails;
      individualDetails.firstName = details.firstName;
      individualDetails.lastName = details.lastName;
      individualDetails.countryOfBirth = details.countryOfBirth;
      individualDetails.isOverEighteen = details.isOverEighteen;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.setAccountHolderIndividualContactDetails,
    (state, { contactDetails }) => {
      const individual = state.acquiringAccountHolder as Individual;
      individual.address = contactDetails.address;
      individual.emailAddress = contactDetails.emailAddress;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.setAccountHolderOrganisationDetails,
    (state, { details }) => {
      const organisationDetails = state.acquiringAccountHolder
        .details as OrganisationDetails;
      organisationDetails.name = details.name;
      organisationDetails.registrationNumber = details.registrationNumber;
      organisationDetails.noRegistrationNumJustification =
        details.noRegistrationNumJustification;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.setAccountHolderOrganisationAddress,
    (state, { address }) => {
      state.acquiringAccountHolder.address = address;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.setAccountHolderContactDetails,
    (state, { contact }) => {
      state.acquiringAccountHolderContact.details = contact.details;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.setAccountHolderContactAddress,
    (state, { contact }) => {
      state.acquiringAccountHolderContact.id = contact.id;
      state.acquiringAccountHolderContact.address = contact.address;
      state.acquiringAccountHolderContact.emailAddress = contact.emailAddress;
      state.acquiringAccountHolderContact.phoneNumber = contact.phoneNumber;
      state.acquiringAccountHolderContact.positionInCompany =
        contact.positionInCompany;
      state.accountHolderCompleted = true;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.populateAccountHolderList,
    (state, { list }) => {
      state.accountHolderList = list;
    }
  ),
  //TODO review for edit
  mutableOn(
    ChangeAccountHolderWizardActions.populateAcquiringAccountHolder,
    (state, { accountHolder }) => {
      state.acquiringAccountHolder = accountHolder;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.populateAcquiringAccountHolderPrimaryContact,
    (state, { accountHolderContact }) => {
      state.acquiringAccountHolderContact = accountHolderContact;
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.clearAccountHolderChangeRequest,
    (state) => {
      resetState(state);
    }
  ),
  mutableOn(
    ChangeAccountHolderWizardActions.setSameAddressPrimaryContact,
    (state, { sameAddress }) => {
      state.isPrimaryAddressSameAsHolder = sameAddress;
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
  state.acquiringAccountHolderContact =
    initialState.acquiringAccountHolderContact;
  state.accountHolderCompleted = initialState.accountHolderCompleted;
  state.accountHolderList = initialState.accountHolderList;
  state.accountHolderSelectionType = initialState.accountHolderSelectionType;
  state.isPrimaryAddressSameAsHolder =
    initialState.isPrimaryAddressSameAsHolder;
}
