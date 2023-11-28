import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import {
  ComplianceStatusHistory,
  VerifiedEmissions,
} from '@account-shared/model';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';

import { AccountComplianceService } from './account-compliance.service';

describe('AccountComplianceService', () => {
  let service: AccountComplianceService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: 'apiBaseUrl' },
      ],
    });
    service = TestBed.inject(AccountComplianceService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should succesfully HTTP GET for account emissions', () => {
    // given
    const verifiedEmissions: VerifiedEmissions[] = [
      {
        compliantEntityId: 1233,
        year: 2021,
        reportableEmissions: 1023,
        lastUpdated: new Date(),
      },
    ];
    const compliantEntityIdentifier = 50005;

    // when
    service
      .fetchAccountVerifiedEmissions(compliantEntityIdentifier)
      .subscribe((res) => expect(res).toEqual(verifiedEmissions));

    //then
    const emissionsRequest = httpMock.expectOne((r) =>
      r.url.startsWith(`${baseApiUrl}/compliance.get.emissions`)
    );
    expect(emissionsRequest.request.url).toBe(
      `${baseApiUrl}/compliance.get.emissions`
    );
    expect(emissionsRequest.request.method).toEqual('GET');
    expect(emissionsRequest.request.params.get('compliantEntityId')).toEqual(
      compliantEntityIdentifier.toString()
    );

    emissionsRequest.flush(verifiedEmissions);
  });

  it('should succesfully HTTP GET for account compliance status history', () => {
    // given
    const history: ComplianceStatusHistory[] = [
      {
        year: 2021,
        status: 'A',
      },
    ];
    const compliantEntityIdentifier = 50005;

    // when
    service
      .fetchAccountComplianceStatusHistory(compliantEntityIdentifier)
      .subscribe((res) => expect(res).toEqual(history));

    //then
    const historyRequest = httpMock.expectOne((r) =>
      r.url.startsWith(`${baseApiUrl}/compliance.get.status.history`)
    );
    expect(historyRequest.request.url).toBe(
      `${baseApiUrl}/compliance.get.status.history`
    );
    expect(historyRequest.request.method).toEqual('GET');
    expect(historyRequest.request.params.get('compliantEntityId')).toEqual(
      compliantEntityIdentifier.toString()
    );

    historyRequest.flush(history);
  });

  it('should succesfully HTTP GET for account compliance events history and comments', () => {
    // given
    const history: ComplianceStatusHistory[] = [
      {
        year: 2021,
        status: 'A',
      },
    ];
    const compliantEntityIdentifier = 50005;

    // when
    service
      .fetchAccountComplianceHistoryAndComments(compliantEntityIdentifier)
      .subscribe((res) => expect(res).toEqual(history));

    //then
    const historyRequest = httpMock.expectOne((r) =>
      r.url.startsWith(`${baseApiUrl}/compliance.get.events.history`)
    );
    expect(historyRequest.request.url).toBe(
      `${baseApiUrl}/compliance.get.events.history`
    );
    expect(historyRequest.request.method).toEqual('GET');
    expect(historyRequest.request.params.get('compliantEntityId')).toEqual(
      compliantEntityIdentifier.toString()
    );

    historyRequest.flush(history);
  });

  it('should succesfully HTTP GET for account compliance overview', () => {
    // given
    const complianceOverviewResult = [
      {
        totalVerifiedEmissions: 33434,
        totalNetSurrenders: 3004,
        currentComplianceStatus: 'A',
      },
    ];
    const accountIdentifier = 1001;

    // when
    service
      .fetchAccountComplianceOverview(accountIdentifier)
      .subscribe((res) => expect(res).toEqual(complianceOverviewResult));

    //then
    const overviewRequest = httpMock.expectOne((r) =>
      r.url.startsWith(`${baseApiUrl}/compliance.get.overview`)
    );
    expect(overviewRequest.request.url).toBe(
      `${baseApiUrl}/compliance.get.overview`
    );
    expect(overviewRequest.request.method).toEqual('GET');
    expect(overviewRequest.request.params.get('accountIdentifier')).toEqual(
      accountIdentifier.toString()
    );

    overviewRequest.flush(complianceOverviewResult);
  });

  afterEach(() => httpMock.verify());
});
