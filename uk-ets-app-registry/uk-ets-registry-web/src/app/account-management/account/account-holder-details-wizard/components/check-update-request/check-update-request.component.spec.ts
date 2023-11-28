import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CheckUpdateRequestComponent } from '@account-management/account/account-holder-details-wizard/components/check-update-request';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { AccountHolderSummaryChangesComponent } from '@shared/components/account/account-holder-summary-changes';
import {
  FormatUkDatePipe,
  GdsDateShortPipe,
  IndividualFullNamePipe,
  IndividualPipe,
  OrganisationPipe,
} from '@shared/pipes';
import { AccountHolderType } from '@shared/model/account';
import { AccountHolderUpdatePipe } from '@account-management/account/account-holder-details-wizard/pipes';
import { AccountHolderDetailsType } from '@account-management/account/account-holder-details-wizard/model';
import { SummaryListComponent } from '@shared/summary-list';
import { provideMockStore } from '@ngrx/store/testing';
import { selectAllCountries } from '@registry-web/shared/shared.selector';

describe('CheckUpdateRequestComponent', () => {
  let component: CheckUpdateRequestComponent;
  let fixture: ComponentFixture<CheckUpdateRequestComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [
          CheckUpdateRequestComponent,
          AccountHolderSummaryChangesComponent,
          FormatUkDatePipe,
          IndividualPipe,
          IndividualFullNamePipe,
          OrganisationPipe,
          GdsDateShortPipe,
          AccountHolderUpdatePipe,
          SummaryListComponent,
        ],
        providers: [
          { provide: APP_BASE_HREF, useValue: '/' },
          IndividualPipe,
          OrganisationPipe,
          IndividualFullNamePipe,
          provideMockStore({
            selectors: [{ selector: selectAllCountries, value: [] }],
          }),
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckUpdateRequestComponent);
    component = fixture.componentInstance;
    component.countries = [];
    component.updateType =
      AccountHolderDetailsType.ACCOUNT_HOLDER_UPDATE_DETAILS;
    component.accountHolderContactChanged = {};
    component.accountHolderInfoChanged = {};
    component.accountHolderContact = {
      id: 1,
      new: true,
      details: {
        firstName: '',
        lastName: '',
        aka: '',
        birthDate: {
          day: '',
          month: '',
          year: '',
        },
      },
      positionInCompany: '',
      address: {
        buildingAndStreet: '',
        buildingAndStreet2: '',
        buildingAndStreet3: '',
        townOrCity: '',
        stateOrProvince: '',
        country: '',
        postCode: '',
      },
      phoneNumber: {
        countryCode1: '',
        phoneNumber1: '',
        countryCode2: '',
        phoneNumber2: '',
      },
      emailAddress: {
        emailAddress: '',
        emailAddressConfirmation: '',
      },
    };
    component.accountHolder = {
      id: 1,
      type: AccountHolderType.INDIVIDUAL,
      details: {
        name: '',
        firstName: '',
        lastName: '',
        birthDate: { day: '', month: '', year: '' },
        countryOfBirth: '',
      },
      address: {},
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
