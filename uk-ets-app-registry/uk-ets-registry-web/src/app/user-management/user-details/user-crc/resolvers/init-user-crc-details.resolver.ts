import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { Store } from '@ngrx/store';
import { selectUserDetails } from '@user-management/user-details/store/reducers';
import { filter, switchMap, take, tap } from 'rxjs';
import { selectUserCrcLoaded, UserCrcActions } from '@user-crc/store';
import { fromISOString, toBoolean } from '@user-crc/model';

export const initUserCrcDetailsResolver: ResolveFn<boolean> = (
  route,
  state
) => {
  const store = inject(Store);

  return store.select(selectUserDetails).pipe(
    take(1),
    tap((user) => {
      store.dispatch(
        UserCrcActions.loadUserCrcDetails({
          crcInfo: {
            urid: user.attributes.urid[0],
            crc: toBoolean(user.attributes.crc[0]),
            details: {
              crcIssuanceDate: fromISOString(
                user.attributes.crcIssuanceDate?.[0]
              ),
            },
          },
          caller: {
            route: `/user-details/${user.attributes.urid[0]}`,
          },
        })
      );
    }),
    switchMap(() =>
      store.select(selectUserCrcLoaded).pipe(
        filter((loaded) => loaded),
        take(1)
      )
    )
  );
};
