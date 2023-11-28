import { waitForAsync, TestBed } from '@angular/core/testing';

import { NoticeApiService } from './notice-api.service';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

const mockNotices = [
  {
    actionDueDate: new Date(),
    commitPeriod: 1,
    content: 'Test content',
    lulucfactivity: 'REVEGETATION',
    messageDate: new Date(),
    status: 'TRANSACTION_PROPOSAL_PENDING',
    type: 'COMMITMENT_PERIOD_RESERVE',
    notificationIdentifier: 1,
    projectNumber: '1',
    targetDate: new Date(),
    targetValue: 1,
    unitBlockIdentifiers: [],
    unitType: 2,
    createdDate: new Date(),
  },
];

describe('NoticeApiService', () => {
  let service: NoticeApiService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        NoticeApiService,
        { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: 'apiBaseUrl' },
      ],
    });
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(NoticeApiService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it(
    'should get notices',
    waitForAsync(() => {
      service
        .getNotices({ page: 0, pageSize: 10 })
        .subscribe((x) => expect(x).toEqual(mockNotices));

      const request = httpTestingController.expectOne(
        'apiBaseUrl/itl.notices.list?page=0&pageSize=10&sortDirection=ASC&sortField=notificationIdentifier'
      );
      expect(request.request.method).toEqual('GET');
      request.flush(mockNotices);
    })
  );

  it(
    'should get notice by identifier',
    waitForAsync(() => {
      service
        .getNoticeByIdentifier('1')
        .subscribe((x) => expect(x).toEqual(mockNotices));

      const request = httpTestingController.expectOne(
        'apiBaseUrl/itl.notices.get?notificationIdentity=1'
      );
      expect(request.request.method).toEqual('GET');
      request.flush(mockNotices);
    })
  );
});
