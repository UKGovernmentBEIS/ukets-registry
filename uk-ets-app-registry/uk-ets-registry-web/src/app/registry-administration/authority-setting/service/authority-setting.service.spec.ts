import { AuthoritySettingService } from '@authority-setting/service';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { removeUserFromAuthorityUsers } from '@authority-setting/action';

describe('Authority setting services', () => {
  const uridParam = 'UK213213';
  let service: AuthoritySettingService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;
  let expectedFetchEnrolledUserServiceUrl;
  let expectedSetAsAuthorityUserServiceUrl;
  let expectedRemoveUserFromAuthorityUsersUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthoritySettingService,
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl'
        }
      ]
    });

    service = TestBed.inject(AuthoritySettingService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
    expectedFetchEnrolledUserServiceUrl = `${baseApiUrl}/admin/users.get.enrolled`;
    expectedSetAsAuthorityUserServiceUrl = `${baseApiUrl}/admin/users.add.authority`;
    expectedRemoveUserFromAuthorityUsersUrl = `${baseApiUrl}/admin/users.remove.authority`;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get the enrolled user by calling the expected url and passing the expected urid', () => {
    service.fetchEnrolledUser(uridParam).subscribe();
    const req = httpMock.expectOne(r =>
      r.url.startsWith(expectedFetchEnrolledUserServiceUrl)
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.url).toBe(expectedFetchEnrolledUserServiceUrl);
    expect(req.request.params.get('urid')).toBe(uridParam);

    req.flush(uridParam);
    httpMock.verify();
  });

  it('setUserAsAuthority should post the urid param to the expected url', () => {
    service.setUserAsAuthority(uridParam).subscribe();
    const req = httpMock.expectOne(r =>
      r.url.startsWith(expectedSetAsAuthorityUserServiceUrl)
    );
    expect(req.request.method).toBe('POST');
    expect(req.request.url).toBe(expectedSetAsAuthorityUserServiceUrl);
    expect(req.request.params.get('urid')).toBe(uridParam);

    req.flush(uridParam);
    httpMock.verify();
  });

  it('removeUserFromAuthorityUsers should post the urid param to the expected url', () => {
    service.removeUserFromAuthorityUsers(uridParam).subscribe();
    const req = httpMock.expectOne(r =>
      r.url.startsWith(expectedRemoveUserFromAuthorityUsersUrl)
    );
    expect(req.request.method).toBe('POST');
    expect(req.request.url).toBe(expectedRemoveUserFromAuthorityUsersUrl);
    expect(req.request.params.get('urid')).toBe(uridParam);

    req.flush(uridParam);
    httpMock.verify();
  });
});
