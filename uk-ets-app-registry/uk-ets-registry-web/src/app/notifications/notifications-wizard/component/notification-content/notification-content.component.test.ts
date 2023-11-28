import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SummaryListComponent } from '@shared/summary-list';
import { NotificationType } from '@notifications/notifications-wizard/model';
import { CancelRequestLinkComponent } from '@shared/components/account/cancel-request-link';
import { NotificationHeaderComponent } from '@shared/components/notifications/notification-header';
import { FeatureHeaderWrapperComponent } from '@shared/sub-headers/header-wrapper/feature-header-wrapper.component';
import { provideMockStore } from '@ngrx/store/testing';
import { NotificationRendererComponent } from '@shared/components/notifications/notification-renderer';
import { NotificationContentComponent } from './notification-content.component';
import { NotificationRequestEnum } from '../../model/notification-request.enum';

describe('NotificationContentComponent', () => {
  let component: NotificationContentComponent;
  let fixture: ComponentFixture<NotificationContentComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [
          NotificationContentComponent,
          SummaryListComponent,
          CancelRequestLinkComponent,
          NotificationHeaderComponent,
          FeatureHeaderWrapperComponent,
          NotificationRendererComponent,
        ],
        providers: [provideMockStore()],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationContentComponent);
    component = fixture.componentInstance;
    component.newNotification = {
      type: NotificationType.EMISSIONS_MISSING_FOR_AOHA,
      activationDetails: {
        scheduledDate: '2021-11-18',
        scheduledTime: '07:00:00',
        scheduledTimeNow: false,
        hasRecurrence: true,
        recurrenceDays: 2,
        expirationDate: '2021-11-28',
        hasExpirationDateSection: null,
        expirationTime: null,
      },
      contentDetails: {
        subject: 'Test subject',
        content: 'Test content',
      },
      lastUpdated: new Date(),
      updatedBy: 'Test user',
      status: 'ACTIVE',
    };

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add Clone suffix to subject when notification type is CLONE', () => {
    component.notificationRequest = NotificationRequestEnum.CLONE;
    component.notificationDefinition = {
      shortText: 'Short Text',
      longText: 'Long Text',
    };

    const formModel = (component as any).getFormModel();

    expect(formModel.notificationSubject[0]).toBe('Test subject Clone');
    expect(formModel.notificationContent[0]).toBe('Test content');
  });
});
