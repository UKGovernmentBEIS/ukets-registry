import { reducer, initialState } from '.';
import { retrieveUserSuccess } from '../actions';
import { KeycloakUser } from '@shared/user/keycloak-user';

describe('User Details reducer', () => {
  const user: KeycloakUser = {
    username: 'user_details_test',
    email: 'ukets_user@gov.uk',
    firstName: 'James',
    lastName: 'Bond',
    eligibleForSpecificActions: false,
    userRoles: null,
    attributes: {
      urid: ['UK230169410292'],
      alsoKnownAs: [''],
      buildingAndStreet: [''],
      buildingAndStreetOptional: [''],
      buildingAndStreetOptional2: [''],
      country: ['GR'],
      countryOfBirth: ['GR'],
      postCode: ['11526'],
      townOrCity: ['ATHENS'],
      stateOrProvince: ['Attica'],
      birthDate: ['24/5/1985'],
      state: ['ATTICA'],
      workBuildingAndStreet: [''],
      workBuildingAndStreetOptional: [''],
      workBuildingAndStreetOptional2: [''],
      workCountry: ['GR'],
      workMobileCountryCode: ['GR (30)'],
      workMobilePhoneNumber: ['2109089765'],
      workAlternativeCountryCode: [''],
      workAlternativePhoneNumber: [''],
      noMobilePhoneNumberReason: [''],
      workPostCode: ['11526'],
      workTownOrCity: ['ATHENS'],
      workStateOrProvince: ['Attica'],
    },
  };

  it('retrieves the user details', () => {
    const beforeRetrievalState = reducer(initialState, {} as any);
    expect(beforeRetrievalState.userDetails).toBe(initialState.userDetails);
    expect(beforeRetrievalState.userDetailsLoaded).toBe(false);

    const retrieveUserActionSuccess = retrieveUserSuccess({ user });
    const afterRetrievalState = reducer(
      initialState,
      retrieveUserActionSuccess
    );
    expect(afterRetrievalState.userDetails).toEqual(user);
    expect(afterRetrievalState.userDetailsLoaded).toBe(true);
  });
});
