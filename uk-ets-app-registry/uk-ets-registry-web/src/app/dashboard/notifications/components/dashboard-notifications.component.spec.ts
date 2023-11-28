import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardNotificationsComponent } from './dashboard-notifications.component';
import { GovukNotificationBannerComponent } from '@shared/govuk-components';
import { GdsDatePipe } from '@shared/pipes';
import { NotificationRendererComponent } from '@shared/components/notifications/notification-renderer';

describe('DashboardNotificationsComponent', () => {
  let component: DashboardNotificationsComponent;
  let fixture: ComponentFixture<DashboardNotificationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        DashboardNotificationsComponent,
        GovukNotificationBannerComponent,
        GdsDatePipe,
        NotificationRendererComponent,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardNotificationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
