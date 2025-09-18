import { APP_BASE_HREF } from '@angular/common';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Notification, NotificationType } from '@registry-web/notifications/notifications-wizard/model';
import { SharedModule } from '@registry-web/shared/shared.module';
import { NotificationHeaderComponent } from './notification-header.component';

describe('NotificationHeaderComponent', () => {
  let component: NotificationHeaderComponent;
  let fixture: ComponentFixture<NotificationHeaderComponent>;

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

  const notificationType:NotificationType = NotificationType.AD_HOC;
  const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));
  const MOCK_CURRENT_URL = 'the current url';

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [SharedModule,  RouterModule.forRoot([])],
        declarations: [
          NotificationHeaderComponent
        ],
        providers: [
          { provide: APP_BASE_HREF, useValue: '/' },
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
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationHeaderComponent);
    component = fixture.componentInstance;
  });

  test('should create a NotificationHeaderComponent', () => {
    expect(component).toBeTruthy();
  });

  test('cancel button should be shown for ACTIVE & PENDING notifications when the showCancelUpdate is true', () => {
    component.notification  = activeNotification;
    component.notificationHeaderVisibility = true;
    component.showCancelUpdate = true;
    fixture.detectChanges();

    const buttons = fixture.debugElement.queryAll(By.css('button'));
    expect(buttons.length).toBe(1);
    expect(buttons[0].nativeElement.textContent.trim()).toBe('Cancel notification');
  });


  test('cancel button should NOT be shown for EXPIRED & CANCELLED notifications when the showCancelUpdate is false', () => {
    component.notification  = activeNotification;
    component.notificationHeaderVisibility = true;
    component.showCancelUpdate = false;
    fixture.detectChanges();

    const buttons = fixture.debugElement.queryAll(By.css('button'));
    expect(buttons.length).toBe(0);
  });
});
