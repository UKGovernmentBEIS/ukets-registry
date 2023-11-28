import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { QuantityTableComponent } from './quantity-table.component';
import { EnvironmentalActivityPipe } from '@shared/pipes/environmental-activity.pipe';
import { UnitTypeSopRenderPipe } from '@shared/pipes';
import { CommitmentPeriod, UnitType } from '@shared/model/transaction';
import { TransactionBlock } from '@transaction-management/model';

describe('QuantityTableComponent', () => {
  let component: QuantityTableComponent;
  let fixture: ComponentFixture<QuantityTableComponent>;
  const transactionBlock: TransactionBlock[] = [
    {
      startBlock: 0,
      endBlock: 0,
      type: UnitType.AAU,
      originatingCountryCode: '',
      originalPeriod: CommitmentPeriod.CP2,
      applicablePeriod: CommitmentPeriod.CP2,
      environmentalActivity: '',
      expiryDate: '',
      quantity: '',
      subjectToSop: true,
      projectNumber: '',
    },
  ];

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          QuantityTableComponent,
          EnvironmentalActivityPipe,
          UnitTypeSopRenderPipe,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(QuantityTableComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.transactionBlocks = transactionBlock;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate', () => {
    const component: QuantityTableComponent = new QuantityTableComponent();
    expect(component).toBeDefined();
  });
});
