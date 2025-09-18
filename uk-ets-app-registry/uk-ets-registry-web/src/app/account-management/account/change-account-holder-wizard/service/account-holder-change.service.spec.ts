import { TestBed } from '@angular/core/testing';

import { AccountHolderChangeService } from '@change-account-holder-wizard/service';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

describe('AccountHolderChangeService', () => {
  let service: AccountHolderChangeService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AccountHolderChangeService,
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl',
        },
      ],
    });
    service = TestBed.inject(AccountHolderChangeService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  afterEach(() => httpMock.verify());
});
