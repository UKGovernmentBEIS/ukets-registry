import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../app.tokens';
import {
  HttpClient,
  HttpEvent,
  HttpParams,
  HttpResponse,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { FileBase } from '@shared/model/file';

@Injectable({
  providedIn: 'root',
})
export class AllocationTableService {
  uploadAllocationTableApi: string;
  submitAllocationTableApi: string;
  downloadAllocationTableErrorsApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.uploadAllocationTableApi = `${ukEtsRegistryApiBaseUrl}/allocation-table.upload`;
    this.submitAllocationTableApi = `${ukEtsRegistryApiBaseUrl}/allocation-table.submit`;
    this.downloadAllocationTableErrorsApi = `${ukEtsRegistryApiBaseUrl}/allocation-table.get.errors`;
  }

  public uploadSelectedAllocationTable(
    allocationTableFile: File
  ): Observable<HttpEvent<any>> {
    const fData = new FormData();
    fData.append('file', allocationTableFile, allocationTableFile.name);
    return this.http.post<HttpEvent<any>>(
      `${this.uploadAllocationTableApi}`,
      fData,
      {
        observe: 'events',
        reportProgress: true,
      }
    ) as Observable<HttpEvent<any>>;
  }

  public submitSelectedAllocationTable(
    fileHeader: FileBase
  ): Observable<string> {
    return this.http.post<string>(
      `${this.submitAllocationTableApi}`,
      fileHeader
    );
  }

  downloadErrorsCSV(fileId: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.downloadAllocationTableErrorsApi}`, {
      observe: 'response',
      responseType: 'blob',
      params: new HttpParams().append('fileId', String(fileId)),
    });
  }
}
