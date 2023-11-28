import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { UK_ETS_REPORTS_API_BASE_URL } from '@registry-web/app.tokens';
import {
  Report,
  ReportCreationRequest,
  ReportCreationResponse,
  ReportRequestingRole,
  ReportType,
} from '@reports/model';

@Injectable({
  providedIn: 'root',
})
export class ReportsApiService {
  constructor(
    @Inject(UK_ETS_REPORTS_API_BASE_URL)
    private ukEtsReportsApiBaseUrl: string,
    private http: HttpClient
  ) {}

  loadReports(urid: string, role: ReportRequestingRole): Observable<Report[]> {
    let params = new HttpParams();
    params = params.append('urid', urid);
    if (role) {
      params = params.append('role', role);
    }
    return this.http.get<Report[]>(
      `${this.ukEtsReportsApiBaseUrl}/reports.list`,
      {
        params,
      }
    );
  }

  requestReport(
    request: ReportCreationRequest
  ): Observable<ReportCreationResponse> {
    return this.http.post<ReportCreationResponse>(
      `${this.ukEtsReportsApiBaseUrl}/reports.request`,
      request
    );
  }

  downloadReport(reportId: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.ukEtsReportsApiBaseUrl}/reports.download`, {
      observe: 'response',
      responseType: 'blob',
      params: new HttpParams().append('reportId', reportId.toString()),
    });
  }

  loadEligibleReportTypes(
    role: ReportRequestingRole
  ): Observable<ReportType[]> {
    return this.http.get<ReportType[]>(
      `${this.ukEtsReportsApiBaseUrl}/reports.list.eligible-types`,
      {
        params: new HttpParams().append('role', role),
      }
    );
  }
}
