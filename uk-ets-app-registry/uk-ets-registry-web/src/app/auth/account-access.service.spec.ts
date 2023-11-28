import { TestBed, waitForAsync } from '@angular/core/testing';
import { AccountAccessService } from '@registry-web/auth/account-access.service';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { MockAuthApiService } from '../../testing/mock-auth-api-service';
import { ARAccessRights } from '@shared/model/account';
import { of } from 'rxjs';

describe('AccountAccessService', () => {
  let service: AccountAccessService;
  let authApiService: AuthApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AccountAccessService,
        { provide: AuthApiService, useClass: MockAuthApiService },
      ],
    });
    service = TestBed.inject(AccountAccessService);
    authApiService = TestBed.inject(AuthApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it(
    'should return false if empty identifier',
    waitForAsync(() => {
      service
        .isArWithAccessRight('', ARAccessRights.APPROVE)
        .subscribe((x) => expect(x).toBeFalsy());
    })
  );

  it('should return true if initiate and approve', () => {
    const accountAccess = {
      accountFullIdentifier: 'test',
      accessRight: ARAccessRights.INITIATE_AND_APPROVE,
    };
    expect(
      service.containsAccessRight(
        accountAccess,
        ARAccessRights.INITIATE_AND_APPROVE
      )
    ).toBeTruthy();
  });

  it('should test initiate access rights', () => {
    const accountAccess = {
      accountFullIdentifier: 'test',
      accessRight: ARAccessRights.INITIATE,
    };

    expect(
      service.containsAccessRight(
        accountAccess,
        ARAccessRights.INITIATE_AND_APPROVE
      )
    ).toBeFalsy();

    expect(
      service.containsAccessRight(accountAccess, ARAccessRights.APPROVE)
    ).toBeFalsy();

    expect(
      service.containsAccessRight(accountAccess, ARAccessRights.READ_ONLY)
    ).toBeTruthy();
  });

  it('should return false if no account access', () => {
    expect(
      service.containsAccessRight(null, ARAccessRights.APPROVE)
    ).toBeFalsy();
  });

  it('should test read only access right', () => {
    const accountAccess = {
      accountFullIdentifier: 'test',
      accessRight: ARAccessRights.READ_ONLY,
    };

    expect(
      service.containsAccessRight(accountAccess, ARAccessRights.READ_ONLY)
    ).toBeTruthy();

    expect(
      service.containsAccessRight(accountAccess, ARAccessRights.INITIATE)
    ).toBeFalsy();
  });

  it('should test approve access right', () => {
    const accountAccess = {
      accountFullIdentifier: 'test',
      accessRight: ARAccessRights.APPROVE,
    };

    expect(
      service.containsAccessRight(accountAccess, ARAccessRights.INITIATE)
    ).toBeFalsy();

    expect(
      service.containsAccessRight(
        accountAccess,
        ARAccessRights.INITIATE_AND_APPROVE
      )
    ).toBeFalsy();

    expect(
      service.containsAccessRight(accountAccess, ARAccessRights.READ_ONLY)
    ).toBeTruthy();
  });

  it(
    'should return true if initiate and approve',
    waitForAsync(() => {
      const accountAccess = {
        accountFullIdentifier: 'test',
        accessRight: ARAccessRights.INITIATE_AND_APPROVE,
      };
      jest
        .spyOn(authApiService, 'retrieveAccessRights')
        .mockReturnValue(of([accountAccess]));

      service
        .isArWithAccessRight('test', ARAccessRights.INITIATE_AND_APPROVE)
        .subscribe((x) => expect(x).toBeTruthy());
    })
  );
});
