import { ItlNotificationSummaryComponent } from '@shared/components/transactions';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

describe('ItlNotificationSummaryComponent', () => {
  let component: ItlNotificationSummaryComponent;
  let fixture: ComponentFixture<ItlNotificationSummaryComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ItlNotificationSummaryComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ItlNotificationSummaryComponent);
    component = fixture.componentInstance;
    component.itlNotification = {
      notificationIdentifier: 17,
      quantity: 1,
      commitPeriod: 1,
      targetDate: null,
      projectNumber: '',
      type: '',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
