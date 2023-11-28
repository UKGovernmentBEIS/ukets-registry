import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF, DatePipe } from '@angular/common';
import { GdsDatePipe, GdsDateTimePipe } from '@shared/pipes';
import { NotificationType } from '@notifications/notifications-wizard/model';
import { CancelRequestLinkComponent } from '@shared/components/account/cancel-request-link';
import { NotificationScheduledDateComponent } from './notification-scheduled-date.component';
import {
  UkProtoFormDatePickerComponent,
  UkProtoFormSelectComponent,
  UkProtoFormTextComponent,
} from '@shared/form-controls/uk-proto-form-controls';
import { UkSingleCheckboxComponent } from '@shared/form-controls/uk-single-checkbox/uk-single-checkbox.component';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import { NotificationHeaderComponent } from '@shared/components/notifications/notification-header';
import { FeatureHeaderWrapperComponent } from '@shared/sub-headers/header-wrapper/feature-header-wrapper.component';
import { GovukTagComponent } from '@shared/govuk-components';
import { PortalModule } from '@angular/cdk/portal';
import { provideMockStore } from '@ngrx/store/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

describe('NotificationScheduledDateComponent', () => {
  let component: NotificationScheduledDateComponent;
  let fixture: ComponentFixture<NotificationScheduledDateComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          FormsModule,
          RouterModule.forRoot([]),
          PortalModule,
          NgbModule,
        ],
        declarations: [
          NotificationScheduledDateComponent,
          CancelRequestLinkComponent,
          UkProtoFormSelectComponent,
          UkProtoFormDatePickerComponent,
          UkSingleCheckboxComponent,
          UkProtoFormTextComponent,
          DisableControlDirective,
          NotificationHeaderComponent,
          FeatureHeaderWrapperComponent,
          GovukTagComponent,
          GdsDatePipe,
          GdsDateTimePipe,
        ],
        providers: [
          DatePipe,
          GdsDatePipe,
          { provide: APP_BASE_HREF, useValue: '/' },
          provideMockStore(),
        ],
      }).compileComponents();
      fixture = TestBed.createComponent(NotificationScheduledDateComponent);
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
    })
  );

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
