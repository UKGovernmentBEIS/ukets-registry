import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed, waitForAsync } from '@angular/core/testing';
import { PWNED_PASSWORDS_API_URL } from '@registry-web/app.tokens';
import { PasswordBlacklistAsyncValidator } from '@shared/validation/password-blacklist-async-validator';

describe('PasswordBlacklistAsyncValidator', () => {
  let service: PasswordBlacklistAsyncValidator;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: PWNED_PASSWORDS_API_URL, useValue: 'pwnedPasswordsApiUrl' },
        PasswordBlacklistAsyncValidator,
      ],
    });
    service = TestBed.inject(PasswordBlacklistAsyncValidator);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(PWNED_PASSWORDS_API_URL);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('#isBlacklistedPassword', () => {
    const passwd = 'qwerty';
    // hexString:b1b3773a05c0ed0176787a4f1574ff0075f7521e
    const prefix = 'b1b37';

    beforeEach(() => {
      service = TestBed.inject(PasswordBlacklistAsyncValidator);
    });

    it(
      'should succesfully report a pwned passwd list',
      waitForAsync(() => {
        const mockedResponse =
          '7237A42BF3D2ED2092E1961BB59D48F3C16:3\
      729B74FA8FEBC7BFAC459915208A590F7E9:1\
      72F5D7AFE3944D66D018E5C7EE2AF28D683:5\
      73678F196DE938F721CD408ED190330F5DB:3\
      7377BA15B8D5E12FCCBA32B074D45503D67:2\
      7387376AFD1B3DAB553D439C8A7D7CDDED1:2\
      73A05C0ED0176787A4F1574FF0075F7521E:3946737';
        service
          .isBlacklistedPassword(passwd)
          .subscribe((res) => expect(res).toEqual(true));

        const req = httpMock.expectOne((r) =>
          r.url.startsWith(`${baseApiUrl}`)
        );
        expect(req.request.url).toBe(`${baseApiUrl}/${prefix}`);
        expect(req.request.method).toEqual('GET');
        req.flush(mockedResponse);
      })
    );

    // This service reports the error but finds a way to let the app keep going.
    it(
      'should turn 400 into an false result',
      waitForAsync(() => {
        service
          .isBlacklistedPassword(passwd)
          .subscribe((res) => expect(res).toEqual(false), fail);

        const req = httpMock.expectOne((r) =>
          r.url.startsWith(`${baseApiUrl}`)
        );
        expect(req.request.url).toBe(`${baseApiUrl}/${prefix}`);
        expect(req.request.method).toEqual('GET');
        // respond with a 400 and the error message in the body
        const msg = 'deliberate 400 error';
        req.flush(msg, { status: 400, statusText: 'Server error' });
      })
    );

    it(
      'can test for network error , false response',
      waitForAsync(() => {
        service
          .isBlacklistedPassword(passwd)
          .subscribe((res) => expect(res).toEqual(false), fail);

        const req = httpMock.expectOne((r) =>
          r.url.startsWith(`${baseApiUrl}`)
        );
        expect(req.request.url).toBe(`${baseApiUrl}/${prefix}`);
        expect(req.request.method).toEqual('GET');
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
