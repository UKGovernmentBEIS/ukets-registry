import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EnrolledUser } from '@authority-setting/model';

@Injectable()
export class AuthoritySettingService {
  fetchEnrolledUserServiceUrl: string;
  setUserAsAuthorityServiceUrl: string;
  removeUserFromAuthorityUsersServiceUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.fetchEnrolledUserServiceUrl = `${ukEtsRegistryApiBaseUrl}/admin/users.get.enrolled`;
    this.setUserAsAuthorityServiceUrl = `${ukEtsRegistryApiBaseUrl}/admin/users.add.authority`;
    this.removeUserFromAuthorityUsersServiceUrl = `${ukEtsRegistryApiBaseUrl}/admin/users.remove.authority`;
  }

  fetchEnrolledUser(urid: string): Observable<EnrolledUser> {
    return this.http.get<EnrolledUser>(this.fetchEnrolledUserServiceUrl, {
      params: new HttpParams().set('urid', urid)
    });
  }

  setUserAsAuthority(urid: string): Observable<any> {
    return this.http.post(this.setUserAsAuthorityServiceUrl, null, {
      params: new HttpParams().set('urid', urid)
    });
  }

  removeUserFromAuthorityUsers(urid: string): Observable<any> {
    return this.http.post(this.removeUserFromAuthorityUsersServiceUrl, null, {
      params: new HttpParams().set('urid', urid)
    });
  }
}
