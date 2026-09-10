import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { Store } from '@ngrx/store';
import { filter, switchMap, take, tap } from 'rxjs';
import { selectUserAgentLoaded, UserAgentActions } from '@user-agent/store';
import { selectUserDetails } from '@user-management/user-details/store/reducers';
import { AgentType } from '@user-management/user-details/model';

export const initUserAgentDetailsResolver: ResolveFn<boolean> = (
  route,
  state
) => {
  const store = inject(Store);

  return store.select(selectUserDetails).pipe(
    take(1),
    tap((user) => {
      store.dispatch(
        UserAgentActions.loadUserAgentDetails({
          agentInfo: {
            urid: user.attributes.urid[0],
            type: user.attributes.agent[0] as AgentType,
            details: {
              companyName: user.attributes.agentCompanyName?.[0],
              contactEmailAddress: user.attributes.agentEmailAddress?.[0],
              phone: {
                countryCode: user.attributes.agentCountryCode?.[0],
                phoneNumber: user.attributes.agentPhoneNumber?.[0],
              },
            },
          },
          caller: {
            route: `/user-details/${user.attributes.urid[0]}`,
          },
        })
      );
    }),
    switchMap(() =>
      store.select(selectUserAgentLoaded).pipe(
        filter((loaded) => loaded),
        take(1)
      )
    )
  );
};
