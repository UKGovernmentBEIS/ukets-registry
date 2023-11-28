import { TestBed, waitForAsync } from '@angular/core/testing';
import { AccountStatusApiService } from './account-status-api.service';
import {
  HttpTestingController,
  HttpClientTestingModule,
} from '@angular/common/http/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import { AccountStatusActionOption } from '@shared/model/account/account-status-action';

describe.skip('AccountStatusApiService', () => {
  let httpMock: HttpTestingController;
  let accountStatusApiService: AccountStatusApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: 'apiBaseUrl' },
        AccountStatusApiService,
      ],
    });
    accountStatusApiService = TestBed.inject(AccountStatusApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    const service: AccountStatusApiService = TestBed.inject(
      AccountStatusApiService
    );
    expect(service).toBeTruthy();
  });

  it(
    'should succesfully get allowed account status actions',
    waitForAsync(() => {
      const allowedAccountStatusActionData: AccountStatusActionOption[] = [
        {
          label: 'Suspend account (full)',
          hint: 'Authorised Representatives will not be able to access or view the account. The account will not be able to receive units.',
          value: 'SUSPEND',
          enabled: true,
          newStatus: 'SUSPENDED',
        },
      ];
      accountStatusApiService
        .getAllowedAccountStatusActions('zzzz')
        .subscribe((res) =>
          expect(res).toEqual(allowedAccountStatusActionData)
        );
      const allowedAccountStatusActionsRequest = httpMock.expectOne(
        'assets/json/account-status-actions.json?accountId=zzzz'
      );
      allowedAccountStatusActionsRequest.flush(allowedAccountStatusActionData);
    })
  );

  it(
    'should return error if request for allowed account status actions failed',
    waitForAsync(() => {
      const errorType = 'CANNOT_LOAD_ALLOWED_ACCOUNT_STATUS_ACTIONS';
      accountStatusApiService
        .getAllowedAccountStatusActions('zzzz')
        .subscribe(undefined, (errorResponse) =>
          expect(errorResponse.error.type).toEqual(errorType)
        );
      const allowedAccountStatusActionsRequest = httpMock.expectOne(
        'assets/json/account-status-actions.json?accountId=zzzz'
      );
      allowedAccountStatusActionsRequest.error(new ErrorEvent(errorType));
    })
  );

  afterEach(() => httpMock.verify());
});
