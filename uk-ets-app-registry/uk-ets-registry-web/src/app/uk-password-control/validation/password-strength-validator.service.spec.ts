import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed, waitForAsync } from '@angular/core/testing';
import { UK_ETS_PASSWORD_VALIDATION_API_BASE_URL } from '@registry-web/app.tokens';
import { PasswordStrengthResponse } from '@uk-password-control/model';

import { PasswordStrengthValidatorService } from '@uk-password-control/validation';

describe('PasswordStrengthValidatorService', () => {
  let service: PasswordStrengthValidatorService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        {
          provide: UK_ETS_PASSWORD_VALIDATION_API_BASE_URL,
          useValue: 'ukEtsPasswordValidationApiBaseUrl',
        },
        PasswordStrengthValidatorService,
      ],
    });
    service = TestBed.inject(PasswordStrengthValidatorService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_PASSWORD_VALIDATION_API_BASE_URL);
  });

  afterEach(() => {
    // After every test, assert that there are no more pending requests.
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('#getPasswordStrength', () => {
    let expectedResponse: PasswordStrengthResponse;
    // passwd: glqwerty12345
    const password = 'glqwerty12345';

    beforeEach(() => {
      service = TestBed.inject(PasswordStrengthValidatorService);
      expectedResponse = { score: 1 };
    });

    it(
      'should succesfully report a weak passwd',
      waitForAsync(() => {
        service
          .getPasswordStrength({ password })
          .subscribe((res) => expect(res).toEqual(expectedResponse));

        const req = httpMock.expectOne((r) =>
          r.url.startsWith(`${baseApiUrl}`)
        );
        expect(req.request.url).toBe(`${baseApiUrl}/strength.calculate`);
        expect(req.request.method).toEqual('POST');
        expect(req.request.body).toEqual({ password });
        req.flush(expectedResponse);
      })
    );

    // This service reports the error but finds a way to let the app keep going.
    it(
      'should turn 400 into an 0 strength result',
      waitForAsync(() => {
        const mockedResponse = { score: 0 };
        service
          .getPasswordStrength({ password })
          .subscribe((res) => expect(res).toEqual(mockedResponse), fail);

        const req = httpMock.expectOne((r) =>
          r.url.startsWith(`${baseApiUrl}`)
        );

        expect(req.request.url).toBe(`${baseApiUrl}/strength.calculate`);
        expect(req.request.method).toEqual('POST');
        expect(req.request.body).toEqual({ password });
        // respond with a 400 and the error message in the body
        const msg = 'deliberate 400 error';
        req.flush(msg, { status: 400, statusText: 'Server error' });
      })
    );

    it(
      'can test for network error',
      waitForAsync(() => {
        const mockedResponse = { score: 0 };
        service
          .getPasswordStrength({ password })
          .subscribe((res) => expect(res).toEqual(mockedResponse), fail);

        const req = httpMock.expectOne((r) =>
          r.url.startsWith(`${baseApiUrl}`)
        );

        // Create mock ErrorEvent, raised when something goes wrong at the network level.
        // Connection timeout, DNS error, offline, etc
        const emsg = 'simulated network error';
        const mockError = new ErrorEvent('Network error', {
          message: emsg,
        });

        // Respond with mock error
        req.error(mockError);
      })
    );
  });
});
