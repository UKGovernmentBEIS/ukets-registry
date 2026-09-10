import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import * as UserDetailsActions from '../actions';
import { KeycloakUser } from '@shared/user/keycloak-user';
import { ArInAccount, EnrolmentKey } from '@user-management/user-details/model';
import { DomainEvent } from '@shared/model/event';
import { FileDetails } from '@shared/model/file/file-details.model';
import { UserAgentActions } from '@user-agent/store';
import { UserCrcActions } from '../../user-crc/store';

export const userDetailsFeatureKey = 'userDetails';

export interface UserDetailsState {
  userDetails: KeycloakUser;
  ARs: ArInAccount[];
  userHistory: DomainEvent[];
  userFiles: FileDetails[];
  enrolmentKeyDetails: EnrolmentKey;
  userDetailsLoaded: boolean;
  hasUserDetailsUpdatePendingApproval: boolean;
  agentDetailsUpdated: boolean;
  crcDetailsUpdated: boolean;
  goBackRoute: string;
}

export const initialState: UserDetailsState = {
  userDetails: {
    username: null,
    email: null,
    firstName: null,
    lastName: null,
    eligibleForSpecificActions: null,
    userRoles: null,
    attributes: {
      urid: null,
      alsoKnownAs: null,
      buildingAndStreet: null,
      buildingAndStreetOptional: null,
      buildingAndStreetOptional2: null,
      country: null,
      countryOfBirth: null,
      postCode: null,
      townOrCity: null,
      stateOrProvince: null,
      birthDate: null,
      state: null,
      workBuildingAndStreet: null,
      workBuildingAndStreetOptional: null,
      workBuildingAndStreetOptional2: null,
      workCountry: null,
      workMobileCountryCode: null,
      workMobilePhoneNumber: null,
      workAlternativeCountryCode: null,
      workAlternativePhoneNumber: null,
      noMobilePhoneNumberReason: null,
      workPostCode: null,
      workTownOrCity: null,
      workStateOrProvince: null,
      lastLoginDate: null,
      memorablePhrase: null,
      recoveryCountryCode: null,
      recoveryPhoneNumber: null,
      recoveryEmailAddress: null,
      hideRecoveryMethodsNotification: null,
      agent: null,
      agentCompanyName: null,
      agentEmailAddress: null,
      agentCountryCode: null,
      agentPhoneNumber: null,
      crc: null,
      crcIssuanceDate: null,
    },
  },
  ARs: null,
  userHistory: null,
  userFiles: [],
  enrolmentKeyDetails: null,
  userDetailsLoaded: false,
  hasUserDetailsUpdatePendingApproval: false,
  agentDetailsUpdated: false,
  crcDetailsUpdated: false,
  goBackRoute: null,
};

const userDetailsReducer = createReducer(
  initialState,
  mutableOn(UserDetailsActions.retrieveUser, (state) => {
    state.userDetailsLoaded = false;
  }),
  mutableOn(UserDetailsActions.retrieveUserSuccess, (state, { user }) => {
    state.userDetails = user;
    state.userDetailsLoaded = true;
  }),
  mutableOn(
    UserDetailsActions.retrieveARsInAccountSuccess,
    (state, { ARs }) => {
      state.ARs = ARs;
    }
  ),
  mutableOn(
    UserDetailsActions.retrieveUserHistorySuccess,
    (state, { userHistory }) => {
      state.userHistory = userHistory;
    }
  ),
  mutableOn(
    UserDetailsActions.retrieveUserFilesSuccess,
    (state, { userFiles }) => {
      state.userFiles = userFiles;
    }
  ),
  mutableOn(
    UserDetailsActions.retrieveEnrolmentKeyDetailsSuccess,
    (state, { enrolmentKeyDetails }) => {
      state.enrolmentKeyDetails = enrolmentKeyDetails;
    }
  ),
  mutableOn(
    UserDetailsActions.fetchAUserDetailsUpdatePendingApprovalSuccess,
    (state, { hasUserDetailsUpdatePendingApproval }) => {
      state.hasUserDetailsUpdatePendingApproval =
        hasUserDetailsUpdatePendingApproval;
    }
  ),
  mutableOn(
    UserDetailsActions.prepareNavigationToUserDetails,
    (state, { urid, backRoute }) => {
      state.goBackRoute = backRoute;
    }
  ),
  mutableOn(UserAgentActions.requestUserAgentUpdateSuccess, (state) => {
    state.agentDetailsUpdated = true;
  }),
  mutableOn(UserDetailsActions.clearAgentDetailsUpdated, (state) => {
    state.agentDetailsUpdated = false;
  }),
  mutableOn(UserCrcActions.requestUserCrcUpdateSuccess, (state) => {
    state.crcDetailsUpdated = true;
  }),
  mutableOn(UserDetailsActions.clearCrcDetailsUpdated, (state) => {
    state.crcDetailsUpdated = false;
  })
);

export function reducer(state: UserDetailsState | undefined, action: Action) {
  return userDetailsReducer(state, action);
}
