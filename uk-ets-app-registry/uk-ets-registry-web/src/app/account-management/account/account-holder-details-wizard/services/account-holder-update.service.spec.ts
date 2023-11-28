import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../../app.tokens';
import { AccountHolderUpdateService } from './account-holder-update.service';
import { AccountHolderDetailsType } from '@account-management/account/account-holder-details-wizard/model';
import { AccountHolderType } from '@shared/model/account';

describe('AccountHolderUpdateService', () => {
  let service: AccountHolderUpdateService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  let expectedSubmitAHUrl;
  let expectedSubmitAHContactUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AccountHolderUpdateService,
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl',
        },
      ],
    });
    service = TestBed.inject(AccountHolderUpdateService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
    expectedSubmitAHUrl = `${baseApiUrl}/account-holder.update-details`;
    expectedSubmitAHContactUrl = `${baseApiUrl}/account-holder.update-primary-contact`;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should HTTP POST for Account Holder Update details', () => {
    // given
    const mockedResponse = '10002';
    let response: string;

    // when
    service
      .submitRequest({
        accountIdentifier: '',
        accountHolderIdentifier: 1,
        currentAccountHolder: {
          id: 1,
          type: AccountHolderType.INDIVIDUAL,
          details: {
            name: '',
            firstName: '',
            lastName: '',
            birthDate: {
              day: '',
              month: '',
              year: '',
            },
            countryOfBirth: '',
          },
          address: {},
        },
        accountHolderDiff: {},
        updateType: AccountHolderDetailsType.ACCOUNT_HOLDER_UPDATE_DETAILS,
      })
      .subscribe((taskIdentifier) => (response = taskIdentifier));
    //then
    const req = httpMock.expectOne((r) =>
      r.url.startsWith(expectedSubmitAHUrl)
    );
    expect(req.request.url).toBe(expectedSubmitAHUrl);
    expect(req.request.method).toEqual('POST');

    req.flush(mockedResponse);
    httpMock.verify();
  });

  it('should HTTP POST for Account Holder Primary Contact Update details', () => {
    // given
    const mockedResponse = '10002';
    let response: string;

    // when
    service
      .submitRequest({
        accountIdentifier: '',
        accountHolderIdentifier: 1,
        currentAccountHolder: {
          id: 1,
          new: true,
          details: {
            firstName: '',
            lastName: '',
            aka: '',
            birthDate: {
              day: '',
              month: '',
              year: '',
            },
          },
          positionInCompany: '',
          address: {
            buildingAndStreet: '',
            buildingAndStreet2: '',
            buildingAndStreet3: '',
            townOrCity: '',
            stateOrProvince: '',
            country: '',
            postCode: '',
          },
          phoneNumber: {
            countryCode1: '',
            phoneNumber1: '',
            countryCode2: '',
            phoneNumber2: '',
          },
          emailAddress: {
            emailAddress: '',
            emailAddressConfirmation: '',
          },
        },
        accountHolderDiff: {},
        updateType:
          AccountHolderDetailsType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS,
      })
      .subscribe((taskIdentifier) => (response = taskIdentifier));
    //then
    const req = httpMock.expectOne((r) =>
      r.url.startsWith(expectedSubmitAHContactUrl)
    );
    expect(req.request.url).toBe(expectedSubmitAHContactUrl);
    expect(req.request.method).toEqual('POST');

    req.flush(mockedResponse);
    httpMock.verify();
  });
});
