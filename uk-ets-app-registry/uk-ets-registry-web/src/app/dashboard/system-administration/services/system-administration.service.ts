import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import { ResetDatabaseResult } from '../model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SystemAdministrationService {
  systemAdmininstrationApiUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.systemAdmininstrationApiUrl = `${ukEtsRegistryApiBaseUrl}/system-administration.reset`;
  }

  reset(): Observable<ResetDatabaseResult> {
    return this.http.post<ResetDatabaseResult>(
      this.systemAdmininstrationApiUrl,
      {}
    );
  }
}
