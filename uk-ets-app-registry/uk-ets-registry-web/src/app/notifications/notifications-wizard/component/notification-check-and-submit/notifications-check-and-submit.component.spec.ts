import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { NotificationCheckAndSubmitComponent } from './notification-check-and-submit.component';
import { GdsDatePipe, GdsDateTimePipe } from '@shared/pipes';
import { SummaryListComponent } from '@shared/summary-list';
import { NotificationType } from '@notifications/notifications-wizard/model';
import { CancelRequestLinkComponent } from '@shared/components/account/cancel-request-link';
import { NotificationHeaderComponent } from '@shared/components/notifications/notification-header';
import { FeatureHeaderWrapperComponent } from '@shared/sub-headers/header-wrapper/feature-header-wrapper.component';
import { GovukTagComponent } from '@shared/govuk-components';
import { PortalModule } from '@angular/cdk/portal';
import { provideMockStore } from '@ngrx/store/testing';
import { NotificationRendererComponent } from '@shared/components/notifications/notification-renderer';

describe('NotificationCheckAndSubmitComponent', () => {
  let component: NotificationCheckAndSubmitComponent;
  let fixture: ComponentFixture<NotificationCheckAndSubmitComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          FormsModule,
          RouterModule.forRoot([]),
          PortalModule,
        ],
        declarations: [
          NotificationCheckAndSubmitComponent,
          GdsDatePipe,
          SummaryListComponent,
          CancelRequestLinkComponent,
          NotificationHeaderComponent,
          FeatureHeaderWrapperComponent,
          GovukTagComponent,
          GdsDateTimePipe,
          GdsDatePipe,
          NotificationRendererComponent,
        ],
        providers: [
          GdsDatePipe,
          GdsDateTimePipe,
          { provide: APP_BASE_HREF, useValue: '/' },
          provideMockStore(),
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationCheckAndSubmitComponent);
    component = fixture.componentInstance;
    component.timeOptions = [
      {
        label: '',
        value: null,
      },
      {
        label: '07:00am',
        value: '07:00:00',
      },
    ];
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
});
