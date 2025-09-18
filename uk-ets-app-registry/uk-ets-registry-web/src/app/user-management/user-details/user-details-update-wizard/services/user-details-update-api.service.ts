import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IUser, KeycloakUser } from '@shared/user';
import { UserDetailService } from '@user-management/service';
import { UserUpdateDetailsType } from '@user-update/model';

@Injectable()
export class UserDetailsUpdateApiService {
  updateUserDetails: string;
  deactivateUser: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient,
    private userDetailService: UserDetailService
  ) {
    this.updateUserDetails = `${ukEtsRegistryApiBaseUrl}/admin/users.update.details`;
    this.deactivateUser = `${ukEtsRegistryApiBaseUrl}/admin/users.deactivate`;
  }

  private static checkValue(value) {
    if (Array.isArray(value) && value.length > 0) {
      return value[0];
    }
    return null;
  }

  private static checkDate(value) {
    const date = UserDetailsUpdateApiService.checkValue(value);
    if (date) {
      return {
        day: date.substring(0, date.indexOf('/')),
        month: date.substring(date.indexOf('/') + 1, date.lastIndexOf('/')),
        year: date.substring(date.lastIndexOf('/') + 1),
      };
    }
    return null;
  }

  submitRequest(
    urid: string,
    userDetailsUpdate: IUser,
    currentUserDetails: IUser
  ): Observable<string> {
    const param = {
      params: new HttpParams().set('urid', urid),
    };
    return this.http.patch<string>(
      `${this.updateUserDetails}`,
      { current: currentUserDetails, diff: userDetailsUpdate },
      param
    );
  }

  deactivateRequest(
    urid: string,
    deactivationComment: string
  ): Observable<string> {
    const param = {
      params: new HttpParams().set('urid', urid),
    };
    return this.http.post<string>(
      `${this.deactivateUser}`,
      {
        deactivationComment: deactivationComment,
      },
      param
    );
  }

  fetchUserDetailsInfo(urid: string) {
    return this.userDetailService.getUserDetail(urid);
  }

  fetchOpenUserDetailsTask(type: UserUpdateDetailsType, urid: string) {
    return this.userDetailService.fetchOpenUserDetailsTask(type, urid);
  }

  validateUserUpdateRequest(type: UserUpdateDetailsType, urid: string) {
    return this.userDetailService.validateUserUpdateRequest(type, urid);
  }

  transformToUserObject(keycloakUser: KeycloakUser): IUser {
    return {
      emailAddress: keycloakUser.email,
      emailAddressConfirmation: keycloakUser.email,
      username: keycloakUser.username,
      firstName: keycloakUser.firstName,
      lastName: keycloakUser.lastName,
      alsoKnownAs: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.alsoKnownAs
      ),
      countryOfBirth: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.countryOfBirth
      ),
      birthDate: UserDetailsUpdateApiService.checkDate(
        keycloakUser.attributes?.birthDate
      ),
      workMobileCountryCode: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.workMobileCountryCode
      ),
      workMobilePhoneNumber: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.workMobilePhoneNumber
      ),
      workAlternativeCountryCode: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.workAlternativeCountryCode
      ),
      workAlternativePhoneNumber: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.workAlternativePhoneNumber
      ),
      noMobilePhoneNumberReason: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.noMobilePhoneNumberReason
      ),
      workBuildingAndStreet: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.workBuildingAndStreet
      ),
      workBuildingAndStreetOptional: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.workBuildingAndStreetOptional
      ),
      workBuildingAndStreetOptional2: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.workBuildingAndStreetOptional2
      ),
      workTownOrCity: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.workTownOrCity
      ),
      workStateOrProvince: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.workStateOrProvince
      ),
      workPostCode: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.workPostCode
      ),
      workCountry: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.workCountry
      ),
      urid: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.urid
      ),
      state: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.state
      ),
      status: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.state
      ),
      memorablePhrase: UserDetailsUpdateApiService.checkValue(
        keycloakUser.attributes?.memorablePhrase
      ),
    } as IUser;
  }
}
