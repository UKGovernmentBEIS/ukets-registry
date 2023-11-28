import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../app.tokens';
import { TaskSearchCriteria, TaskStatus } from '@task-management/model';
import { PageParameters } from '@shared/search/paginator';
import { TaskService } from '@shared/services/task-service';

class TestCriteria implements TaskSearchCriteria {
  accountNumber: string;
  accountHolder: string;
  taskStatus: TaskStatus;
  claimantName: string;
  taskType: string;
  requestId: string;
  claimedOnFrom: string;
  claimedOnTo: string;
  createdOnFrom: string;
  createdOnTo: string;
  completedOnFrom: string;
  completedOnTo: string;
  transactionId: string;
  taskOutcome: string;
  initiatorName: string;
  accountType: string;
  excludeUserTasks: boolean;
  initiatedBy: string;
  claimedBy: string;
}

class TestPageParameters implements PageParameters {
  page: number;
  pageSize: number;
}

describe('TaskListService', () => {
  let taskService: TaskService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        TaskService,
        { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: 'apiBaseUrl' },
      ],
    });
    taskService = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
  });

  test('The service should make a GET request to {apiBaseUrl}/task URI for searching tasks', () => {
    taskService
      .search(new TestCriteria(), new TestPageParameters(), {
        sortDirection: null,
        sortField: null,
      })
      .subscribe((response) => {
        console.log(response);
      });
    const req = httpMock.expectOne((request) =>
      request.url.startsWith(baseApiUrl)
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.url).toBe(`${baseApiUrl}/tasks.list`);
  });

  test('Service should not send to backend the null params', () => {
    taskService
      .search(new TestCriteria(), new TestPageParameters(), {
        sortDirection: null,
        sortField: null,
      })
      .subscribe((response) => {
        console.log(response);
      });
    const req = httpMock.expectOne((request) =>
      request.url.startsWith(baseApiUrl)
    );
    expect(req.request.params.keys().length).toBe(0);
  });

  test(`Service should not send to backend the empty params`, () => {
    const criteria = new TestCriteria();
    criteria.accountHolder = '';
    taskService
      .search(criteria, new TestPageParameters(), {
        sortDirection: null,
        sortField: null,
      })
      .subscribe((response) => {
        console.log(response);
      });
    const req = httpMock.expectOne((request) =>
      request.url.startsWith(baseApiUrl)
    );
    expect(req.request.params.keys().length).toBe(0);
  });

  test(`The non null page parameters and all the filled criteria fields should be sent to backend on search service call`, () => {
    const criteria = {
      accountNumber: 'accountNumber',
      accountHolder: 'accountHolder',
      taskStatus: 'COMPLETED' as TaskStatus,
      claimantName: 'claimantName',
      taskType: 'taskType',
      requestId: 'requestId',
      claimedOnFrom: '2019-09-12',
      claimedOnTo: '2019-09-13',
      createdOnFrom: '2019-09-14',
      createdOnTo: '2019-09-15',
      completedOnFrom: '2019-09-16',
      completedOnTo: '2019-09-17',
      transactionId: 'transaction-id',
      taskOutcome: 'taskOutcome',
      initiatorName: 'initiatorName',
      accountType: 'accountType',
      excludeUserTasks: true,
      initiatedBy: 'initiatedBy',
      claimedBy: 'claimedBy',
    };

    taskService
      .search(criteria, new TestPageParameters(), {
        sortDirection: null,
        sortField: null,
      })
      .subscribe((response) => {
        console.log(response);
      });

    const req = httpMock.expectOne((request) =>
      request.url.startsWith(baseApiUrl)
    );
    expect(req.request.params.keys().length).toBe(Object.keys(criteria).length);
    for (const field in criteria) {
      if (field) {
        expect(req.request.params.get(field)).toContain(
          String(criteria[field])
        );
      }
    }
  });

  test(`claim service should POST an array of request ids to the {apiBaseUrl}/tasks.claim URI`, () => {
    const requestIds = ['12321', '33221', '6567', '324234'];
    taskService.claim(requestIds, null).subscribe(console.log);

    const req = httpMock.expectOne((request) =>
      request.url.startsWith(baseApiUrl)
    );
    expect(req.request.url.endsWith('/tasks.claim'));
  });

  // test('should succesfully get Task History', waitForAsync(() => {
  //   const historyData: TaskHistory[] = [
  //     {
  //       requestId: '113456789',
  //       eventId: '113456789',
  //       generatedOn: '6/1/2020 11:25', // when
  //       triggeredBy: 'Leftheris Q', // who
  //       eventType: 'Account 1 Opening Request Submitted', // what
  //       comment: 'Comment for account request 1' // comments
  //     }
  //   ];
  //   service
  //     .taskHistory('111111')
  //     .subscribe(res => expect(res).toEqual(historyData));
  //   const taskHistoryRequest = httpMock.expectOne(
  //     '/assets/json/task-history.json'
  //   );
  //   taskHistoryRequest.flush(historyData);
  // }));

  // test('should return error if get Task History failed', waitForAsync(() => {
  //   const errorType = 'CANNOT_FETCH_TASK_HISTORY';
  //   service
  //     .taskHistory('111111')
  //     .subscribe(
  //       () => {},
  //       errorResponse => expect(errorResponse.error.type).toEqual(errorType)
  //     );
  //   const taskHistoryRequest = httpMock.expectOne(
  //     '/assets/json/task-history.json'
  //   );
  //   taskHistoryRequest.error(new ErrorEvent(errorType));
  // }));

  afterEach(() => {
    httpMock.verify();
  });
});
