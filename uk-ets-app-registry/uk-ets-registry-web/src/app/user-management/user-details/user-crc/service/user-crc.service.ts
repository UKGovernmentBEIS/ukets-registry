import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { Observable } from 'rxjs';
import { UserCrcUpdateRequest } from '@user-crc/model';

@Injectable({
  providedIn: 'root',
})
export class UserCrcService {
  updateUserCrc: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.updateUserCrc = `${ukEtsRegistryApiBaseUrl}/admin/users.update.crc`;
  }

  submitRequest(
    urid: string,
    userCrcUpdateRequest: UserCrcUpdateRequest
  ): Observable<string> {
    const param = {
      params: new HttpParams().set('urid', urid),
    };
    return this.http.patch<string>(
      `${this.updateUserCrc}`,
      userCrcUpdateRequest,
      param
    );
  }
}
