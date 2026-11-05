import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AutoClaimChangeService {
  changeAutoSendInvitationStatusURL: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.changeAutoSendInvitationStatusURL = `${ukEtsRegistryApiBaseUrl}/accounts.change.auto.send.invitation.status`;
  }

  submitRequest(accountIdentifier: string): Observable<void> {
    const param = {
      params: new HttpParams().set('identifier', accountIdentifier),
    };
    return this.http.post<void>(
      `${this.changeAutoSendInvitationStatusURL}`,
      accountIdentifier,
      param
    );
  }
}
