import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import * as AccountOpeningActions from './account-opening.actions';
import * as AccountHolderWizardActions from './account-holder/account-holder.actions';
import * as AccountDetailsWizardActions from './account-details/account-details.actions';
import * as TrustedAccountListWizardActions from './trusted-account-list/trusted-account-list.actions';
import { AccountOpeningOperatorActions } from '@account-opening/operator/actions';
import * as AuthorisedRepresentativeActions from './authorised-representative/authorised-representative.actions';
import * as AccountHolderContactWizardActions from './account-holder-contact/account-holder-contact.actions';
import { AccountOpeningState, ViewOrCheck } from './account-opening.model';
import {
  AccountHolderType,
  Individual,
  Organisation,
} from '../shared/model/account/account-holder';
import { ContactType } from '@shared/model/account-holder-contact-type';
import {
  AccountType,
  AuthorisedRepresentative,
  DEFAULT_KP_TRADING_TRANSACTION_RULES,
  DEFAULT_OHA_AOHA_TRANSACTION_RULES,
} from '@shared/model/account';

export const accountOpeningFeatureKey = 'accountOpening';

export const initialState: AccountOpeningState = {
  accountType: null,
  accountHolder: null,
  accountHolderList: [],
  accountHolderSelectionType: null,
  accountHolderContactInfo: {
    primaryContact: null,
    alternativeContact: null,
  },
  accountHolderCompleted: false,
  accountHolderContact: null,
  accountHolderContactView: false,
  accountHolderContactsCompleted: false,
  accountDetails: null,
  accountDetailsCompleted: false,
  accountDetailsSameBillingAddress: false,
  trustedAccountList: null,
  trustedAccountListCompleted: false,
  operator: null,
  operatorCompleted: false,
  authorisedRepresentativeIndex: null,
  currentAuthorisedRepresentative: null,
  authorisedRepresentatives: [],
  fetchedAuthorisedRepresentatives: [],
  authorisedRepresentativeViewOrCheck: ViewOrCheck.CHECK,
  requestID: null,
  installationToBeTransferred: null,
  initialPermitId: null,
  maxNumberOfARs: 0,
  minNumberOfARs: 0,
  taskType: null,
};

const accountOpeningReducer = createReducer(
  initialState,
  mutableOn(
    AccountOpeningActions.setAccountType,
    (state, { accountType, minNumberOfARs, maxNumberOfARs }) => {
      for (const key of Object.keys(state)) {
        state[key] = initialState[key];
      }
      state.accountType = accountType;
      state.minNumberOfARs = minNumberOfARs;
      state.maxNumberOfARs = maxNumberOfARs;
      setDefaultTransactionRules(state);
    }
  ),
  mutableOn(AccountOpeningActions.cancelRequest, (state) => {
    for (const key of Object.keys(state)) {
      state[key] = initialState[key];
    }
  }),
  mutableOn(
    AccountHolderWizardActions.completeWizard,
    (state, { complete }) => {
      state.accountHolderCompleted = complete;
    }
  ),
  mutableOn(AccountHolderWizardActions.deleteAccountHolder, (state) => {
    const accountType = state.accountType;
    const minNumberOfARs = state.minNumberOfARs;
    const maxNumberOfARs = state.maxNumberOfARs;
    for (const key of Object.keys(state)) {
      state[key] = initialState[key];
    }
    state.accountType = accountType;
    state.minNumberOfARs = minNumberOfARs;
    state.maxNumberOfARs = maxNumberOfARs;
  }),
  mutableOn(
    AccountHolderWizardActions.setAccountHolderType,
    (state, { holderType }) => {
      if (holderType === AccountHolderType.INDIVIDUAL) {
        state.accountHolder = new Individual();
      } else {
        state.accountHolder = new Organisation();
      }
      state.accountHolderSelectionType = null;
    }
  ),
  mutableOn(
    AccountHolderWizardActions.populateAccountHolderList,
    (state, { list }) => {
      state.accountHolderList = list;
    }
  ),
  mutableOn(
    AccountHolderWizardActions.populateAccountHolderContactInfo,
    (state, { accountHolderContactInfo }) => {
      state.accountHolderContactInfo = accountHolderContactInfo;
      state.accountHolderContactsCompleted =
        !!accountHolderContactInfo.primaryContact;
    }
  ),
  mutableOn(
    AccountHolderWizardActions.populateAccountHolder,
    (state, { accountHolder }) => {
      state.accountHolder = accountHolder;
    }
  ),
  mutableOn(AccountHolderWizardActions.cleanAccountHolderList, (state) => {
    state.accountHolderList = [];
  }),
  mutableOn(
    AccountHolderWizardActions.setAccountHolderSelectionType,
    (state, { selectionType }) => {
      state.accountHolderSelectionType = selectionType;
    }
  ),
  mutableOn(AccountHolderWizardActions.nextPage, (state, { accountHolder }) => {
    state.accountHolder = { ...state.accountHolder, ...accountHolder };
    if (accountHolder.address && state.accountDetailsSameBillingAddress) {
      const accountHolderLocal =
        state.accountHolder.type === AccountHolderType.INDIVIDUAL
          ? (state.accountHolder as Individual)
          : (state.accountHolder as Organisation);
      state.accountDetails = {
        ...state.accountDetails,
        ...{
          address: {
            buildingAndStreet: accountHolderLocal.address.buildingAndStreet,
            buildingAndStreet2: accountHolderLocal.address.buildingAndStreet2,
            buildingAndStreet3: accountHolderLocal.address.buildingAndStreet3,
            townOrCity: accountHolderLocal.address.townOrCity,
            stateOrProvince: accountHolderLocal.address.stateOrProvince,
            country: accountHolderLocal.address.country,
            postCode: accountHolderLocal.address.postCode,
          },
        },
      };
    }
  }),
  mutableOn(
    AccountDetailsWizardActions.completeWizard,
    (state, { complete }) => {
      state.accountDetailsCompleted = complete;
    }
  ),
  mutableOn(AccountDetailsWizardActions.deleteAccountDetails, (state) => {
    state.accountDetails = null;
    state.accountDetailsCompleted = false;
    state.accountDetailsSameBillingAddress = false;
  }),
  mutableOn(
    AccountDetailsWizardActions.nextPage,
    (state, { accountDetails }) => {
      state.accountDetails = { ...state.accountDetails, ...accountDetails };
    }
  ),
  mutableOn(
    TrustedAccountListWizardActions.completeWizard,
    (state, { complete }) => {
      state.trustedAccountListCompleted = complete;
    }
  ),
  mutableOn(
    TrustedAccountListWizardActions.deleteTrustedAccountList,
    (state) => {
      state.trustedAccountList = null;
      state.trustedAccountListCompleted = false;
    }
  ),
  mutableOn(
    TrustedAccountListWizardActions.nextPage,
    (state, { trustedAccountList }) => {
      state.trustedAccountList = {
        ...state.trustedAccountList,
        ...trustedAccountList,
      };
    }
  ),
  mutableOn(
    AccountOpeningOperatorActions.setOperator,
    (state, { operator }) => {
      state.operator = { ...state.operator, ...operator };
    }
  ),
  mutableOn(
    AccountOpeningOperatorActions.initialPermitId,
    (state, { permitID }) => {
      state.initialPermitId = permitID;
    }
  ),
  mutableOn(
    AccountOpeningOperatorActions.completeWizard,
    (state, { complete }) => {
      state.operatorCompleted = complete;
    }
  ),
  mutableOn(AccountOpeningOperatorActions.deleteOperator, (state) => {
    state.operator = null;
    state.operatorCompleted = false;
  }),
  mutableOn(
    AccountOpeningOperatorActions.validateInstallationTransfer,
    (state, { installationTransfer }) => {
      state.operator = installationTransfer;
    }
  ),
  mutableOn(
    AccountOpeningOperatorActions.validateInstallationTransferSuccess,
    (state, { installationToBeTransferred }) => {
      state.installationToBeTransferred = installationToBeTransferred;
      state.operatorCompleted = false;
    }
  ),
  mutableOn(
    AccountHolderContactWizardActions.nextPage,
    (state, { accountHolderContact }) => {
      state.accountHolderContact = {
        ...state.accountHolderContact,
        ...accountHolderContact,
      };
    }
  ),
  mutableOn(
    AccountHolderContactWizardActions.completeWizard,
    (state, { contactType }) => {
      state.accountHolderContactsCompleted = true;
      state.accountHolderContactView = false;

      if (contactType === ContactType.PRIMARY.valueOf()) {
        state.accountHolderContactInfo.primaryContact =
          state.accountHolderContact;
      } else if (contactType === ContactType.ALTERNATIVE.valueOf()) {
        state.accountHolderContactInfo.alternativeContact =
          state.accountHolderContact;
      }
      state.accountHolderContact = null;
      state.accountHolderContactView = false;
    }
  ),
  mutableOn(
    AccountHolderContactWizardActions.setViewState,
    (state, { view }) => {
      state.accountHolderContactView = view;
      if (!view && !state.accountHolderContactsCompleted) {
        state.accountHolderContactsCompleted = false;
      }
    }
  ),
  mutableOn(
    AccountHolderContactWizardActions.deleteAccountHolderContact,
    (state, { contactType }) => {
      state.accountHolderContact = null;
      state.accountHolderContactView = false;
      if (contactType === ContactType.PRIMARY.valueOf()) {
        state.accountHolderContactInfo.primaryContact = null;
      } else if (contactType === ContactType.ALTERNATIVE.valueOf()) {
        state.accountHolderContactInfo.alternativeContact = null;
      }
    }
  ),
  mutableOn(
    AccountHolderContactWizardActions.removeAccountHolderContact,
    (state) => {
      state.accountHolderContact = null;
    }
  ),
  mutableOn(
    AuthorisedRepresentativeActions.loadAuthorisedRepresentatives,
    (state, { ARs }) => {
      /**
       * TODO Refactor the use of the AuthorisedRepresentative entity here,
       * as we should be dealing with users at this phase and not ARs
       */
      state.fetchedAuthorisedRepresentatives = ARs.filter((ar) =>
        state.authorisedRepresentatives.every((ar2) => ar.urid !== ar2.urid)
      ).map((ar) => {
        const newAr = new AuthorisedRepresentative();
        newAr.urid = ar.urid;
        newAr.firstName = ar.firstName;
        newAr.lastName = ar.lastName;
        newAr.user = ar;
        return newAr;
      });
    }
  ),
  mutableOn(
    AuthorisedRepresentativeActions.setCurrentAuthorisedRepresentative,
    (state, { AR }) => {
      state.currentAuthorisedRepresentative = AR;
    }
  ),
  mutableOn(
    AuthorisedRepresentativeActions.setCurrentAuthorisedRepresentativeByIndex,
    (state, { index }) => {
      state.currentAuthorisedRepresentative =
        state.authorisedRepresentatives[index];
    }
  ),
  mutableOn(
    AuthorisedRepresentativeActions.setAccessRightsToCurrentAR,
    (state, { accessRights }) => {
      state.currentAuthorisedRepresentative.right = accessRights;
    }
  ),
  mutableOn(AuthorisedRepresentativeActions.addCurrentARToList, (state) => {
    if (state.authorisedRepresentativeIndex === null) {
      state.authorisedRepresentatives.push(
        state.currentAuthorisedRepresentative
      );
    } else {
      state.authorisedRepresentatives[state.authorisedRepresentativeIndex] =
        state.currentAuthorisedRepresentative;
    }
  }),
  mutableOn(AuthorisedRepresentativeActions.removeARFromList, (state) => {
    state.authorisedRepresentatives.splice(
      state.authorisedRepresentativeIndex,
      1
    );
  }),
  mutableOn(
    AuthorisedRepresentativeActions.setAuthorisedRepresentativeViewOrCheck,
    (state, { viewOrCheck }) => {
      state.authorisedRepresentativeViewOrCheck = viewOrCheck;
    }
  ),
  mutableOn(
    AuthorisedRepresentativeActions.setAuthorisedRepresentativeIndex,
    (state, { index }) => {
      state.authorisedRepresentativeIndex = index;
    }
  ),
  mutableOn(
    AuthorisedRepresentativeActions.clearCurrentAuthorisedRepresentative,
    (state) => {
      state.currentAuthorisedRepresentative = null;
    }
  ),
  mutableOn(
    AccountOpeningActions.completeRequest,
    (state, { requestID, taskType }) => {
      for (const key of Object.keys(state)) {
        state[key] = key !== 'requestID' ? initialState[key] : requestID;
      }
      state.taskType = taskType;
    }
  ),
  mutableOn(
    AccountOpeningActions.accountDetailsSameBillingAddress,
    (state, { accountDetailsSameBillingAddress }) => {
      state.accountDetailsSameBillingAddress = accountDetailsSameBillingAddress;
    }
  ),
  mutableOn(
    AccountHolderContactWizardActions.setSameAddress,
    (state, { sameAddress, contactType }) => {
      if (contactType === ContactType.PRIMARY) {
        state.accountHolderContactInfo.isPrimaryAddressSameAsHolder =
          sameAddress;
      } else if (contactType === ContactType.ALTERNATIVE) {
        state.accountHolderContactInfo.isAlternativeAddressSameAsHolder =
          sameAddress;
      }
    }
  )
);

function setDefaultTransactionRules(state) {
  if (
    AccountType.OPERATOR_HOLDING_ACCOUNT === state.accountType ||
    AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT === state.accountType
  ) {
    state.trustedAccountList = DEFAULT_OHA_AOHA_TRANSACTION_RULES;
  } else {
    state.trustedAccountList = DEFAULT_KP_TRADING_TRANSACTION_RULES;
  }
  state.trustedAccountListCompleted = true;
}

export function reducer(
  state: AccountOpeningState | undefined,
  action: Action
) {
  return accountOpeningReducer(state, action);
}
