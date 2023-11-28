import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { UserDetailsUpdateActions } from '@user-update/action';
import { IUser } from '@shared/user';
import { UserUpdateDetailsType } from '@user-update/model';
import { Draft } from 'immer';

export const userDetailsUpdateFeatureKey = 'user-details-update';

export interface UserDetailsUpdateState {
  updateType: UserUpdateDetailsType;
  userDetails: IUser;
  newUserDetails: IUser;
  submittedRequestIdentifier: string;
  isMyProfilePage: boolean;
  deactivationComment: string;
}

export const initialState: UserDetailsUpdateState = {
  updateType: null,
  userDetails: null,
  newUserDetails: null,
  submittedRequestIdentifier: null,
  isMyProfilePage: null,
  deactivationComment: null,
};

const userDetailsUpdateReducer = createReducer(
  initialState,
  mutableOn(
    UserDetailsUpdateActions.loadedFromMyProfilePage,
    (state: Draft<UserDetailsUpdateState>, { isMyProfilePage }) => {
      state.isMyProfilePage = isMyProfilePage;
    }
  ),
  mutableOn(
    UserDetailsUpdateActions.setCurrentUserDetailsInfoSuccess,
    (state: Draft<UserDetailsUpdateState>, { userDetails }) => {
      state.userDetails = userDetails;
    }
  ),
  mutableOn(
    UserDetailsUpdateActions.setRequestUpdateType,
    (state: Draft<UserDetailsUpdateState>, { updateType }) => {
      state.updateType = updateType;
    }
  ),
  mutableOn(
    UserDetailsUpdateActions.setDeactivationComment,
    (state: Draft<UserDetailsUpdateState>, { comment }) => {
      state.deactivationComment = comment;
    }
  ),
  mutableOn(
    UserDetailsUpdateActions.setPersonalDetailsRequest,
    (state: Draft<UserDetailsUpdateState>, { userDetails }) => {
      if (!state.newUserDetails) {
        state.newUserDetails = {
          ...state.userDetails,
          ...userDetails,
        };
      } else {
        state.newUserDetails.firstName = userDetails.firstName;
        state.newUserDetails.lastName = userDetails.lastName;
        state.newUserDetails.alsoKnownAs = userDetails.alsoKnownAs;
        state.newUserDetails.countryOfBirth = userDetails.countryOfBirth;
        state.newUserDetails.birthDate = userDetails.birthDate;
      }
    }
  ),
  mutableOn(
    UserDetailsUpdateActions.setWorkDetailsRequest,
    (state: Draft<UserDetailsUpdateState>, { userDetails }) => {
      state.newUserDetails.workCountryCode = userDetails.workCountryCode;
      state.newUserDetails.workPhoneNumber = userDetails.workPhoneNumber;
      state.newUserDetails.workEmailAddress = userDetails.workEmailAddress;
      state.newUserDetails.workEmailAddressConfirmation =
        userDetails.workEmailAddressConfirmation;
      state.newUserDetails.workBuildingAndStreet =
        userDetails.workBuildingAndStreet;
      state.newUserDetails.workBuildingAndStreetOptional =
        userDetails.workBuildingAndStreetOptional;
      state.newUserDetails.workBuildingAndStreetOptional2 =
        userDetails.workBuildingAndStreetOptional2;
      state.newUserDetails.workTownOrCity = userDetails.workTownOrCity;
      state.newUserDetails.workStateOrProvince =
        userDetails.workStateOrProvince;
      state.newUserDetails.workPostCode = userDetails.workPostCode;
      state.newUserDetails.workCountry = userDetails.workCountry;
    }
  ),
  mutableOn(
    UserDetailsUpdateActions.setNewUserDetailsInfoSuccess,
    (state: Draft<UserDetailsUpdateState>, { userDetails }) => {
      state.newUserDetails.memorablePhrase = userDetails.memorablePhrase;
    }
  ),
  mutableOn(
    UserDetailsUpdateActions.submitUpdateRequestSuccess,
    (state, { requestId }) => {
      state.submittedRequestIdentifier = requestId;
    }
  ),
  mutableOn(UserDetailsUpdateActions.clearUserDetailsUpdateRequest, (state) => {
    resetState(state);
  })
);

export function reducer(
  state: UserDetailsUpdateState | undefined,
  action: Action
) {
  return userDetailsUpdateReducer(state, action);
}

function resetState(state) {
  state.updateType = initialState.updateType;
  state.userDetails = initialState.userDetails;
  state.newUserDetails = initialState.newUserDetails;
  state.submittedRequestIdentifier = initialState.submittedRequestIdentifier;
  state.isMyProfilePage = initialState.isMyProfilePage;
  state.deactivationComment = initialState.deactivationComment;
}
