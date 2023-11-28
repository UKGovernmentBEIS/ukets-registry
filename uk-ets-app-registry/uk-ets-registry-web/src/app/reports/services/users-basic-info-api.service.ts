import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { HttpClient } from '@angular/common/http';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { User } from '@shared/user';

@Injectable({
  providedIn: 'root',
})
export class UsersBasicInfoApiService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {}

  loadReportUsers(urids: string[]): Observable<User[]> {
    return this.http.post<User[]>(
      `${this.ukEtsRegistryApiBaseUrl}/users.get.basic-info`,
      urids
    );
  }
}
