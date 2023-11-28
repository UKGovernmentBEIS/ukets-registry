import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {UK_ETS_REGISTRY_API_BASE_URL, USER_REGISTRATION_SERVICE_URL} from '../../app.tokens';
import { Configuration } from './configuration.interface';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ConfigurationService {
  constructor(
    private http: HttpClient,
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    @Inject(USER_REGISTRATION_SERVICE_URL)
    private ukEtsRegistrationApiBaseUrl: string,
  ) {}

  getConfigurationRegistry(): Observable<Configuration[]> {
    return this.getConfiguration(`${this.ukEtsRegistryApiBaseUrl}/configuration`);
  }

  getConfigurationRegistration(): Observable<Configuration[]> {
    return this.getConfiguration(`${this.ukEtsRegistrationApiBaseUrl}/configuration`);
  }

  getConfiguration(url: string): Observable<Configuration[]> {
    return this.http
      .get<Configuration>(url)
      .pipe(
        map((response) => {
          const properties: Configuration[] = [];
          for (const [key, value] of Object.entries(response)) {
            properties.push({ [key]: value });
          }
          return properties;
        })
      );
  }
}
