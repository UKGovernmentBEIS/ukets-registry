import { Injectable } from '@angular/core';
import { NoticeApiService } from '@kp-administration/itl-notices/service/notice-api.service';
import { PageParameters } from '@shared/search/paginator';
import { Observable, of } from 'rxjs';
import { PagedResults } from '@shared/search/util/search-service.util';
import { Notice } from '@kp-administration/itl-notices/model/notice';
import { RegistryDbEnvirnomentalActivityEnum } from '../../e2e/src/util/test-data/registry-db/registry-level/model/types/registry-db-envirnomental-activity.enum';
import { SortParameters } from '@shared/search/sort/SortParameters';

@Injectable()
export class NoticeApiServiceStub implements Partial<NoticeApiService> {
  getNotices(
    pageParams: PageParameters,
    sortParams: SortParameters = {
      sortDirection: 'ASC',
      sortField: 'notificationIdentifier',
    }
  ): Observable<PagedResults<Notice>> {
    if (pageParams.page === 0) {
      return of({
        totalResults: 15,
        items: this.getMockNotices().slice(0, 10),
      });
    } else {
      return of({
        totalResults: 15,
        items: this.getMockNotices().slice(10),
      });
    }
  }

  getNoticeByIdentifier(notificationIdentifier: string): Observable<Notice[]> {
    return of(this.getMockNotices().slice(3));
  }

  getMockNotices(): Notice[] {
    const mockNotifications = new Array<Notice>();
    for (let i = 0; i < 15; i++) {
      mockNotifications.push({
        createdDate: new Date(),
        id: i,
        receivedOn: new Date(),
        lastUpdateOn: new Date(),
        actionDueDate: new Date(),
        commitPeriod: 1,
        content: 'Test content',
        lulucfactivity: RegistryDbEnvirnomentalActivityEnum.REVEGETATION,
        messageDate: new Date(),
        status: 'TRANSACTION_PROPOSAL_PENDING',
        type: 'COMMITMENT_PERIOD_RESERVE',
        notificationIdentifier: i,
        projectNumber: '1',
        targetDate: new Date(),
        targetValue: 1,
        unitBlockIdentifiers: [],
        unitType: 2,
      });
    }
    return mockNotifications;
  }
}
