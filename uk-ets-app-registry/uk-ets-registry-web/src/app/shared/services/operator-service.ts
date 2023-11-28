import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient } from '@angular/common/http';
import { Installation, InstallationTransfer } from '@shared/model/account';

@Injectable({
  providedIn: 'root',
})
export class OperatorService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  fetchExistsMonitoringPlanId(monitoringPlanIdentifier: string) {
    return this.httpClient.get<boolean>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.get.monitoring-plan`,
      {
        params: {
          monitoringPlanId: monitoringPlanIdentifier,
        },
      }
    );
  }

  fetchExistsInstallationPermitId(installationPermitIdentifier: string) {
    return this.httpClient.get<boolean>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.get.installation-permit-id`,
      {
        params: {
          installationPermitId: installationPermitIdentifier,
        },
      }
    );
  }

  validateInstallationTransfer(installationTransfer: InstallationTransfer) {
    return this.httpClient.post<Installation>(
      `${this.ukEtsRegistryApiBaseUrl}/account.validate.installation-transfer`,
      installationTransfer
    );
  }
}
