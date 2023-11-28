import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthorisedRepresentative } from '@shared/model/account';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../app.tokens';
import { User } from '@shared/user';

@Injectable({
  providedIn: 'root',
})
export class AuthorisedRepresentativeService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  getAuthorisedRepresentatives(
    accountHolderId: string | number = '',
    includeCurrentUser = true
  ): Observable<User[]> {
    //remove null parameters
    const params = Object.entries({
      accountHolderId,
      includeCurrentUser,
    }).reduce(
      (acc, [key, val]) => {
        if (!(val === null || val === undefined || val === '')) {
          acc[key] = val;
        }
        return acc;
      },
      {}
    );
    return this.httpClient.get<User[]>(
      `${this.ukEtsRegistryApiBaseUrl}/users.get.authorised-representatives`,
      { params }
    );
  }

  getAuthorisedRepresentative(
    urid: string
  ): Observable<AuthorisedRepresentative> {
    const options = {
      params: new HttpParams().set('urid', urid),
    };
    return this.httpClient.get<AuthorisedRepresentative>(
      `${this.ukEtsRegistryApiBaseUrl}/authorised-representatives.get.candidate`,
      options
    );
  }
}
