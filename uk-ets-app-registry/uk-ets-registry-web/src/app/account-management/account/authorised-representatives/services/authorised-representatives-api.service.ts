import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthorisedRepresentative } from '@shared/model/account';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../../app.tokens';
import { ArUpdateRequest } from '@authorised-representatives/model/ar-update-request';

@Injectable()
export class AuthorisedRepresentativesApiService {
  constructor(
    private http: HttpClient,
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string
  ) {}

  public getAuthorisedRepresentatives(
    accountId: string,
    updateType: AuthorisedRepresentativesUpdateType
  ): Observable<AuthorisedRepresentative[]> {
    let params = new HttpParams();
    params = params.set('accountId', accountId);
    switch (updateType) {
      case AuthorisedRepresentativesUpdateType.REPLACE:
      case AuthorisedRepresentativesUpdateType.REMOVE:
      case AuthorisedRepresentativesUpdateType.SUSPEND:
        params = params.set('state', 'ACTIVE');
        break;
      case AuthorisedRepresentativesUpdateType.RESTORE:
        params = params.set('state', 'SUSPENDED');
    }
    return this.http.get<AuthorisedRepresentative[]>(
      `${this.ukEtsRegistryApiBaseUrl}/authorised-representatives.get.by-account`,
      {
        params,
      }
    );
  }

  getOtherAuthorisedRepresentativesOtherAccounts(
    accountId: string
  ): Observable<AuthorisedRepresentative[]> {
    return this.http.get<AuthorisedRepresentative[]>(
      `${this.ukEtsRegistryApiBaseUrl}/authorised-representatives.get.other-accounts-ars`,
      { params: { accountId } }
    );
  }

  getAuthorisedRepresentativeCandidate(
    accountId: string,
    urid: string
  ): Observable<AuthorisedRepresentative> {
    return this.http.get<AuthorisedRepresentative>(
      `${this.ukEtsRegistryApiBaseUrl}/authorised-representatives.get.candidate`,
      { params: { accountId, urid } }
    );
  }

  submitUpdateRequest(
    updateType: AuthorisedRepresentativesUpdateType,
    accountId: string,
    arUpdateRequest: ArUpdateRequest
  ): Observable<string> {
    let action: string;
    switch (updateType) {
      case AuthorisedRepresentativesUpdateType.CHANGE_ACCESS_RIGHTS:
        action = 'change.access-rights';
        break;
      default:
        action = updateType.toString().toLowerCase();
    }
    return this.http.post<string>(
      `${this.ukEtsRegistryApiBaseUrl}/authorised-representatives.${action}`,
      arUpdateRequest,
      { params: { accountId } }
    );
  }
}
