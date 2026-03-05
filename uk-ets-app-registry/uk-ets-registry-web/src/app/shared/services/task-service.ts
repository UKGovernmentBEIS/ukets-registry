import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import {
  HttpClient,
  HttpHeaders,
  HttpParams,
  HttpResponse,
} from '@angular/common/http';
import {
  CompleteTaskInfo,
  Task,
  TaskDetails,
  TaskDetailsBase,
  TaskFileDownloadInfo,
  TaskSearchCriteria,
  TaskUpdateDetails,
  TaskCompleteResponse,
} from '@shared/task-and-regulator-notice-management/model';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Observable } from 'rxjs';
import { PagedResults, search } from '@shared/search/util/search-service.util';
import { map } from 'rxjs/operators';
import { DomainEvent } from '@shared/model/event';
import {
  RegulatorNoticeSearchCriteria,
  RegulatorNoticeTask,
} from '@shared/task-and-regulator-notice-management/model';
import {
  BulkActionSuccess,
  BulkActionSuccessResponse,
} from '@shared/task-and-regulator-notice-management/bulk-actions';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private readonly searchTasksApiUrl = `${this.ukEtsRegistryApiBaseUrl}/tasks.list`;
  private readonly fetchTask = `${this.ukEtsRegistryApiBaseUrl}/tasks.get`;
  private readonly updateTask = `${this.ukEtsRegistryApiBaseUrl}/tasks.update`;
  private readonly completeTask = `${this.ukEtsRegistryApiBaseUrl}/tasks.complete`;
  private readonly claimTasksApiUrl = `${this.ukEtsRegistryApiBaseUrl}/tasks.claim`;
  private readonly assignTasksApiUrl = `${this.ukEtsRegistryApiBaseUrl}/tasks.assign`;
  private readonly taskHistoryApiUrl = `${this.ukEtsRegistryApiBaseUrl}/tasks.get.history`;
  private readonly getTemplateFile = `${this.ukEtsRegistryApiBaseUrl}/tasks.get.template-file`;
  private readonly getRequestedDocument = `${this.ukEtsRegistryApiBaseUrl}/tasks.get.task-file`;
  private readonly searchRegulatorNoticesApiUrl = `${this.ukEtsRegistryApiBaseUrl}/regulator-notices.list`;
  private readonly regulatorNoticeTypesApiUrl = `${this.ukEtsRegistryApiBaseUrl}/regulator-notices.types`;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  search(
    criteria: TaskSearchCriteria,
    pageParams: PageParameters,
    sortParams: SortParameters,
    isReport?: boolean
  ): Observable<PagedResults<Task>> {
    return search<TaskSearchCriteria, Task>({
      pageParams,
      sortParams,
      api: this.searchTasksApiUrl,
      criteria,
      http: this.httpClient,
      isReport,
    });
  }

  searchRegulatorNotices(
    criteria: RegulatorNoticeSearchCriteria,
    pageParams: PageParameters,
    sortParams: SortParameters,
    isReport?: boolean
  ): Observable<PagedResults<RegulatorNoticeTask>> {
    return search<RegulatorNoticeSearchCriteria, RegulatorNoticeTask>({
      pageParams,
      sortParams,
      api: this.searchRegulatorNoticesApiUrl,
      criteria,
      http: this.httpClient,
      isReport,
    });
  }

  claim(requestIds: string[], comment: string): Observable<BulkActionSuccess> {
    let params = new HttpParams().set('requestIds', requestIds.join(','));
    if (comment && comment.trim().length > 0) {
      params = params.set('comment', comment);
    }
    return this.httpClient
      .post<BulkActionSuccessResponse>(this.claimTasksApiUrl, params)
      .pipe(
        map((response) => {
          return {
            message: `You have successfully claimed ${response.updated} tasks.`,
          };
        })
      );
  }

  assign(
    requestIds: string[],
    comment: string,
    urid: string
  ): Observable<BulkActionSuccess> {
    let params = new HttpParams().set('requestIds', requestIds.join(','));
    if (comment && comment.trim().length > 0) {
      params = params.set('comment', comment);
    }
    if (urid) {
      params = params.set('urid', urid);
    }
    return this.httpClient
      .post<BulkActionSuccessResponse>(this.assignTasksApiUrl, params)
      .pipe(
        map((response) => {
          return {
            message: `You have successfully assigned ${response.updated} task${
              response.updated > 1 ? 's' : ''
            }.`,
          };
        })
      );
  }

  taskHistory(requestId: string): Observable<DomainEvent[]> {
    const params = new HttpParams().set('requestId', requestId);
    return this.httpClient.get<DomainEvent[]>(`${this.taskHistoryApiUrl}`, {
      params,
    });
  }

  addComment(
    requestId: string,
    comment: string
  ): Observable<{ requestId: string }> {
    let params = new HttpParams().set('requestId', requestId);
    if (comment && comment.trim().length) {
      params = params.set('comment', comment);
    }
    return this.httpClient.post<{ requestId: string }>(
      `${this.taskHistoryApiUrl}`,
      params
    );
  }

  fetchOneTask<T extends TaskDetailsBase>(taskId: string): Observable<T> {
    const params = new HttpParams().set('requestId', taskId);
    return this.httpClient.get<T>(this.fetchTask, { params });
  }

  update(taskUpdateDetails: TaskUpdateDetails): Observable<TaskDetails> {
    let params: HttpParams = new HttpParams();
    params = params.append(
      'requestId',
      taskUpdateDetails.taskDetails.requestId
    );
    if (taskUpdateDetails.taskUpdateAction) {
      params = params.append('updateInfo', taskUpdateDetails.updateInfo);
      params = params.append(
        'taskUpdateAction',
        taskUpdateDetails.taskUpdateAction
      );
    }
    return this.httpClient.post<TaskDetails>(
      `${this.updateTask}`,
      taskUpdateDetails.taskDetails,
      { params }
    );
  }

  complete(completeTaskInfo: CompleteTaskInfo) {
    let params = new HttpParams();
    params = params.append('taskOutcome', completeTaskInfo.taskOutcome);
    params = params.append('requestId', completeTaskInfo.taskId);
    if (
      completeTaskInfo.comment &&
      completeTaskInfo.comment.trim().length > 0
    ) {
      params = params.append('comment', completeTaskInfo.comment);
    }
    if (completeTaskInfo.amountPaid) {
      params = params.append('amountPaid', completeTaskInfo.amountPaid);
    }
    return this.httpClient.post<TaskCompleteResponse>(
      `${this.completeTask}`,
      params
    );
  }

  fetchTaskRelatedFile(fileId: number): Observable<HttpResponse<Blob>> {
    const headers = new HttpHeaders({
      responseType: 'blob',
    });
    let params = new HttpParams();
    params = params.append('fileId', fileId.toString());
    return this.httpClient.get(`${this.getTemplateFile}`, {
      headers,
      observe: 'response',
      responseType: 'blob',
      params,
    });
  }

  fetchRequestedFile(
    taskFileInfo: TaskFileDownloadInfo
  ): Observable<HttpResponse<Blob>> {
    const headers = new HttpHeaders({
      responseType: 'blob',
    });
    let params = new HttpParams();
    params = params.set('taskType', taskFileInfo.taskType);
    params = params.set('taskRequestId', taskFileInfo.taskRequestId);
    if (taskFileInfo.fileId) {
      params = params.set('fileId', taskFileInfo.fileId.toString());
    }
    return this.httpClient.get(`${this.getRequestedDocument}`, {
      headers,
      observe: 'response',
      responseType: 'blob',
      params,
    });
  }

  getRegulatorNoticeProcessTypesList(): Observable<string[]> {
    return this.httpClient.get<string[]>(this.regulatorNoticeTypesApiUrl);
  }
}
