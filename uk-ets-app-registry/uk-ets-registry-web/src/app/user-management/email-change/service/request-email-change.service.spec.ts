import { TestBed } from '@angular/core/testing';

import { RequestEmailChangeService } from './request-email-change.service';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../app.tokens';
import {
  EmailChange,
  EmailChangeRequest
} from '@email-change/model/email-change.model';

describe('RequestEmailChangeService', () => {
  const newEmailParam = 'newEmail';
  const uridParam = 'urid';
  let service: RequestEmailChangeService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;
  let expectedRequestEmailChangeUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        RequestEmailChangeService,
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl'
        }
      ]
    });
    service = TestBed.inject(RequestEmailChangeService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
    expectedRequestEmailChangeUrl = `${baseApiUrl}/user-profile.update.email`;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should post the new email and otp code', () => {
    const request: EmailChangeRequest = {
      newEmail: 'test@test.test',
      otp: '12321'
    };
    service.changeEmail(request).subscribe();
    const req = httpMock.expectOne(r =>
      r.url.startsWith(expectedRequestEmailChangeUrl)
    );
    expect(req.request.url).toBe(expectedRequestEmailChangeUrl);
    expect(req.request.method).toEqual('POST');
    expect(req.request.params.get('otp')).toBe(request.otp);
    const expectedBody = {
      newEmail: request.newEmail
    };
    expect(req.request.body).toEqual(expectedBody);

    req.flush(request);
    httpMock.verify();
  });
});
