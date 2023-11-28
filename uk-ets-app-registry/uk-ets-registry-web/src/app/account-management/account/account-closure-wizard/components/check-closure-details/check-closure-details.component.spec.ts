import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckClosureDetailsComponent } from './check-closure-details.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormGroupDirective } from '@angular/forms';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { UserDetailsUpdatePipe } from '@user-update/pipes';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { CountryNamePipe, FormatUkDatePipe } from '@shared/pipes';
import { AccountType } from '@shared/model/account';

describe('CheckClosureDetailsComponent', () => {
  let component: CheckClosureDetailsComponent;
  let fixture: ComponentFixture<CheckClosureDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [
        CheckClosureDetailsComponent,
        FormGroupDirective,
        ConnectFormDirective,
        UserDetailsUpdatePipe,
        ScreenReaderPageAnnounceDirective,
      ],
      providers: [FormatUkDatePipe, CountryNamePipe, UserDetailsUpdatePipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckClosureDetailsComponent);
    component = fixture.componentInstance;
    component.authorisedRepresentatives = [];
    component.accountDetails = {
      name: 'Account1',
      accountNumber: '1000034',
      accountType: AccountType.OPERATOR_HOLDING_ACCOUNT,
      accountStatus: 'OPEN',
      accountHolderId: '12345',
      accountHolderName: 'An Account Holder',
      complianceStatus: 'NOT_APPLICABLE',
      address: {
        buildingAndStreet: '',
        buildingAndStreet2: '',
        buildingAndStreet3: '',
        townOrCity: '',
        stateOrProvince: '',
        country: '',
        postCode: '',
      },
    };

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
