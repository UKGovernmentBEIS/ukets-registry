import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '@shared/user';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../app.tokens';
import { DocumentsRequest } from '@shared/model/request-documents/documents-request';
import { AuthorisedRepresentativeService } from '@account-opening/authorised-representative/authorised-representative.service';

@Injectable({
  providedIn: 'root',
})
export class RequestDocumentsService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private authorisedRepresentativeService: AuthorisedRepresentativeService,
    private httpClient: HttpClient
  ) {}

  getAuthorisedRepresentatives(
    accountHolderIdentifier: number
  ): Observable<User[]> {
    return this.authorisedRepresentativeService.getAuthorisedRepresentatives(
      accountHolderIdentifier,
      false
    );
  }

  submitRequest(documentsRequest: DocumentsRequest): Observable<string> {
    return this.httpClient.post<string>(
      `${this.ukEtsRegistryApiBaseUrl}/documents-request.submit`,
      documentsRequest
    );
  }
}
