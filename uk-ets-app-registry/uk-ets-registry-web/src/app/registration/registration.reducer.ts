import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { IUser } from 'src/app/shared/user/user';
import * as RegistrationActions from './registration.actions';
import { MobileNumberVerificationStatus } from '@registry-web/shared/form-controls/uk-select-phone';

export const registrationFeatureKey = 'registration';

export interface RegistrationState {
  emailVerificationNextStep: string;
  user: IUser;
  hasWorkMobilePhone: boolean;
  declarationConfirmed: boolean;
  mobileNumberVerificationStatus: MobileNumberVerificationStatus;
}

export const initialState: RegistrationState = getInitialState();

const registrationReducer = createReducer(
  initialState,
  mutableOn(RegistrationActions.updateUserHomeDetails, (state, { user }) => {
    const alterUser = state.user;

    if (alterUser) {
      alterUser.firstName = user.firstName;
      alterUser.lastName = user.lastName;
      alterUser.alsoKnownAs = user.alsoKnownAs;
      alterUser.buildingAndStreet = user.buildingAndStreet;
      alterUser.buildingAndStreetOptional = user.buildingAndStreetOptional;
      alterUser.buildingAndStreetOptional2 = user.buildingAndStreetOptional2;
      alterUser.postCode = user.postCode;
      alterUser.townOrCity = user.townOrCity;
      alterUser.stateOrProvince = user.stateOrProvince;
      alterUser.country = user.country;
      alterUser.birthDate = user.birthDate;
      alterUser.countryOfBirth = user.countryOfBirth;
    }
    alterUser.differentCountryLastFiveYears =
      user.differentCountryLastFiveYears;
  }),
  mutableOn(RegistrationActions.updateUserWorkDetails, (state, { user }) => {
    return {
      ...state,
      user: {
        ...state.user,
        workMobileCountryCode: user.workMobileCountryCode,
        workMobilePhoneNumber: user.workMobilePhoneNumber,
        workAlternativeCountryCode: user.workAlternativeCountryCode,
        workAlternativePhoneNumber: user.workAlternativePhoneNumber,
        noMobilePhoneNumberReason: user.noMobilePhoneNumberReason,
        workBuildingAndStreet: user.workBuildingAndStreet,
        workBuildingAndStreetOptional: user.workBuildingAndStreetOptional,
        workBuildingAndStreetOptional2: user.workBuildingAndStreetOptional2,
        workTownOrCity: user.workTownOrCity,
        workStateOrProvince: user.workStateOrProvince,
        workPostCode: user.workPostCode,
        workCountry: user.workCountry,
      },
    };
  }),
  mutableOn(RegistrationActions.updateUserPassword, (state, { password }) => {
    return {
      ...state,
      user: {
        ...state.user,
        password,
      },
    };
  }),
  mutableOn(
    RegistrationActions.updateUserMemorablePhrase,
    (state, { user }) => {
      return {
        ...state,
        user: {
          ...state.user,
          memorablePhrase: user.memorablePhrase,
        },
      };
    }
  ),
  mutableOn(
    RegistrationActions.updateUserRepresentation,
    (state, { userRepresentation }) => {
      return {
        ...state,
        user: {
          ...state.user,
          userId: userRepresentation.id,
          username: userRepresentation.username,
          emailAddress: userRepresentation.email,
          urid: userRepresentation.attributes['urid'][0],
        },
      };
    }
  ),
  mutableOn(RegistrationActions.cleanUser, (state) => {
    return {
      ...state,
    };
  }),
  mutableOn(
    RegistrationActions.hasWorkMobilePhoneChange,
    (state, { hasWorkMobilePhone }) => {
      state.hasWorkMobilePhone = hasWorkMobilePhone;

      if (!hasWorkMobilePhone) {
        state.mobileNumberVerificationStatus = null;
      }
    }
  ),
  mutableOn(
    RegistrationActions.mobileNumberVerificationStatusChange,
    (state, { mobileNumberVerificationStatus }) => {
      state.mobileNumberVerificationStatus = mobileNumberVerificationStatus;
    }
  ),
  mutableOn(RegistrationActions.cleanUpRegistration, (state) => {
    state = initialState;
  }),
  mutableOn(RegistrationActions.verificationNextStep, (state, { message }) => {
    state.emailVerificationNextStep = message;
  }),
  mutableOn(RegistrationActions.confirmDeclaration, (state, { confirmed }) => {
    state.declarationConfirmed = confirmed;
  })
);

export function reducer(state: RegistrationState | undefined, action: Action) {
  return registrationReducer(state, action);
}

function getInitialState(): RegistrationState {
  return {
    emailVerificationNextStep: null,
    user: {
      emailAddress: '',
      emailAddressConfirmation: '',
      userId: '',
      username: '',
      password: '',
      firstName: '',
      lastName: '',
      alsoKnownAs: '',
      buildingAndStreet: '',
      buildingAndStreetOptional: '',
      buildingAndStreetOptional2: '',
      postCode: '',
      townOrCity: '',
      stateOrProvince: '',
      country: '',
      birthDate: { day: null, month: null, year: null },
      countryOfBirth: '',
      workMobileCountryCode: '',
      workMobilePhoneNumber: '',
      workAlternativeCountryCode: '',
      workAlternativePhoneNumber: '',
      noMobilePhoneNumberReason: '',
      workBuildingAndStreet: '',
      workBuildingAndStreetOptional: '',
      workBuildingAndStreetOptional2: '',
      workTownOrCity: '',
      workStateOrProvince: '',
      workPostCode: '',
      workCountry: '',
      memorablePhrase: '',
      urid: '',
      recoveryCountryCode: '',
      recoveryPhoneNumber: '',
      recoveryEmailAddress: '',

      // TODO: UKETS-427 the state and status in the initial state should be empty. This one should be only set in the server.
      //  ALso consider using a readonly property for these. They will never change in the client
      state: 'REGISTERED',
      status: 'REGISTERED',
    },
    hasWorkMobilePhone: null,
    mobileNumberVerificationStatus: null,
    declarationConfirmed: false,
  };
}
