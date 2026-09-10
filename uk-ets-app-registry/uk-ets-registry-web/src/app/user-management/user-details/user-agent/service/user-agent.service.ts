import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { Observable } from 'rxjs';
import { UserAgentUpdateRequest } from '@user-agent/model';

@Injectable({
  providedIn: 'root',
})
export class UserAgentService {
  updateUserAgent: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.updateUserAgent = `${ukEtsRegistryApiBaseUrl}/admin/users.update.agent`;
  }

  submitRequest(
    urid: string,
    userAgentUpdateRequest: UserAgentUpdateRequest
  ): Observable<string> {
    const param = {
      params: new HttpParams().set('urid', urid),
    };
    return this.http.patch<string>(
      `${this.updateUserAgent}`,
      userAgentUpdateRequest,
      param
    );
  }
}
