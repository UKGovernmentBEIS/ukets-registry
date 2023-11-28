import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TransactionConnectionSummaryComponent } from '@shared/components/transactions';
import { SummaryListComponent } from '@shared/summary-list';
import { RouterTestingModule } from '@angular/router/testing';

describe('TransactionConnectionSummaryComponent', () => {
  let component: TransactionConnectionSummaryComponent;
  let fixture: ComponentFixture<TransactionConnectionSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [
        TransactionConnectionSummaryComponent,
        SummaryListComponent,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionConnectionSummaryComponent);
    component = fixture.componentInstance;
    component.transactionConnectionSummary = {
      reversalStatus: 'COMPLETED',
      reversalIdentifier: 'UK100001',
      originalIdentifier: null,
      originalStatus: null,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
