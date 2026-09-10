import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';
import { selectUrid } from '@registry-web/auth/auth.selector';

import { initUserDetailsResolver } from './init-user-details.resolver';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import {
  areUserDetailsLoaded,
  selectUserDetails,
} from '@user-management/user-details/store/reducers';
import { UserUpdateDetailsType } from '../user-details-update-wizard/model';
import {
  fetchAUserDetailsUpdatePendingApproval,
  prepareNavigationToUserDetails,
} from '@user-management/user-details/store/actions';

describe('initUserDetailsResolver', () => {
  let store: MockStore;

  const executeResolver: ResolveFn<boolean> = (...resolverParameters) =>
    TestBed.runInInjectionContext(() =>
      initUserDetailsResolver(...resolverParameters)
    );

  const mockRoute: any = {
    paramMap: {
      get: jasmine.createSpy().and.returnValue('123'),
    },
  };

  const mockState: any = {};

  const mockUserDetails = {
    attributes: {
      urid: ['123'],
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideMockStore({
          selectors: [
            { selector: selectUserDetails, value: mockUserDetails },
            { selector: selectUrid, value: '999' },
            { selector: areUserDetailsLoaded, value: true },
          ],
        }),
      ],
    });

    store = TestBed.inject(MockStore);
  });

  test('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });

  test('should dispatch fetch and prepare actions with correct URID', (done) => {
    const dispatchSpy = spyOn(store, 'dispatch');
    TestBed.runInInjectionContext(() => {
      executeResolver(mockRoute, mockState).subscribe((result) => {
        expect(result).toBeTruthy();

        expect(dispatchSpy).toHaveBeenCalledWith(
          fetchAUserDetailsUpdatePendingApproval({
            updateType: UserUpdateDetailsType.UPDATE_USER_DETAILS,
            urid: '123',
          })
        );

        expect(dispatchSpy).toHaveBeenCalledWith(
          prepareNavigationToUserDetails({
            urid: '123',
            backRoute: null,
          })
        );

        done();
      });
    });
  });

  test('should resolve only when userDetailsLoaded is true', (done) => {
    store.overrideSelector(areUserDetailsLoaded, true);
    TestBed.runInInjectionContext(() => {
      executeResolver(mockRoute, mockState).subscribe((result) => {
        expect(result).toBeTruthy();
        done();
      });
    });
  });

  test('should wait until userDetails.urid matches expectedUrid', (done) => {
    const dispatchSpy = spyOn(store, 'dispatch');

    store.overrideSelector(selectUserDetails, {
      attributes: { urid: ['123'] },
    });

    executeResolver(mockRoute, mockState).subscribe((result) => {
      expect(result).toBeTruthy();
      expect(dispatchSpy).toHaveBeenCalled();
      done();
    });
  });
});
