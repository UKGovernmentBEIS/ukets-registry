import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardNotificationsContainerComponent } from './dashboard-notifications-container.component';
import { provideMockStore } from '@ngrx/store/testing';
import { DashboardNotificationsComponent } from '@registry-web/dashboard/notifications/components/dashboard-notifications.component';
import { GovukNotificationBannerComponent } from '@shared/govuk-components';
import { GdsDatePipe } from '@shared/pipes';
import { NotificationRendererComponent } from '@shared/components/notifications/notification-renderer';

describe('DashboardNotificationsContainerComponent', () => {
  let component: DashboardNotificationsContainerComponent;
  let fixture: ComponentFixture<DashboardNotificationsContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        DashboardNotificationsContainerComponent,
        DashboardNotificationsComponent,
        GovukNotificationBannerComponent,
        GdsDatePipe,
        NotificationRendererComponent,
      ],
      providers: [provideMockStore()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardNotificationsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
