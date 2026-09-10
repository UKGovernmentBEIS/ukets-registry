import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  areUserDetailsLoaded,
  selectUserDetails,
} from '@user-management/user-details/store/reducers';
import { filter, switchMap, take, tap, withLatestFrom } from 'rxjs/operators';
import {
  fetchAUserDetailsUpdatePendingApproval,
  prepareNavigationToUserDetails,
} from '@user-management/user-details/store/actions';
import { UserUpdateDetailsType } from '@user-management/user-details/user-details-update-wizard/model';
import { empty } from '@shared/shared.util';
import { selectUrid } from '@registry-web/auth/auth.selector';

export const initUserDetailsResolver: ResolveFn<boolean> = (route, state) => {
  const store = inject(Store);
  const urid = route.paramMap.get('urid');

  return store.select(selectUserDetails).pipe(
    withLatestFrom(store.select(selectUrid)),
    tap(([userDetails, currentUserUrid]) => {
      const effectiveUrid = empty(urid) ? currentUserUrid : urid;
      store.dispatch(
        fetchAUserDetailsUpdatePendingApproval({
          updateType: UserUpdateDetailsType.UPDATE_USER_DETAILS,
          urid: effectiveUrid,
        })
      );
      store.dispatch(
        prepareNavigationToUserDetails({
          urid: effectiveUrid,
          backRoute: null,
        })
      );
    }),
    filter(([userDetails, currentUserUrid]) => {
      const expectedUrid = empty(urid) ? currentUserUrid : urid;
      return (
        !empty(userDetails.attributes.urid) &&
        userDetails.attributes.urid[0] === expectedUrid
      );
    }),
    take(1),
    switchMap(() =>
      store.select(areUserDetailsLoaded).pipe(filter((loaded) => loaded))
    )
  );
};
