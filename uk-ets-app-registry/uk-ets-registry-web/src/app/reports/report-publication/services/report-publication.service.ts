import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  HttpClient,
  HttpEvent,
  HttpParams,
  HttpResponse,
} from '@angular/common/http';
import { UK_ETS_PUBLICATION_API_BASE_URL } from '@registry-web/app.tokens';
import {
  DisplayType,
  PublicationHistory,
  Section,
  SectionType,
} from '@report-publication/model';
import { FileBase } from '@shared/model/file';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import { fillSearchParams } from '@registry-web/shared/search/util/search-service.util';

@Injectable({
  providedIn: 'root',
})
export class ReportPublicationService {
  uploadPublicationReportApi: string;
  submitPublicationReportApi: string;

  constructor(
    @Inject(UK_ETS_PUBLICATION_API_BASE_URL)
    private ukEtsReportPublicationBaseUrl: string,
    private http: HttpClient
  ) {
    this.uploadPublicationReportApi = `${ukEtsReportPublicationBaseUrl}/sections.upload-file`;
    this.submitPublicationReportApi = `${ukEtsReportPublicationBaseUrl}/sections.submit-file`;
  }

  loadReportPublicationSections(
    sectionType: SectionType
  ): Observable<Section[]> {
    let params = new HttpParams();
    params = params.append('sectionType', sectionType);
    return this.http.get<Section[]>(
      `${this.ukEtsReportPublicationBaseUrl}/sections.list`,
      { params }
    );
  }

  getReportPublicationSection(id): Observable<Section> {
    let params = new HttpParams();
    params = params.append('id', id);
    return this.http.get<Section>(
      `${this.ukEtsReportPublicationBaseUrl}/sections.get`,
      { params }
    );
  }

  loadPublicationHistory(
    id: string,
    sortParams: SortParameters
  ): Observable<PublicationHistory[]> {
    let params = new HttpParams();
    params = params.append('id', id);
    params = fillSearchParams(params, sortParams);
    return this.http.get<PublicationHistory[]>(
      `${this.ukEtsReportPublicationBaseUrl}/sections.list-files`,
      { params }
    );
  }

  unpublishFile(file: PublicationHistory): Observable<any> {
    return this.http.post<any>(
      `${this.ukEtsReportPublicationBaseUrl}/sections.unpublish-file`,
      file
    );
  }

  downloadFile(id): Observable<HttpResponse<Blob>> {
    return this.http.get(
      `${this.ukEtsReportPublicationBaseUrl}/sections.download-file`,
      {
        observe: 'response',
        responseType: 'blob',
        params: new HttpParams().append('id', id),
      }
    );
  }

  public submitUpdatedPublicationDetails(
    updatedPublicationDetails
  ): Observable<any> {
    return this.http.post<any>(
      `${this.ukEtsReportPublicationBaseUrl}/sections.update`,
      updatedPublicationDetails
    );
  }

  public uploadSelectedPublicationReportFile(
    publicationReportFile: File,
    displayType: DisplayType
  ): Observable<HttpEvent<any>> {
    const fData = new FormData();
    fData.append('file', publicationReportFile, publicationReportFile.name);
    fData.append('displayType', displayType);
    return this.http.post<HttpEvent<any>>(
      `${this.uploadPublicationReportApi}`,
      fData,
      {
        observe: 'events',
        reportProgress: true,
      }
    ) as Observable<HttpEvent<any>>;
  }

  public submitSelectedPublicationReportFile(
    fileHeader: FileBase
  ): Observable<string> {
    return this.http.post<string>(
      `${this.submitPublicationReportApi}`,
      fileHeader
    );
  }
}
