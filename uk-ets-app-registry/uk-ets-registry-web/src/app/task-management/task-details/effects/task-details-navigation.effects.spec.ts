import { Observable, from } from 'rxjs';
import { TaskDetailsNavigationEffects } from './task-details-navigation.effects';
import { Action } from '@ngrx/store';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import {
  selectHasUploadedFiles,
  selectTask,
} from '../reducers/task-details.selector';
import { selectLoggedInUser } from '@registry-web/auth/auth.selector';
import { RequestType } from '@registry-web/task-management/model';
import { TaskDetailsState } from '../reducers/task-details.reducer';
import { ROUTER_NAVIGATED } from '@ngrx/router-store';
import { TaskDetailsNavigationActions } from '../actions';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';

describe('TaskDetailsNavigationEffects', () => {
  let effects: TaskDetailsNavigationEffects;
  let store: MockStore<TaskDetailsState>;

  let actions$ = new Observable<Action>();

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        TaskDetailsNavigationEffects,
        provideMockStore({
          selectors: [
            {
              selector: selectTask,
              value: {
                taskType: RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,
                taskStatus: 'OPEN',
                claimantURID: 1,
              },
            },
            { selector: selectHasUploadedFiles, value: true },
            { selector: selectLoggedInUser, value: { urid: 1 } },
          ],
        }),
        provideMockActions(() => actions$),
        { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: '' },
      ],
    }).compileComponents();
    effects = TestBed.inject(TaskDetailsNavigationEffects);
    store = TestBed.inject(MockStore);
  });

  it('should create', () => {
    expect(effects).toBeTruthy();
  });

  it('should navigate to warning after incomplete task with file uploads', () => {
    actions$ = from([
      {
        type: ROUTER_NAVIGATED,
        payload: {
          routerState: {
            url: '/task-details',
          },
        },
      },
      {
        type: ROUTER_NAVIGATED,
        payload: {
          routerState: {
            url: '/dashboard',
          },
        },
      },
    ]);
    effects.navigateAway$.subscribe((res) => {
      expect(res).toEqual(TaskDetailsNavigationActions.navigationAwayTargetURL);
    });
  });
});
