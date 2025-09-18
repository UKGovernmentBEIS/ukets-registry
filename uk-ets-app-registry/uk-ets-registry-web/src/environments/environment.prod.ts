import { KeycloakOptions } from 'keycloak-angular';
import { KeycloakConfig, KeycloakInitOptions } from 'keycloak-js';

// Add here your keycloak setup infos
const keycloakConfig: KeycloakConfig = {
  url: '/auth',
  realm: 'uk-ets',
  clientId: 'uk-ets-web-app',
};

const keycloakInitOptions: KeycloakInitOptions = {
  onLoad: 'check-sso',
  enableLogging: false,
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
  production: true,
  userRegistrationServiceUrl: '/api-registration',
  pwnedPasswordsApiUrl: 'https://api.pwnedpasswords.com/range',
  ukEtsRegistryApiBaseUrl: '/api-registry',
  ukEtsReportsApiBaseUrl: 'api-reports',
  ukEtsSigningApiBaseUrl: '/api-signing',
  ukEtsPasswordValidationApiBaseUrl: '/api-password-validate',
  ukEtsUILogsApiBaseUrl: '/api-ui-logs',
  ukEtsPublicationApiBaseUrl: '/api-publication',
  keycloakOptions,
  gtag: 'https://www.googletagmanager.com/gtag/js?id=',
  documentGuidePath: './assets/documentation/',
  documentGuideFilename: 'UK Registry Document Guide v4.0.pdf',
  documentSurrenderGuideFilename:
    'Surrender Guidance - UK ETS Registry - v1.0.pdf',
  guidanceVideoUrl: 'https://www.youtube.com/embed/clq2fdw6dCA',
};
