import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { TransactionQuantityComponent } from './transaction-quantity.component';
import { QuantityTableComponent } from '@shared/components/transactions/quantity-table';
import { TransactionType } from '@shared/model/transaction';
import {
  EnvironmentalActivityPipe,
  UnitTypeSopRenderPipe,
} from '@shared/pipes';

describe('TransactionQuantityComponent', () => {
  let component: TransactionQuantityComponent;
  let fixture: ComponentFixture<TransactionQuantityComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          TransactionQuantityComponent,
          QuantityTableComponent,
          EnvironmentalActivityPipe,
          UnitTypeSopRenderPipe,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionQuantityComponent);
    component = fixture.componentInstance;
    component.transactionBlocks = [];
    component.transactionType = TransactionType.CentralTransferAllowances;
    component.isEtsTransaction = true;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
