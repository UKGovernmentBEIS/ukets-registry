import { TestBed } from '@angular/core/testing';
import { UserAgentService } from './user-agent.service';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

describe('UserAgentService', () => {
  let service: UserAgentService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserAgentService,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl',
        },
      ],
    });

    service = TestBed.inject(UserAgentService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
  });

  afterEach(() => {
    httpMock.verify();
  });

  test('should be created', () => {
    expect(service).toBeTruthy();
  });

  // test('should call the correct endpoint', () => {
  //   service.submitRequest().subscribe();

  //   const req = httpMock.expectOne(`${baseApiUrl}/user-agent`);
  //   expect(req.request.method).toBe('GET');

  //   req.flush({ userAgent: 'test-agent' });
  // });
});
