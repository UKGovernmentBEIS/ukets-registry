import { APP_BASE_HREF, AsyncPipe, CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { isSeniorAdmin } from '@registry-web/auth/auth.selector';
import { selectTimeOptions } from '@registry-web/notifications/notifications-list/reducers';
import { Notification, NotificationType } from '@registry-web/notifications/notifications-wizard/model';
import { selectNotificationId } from '@registry-web/notifications/notifications-wizard/reducers';
import { GdsDatePipe } from '@registry-web/shared/pipes';
import { canGoBack } from '@registry-web/shared/shared.action';
import { selectNotificationDetails } from '../../reducer/notification-details.selector';
import { NotificationDetailsContainerComponent } from './notification-details-container.component';
import { NotificationDetailsComponent } from './notification-details.component';

describe('NotificationDetailsContainerComponent', () => {
  let component: NotificationDetailsContainerComponent;
  let fixture: ComponentFixture<NotificationDetailsContainerComponent>;
  let store: MockStore;

  const seniorAdmin:boolean = true;
  const notificationId:string = "55";
  const activeNotification:Notification = {
    notificationId: notificationId,
    type: NotificationType.USER_INACTIVITY,
    activationDetails: {
      scheduledDate: "2025-03-10",
      scheduledTime: "10:00:00",
      expirationDate: undefined,
      expirationTime: undefined,
      hasRecurrence: false,
      recurrenceDays: 0,
      hasExpirationDateSection: false,
      scheduledTimeNow: false
    },
    contentDetails: {subject: 'Test Notification',content:'Test Data'},
    status: 'ACTIVE',
    lastUpdated: new Date(),
    updatedBy: "Femi Ashiru",
    tentativeRecipients: 0,
    uploadedFileId: 0  
  } as Notification;
  const expiredNotification:Notification = {
    notificationId: notificationId,
    type: NotificationType.USER_INACTIVITY,
    activationDetails: {
      scheduledDate: "2025-03-10",
      scheduledTime: "10:00:00",
      expirationDate: "2025-03-10",
      hasRecurrence: false,
      recurrenceDays: 0,
      hasExpirationDateSection: false,
      expirationTime: "",
      scheduledTimeNow: false
    },
    contentDetails: {subject: 'Test Notification',content:'Test Data'},
    // lastUpdated?: Date;
    // updatedBy?: string;
    status: 'EXPIRED'
  } as Notification;
  const timeOptions:any = [
      {
        label: '',
        value: null
      },
      {
        label: '12:00am',
        value: '00:00:00'
      },
      {
        label: '12:30am',
        value: '00:30:00'
      },
      {
        label: '1:00am',
        value: '01:00:00'
      },
      {
        label: '1:30am',
        value: '01:30:00'
      },
      {
        label: '2:00am',
        value: '02:00:00'
      }
  ];
  const mockState = {
    notificationDetailsState: {
      notificationId: notificationId,
      notification: activeNotification,
    },
  };
  const notificationType:NotificationType = NotificationType.AD_HOC;


  const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));
  const MOCK_CURRENT_URL = 'the current url';
  
  const overrideSelectors = function(){
      // Reset fixture and component
      fixture = TestBed.createComponent(NotificationDetailsContainerComponent);
      store = TestBed.inject(MockStore);
      component = fixture.componentInstance;
      
      store.overrideSelector(selectNotificationId, notificationId);
      store.overrideSelector(selectNotificationDetails, activeNotification);
      store.overrideSelector(selectTimeOptions, timeOptions);
      store.overrideSelector(isSeniorAdmin, seniorAdmin);
      component.ngOnInit();
      fixture.detectChanges();    
  }

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CommonModule,  RouterModule.forRoot([])],
        declarations: [
          NotificationDetailsContainerComponent,
          NotificationDetailsComponent
        ],
        providers: [
          { provide: APP_BASE_HREF, useValue: '/' },
          provideMockStore(),
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: {
                params: {
                  notificationId: notificationId,
                },
                _routerState: {
                  url: MOCK_CURRENT_URL,
                },
              },
            },
          },
          { provide: Router, useValue: routerSpy },
          AsyncPipe,
          GdsDatePipe
        ],
        schemas: [CUSTOM_ELEMENTS_SCHEMA]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationDetailsContainerComponent);
    store = TestBed.inject(MockStore);

    component = fixture.componentInstance;
  });

  test('should create a NotificationDetailsContainerComponent', () => {
    expect(component).toBeTruthy();
  });


  test('should dispatch canGoBack action on init', () => {
    const dispatchSpy = spyOn(store, 'dispatch');
    component.ngOnInit();
    expect(dispatchSpy).toHaveBeenCalledWith(canGoBack({ goBackRoute: null }));
  });

  describe('when Cancel Button is displayed',()=> {
    beforeEach(() => {
      overrideSelectors();
    });

    afterEach(() => {
      store.resetSelectors();
    });
      
    test('cancel button should be shown for ACTIVE notifications', () => {
      overrideSelectors();

      store.select(selectNotificationDetails).subscribe(result => {
        const elements = fixture.debugElement.queryAll(By.css('app-notification-header'));

        expect(elements.length).toBeGreaterThan(0);
        expect(elements[0].componentInstance.showCancelUpdate).toBe(true);
      });
    });

  });
});
