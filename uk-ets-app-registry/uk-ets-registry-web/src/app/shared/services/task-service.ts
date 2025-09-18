import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import {
  HttpClient,
  HttpHeaders,
  HttpParams,
  HttpResponse,
} from '@angular/common/http';
import {
  BulkActionSuccess,
  BulkActionSuccessResponse,
  CompleteTaskInfo,
  Task,
  TaskCompleteResponse,
  TaskDetails,
  TaskFileDownloadInfo,
  TaskOutcome,
  TaskSearchCriteria,
  TaskUpdateDetails,
} from '@task-management/model';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Observable } from 'rxjs';
import { PagedResults, search } from '@shared/search/util/search-service.util';
import { map } from 'rxjs/operators';
import { DomainEvent } from '@shared/model/event';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  searchTasksApiUrl: string;
  taskApiUrl: string;
  fetchTask: string;
  updateTask: string;
  completeTask: string;
  claimTasksApiUrl: string;
  assignTasksApiUrl: string;
  taskHistoryApiUrl: string;
  getTemplateFile: string;
  getRequestedDocument: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.searchTasksApiUrl = `${ukEtsRegistryApiBaseUrl}/tasks.list`;
    this.taskApiUrl = `${ukEtsRegistryApiBaseUrl}`;
    this.fetchTask = `${ukEtsRegistryApiBaseUrl}/tasks.get`;
    this.updateTask = `${ukEtsRegistryApiBaseUrl}/tasks.update`;
    this.completeTask = `${ukEtsRegistryApiBaseUrl}/tasks.complete`;
    this.claimTasksApiUrl = `${ukEtsRegistryApiBaseUrl}/tasks.claim`;
    this.assignTasksApiUrl = `${ukEtsRegistryApiBaseUrl}/tasks.assign`;
    this.taskHistoryApiUrl = `${ukEtsRegistryApiBaseUrl}/tasks.get.history`;
    this.getTemplateFile = `${ukEtsRegistryApiBaseUrl}/tasks.get.template-file`;
    this.getRequestedDocument = `${ukEtsRegistryApiBaseUrl}/tasks.get.task-file`;
  }

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
      http: this.http,
      isReport,
    });
  }

  claim(requestIds: string[], comment: string): Observable<BulkActionSuccess> {
    let params = new HttpParams().set('requestIds', requestIds.join(','));
    if (comment && comment.trim().length > 0) {
      params = params.set('comment', comment);
    }
    return this.http
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
    return this.http
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
    return this.http.get<DomainEvent[]>(`${this.taskHistoryApiUrl}`, {
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
    return this.http.post<{ requestId: string }>(
      `${this.taskHistoryApiUrl}`,
      params
    );
  }

  fetchOneTask(taskId: string): Observable<TaskDetails> {
    const params = new HttpParams().set('requestId', taskId);
    return this.http.get<TaskDetails>(this.fetchTask, {
      params,
    });
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
    return this.http.post<TaskDetails>(
      `${this.updateTask}`,
      taskUpdateDetails.taskDetails,
      {
        params,
      }
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
    return this.http.post<TaskCompleteResponse>(`${this.completeTask}`, params);
  }

  fetchTaskRelatedFile(fileId: number): Observable<HttpResponse<Blob>> {
    const headers = new HttpHeaders({
      responseType: 'blob',
    });
    let params = new HttpParams();
    params = params.append('fileId', fileId.toString());
    return this.http.get(`${this.getTemplateFile}`, {
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
    return this.http.get(`${this.getRequestedDocument}`, {
      headers,
      observe: 'response',
      responseType: 'blob',
      params,
    });
  }
}
