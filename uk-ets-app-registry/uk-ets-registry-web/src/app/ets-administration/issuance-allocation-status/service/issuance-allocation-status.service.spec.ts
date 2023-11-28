import {
  HttpTestingController,
  HttpClientTestingModule,
} from '@angular/common/http/testing';
import { IssuanceAllocationStatusService } from '.';
import { TestBed, waitForAsync } from '@angular/core/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import { AllowanceReport } from '../model';
import { DomainEvent } from '@shared/model/event';

describe('IssuanceAllocationStatusService', () => {
  let issuanceAllocationStatusService: IssuanceAllocationStatusService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;
  let loadIssuanceAllocationStatusesDataUrl;
  let allocationTableHistoryDataUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: 'apiBaseUrl' },
        IssuanceAllocationStatusService,
      ],
    });
    issuanceAllocationStatusService = TestBed.inject(
      IssuanceAllocationStatusService
    );
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
    loadIssuanceAllocationStatusesDataUrl = `${baseApiUrl}/allocations.get.issuance-by-year`;
    allocationTableHistoryDataUrl = `${baseApiUrl}/allocation-table.get.history`;
  });

  it('should be created', () => {
    const service: IssuanceAllocationStatusService = TestBed.inject(
      IssuanceAllocationStatusService
    );
    expect(service).toBeTruthy();
  });

  it(
    'should succesfully get issuance and allocation statuses',
    waitForAsync(() => {
      const issuanceAllocationStatusesData: AllowanceReport[] = [
        {
          description: '2021',
          cap: 500000,
          entitlement: 1000000,
          issued: 900000,
          allocated: 700000,
          forAuction: 50000,
          auctioned: 100000,
        },
      ];
      issuanceAllocationStatusService
        .getIssuanceAllocationStatuses()
        .subscribe((res) =>
          expect(res).toEqual(issuanceAllocationStatusesData)
        );
      const req = httpMock.expectOne(loadIssuanceAllocationStatusesDataUrl);
      req.flush(issuanceAllocationStatusesData);
      expect(req.request.url).toBe(loadIssuanceAllocationStatusesDataUrl);
      expect(req.request.method).toEqual('GET');
    })
  );

  it(
    'should succesfully get issuance and allocation events',
    waitForAsync(() => {
      const events: DomainEvent[] = [
        {
          domainType: 'gov.uk.ets.registry.api.file.upload.domain.UploadedFile',
          domainId: '1000631',
          domainAction: 'Upload allocation table',
          description: 'UK_NER_2021_2030_A0B4A6A76A7DF5A80BDCC648B94AA40A.xlsx',
          creator: 'UK802061111112',
          creatorType: 'user',
          creationDate: new Date(),
        },
      ];
      issuanceAllocationStatusService
        .getAllocationTableEvents()
        .subscribe((res) => expect(res).toEqual(events));
      const req = httpMock.expectOne(allocationTableHistoryDataUrl);
      req.flush(events);
      expect(req.request.url).toBe(allocationTableHistoryDataUrl);
      expect(req.request.method).toEqual('GET');
    })
  );

  afterEach(() => httpMock.verify());
});
