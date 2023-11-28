import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { IssuanceTransactionSummaryTableComponent } from '@shared/components/transactions';
import { Component } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';
import { UnitTypeAndActivityPipe } from '../../../../../kp-administration/issue-kp-units/pipes/registry-level-info-view.pipe';
import { IssueKpUnitsTaskDetailsComponent } from '@task-details/components';
import { TransactionType } from '@shared/model/transaction';
import { ApiEnumTypesPipe } from '@shared/pipes/api-enum-types.pipe';
import { RequestType } from '@task-management/model';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import {
  TransactionReferenceComponent,
  TransactionReferenceWarningComponent,
} from '@shared/components/transactions/transaction-reference';

// https://stackoverflow.com/questions/39577920/angular-2-unit-testing-components-with-routerlink
@Component({
  selector: 'app-dummy-component',
  template: '',
})
class DummyComponent {}

describe('IssueKpUnitsTaskDetailsComponent', () => {
  let component: IssueKpUnitsTaskDetailsComponent;
  let fixture: ComponentFixture<IssueKpUnitsTaskDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          CommonModule,
          RouterTestingModule.withRoutes([
            { path: 'task-details/history/', component: DummyComponent },
          ]),
        ],
        declarations: [
          IssueKpUnitsTaskDetailsComponent,
          IssuanceTransactionSummaryTableComponent,
          DummyComponent,
          UnitTypeAndActivityPipe,
          ApiEnumTypesPipe,
          TransactionReferenceComponent,
          TransactionReferenceWarningComponent,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(IssueKpUnitsTaskDetailsComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.transactionTaskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.TRANSACTION_REQUEST,
      transactionIdentifier: '',
      trType: TransactionType.IssueOfAAUsAndRMUs,
      commitmentPeriod: '',
      acquiringAccount: '',
      unitType: '',
      environmentalActivity: undefined,
      initialQuantity: 0,
      consumedQuantity: 0,
      pendingQuantity: 0,
      remainingQuantity: 0,
      quantity: 0,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
