import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { GenericTransactionTaskDetailsComponent } from '@task-details/components';
import { AccountSummaryComponent } from '@shared/components/transactions/account-summary/account-summary.component';
import { QuantityTableComponent } from '@shared/components/transactions/quantity-table';
import { RouterLinkDirectiveStub } from '@shared/test/router-link-directive-stub';
import { EnvironmentalActivityPipe } from '@shared/pipes/environmental-activity.pipe';
import {
  TransactionAtrributesPipe,
  TrustedAccountPipe,
  UnitTypeSopRenderPipe,
} from '@shared/pipes';
import { TransactionType } from '@shared/model/transaction';
import { RequestType } from '@task-management/model';
import {
  ItlNotificationSummaryComponent,
  TransactionConnectionSummaryComponent,
  TransactionSigningDetailsComponent,
} from '@shared/components/transactions';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import { SummaryListComponent } from '@shared/summary-list';
import { RouterTestingModule } from '@angular/router/testing';
import {
  TransactionReferenceComponent,
  TransactionReferenceWarningComponent,
} from '@shared/components/transactions/transaction-reference';

describe('GenericTransactionTaskDetailsComponent', () => {
  let component: GenericTransactionTaskDetailsComponent;
  let fixture: ComponentFixture<GenericTransactionTaskDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [
          GenericTransactionTaskDetailsComponent,
          ItlNotificationSummaryComponent,
          AccountSummaryComponent,
          QuantityTableComponent,
          RouterLinkDirectiveStub,
          EnvironmentalActivityPipe,
          TrustedAccountPipe,
          UnitTypeSopRenderPipe,
          TransactionAtrributesPipe,
          TransactionSigningDetailsComponent,
          TransactionConnectionSummaryComponent,
          SummaryListComponent,
          TransactionReferenceComponent,
          TransactionReferenceWarningComponent,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericTransactionTaskDetailsComponent);
    component = fixture.componentInstance;
    component.transactionTaskDetails = {
      taskType: RequestType.TRANSACTION_REQUEST,
      transactionIdentifiers: [],
      itlNotification: null,
      allocationDetails: null,
      trType: TransactionType.ExternalTransfer,
      acquiringAccount: {
        identifier: 123456,
        fullIdentifier: '',
        accountName: '',
        accountHolderName: '',
        trusted: false,
      },
      transferringAccount: {
        identifier: 123456,
        fullIdentifier: '',
        accountName: '',
        accountHolderName: '',
      },
      transactionBlocks: [],
      transactionType: {
        type: TransactionType.ExternalTransfer,
        description: '',
        category: '',
        supportsNotification: false,
        skipAccountStep: false,
      },
      ...taskDetailsBase,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
