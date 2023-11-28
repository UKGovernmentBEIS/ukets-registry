import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed, waitForAsync } from '@angular/core/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';

import { AccountTransferService } from '@account-transfer/services/account-transfer.service';
import {
  AccountHolder,
  AccountHolderContactInfo,
  AccountHolderType,
} from '@shared/model/account';

describe('AccountTransferService', () => {
  let service: AccountTransferService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AccountTransferService,
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl',
        },
      ],
    });
    service = TestBed.inject(AccountTransferService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it(
    'should succesfully fetch Account Holder',
    waitForAsync(() => {
      const identifier = 10003;
      const accountHolder: AccountHolder = {
        id: identifier,
        type: AccountHolderType.INDIVIDUAL,
        details: null,
        address: null,
      };
      service
        .fetchAccountHolder(identifier)
        .subscribe((res) => expect(res).toEqual(accountHolder));
      const fetchAccountHolderRequest = httpMock.expectOne((r) =>
        r.url.startsWith(`${baseApiUrl}/account-holder.get`)
      );
      fetchAccountHolderRequest.flush(accountHolder);
    })
  );

  it(
    'should succesfully fetch Account Holder Contacts',
    waitForAsync(() => {
      const identifier = 10003;
      const accountHolderContacts: AccountHolderContactInfo = {
        primaryContact: {
          id: 10002,
          new: true,
          details: {
            firstName: 'string',
            lastName: 'string',
            aka: 'string',
            birthDate: null,
            isOverEighteen: true,
          },
          positionInCompany: 'string',
          address: {
            buildingAndStreet: 'string',
            buildingAndStreet2: 'string',
            buildingAndStreet3: 'string',
            townOrCity: 'string',
            stateOrProvince: 'string',
            country: 'string',
            postCode: 'string',
          },
          phoneNumber: {
            countryCode1: 'string',
            phoneNumber1: 'string',
            countryCode2: 'string',
            phoneNumber2: 'string',
          },
          emailAddress: {
            emailAddress: 'string',
            emailAddressConfirmation: 'string',
          },
        },
        alternativeContact: null,
        isPrimaryAddressSameAsHolder: true,
        isAlternativeAddressSameAsHolder: true,
      };
      service
        .fetchAccountHolderContacts(identifier)
        .subscribe((res) => expect(res).toEqual(accountHolderContacts));
      const fetchAccountHolderContactsRequest = httpMock.expectOne((r) =>
        r.url.startsWith(`${baseApiUrl}/account-holder.get.contacts`)
      );
      expect(
        fetchAccountHolderContactsRequest.request.params.get('holderId')
      ).toBe(identifier.toString());
      fetchAccountHolderContactsRequest.flush(accountHolderContacts);
    })
  );

  it('should HTTP POST for Transfer Account', () => {
    // given
    const mockedResponse = '10002';
    let response: string;

    // when
    service
      .submitRequest({
        accountIdentifier: '100002',
        accountTransferType: 'ACCOUNT_TRANSFER_TO_CREATED_HOLDER',
        acquiringAccountHolder: {
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
        acquiringAccountHolderContactInfo: null,
      })
      .subscribe((taskIdentifier) => (response = taskIdentifier));
    //then
    const req = httpMock.expectOne((r) =>
      r.url.startsWith(`${baseApiUrl}/account-transfer.perform`)
    );
    expect(req.request.url).toBe(`${baseApiUrl}/account-transfer.perform`);
    expect(req.request.method).toEqual('POST');

    req.flush(mockedResponse);
    httpMock.verify();
  });

  afterEach(() => httpMock.verify());
});
