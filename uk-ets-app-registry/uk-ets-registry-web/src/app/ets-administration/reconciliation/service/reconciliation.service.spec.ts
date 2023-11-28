import { TestBed } from '@angular/core/testing';

import { ReconciliationService } from './reconciliation.service';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../app.tokens';
import { Reconciliation } from '@shared/model/reconciliation-model';

describe('ReconciliationService', () => {
  let service: ReconciliationService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  let expectedFetchLatestReconciliationUrl;
  let expectedStartReconciliationUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ReconciliationService,
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl',
        },
      ],
    });
    service = TestBed.inject(ReconciliationService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
    expectedFetchLatestReconciliationUrl = `${baseApiUrl}/reconciliation.get.latest`;
    expectedStartReconciliationUrl = `${baseApiUrl}/reconciliation.start`;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should HTTP GET the latest reconciliation from /reconciliation.get.latest', () => {
    // given
    const mockedResponse: Reconciliation = {
      identifier: 1232,
      created: '10/08/2020 11:23',
      updated: '10/08/2020 13:00',
      status: 'COMPLETED',
    };
    let response: Reconciliation;

    // when
    service
      .fetchLatestReconciliation()
      .subscribe((reconciliation) => (response = reconciliation));
    //then
    const req = httpMock.expectOne((r) =>
      r.url.startsWith(expectedFetchLatestReconciliationUrl)
    );
    expect(req.request.url).toBe(expectedFetchLatestReconciliationUrl);
    expect(req.request.method).toEqual('GET');

    req.flush(mockedResponse);
    httpMock.verify();
  });

  it('should HTTP POST to /reconciliation.start url to start a new reconciliation process', () => {
    //given
    let response: Reconciliation;
    // when
    service.startReconciliation().subscribe((result) => (response = result));
    // then
    const req = httpMock.expectOne((r) =>
      r.url.startsWith(expectedStartReconciliationUrl)
    );
    expect(req.request.url).toBe(expectedStartReconciliationUrl);
    expect(req.request.method).toEqual('POST');
  });
});
