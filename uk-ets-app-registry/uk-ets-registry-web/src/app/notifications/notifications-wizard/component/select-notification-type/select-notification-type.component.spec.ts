import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CancelRequestLinkComponent } from '@shared/components/account/cancel-request-link';
import {
  GdsDatePipe,
  GdsDateTimePipe,
  GdsDateTimeShortPipe,
} from '@shared/pipes';
import { APP_BASE_HREF } from '@angular/common';
import { SelectNotificationTypeComponent } from './select-notification-type.component';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { NotificationHeaderComponent } from '@shared/components/notifications/notification-header';
import { FeatureHeaderWrapperComponent } from '@shared/sub-headers/header-wrapper/feature-header-wrapper.component';
import { GovukTagComponent } from '@shared/govuk-components';
import { PortalModule } from '@angular/cdk/portal';
import { provideMockStore } from '@ngrx/store/testing';

describe('SelectNotificationTypeComponent', () => {
  let component: SelectNotificationTypeComponent;
  let fixture: ComponentFixture<SelectNotificationTypeComponent>;

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
          SelectNotificationTypeComponent,
          CancelRequestLinkComponent,
          UkRadioInputComponent,
          NotificationHeaderComponent,
          FeatureHeaderWrapperComponent,
          GovukTagComponent,
          GdsDateTimePipe,
          GdsDatePipe,
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
    fixture = TestBed.createComponent(SelectNotificationTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
