import { BrowserModule } from '@angular/platform-browser';
import { APP_INITIALIZER, ErrorHandler, NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { APP_BASE_HREF } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import {
  HTTP_INTERCEPTORS,
  provideHttpClient,
  withInterceptorsFromDi,
} from '@angular/common/http';
import { StorageModule } from '@ngx-pwa/local-storage';
import { environment } from 'src/environments/environment';
import {
  PWNED_PASSWORDS_API_URL,
  UK_ETS_PASSWORD_VALIDATION_API_BASE_URL,
  UK_ETS_PUBLICATION_API_BASE_URL,
  UK_ETS_REGISTRY_API_BASE_URL,
  UK_ETS_REPORTS_API_BASE_URL,
  UK_ETS_SIGNING_API_BASE_URL,
  UK_ETS_UI_LOGS_API_BASE_URL,
  USER_REGISTRATION_SERVICE_URL,
} from './app.tokens';
import { META_REDUCERS, Store, StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { metaReducers, reducers } from './reducers';
import { EffectsModule } from '@ngrx/effects';
import { HeaderComponent } from '@shared/header/header.component';
import { InlineSVGModule } from 'ng-inline-svg-2';
import { LoginGuard } from '@shared/guards';
import { AuthEffects } from './auth/auth.effect';
import { AuthApiService } from './auth/auth-api.service';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import {
  ApiErrorInterceptor,
  AuthInterceptor,
  XRequestIdInterceptor,
} from '@shared/interceptor';
import { Configuration } from '@shared/configuration/configuration.interface';
import {
  loadRegistrationConfigurationRequested,
  loadRegistryConfigurationRequested,
} from '@shared/shared.action';
import {
  MinimalRouterStateSerializer,
  StoreRouterConnectingModule,
} from '@ngrx/router-store';
import {
  SitemapComponent,
  SitemapContainerComponent,
} from '@registry-web/sitemap';
import { FooterComponent } from './footer/footer.component';
import { GlobalErrorHandlingService } from '@shared/services';
import { TimeoutModule } from '@registry-web/timeout/timeout.module';
import { FeedbackBannerComponent } from '@registry-web/feedback-banner/feedback-banner.component';
import { GuidanceContainerComponent } from '@guidance/components/guidance-container.component';
import { GuidanceModule } from '@registry-web/guidance/guidance.module';
import { GoogleAnalyticsModule } from '@google-analytics/google-analytics.module';
import { QuillModule } from 'ngx-quill';
import { GenerateLogsModule } from '@generate-logs/generate-logs.module';
import { GenerateLogsActionTypes } from '@generate-logs/actions/generate-logs.actions';
import { LogsFactoryService } from '@generate-logs/services/logs.factory.service';
import { logsFactory } from '@generate-logs/reducers/log-factory.meta-reducer';
import { UiLogInterceptor } from '@shared/interceptor/ui-log.interceptor';
import { SharedEffects } from '@shared/shared.effect';
import { XRequestTimeInterceptor } from '@shared/interceptor/x-request-time.interceptor';
import { DebounceClickInterceptor } from '@shared/interceptor/debounce-click.interceptor';
import { DebounceClickService } from '@shared/debounce-click.service';
import { TitleStrategy } from '@angular/router';
import { ExtendedTitleStrategyService } from '@shared/services/extended-title-strategy.service';
import { ReactiveFormsModule } from '@angular/forms';

const keycloakService = new KeycloakService();

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    SitemapContainerComponent,
    SitemapComponent,
    FooterComponent,
    FeedbackBannerComponent,
    GuidanceContainerComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    KeycloakAngularModule,
    InlineSVGModule.forRoot(),
    SharedModule,
    TimeoutModule,
    StorageModule.forRoot({ IDBNoWrap: true }),
    EffectsModule.forRoot([AuthEffects, SharedEffects]),
    StoreModule.forRoot(reducers, {
      // eslint-disable-next-line ngrx/no-reducer-in-key-names
      metaReducers,
      runtimeChecks: {
        strictStateImmutability: true,
        strictActionImmutability: true,
        // TODO enable this, right now causes errors like Detected unserializable state (e.g. see UKETS-809).
        // strictStateSerializability: true
        // TODO: enable this serializer, right now we have a non serializable action
        // strictActionSerializability: true,
      },
    }),
    StoreDevtoolsModule.instrument({
      maxAge: 50, // Retains last 50 states
      logOnly: environment.production, // Restrict extension to log-only mode
      actionsBlocklist: [...Object.values(GenerateLogsActionTypes)], // Block all actions related to generate logs module
      connectInZone: true,
    }),
    StoreRouterConnectingModule.forRoot({
      serializer: MinimalRouterStateSerializer,
    }),
    GuidanceModule,
    GoogleAnalyticsModule,
    QuillModule.forRoot(),
    GenerateLogsModule,
    ReactiveFormsModule,
  ],
  providers: [
    { provide: APP_BASE_HREF, useValue: '/' },
    {
      provide: USER_REGISTRATION_SERVICE_URL,
      useValue: environment.userRegistrationServiceUrl,
    },
    {
      provide: UK_ETS_REGISTRY_API_BASE_URL,
      useValue: environment.ukEtsRegistryApiBaseUrl,
    },
    {
      provide: UK_ETS_SIGNING_API_BASE_URL,
      useValue: environment.ukEtsSigningApiBaseUrl,
    },
    {
      provide: PWNED_PASSWORDS_API_URL,
      useValue: environment.pwnedPasswordsApiUrl,
    },
    {
      provide: UK_ETS_PASSWORD_VALIDATION_API_BASE_URL,
      useValue: environment.ukEtsPasswordValidationApiBaseUrl,
    },
    {
      provide: UK_ETS_SIGNING_API_BASE_URL,
      useValue: environment.ukEtsSigningApiBaseUrl,
    },
    {
      provide: UK_ETS_REPORTS_API_BASE_URL,
      useValue: environment.ukEtsReportsApiBaseUrl,
    },
    {
      provide: UK_ETS_UI_LOGS_API_BASE_URL,
      useValue: environment.ukEtsUILogsApiBaseUrl,
    },
    {
      provide: UK_ETS_PUBLICATION_API_BASE_URL,
      useValue: environment.ukEtsPublicationApiBaseUrl,
    },
    AuthApiService,
    DebounceClickService,
    {
      provide: KeycloakService,
      useValue: keycloakService,
    },
    LoginGuard,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: XRequestIdInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: XRequestTimeInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ApiErrorInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: UiLogInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: DebounceClickInterceptor,
      multi: true,
    },
    {
      provide: APP_INITIALIZER,
      useFactory: (store: Store<Configuration>) => {
        return () => {
          store.dispatch(loadRegistryConfigurationRequested());
          store.dispatch(loadRegistrationConfigurationRequested());
        };
      },
      multi: true,
      deps: [Store],
    },
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandlingService,
    },
    {
      provide: META_REDUCERS,
      deps: [LogsFactoryService],
      useFactory: logsFactory,
      multi: true,
    },
    {
      provide: TitleStrategy,
      useClass: ExtendedTitleStrategyService,
    },
    provideHttpClient(withInterceptorsFromDi()),
  ],
})
export class AppModule {
  ngDoBootstrap(app) {
    keycloakService
      .init(environment.keycloakOptions)
      .then(() => {
        app.bootstrap(AppComponent);
      })
      .catch((error) => console.error('[ngDoBootstrap] init failed', error));
  }
}
