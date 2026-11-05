import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { AccountContactChangeInvitationStatusDTO } from '@shared/model/account';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ChangeContactInvitationService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {}

  changeInvitationStatusOfAccountContacts(
    identifier: number | string,
    changeInvitationStatusDTO: AccountContactChangeInvitationStatusDTO
  ): Observable<string> {
    const params = new HttpParams().set('identifier', identifier.toString());

    return this.http.post(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.change.contacts.invitation.status`,
      changeInvitationStatusDTO,
      { params, responseType: 'text' }
    );
  }
}
