import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../../app.tokens';
import { OperatorEmissionsExclusionStatus } from '../model';
import { AccountComplianceService } from '../../account-details/services/account-compliance.service';

@Injectable()
export class ExclusionStatusUpdateWizardService {
  constructor(
    private http: HttpClient,
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private accountComplianceService: AccountComplianceService
  ) {}

  getEmissions(compliantEntityId: number) {
    return this.accountComplianceService.fetchAccountVerifiedEmissions(
      compliantEntityId
    );
  }

  submitUpdate(
    accountIdentifier: string,
    patch: OperatorEmissionsExclusionStatus
  ): Observable<string> {
    const param = {
      params: new HttpParams().set('accountIdentifier', accountIdentifier),
    };
    return this.http.patch<string>(
      `${this.ukEtsRegistryApiBaseUrl}/compliance.update.exclusion-status`,
      patch,
      param
    );
  }
}
