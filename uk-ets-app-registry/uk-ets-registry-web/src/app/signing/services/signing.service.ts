import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs';
import { UK_ETS_SIGNING_API_BASE_URL } from '../../app.tokens';
import { SignatureInfo, SigningRequestInfo } from '@signing/model';

@Injectable({
  providedIn: 'root',
})
export class SigningService {
  constructor(
    @Inject(UK_ETS_SIGNING_API_BASE_URL)
    private ukEtsSigningApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  signData(
    signingRequestInfo: SigningRequestInfo,
    signingEnabled: string
  ): Observable<SignatureInfo> {
    if (signingEnabled === 'true') {
      return this.httpClient.post<SignatureInfo>(
        `${this.ukEtsSigningApiBaseUrl}/sign`,
        signingRequestInfo
      );
    } else {
      return this.httpClient.get<SignatureInfo>('/assets/mock-signature.json');
    }
  }
}
