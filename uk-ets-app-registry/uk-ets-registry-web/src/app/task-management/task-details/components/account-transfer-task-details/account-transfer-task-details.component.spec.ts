import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountTransferTaskDetailsComponent } from './account-transfer-task-details.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RequestType } from '@task-management/model';
import { AccountHolderType } from '@shared/model/account';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import { FormatUkDatePipe } from '@registry-web/shared/pipes';

describe('AccountTransferTaskDetailsComponent', () => {
  let component: AccountTransferTaskDetailsComponent;
  let fixture: ComponentFixture<AccountTransferTaskDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [AccountTransferTaskDetailsComponent],
      providers: [FormatUkDatePipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountTransferTaskDetailsComponent);
    component = fixture.componentInstance;
    component.taskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.ACCOUNT_TRANSFER,
      action: {
        accountHolderDTO: {
          id: 12345,
          type: AccountHolderType.ORGANISATION,
          details: {
            name: '',
            firstName: '',
            lastName: '',
            birthDate: null,
            countryOfBirth: '',
          },
          address: null,
        },
        accountHolderContactInfo: null,
      },
      currentAccountHolder: {
        id: 1,
        type: AccountHolderType.INDIVIDUAL,
        details: {
          name: '',
          firstName: '',
          lastName: '',
          birthDate: null,
          countryOfBirth: '',
        },
        address: {},
      },
      account: {
        name: '',
        accountType: null,
        accountNumber: '',
        accountHolderName: '',
        accountHolderId: '12345',
        complianceStatus: 'NOT_APPLICABLE',
        accountStatus: 'OPEN',
        address: null,
      },
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
