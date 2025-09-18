import { AfterViewInit, Component, HostListener, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { fromEvent, Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { isAuthenticated } from '@registry-web/auth/auth.selector';
import { KeycloakService } from 'keycloak-angular';
import { environment } from '../environments/environment';
import { selectIsTimeoutDialogVisible } from '@registry-web/timeout/store/timeout.selectors';
import { LogsFactoryService } from '@generate-logs/services/logs.factory.service';
import {
  Interaction,
  receivedUserAction,
} from '@generate-logs/actions/generate-logs.actions';
import { initAll } from 'govuk-frontend';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit, AfterViewInit {
  isAuthenticated$: Observable<boolean>;
  isTimeoutDialogVisible$: Observable<boolean> = this.store.select(
    selectIsTimeoutDialogVisible
  );

  noPadding: boolean;

  constructor(
    private router: Router,
    private store: Store,
    private keycloakService: KeycloakService,
    private logsfactoryService: LogsFactoryService
  ) {
    initAll();
  }

  ngOnInit(): void {
    this.isAuthenticated$ = this.store.select(isAuthenticated);

    // Dispatch a user interaction for log purposes
    fromEvent(document, 'click').subscribe((event: MouseEvent) => {
      const interaction: Interaction =
        this.logsfactoryService.createInteractionObj(event);
      this.store.dispatch(receivedUserAction({ interaction }));
    });
  }

  ngAfterViewInit(): void {
    this.router.events
      .pipe(filter((e) => e instanceof NavigationEnd))
      .subscribe((e: NavigationEnd) => {
        window.scrollTo(0, 0);
        // we need to style the parent element only when we are in the dashboard or about page/route
        this.noPadding = e.url === '/dashboard' || e.url === '/about';
      });
  }

  @HostListener('window:beforeunload', ['$event'])
  onBeforeUnload(): void {
    if (environment.production) {
      fetch(
        this.keycloakService.getKeycloakInstance().authServerUrl +
          '/realms/' +
          this.keycloakService.getKeycloakInstance().realm +
          '/protocol/openid-connect/logout?post_logout_redirect_uri=' +
          location.origin +
          '/dashboard',
        {
          keepalive: true,
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
          body:
            'client_id=' +
            this.keycloakService.getKeycloakInstance().clientId +
            '&refresh_token=' +
            this.keycloakService.getKeycloakInstance().refreshToken,
        }
      );
    }
  }
}
