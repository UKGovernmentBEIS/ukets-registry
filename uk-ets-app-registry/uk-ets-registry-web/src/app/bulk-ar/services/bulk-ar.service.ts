import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FileBase } from '@shared/model/file';

@Injectable({
  providedIn: 'root',
})
export class BulkArService {
  uploadBulkArApi: string;
  submitBulkArApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.uploadBulkArApi = `${ukEtsRegistryApiBaseUrl}/bulk-ar.upload`;
    this.submitBulkArApi = `${ukEtsRegistryApiBaseUrl}/bulk-ar.submit`;
  }

  public uploadSelectedBulkArFile(
    bulkArFile: File
  ): Observable<HttpEvent<any>> {
    const fData = new FormData();
    fData.append('file', bulkArFile, bulkArFile.name);
    return this.http.post<HttpEvent<any>>(`${this.uploadBulkArApi}`, fData, {
      observe: 'events',
      reportProgress: true,
    }) as Observable<HttpEvent<any>>;
  }

  public submitSelectedBulkArFile(fileHeader: FileBase): Observable<string> {
    return this.http.post<string>(`${this.submitBulkArApi}`, fileHeader);
  }
}
