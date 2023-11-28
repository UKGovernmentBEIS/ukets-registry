import { Injectable } from '@angular/core';
import { KeycloakEventType, KeycloakService } from 'keycloak-angular';
import { filter, map } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { triggerTimeoutTimer } from '@registry-web/timeout/store/timeout.actions';

@Injectable({
  providedIn: 'root',
})
export class TimeoutBannerService {
  constructor(
    private readonly keycloak: KeycloakService,
    private readonly store: Store
  ) {
    this.keycloak.keycloakEvents$
      .asObservable()
      .pipe(
        filter(
          (event) =>
            event &&
            [
              KeycloakEventType.OnTokenExpired,
              KeycloakEventType.OnAuthRefreshSuccess,
            ].includes(event.type)
        ),
        map((event) => event.type),
        map((eventType) =>
          this.store.dispatch(triggerTimeoutTimer({ eventType }))
        )
      )
      .subscribe();
  }

  getExpirationPeriod(): number {
    const refreshTokenExp = new Date(
      this.keycloak.getKeycloakInstance().refreshTokenParsed.exp * 1000
    ).getTime();
    const accessTokenExp = new Date(
      this.keycloak.getKeycloakInstance().tokenParsed.exp * 1000
    ).getTime();
    return refreshTokenExp - accessTokenExp;
  }
}
