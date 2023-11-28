import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { TransactionRulesUpdateTaskDetailsComponent } from '@task-details/components/transaction-rules-update';
import { AccountSummaryComponent } from '@shared/components/transactions/account-summary/account-summary.component';
import { CommonModule } from '@angular/common';
import { RouterTestingModule } from '@angular/router/testing';
import { RequestType } from '@task-management/model';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import { SummaryListComponent } from '@shared/summary-list';

describe('TransactionRulesUpdateTaskDetailsComponent', () => {
  let component: TransactionRulesUpdateTaskDetailsComponent;
  let fixture: ComponentFixture<TransactionRulesUpdateTaskDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CommonModule, RouterTestingModule],
        declarations: [
          TransactionRulesUpdateTaskDetailsComponent,
          AccountSummaryComponent,
          SummaryListComponent,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(
      TransactionRulesUpdateTaskDetailsComponent
    );
    component = fixture.componentInstance;
    component.transactionRuleUpdateTaskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.TRANSACTION_RULES_UPDATE_REQUEST,
      accountInfo: {
        identifier: 10001,
        fullIdentifier: 'GB-100-1002-1-84',
        accountName: ' Party Holding 1',
        accountHolderName: 'Name 1',
      },
      trustedAccountListRules: {
        // Is the approval of a second authorised representative necessary to
        // execute transfers to accounts on the trusted account list?
        rule1: true,
        // Are transfers to accounts not on the trusted account list blocked?
        rule2: false,
        // Is a single person approval is required for specific transactions?
        rule3: true,
      },
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
