import { TestBed } from '@angular/core/testing';

import { NoticeDetailsGuard } from './notice-details-guard.service';
import { RouterTestingModule } from '@angular/router/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { selectNoticeByIndex } from '@kp-administration/store';
import { Notice } from '@kp-administration/itl-notices/model';
import { RegistryDbEnvirnomentalActivityEnum } from '../../../../../e2e/src/util/test-data/registry-db/registry-level/model/types/registry-db-envirnomental-activity.enum';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
  UrlSegment,
} from '@angular/router';
import { take } from 'rxjs/operators';

const mockNotification: Notice[] = [
  {
    createdDate: new Date(),
    id: 1,
    receivedOn: new Date(),
    lastUpdateOn: new Date(),
    actionDueDate: new Date(),
    commitPeriod: 1,
    content: 'Test content',
    lulucfactivity: RegistryDbEnvirnomentalActivityEnum.REVEGETATION,
    messageDate: new Date(),
    status: 'TRANSACTION_PROPOSAL_PENDING',
    type: 'COMMITMENT_PERIOD_RESERVE',
    notificationIdentifier: 1,
    projectNumber: '1',
    targetDate: new Date(),
    targetValue: 1,
    unitBlockIdentifiers: [],
    unitType: 2,
  },
];

describe('NoticeDetailsGuard', () => {
  let guard: NoticeDetailsGuard;
  let store: MockStore;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('0', null)];
  const routerStateSnapshot = {
    url: '/kp-administration/itl-notices/13/0',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        NoticeDetailsGuard,
        provideMockStore({
          initialState: {
            notification: undefined,
          },
        }),
      ],
    });
    guard = TestBed.inject(NoticeDetailsGuard);
    store = TestBed.inject(MockStore);
    router = TestBed.inject(Router);
  });

  afterEach(() => store.resetSelectors());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should navigate away if notifications undefined', async () => {
    const navigateSpy = jest.spyOn(router, 'parseUrl').mockImplementation();

    await guard
      .canActivate(activatedRouteSnapshot, routerStateSnapshot)
      .pipe(take(1))
      .toPromise();

    expect(navigateSpy).toHaveBeenCalledWith(
      '/kp-administration/itl-notices/13'
    );
  });

  it('should navigate if notifications not undefined', async () => {
    store.overrideSelector(selectNoticeByIndex, mockNotification);
    const navigateSpy = jest.spyOn(router, 'parseUrl').mockImplementation();

    await guard
      .canActivate(activatedRouteSnapshot, routerStateSnapshot)
      .pipe(take(1))
      .toPromise();

    expect(navigateSpy).not.toHaveBeenCalledWith();
  });
});
