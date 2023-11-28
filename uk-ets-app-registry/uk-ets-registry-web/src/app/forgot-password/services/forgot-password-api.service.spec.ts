import { waitForAsync, TestBed } from '@angular/core/testing';

import { ForgotPasswordApiService } from './forgot-password-api.service';
import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { ResetPasswordResponse, ValidateTokenResponse } from '../model';

describe('ResetPasswordApiService', () => {
  let service: ForgotPasswordApiService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;
  let forgotPasswordRequestPostUrl;
  let validateTokenPostUrl;
  let resetPasswordPostUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: 'apiBaseUrl' },
        ForgotPasswordApiService,
      ],
    });
    service = TestBed.inject(ForgotPasswordApiService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
    forgotPasswordRequestPostUrl = `${baseApiUrl}/forgot-password.request.link`;
    validateTokenPostUrl = `${baseApiUrl}/forgot-password.validate.token`;
    resetPasswordPostUrl = `${baseApiUrl}/forgot-password.reset.password`;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it(
    'should succesfully request a reset password email',
    waitForAsync(() => {
      const emailAddress = 'test@reset.pwd';
      const mockedResponse = {};
      service
        .requestResetPasswordEmail(emailAddress)
        .subscribe((res) => expect(res).toEqual(mockedResponse));

      const req = httpMock.expectOne((r) =>
        r.url.startsWith(forgotPasswordRequestPostUrl)
      );
      expect(req.request.url).toBe(forgotPasswordRequestPostUrl);
      expect(req.request.method).toEqual('POST');
      expect(req.request.params.get('email')).toBe(emailAddress);
      req.flush(mockedResponse);
    })
  );

  it(
    'should succesfully validate a token',
    waitForAsync(() => {
      const token =
        'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJcInp6enp6QHRlZHQuY29tXCIi' +
        'LCJhdWQiOiIgdWstZXRzLXdlYi1hcHAiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwOTEvY' +
        'XV0aCIsImV4cCI6MTYwMDYwNzAyOCwiaWF0IjoxNjAwNjAzNDI4fQ.barlXfK2rj0vhmF0F6l' +
        'DOkb3z86y8QnG-yiZItYoIW4';
      const mockedResponse: ValidateTokenResponse = {
        success: true,
      };
      service
        .validateToken(token)
        .subscribe((res) => expect(res).toEqual(mockedResponse));

      const req = httpMock.expectOne((r) =>
        r.url.startsWith(validateTokenPostUrl)
      );
      expect(req.request.url).toBe(validateTokenPostUrl);
      expect(req.request.method).toEqual('POST');
      expect(req.request.params.get('token')).toBe(token);
      req.flush(mockedResponse);
    })
  );

  it(
    'should succesfully reset a password',
    waitForAsync(() => {
      const token =
        'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJcInp6enp6QHRlZHQuY29tXCIi' +
        'LCJhdWQiOiIgdWstZXRzLXdlYi1hcHAiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwOTEvY' +
        'XV0aCIsImV4cCI6MTYwMDYwNzAyOCwiaWF0IjoxNjAwNjAzNDI4fQ.barlXfK2rj0vhmF0F6l' +
        'DOkb3z86y8QnG-yiZItYoIW4';
      const otp = '123456';
      const newPasswd = '12345678';
      const mockedResponse: ResetPasswordResponse = {
        email: 'test@reset.pwd',
        success: true,
      };
      service
        .resetPassword({ token, otp, newPasswd })
        .subscribe((res) => expect(res).toEqual(mockedResponse));

      const req = httpMock.expectOne((r) =>
        r.url.startsWith(resetPasswordPostUrl)
      );
      expect(req.request.url).toBe(resetPasswordPostUrl);
      expect(req.request.method).toEqual('POST');
      expect(req.request.body).toEqual({
        token,
        otp,
        newPasswd,
      });
      req.flush(mockedResponse);
    })
  );

  afterEach(() => httpMock.verify());
});
