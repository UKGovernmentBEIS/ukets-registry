import {
  HttpClient,
  HttpHeaders,
  HttpParams,
  HttpResponse,
} from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import { KeycloakUser } from '@shared/user/keycloak-user';
import {
  UserStatusActionOption,
  UserStatusChangedResult,
  UserStatusRequest,
} from '@user-management/model';
import { ArInAccount, EnrolmentKey } from '@user-management/user-details/model';
import { DomainEvent } from '@shared/model/event';
import { FileDetails } from '@shared/model/file/file-details.model';
import { KeycloakService } from 'keycloak-angular';
import { TaskService } from '@shared/services/task-service';
import {
  REQUEST_TYPES_CAUSING_USER_SUSPENSION,
  RequestType,
  TaskSearchCriteria,
} from '@task-management/model';
import { UserUpdateDetailsType } from '@user-update/model';

@Injectable({
  providedIn: 'root',
})
export class UserDetailService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient,
    private readonly keycloak: KeycloakService,
    private taskService: TaskService
  ) {}

  getUserDetail(urid: string): Observable<KeycloakUser> {
    const options = { params: new HttpParams().set('urid', urid) };
    return this.httpClient.get<KeycloakUser>(
      `${this.ukEtsRegistryApiBaseUrl}/admin/users.get`,
      options
    );
  }

  fetchOpenUserDetailsTask(type: UserUpdateDetailsType, urid: string) {
    let requestType = null;
    if (type == UserUpdateDetailsType.UPDATE_USER_DETAILS) {
      requestType = RequestType.USER_DETAILS_UPDATE_REQUEST;
    } else if (type == UserUpdateDetailsType.DEACTIVATE_USER) {
      requestType = RequestType.USER_DEACTIVATION_REQUEST;
    }
    const criteria = {
      urid: urid,
      taskStatus: 'OPEN',
      taskType: requestType,
    } as TaskSearchCriteria;
    const pageParams = {
      page: 0,
      pageSize: 1,
    };
    const sortParams = {
      sortDirection: 'DESC',
      sortField: 'createdOn',
    };
    return this.taskService.search(criteria, pageParams, sortParams);
  }

  validateUserUpdateRequest(
    type: UserUpdateDetailsType,
    urid: string
  ): Observable<void> {
    const options = {
      params: new HttpParams()
        .set('urid', urid)
        .set('userDetailsUpdateType', type),
    };
    return this.httpClient.get<void>(
      `${this.ukEtsRegistryApiBaseUrl}/admin/users.validateUserUpdateRequest`,
      options
    );
  }

  getAllowedUserStatusActions(
    urid: string
  ): Observable<UserStatusActionOption[]> {
    const options = { params: new HttpParams().set('urid', urid) };
    return this.httpClient.get<UserStatusActionOption[]>(
      `${this.ukEtsRegistryApiBaseUrl}/admin/users.get.statuses`,
      options
    );
  }

  changeUserStatus(
    request: UserStatusRequest
  ): Observable<UserStatusChangedResult> {
    return this.httpClient.patch<UserStatusChangedResult>(
      `${this.ukEtsRegistryApiBaseUrl}/admin/users.update.status`,
      {
        urid: request.urid,
        userStatus: request.status,
        comment: request.comment,
      }
    );
  }

  getArsInAccount(urid: string): Observable<ArInAccount[]> {
    return this.httpClient.get<ArInAccount[]>(
      `${this.ukEtsRegistryApiBaseUrl}/authorised-representatives.get.by-user/` +
        urid
    );
  }

  getUserFiles(urid: string): Observable<FileDetails[]> {
    const options = { params: new HttpParams().set('urid', urid) };
    return this.httpClient.get<FileDetails[]>(
      `${this.ukEtsRegistryApiBaseUrl}/users.get.files`,
      options
    );
  }

  getUserFile(fileId: number): Observable<HttpResponse<Blob>> {
    const headers = new HttpHeaders({
      responseType: 'blob',
    });
    let params = new HttpParams();
    params = params.append('fileId', fileId.toString());
    return this.httpClient.get(
      `${this.ukEtsRegistryApiBaseUrl}/users.get.file`,
      {
        headers,
        observe: 'response',
        responseType: 'blob',
        params,
      }
    );
  }

  getUserHistory(urid: string): Observable<DomainEvent[]> {
    const options = { params: new HttpParams().set('urid', urid) };
    return this.httpClient.get<DomainEvent[]>(
      `${this.ukEtsRegistryApiBaseUrl}/users.get.history`,
      options
    );
  }

  getEnrolmentKeyDetails(urid: string): Observable<EnrolmentKey> {
    const options = { params: new HttpParams().set('urid', urid) };
    return this.httpClient.get<EnrolmentKey>(
      `${this.ukEtsRegistryApiBaseUrl}/users.get.enrolment-key`,
      options
    );
  }

  /**
   * Returns the refresh token timeout in minutes which is equal to Keycloak ssoSessionIdleTimeout setting.
   * The value is calculated by subtracting 'issued at' time from 'expiration at' time
   */
  getSessionIdleTimeout(): number {
    const issuedAt =
      this.keycloak.getKeycloakInstance().refreshTokenParsed?.iat;
    const expAt = this.keycloak.getKeycloakInstance().refreshTokenParsed?.exp;

    if (issuedAt && expAt) {
      return (expAt - issuedAt) / 60;
    } else {
      return null;
    }
  }

  checkIfUserSuspendedByTheSystem(urid: string): Observable<boolean> {
    const criteria = {
      urid: urid,
      taskStatus: 'OPEN',
    } as TaskSearchCriteria;
    const pageParams = {
      page: 0,
      pageSize: 1,
    };
    const sortParams = {
      sortDirection: 'DESC',
      sortField: 'createdOn',
    };
    return this.taskService.search(criteria, pageParams, sortParams).pipe(
      map((results) => {
        return results?.items.some((task) =>
          Object.values(REQUEST_TYPES_CAUSING_USER_SUSPENSION).some(
            (val) => val === task.taskType
          )
        );
      })
    );
  }
}
