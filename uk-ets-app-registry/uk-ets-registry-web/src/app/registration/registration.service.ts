import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import {
  UK_ETS_REGISTRY_API_BASE_URL,
  USER_REGISTRATION_SERVICE_URL,
} from '../app.tokens';
import { User } from '../shared/user/user';
import { mergeMap, map, catchError } from 'rxjs/operators';
import { leftPadZeros } from '../shared/shared.util';

export class UserRepresentation {
  static readonly keycloakAttributes = [
    'alsoKnownAs',
    'buildingAndStreet',
    'buildingAndStreetOptional',
    'buildingAndStreetOptional2',
    'postCode',
    'townOrCity',
    'stateOrProvince',
    'country',
    'countryOfBirth',
    'workMobileCountryCode',
    'workMobilePhoneNumber',
    'workAlternativeCountryCode',
    'workAlternativePhoneNumber',
    'noMobilePhoneNumberReason',
    'workBuildingAndStreet',
    'workBuildingAndStreetOptional',
    'workBuildingAndStreetOptional2',
    'workTownOrCity',
    'workStateOrProvince',
    'workPostCode',
    'workCountry',
    'state',
    'memorablePhrase',
    'differentCountryLastFiveYears',
  ];

  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  requiredActions: string[];
  attributes = {};
  credentials = [];

  static updateFromUser(
    userRep: UserRepresentation,
    user: User
  ): UserRepresentation {
    if (user && userRep) {
      userRep.firstName = user.firstName;
      userRep.lastName = user.lastName;
      if (!userRep.attributes) {
        userRep.attributes = {};
      }
      UserRepresentation.keycloakAttributes.forEach(
        (attr) => (userRep.attributes[attr] = user[attr])
      );
      userRep.attributes['birthDate'] = `${leftPadZeros(
        user.birthDate.day,
        2
      )}/${leftPadZeros(user.birthDate.month, 2)}/${leftPadZeros(
        user.birthDate.year,
        4
      )}`;
      userRep.credentials = [
        {
          type: 'password',
          temporary: 'false',
          value: user.password,
        },
      ];
    }
    return userRep;
  }
}

@Injectable({
  providedIn: 'root',
})
export class RegistrationService {
  jsonAccept = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

  constructor(
    @Inject(USER_REGISTRATION_SERVICE_URL)
    private userRegistrationServiceUrl: string,
    private httpClient: HttpClient,
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string
  ) {}

  registerUser(emailAddress: string): Observable<boolean> {
    return this.httpClient
      .post<UserRepresentation>(
        this.userRegistrationServiceUrl,
        { email: emailAddress },
        this.jsonAccept
      )
      .pipe(
        map(() => true),
        catchError((err) => of(false))
      );
  }

  getUser(userId: string): Observable<UserRepresentation> {
    return this.httpClient.get<UserRepresentation>(
      this.userRegistrationServiceUrl + '/' + userId
    );
  }

  updateUser(userId: string, user: User) {
    const result = this.getUser(userId).pipe(
      mergeMap((userRep: UserRepresentation) => {
        return this.httpClient.put<UserRepresentation>(
          this.userRegistrationServiceUrl,
          UserRepresentation.updateFromUser(userRep, user),
          this.jsonAccept
        );
      })
    );
    return result;
  }

  persistUser(user: UserRepresentation): Observable<User> {
    return this.httpClient.post<User>(
      `${this.ukEtsRegistryApiBaseUrl}/users.create`,
      {
        userId: user.id,
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
        urid: user.attributes['urid'].toString(),
        alsoKnownAs: user.attributes['alsoKnownAs'].toString(),
      },
      this.jsonAccept
    );
  }

  deleteUser(userKeycloakId: string): Observable<boolean> {
    return this.httpClient
      .delete(this.userRegistrationServiceUrl, {
        params: { userId: userKeycloakId },
      })
      .pipe(
        map(() => true),
        catchError((err) => of(false))
      );
  }

  verifyUserEmail(userId: string): Observable<UserRepresentation> {
    const url = this.userRegistrationServiceUrl + '/' + userId; // PATCH registration/e89305d6-af41-4a53-af1e-1bb992c6d3ca
    return this.httpClient.patch<UserRepresentation>(url, {}, this.jsonAccept);
  }
}
