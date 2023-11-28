import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { IssuanceTransactionSummaryTableComponent } from './issuance-transaction-summary-table.component';

describe('IssuanceTransactionSummaryTableComponent', () => {
  let component: IssuanceTransactionSummaryTableComponent;
  let fixture: ComponentFixture<IssuanceTransactionSummaryTableComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [IssuanceTransactionSummaryTableComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(IssuanceTransactionSummaryTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
