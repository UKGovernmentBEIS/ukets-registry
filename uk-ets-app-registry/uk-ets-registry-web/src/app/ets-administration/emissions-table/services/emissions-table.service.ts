import {
  HttpClient,
  HttpEvent,
  HttpParams,
  HttpResponse,
} from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { Observable } from 'rxjs';
import { EmissionsTableRequest } from '@emissions-table/model';

@Injectable({
  providedIn: 'root',
})
export class EmissionsTableService {
  uploadEmissionsTableApi: string;
  submitEmissionsTableApi: string;
  downloadEmissionsTableErrorsApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.uploadEmissionsTableApi = `${ukEtsRegistryApiBaseUrl}/emissions-table.upload`;
    this.submitEmissionsTableApi = `${ukEtsRegistryApiBaseUrl}/emissions-table.submit`;
    this.downloadEmissionsTableErrorsApi = `${ukEtsRegistryApiBaseUrl}/emissions-table.get.errors`;
  }

  public uploadSelectedEmissionsTable(
    emissionsTableFile: File
  ): Observable<HttpEvent<any>> {
    const fData = new FormData();
    fData.append('file', emissionsTableFile, emissionsTableFile.name);
    return this.http.post<HttpEvent<any>>(
      `${this.uploadEmissionsTableApi}`,
      fData,
      {
        observe: 'events',
        reportProgress: true,
      }
    ) as Observable<HttpEvent<any>>;
  }

  public submitSelectedEmissionsTable(
    emissionsTableRequest: EmissionsTableRequest
  ): Observable<string> {
    return this.http.post<string>(
      `${this.submitEmissionsTableApi}`,
      emissionsTableRequest
    );
  }

  downloadErrorsCSV(fileId: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.downloadEmissionsTableErrorsApi}`, {
      observe: 'response',
      responseType: 'blob',
      params: new HttpParams().append('fileId', fileId.toString()),
    });
  }
}
