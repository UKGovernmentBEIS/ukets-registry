import { InjectionToken } from '@angular/core';

export const USER_REGISTRATION_SERVICE_URL = new InjectionToken<string>(
  'USER_REGISTRATION_SERVICE_URL'
);

export const PWNED_PASSWORDS_API_URL = new InjectionToken<string>(
  'PWNED_PASSWORDS_API_URL'
);

export const UK_ETS_REGISTRY_API_BASE_URL = new InjectionToken<string>(
  'UK_ETS_REGISTRY_API_BASE_URL'
);

export const UK_ETS_SIGNING_API_BASE_URL = new InjectionToken<string>(
  'UK_ETS_SIGNING_API_BASE_URL'
);

export const UK_ETS_PASSWORD_VALIDATION_API_BASE_URL =
  new InjectionToken<string>('UK_ETS_PASSWORD_VALIDATION_API_BASE_URL');

export const UK_ETS_REPORTS_API_BASE_URL = new InjectionToken<string>(
  'UK_ETS_REPORTS_API_BASE_URL'
);

export const UK_ETS_UI_LOGS_API_BASE_URL = new InjectionToken<string>(
  'UK_ETS_UI_LOGS_API_BASE_URL'
);

export const UK_ETS_PUBLICATION_API_BASE_URL = new InjectionToken<string>(
  'UK_ETS_PUBLICATION_API_BASE_URL'
);
