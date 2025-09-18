import { KeycloakOptions } from 'keycloak-angular';
import { KeycloakConfig, KeycloakInitOptions } from 'keycloak-js';

// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

// Add here your keycloak setup infos
// Since v8.0.1 KeycloakConfig is imported from keycloak-js
const keycloakConfig: KeycloakConfig = {
  url: 'http://localhost:8091/auth',
  realm: 'uk-ets',
  clientId: 'uk-ets-web-app',
};

const keycloakInitOptions: KeycloakInitOptions = {
  onLoad: 'check-sso',
  enableLogging: true,
  pkceMethod: 'S256',
};

const keycloakOptions: KeycloakOptions = {
  config: keycloakConfig,
  initOptions: keycloakInitOptions,
  enableBearerInterceptor: true,
  loadUserProfileAtStartUp: true,
  bearerExcludedUrls: [
    '/assets',
    '/clients/public',
    '/api-registration',
    { url: '/api-registry/users.create', httpMethods: ['POST'] },
    'https://api.pwnedpasswords.com/range/',
    '/api-password-validate',
  ],
};

export const environment = {
  production: false,
  userRegistrationServiceUrl: 'http://localhost:8092/api-registration',
  pwnedPasswordsApiUrl: 'https://api.pwnedpasswords.com/range',
  ukEtsRegistryApiBaseUrl: 'http://localhost:8080/api-registry',
  ukEtsReportsApiBaseUrl: 'http://localhost:8097/api-reports',
  ukEtsSigningApiBaseUrl: 'http://localhost:8595/api-signing',
  ukEtsUILogsApiBaseUrl: 'http://localhost:9097/api-ui-logs',
  ukEtsPublicationApiBaseUrl: 'http://localhost:9099/api-publication',
  ukEtsPasswordValidationApiBaseUrl:
    'http://localhost:8096/api-password-validate',
  keycloakOptions,
  gtag: 'https://www.googletagmanager.com/gtag/js?id=',
  documentGuidePath: './assets/documentation/',
  documentGuideFilename: 'UK Registry Document Guide v4.0.pdf',
  documentSurrenderGuideFilename:
    'Surrender Guidance - UK ETS Registry - v1.0.pdf',
  guidanceVideoUrl: 'https://www.youtube.com/embed/clq2fdw6dCA',
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
