import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BulkClaimAccountService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {}

  public countEligibleBulkClaimAccounts(): Observable<number> {
    return this.http.get<number>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.claim.bulk.eligible.count`
    );
  }

  public sendBulkAccountClaims(): Observable<void> {
    return this.http.post<void>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.claim.bulk.send`,
      {}
    );
  }
}
