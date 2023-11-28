import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../app.tokens';
import { HttpClient, HttpEvent, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RequestedDocumentsModel } from '@task-management/model';

@Injectable({
  providedIn: 'root',
})
export class SubmitDocumentService {
  uploadDocumentApi: string;
  deleteDocumentApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.uploadDocumentApi = `${ukEtsRegistryApiBaseUrl}/requested-document.upload`;
    this.deleteDocumentApi = `${ukEtsRegistryApiBaseUrl}/requested-document.delete`;
  }

  public uploadSelectedDocument(
    documentFile: File,
    documentName: string,
    task: any,
    fileId: number,
    totalFileUploads: RequestedDocumentsModel[]
  ): Observable<HttpEvent<any>> {
    const fData = new FormData();
    if (documentFile) {
      fData.append('file', documentFile, documentFile.name);
      fData.append('documentName', documentName);
    }
    if (task.userUrid) {
      fData.append('userUrid', task.userUrid);
    }
    if (task.accountHolderIdentifier) {
      fData.append('accountHolderIdentifier', task.accountHolderIdentifier);
    }
    if (task.requestId) {
      fData.append('taskRequestId', task.requestId);
    }
    if (fileId) {
      fData.append('fileId', fileId.toString());
    }
    if (totalFileUploads) {
      fData.append(
        'totalFileUploads',
        totalFileUploads.map((r) => r.id).toString()
      );
    }
    return this.http.post<HttpEvent<any>>(`${this.uploadDocumentApi}`, fData, {
      observe: 'events',
      reportProgress: true,
    }) as Observable<HttpEvent<any>>;
  }

  public deleteSelectedDocument(
    fileId: number,
    taskRequestId: string,
    totalFileUploads: RequestedDocumentsModel[],
    userUrid?: string,
    accountHolderIdentifier?: string
  ): Observable<any> {
    let params = new HttpParams();
    params = params.append('fileId', fileId);
    params = params.append('taskRequestId', taskRequestId);
    params = params.append(
      'totalFileUploads',
      totalFileUploads.map((t) => t.id).join(',')
    );
    if (userUrid) {
      params = params.append('userUrid', userUrid);
    }
    if (accountHolderIdentifier) {
      params = params.append(
        'accountHolderIdentifier',
        accountHolderIdentifier
      );
    }
    return this.http.delete(`${this.deleteDocumentApi}`, { params });
  }
}
