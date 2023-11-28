import { APP_BASE_HREF } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { CountryNamePipe } from '@registry-web/shared/pipes';
import { RequestType } from '@registry-web/task-management/model';
import { taskDetailsBase } from '@registry-web/task-management/model/task-details.model.spec';
import { UserDeactivationDetails } from '@registry-web/user-management/user-details/model/user-deactivation-details';
import { UserDeactivationTaskDetailsComponent } from './user-deactivation-task-details.component';

describe('UserDeactivationTaskDetailsComponent', () => {
  let component: UserDeactivationTaskDetailsComponent;
  let fixture: ComponentFixture<UserDeactivationTaskDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [UserDeactivationTaskDetailsComponent],
        providers: [
          { provide: APP_BASE_HREF, useValue: '/' },
          CountryNamePipe,
          provideMockStore(),
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDeactivationTaskDetailsComponent);
    component = fixture.componentInstance;
    component.userDeactivationTaskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.USER_DEACTIVATION_REQUEST,
      changed: {
        userDetails: {
          username: 'test@test.com',
          email: 'test@test.com',
          attributes: {
            urid: ['UK192997298621'],
            state: ['DEACTIVATION_PENDING'],
          },
        },
        enrolmentKeyDetails: {
          urid: 'UK192997298621',
          enrolmentKey: 'XXXX-XXXX-XXXX-XXXX-XXXX',
          enrolmentKeyDateCreated: null,
          enrolmentKeyDateExpired: null,
        },
        deactivationComment: 'test comment',
      } as UserDeactivationDetails,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
