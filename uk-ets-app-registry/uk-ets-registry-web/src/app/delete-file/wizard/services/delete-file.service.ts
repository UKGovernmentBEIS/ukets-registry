import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { DocumentsRequestType } from '@registry-web/shared/model/request-documents/documents-request-type';
import { Observable } from 'rxjs';

@Injectable()
export class DeleteFileService {
  constructor(
    private http: HttpClient,
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string
  ) {}

  submitDeleteFile(
    id: string,
    fileId: string,
    documentsRequestType: DocumentsRequestType
  ): Observable<any> {
    let params = new HttpParams();
    params = params.append('id', id);
    params = params.append('fileId', fileId);
    const param = { params };
    if (documentsRequestType === DocumentsRequestType.USER) {
      return this.http.delete(
        `${this.ukEtsRegistryApiBaseUrl}/users.delete.file`,
        param
      );
    } else if (documentsRequestType === DocumentsRequestType.ACCOUNT_HOLDER) {
      return this.http.delete(
        `${this.ukEtsRegistryApiBaseUrl}/account-holder.delete.file`,
        param
      );
    }
  }
}
