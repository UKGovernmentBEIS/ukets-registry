import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../app.tokens';

@Injectable({
  providedIn: 'root',
})
export class RegistryActivationService {
  enrolmentApiUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.enrolmentApiUrl = `${this.ukEtsRegistryApiBaseUrl}/users.enrol`;
  }

  enrolUser(enrolmentKey: string) {
    const params: HttpParams = new HttpParams().append(
      'enrolmentKey',
      enrolmentKey
    );
    return this.http.put(this.enrolmentApiUrl, null, {
      params,
    });
  }

  submitNewRegistryActivationCodeRequest() {
    return this.http.post<string>(
      `${this.ukEtsRegistryApiBaseUrl}/users.request.registry-code`,
      {}
    );
  }
}
