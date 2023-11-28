import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReconciliationStartComponent } from '@shared/components/reconciliation/reconciliation-start.component';
import { GovukTagComponent } from '@shared/govuk-components';
import { GdsDateTimePipe } from '@shared/pipes';

describe('ReconciliationStartComponent', () => {
  let component: ReconciliationStartComponent;
  let fixture: ComponentFixture<ReconciliationStartComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          ReconciliationStartComponent,
          GovukTagComponent,
          GdsDateTimePipe,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ReconciliationStartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
