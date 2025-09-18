import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { UserDetailsUpdateApiService } from './user-details-update-api.service';
import { UserDetailService } from '@user-management/service';
import { KeycloakService } from 'keycloak-angular';
import { IUser, KeycloakUser } from '@shared/user';

describe('UserDetailsUpdateApiService', () => {
  let service: UserDetailsUpdateApiService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        UserDetailsUpdateApiService,
        UserDetailService,
        KeycloakService,
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl',
        },
      ],
    });
    service = TestBed.inject(UserDetailsUpdateApiService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it(`should HTTP GET from /admin/users.get`, () => {
    const uridParam = 'urid';
    const mockedResponse: KeycloakUser = {
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      eligibleForSpecificActions: false,
      attributes: {
        urid: [],
        alsoKnownAs: [],
        buildingAndStreet: [],
        buildingAndStreetOptional: [],
        buildingAndStreetOptional2: [],
        country: [],
        countryOfBirth: [],
        postCode: [],
        townOrCity: [],
        stateOrProvince: [],
        birthDate: [],
        state: [],
        workBuildingAndStreet: [],
        workBuildingAndStreetOptional: [],
        workBuildingAndStreetOptional2: [],
        workCountry: [],
        workMobileCountryCode: [],
        workMobilePhoneNumber: [],
        workAlternativeCountryCode: [],
        workAlternativePhoneNumber: [],
        noMobilePhoneNumberReason: [],
        workPostCode: [],
        workTownOrCity: [],
        workStateOrProvince: [],
        lastLoginDate: [],
        memorablePhrase: '',
      },
    };

    const urid = 'UK10098';
    let response: KeycloakUser;
    service
      .fetchUserDetailsInfo(urid)
      .subscribe((keycloakUser) => (response = keycloakUser));
    const req = httpMock.expectOne((r) =>
      r.url.startsWith(`${baseApiUrl}/admin/users.get`)
    );
    expect(req.request.url).toBe(`${baseApiUrl}/admin/users.get`);
    expect(req.request.method).toEqual('GET');
    expect(req.request.params.get(uridParam)).toBe(urid);
    req.flush(mockedResponse);
    httpMock.verify();
  });

  it(`should HTTP Patch from /admin/users.update.details`, () => {
    const uridParam = 'urid';
    const mockedResponse = '12345';

    const urid = 'UK10098';
    let response: string;
    service
      .submitRequest(urid, {} as IUser, {} as IUser)
      .subscribe((requestId) => (response = requestId));
    const req = httpMock.expectOne((r) =>
      r.url.startsWith(`${baseApiUrl}/admin/users.update.details`)
    );
    expect(req.request.url).toBe(`${baseApiUrl}/admin/users.update.details`);
    expect(req.request.method).toEqual('PATCH');
    expect(req.request.params.get(uridParam)).toBe(urid);
    req.flush(mockedResponse);
    httpMock.verify();
  });
});
