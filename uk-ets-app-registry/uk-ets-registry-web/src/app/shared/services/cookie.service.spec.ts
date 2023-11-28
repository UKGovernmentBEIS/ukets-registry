import { CookieService } from '@shared/services/cookie.service';
import { TestBed } from '@angular/core/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';

describe('CookieService', () => {
  let service: CookieService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CookieService,
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl',
        },
      ],
    });
    service = TestBed.inject(CookieService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return null a non-existent cookie', () => {
    const key = 'nonExistentCookieKey';
    expect(service.getCookie(key)).toBeNull();
  });

  it('should create two cookies', () => {
    const cookiesExpirationTime = 1; //days
    service.acceptAllCookies(cookiesExpirationTime);
    expect(document.cookie).toBe(
      service.PREFERENCES_SET_COOKIE +
        '=true; ' +
        service.COOKIES_POLICY +
        '={"essential":true,"usage":true}'
    );
  });

  it('should be enabled', () => {
    expect(service.checkIfCookiesEnabled()).toBeTruthy();
  });

  it('should be accepted', () => {
    expect(service.notAccepted()).toBeFalsy();
  });

  it('should return Cookies Values', () => {
    expect(service.getCookie(service.PREFERENCES_SET_COOKIE)).toBe('true');
    expect(service.getCookie(service.COOKIES_POLICY)).toBe(
      '{"essential":true,"usage":true}'
    );
  });
});
