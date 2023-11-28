import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Operator } from '@shared/model/account';
import { OperatorUpdate } from '@operator-update/model/operator-update';
import { OperatorService } from '@shared/services/operator-service';

@Injectable()
export class OperatorUpdateApiService {
  getOperatorInfo: string;
  updateOperatorInfo: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient,
    private operatorService: OperatorService
  ) {
    this.getOperatorInfo = `${ukEtsRegistryApiBaseUrl}/accounts.get.operator`;
    this.updateOperatorInfo = `${ukEtsRegistryApiBaseUrl}/accounts-operator.update-details`;
  }

  public fetchAccountOperatorInfo(accountId: string): Observable<Operator> {
    const param = { params: new HttpParams().set('accountId', accountId) };
    return this.http.get<Operator>(`${this.getOperatorInfo}`, param);
  }

  submitRequest(
    accountIdentifier: string,
    operatorUpdate: OperatorUpdate,
    currentOperatorInfo: Operator
  ): Observable<string> {
    let params = new HttpParams();
    params = params.set('accountIdentifier', accountIdentifier);
    return this.http.post<string>(
      `${this.updateOperatorInfo}`,
      { current: currentOperatorInfo, diff: operatorUpdate },
      { params }
    );
  }

  fetchExistsMonitoringPlanId(monitoringPlanIdentifier: string, current) {
    if (monitoringPlanIdentifier == current) {
      return of(true);
    }
    return this.operatorService.fetchExistsMonitoringPlanId(
      monitoringPlanIdentifier
    );
  }

  fetchExistsInstallationPermitId(
    installationPermitIdentifier: string,
    current
  ) {
    if (installationPermitIdentifier == current) {
      return of(true);
    }
    return this.operatorService.fetchExistsInstallationPermitId(
      installationPermitIdentifier
    );
  }
}
