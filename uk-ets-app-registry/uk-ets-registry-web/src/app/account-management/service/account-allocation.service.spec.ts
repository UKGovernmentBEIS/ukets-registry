import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../app.tokens';
import { AccountAllocationStatus } from '@allocation-status/model';
import { AccountAllocation, AllocationStatus } from '@shared/model/account';
import { UpdateAllocationStatusRequest } from '@allocation-status/model/allocation-status.model';
import { AccountAllocationService } from '@account-management/service/account-allocation.service';

describe('AllocationService', () => {
  const accountIdParam = 'accountId';
  let service: AccountAllocationService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;
  let expectedGetAllocationUrl;
  let expectedGetAllocationStatusUrl;
  let expectedUpdateAllocationStatusUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AccountAllocationService,
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl'
        }
      ]
    });
    service = TestBed.inject(AccountAllocationService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
    expectedGetAllocationUrl = `${baseApiUrl}/allocations.get`;
    expectedGetAllocationStatusUrl = `${baseApiUrl}/allocations.get.status`;
    expectedUpdateAllocationStatusUrl = `${baseApiUrl}/allocations.update.status`;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it(`should HTTP GET from /get.account.allocation?accountId`, () => {
    const accountIdParam = 'accountId';
    const mockedResponse: AccountAllocation = {
      standard: null,
      underNewEntrantsReserve: null
    };

    const accountId = '10098';
    let response: AccountAllocation;
    service
      .fetchAllocation(accountId)
      .subscribe(allocation => (response = allocation));
    const req = httpMock.expectOne(r =>
      r.url.startsWith(expectedGetAllocationUrl)
    );
    expect(req.request.url).toBe(expectedGetAllocationUrl);
    expect(req.request.method).toEqual('GET');
    expect(req.request.params.get(accountIdParam)).toBe(accountId);
    req.flush(mockedResponse);
    httpMock.verify();
  });

  it(`should fetch the annual allocation statuses by http GET from /get.allocation.status`, () => {
    const mockedHttpResponse: AccountAllocationStatus = {
      2021: AllocationStatus.WITHHELD
    };
    const accountId = '10098';
    service.fetchAllocationStatus(accountId).subscribe(status => {
      expect(status).toBe(mockedHttpResponse);
    });
    const req = httpMock.expectOne(r =>
      r.url.startsWith(expectedGetAllocationStatusUrl)
    );
    expect(req.request.url).toBe(expectedGetAllocationStatusUrl);
    expect(req.request.method).toEqual('GET');
    expect(req.request.params.get(accountIdParam)).toBe(accountId);
    req.flush(mockedHttpResponse);
    httpMock.verify();
  });

  it(`should post the annual allocation statuses changes by http POST to /allocation.status.update`, () => {
    const accountId = '10098';
    const updateReq: UpdateAllocationStatusRequest = {
      changedStatus: {
        2022: AllocationStatus.WITHHELD
      },
      justification: 'some test reason'
    };
    service
      .updateAllocationStatus(accountId, updateReq)
      .subscribe(response => expect(response).toBe(accountId));
    const req = httpMock.expectOne(r =>
      r.url.startsWith(expectedUpdateAllocationStatusUrl)
    );

    expect(req.request.url).toBe(expectedUpdateAllocationStatusUrl);
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toBe(updateReq);
    expect(req.request.params.get(accountIdParam)).toBe(accountId);
    req.flush(accountId);
    httpMock.verify();
  });
});
