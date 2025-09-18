import { Observable } from 'rxjs';
import {
  RegistryDocumentCategory,
  SaveRegistryDocumenCategorytDTO,
  SaveRegistryDocumentDTO,
} from '../models/document.model';
import { Injectable, Inject } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import {
  HttpClient,
  HttpEvent,
  HttpHeaders,
  HttpResponse,
} from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class DocumentsApiService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {}

  documentCategoriesGetUrl = `${this.ukEtsRegistryApiBaseUrl}/document-categories.get`;
  documentCategoriesCreateUrl = `${this.ukEtsRegistryApiBaseUrl}/document-categories.add`;
  documentCategoriesUpdateUrl = `${this.ukEtsRegistryApiBaseUrl}/document-categories.update`;
  documentCategoriesDeleteUrl = `${this.ukEtsRegistryApiBaseUrl}/document-categories.delete`;

  documentsGetUrl = `${this.ukEtsRegistryApiBaseUrl}/document.get`;
  documentsCreateUrl = `${this.ukEtsRegistryApiBaseUrl}/document.add`;
  documentsUpdateUrl = `${this.ukEtsRegistryApiBaseUrl}/document.update`;
  documentsDeleteUrl = `${this.ukEtsRegistryApiBaseUrl}/document.delete`;

  fetchDocumentCategories(): Observable<RegistryDocumentCategory[]> {
    return this.http.get<RegistryDocumentCategory[]>(
      this.documentCategoriesGetUrl
    );
  }

  createDocumentCategory(categoryDTO: SaveRegistryDocumenCategorytDTO) {
    return this.http.post<any>(this.documentCategoriesCreateUrl, categoryDTO);
  }

  updateDocumentCategory(categoryDTO: SaveRegistryDocumenCategorytDTO) {
    return this.http.put<any>(this.documentCategoriesUpdateUrl, categoryDTO);
  }

  deleteDocumentCategory(categoryId: number) {
    return this.http.delete<any>(
      `${this.documentCategoriesDeleteUrl}/${categoryId}`
    );
  }

  fetchDocumentFile(documentId: number): Observable<HttpResponse<Blob>> {
    const headers = new HttpHeaders({
      responseType: 'blob',
    });
    return this.http.get(`${this.documentsGetUrl}/${documentId}`, {
      headers,
      observe: 'response',
      responseType: 'blob',
    });
  }

  createDocument(
    documentDTO: SaveRegistryDocumentDTO
  ): Observable<HttpEvent<any>> {
    const fData = new FormData();
    if (documentDTO.title) {
      fData.append('title', String(documentDTO.title));
    }
    if (documentDTO.file) {
      fData.append('file', documentDTO.file, documentDTO.file.name);
    }
    if (documentDTO.categoryId) {
      fData.append('categoryId', String(documentDTO.categoryId));
    }
    if (documentDTO.order) {
      fData.append('order', String(documentDTO.order));
    }

    return this.http.post<any>(this.documentsCreateUrl, fData, {
      observe: 'events',
      reportProgress: true,
    }) as Observable<HttpEvent<any>>;
  }

  updateDocument(documentDTO: SaveRegistryDocumentDTO) {
    const fData = new FormData();
    fData.append('id', String(documentDTO.id));
    fData.append('categoryId', String(documentDTO.categoryId));
    if (documentDTO.title) {
      fData.append('title', String(documentDTO.title));
    }

    if (documentDTO.file) {
      fData.append('file', documentDTO.file, documentDTO.file.name);
    }
    if (documentDTO.order) {
      fData.append('order', String(documentDTO.order));
    }

    return this.http.patch<any>(this.documentsUpdateUrl, fData, {
      observe: 'events',
      reportProgress: true,
    }) as Observable<HttpEvent<any>>;
  }

  deleteDocument(documentId: string) {
    return this.http.delete<any>(`${this.documentsDeleteUrl}/${documentId}`);
  }
}
