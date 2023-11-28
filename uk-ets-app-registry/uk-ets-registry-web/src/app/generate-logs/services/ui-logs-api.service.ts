import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Log } from '@generate-logs/reducers/generate-logs.reducer';
import { UK_ETS_UI_LOGS_API_BASE_URL } from '@registry-web/app.tokens';

@Injectable({
  providedIn: 'root',
})
export class UILogsApiService {
  constructor(
    @Inject(UK_ETS_UI_LOGS_API_BASE_URL) private ukEtsUILogsApiBaseUrl: string,
    private http: HttpClient
  ) {}

  public postLogs(logs: Log[]): Observable<void> {
    return this.http.post<void>(
      `${this.ukEtsUILogsApiBaseUrl}/logs.submit`,
      logs
    );
  }
}
