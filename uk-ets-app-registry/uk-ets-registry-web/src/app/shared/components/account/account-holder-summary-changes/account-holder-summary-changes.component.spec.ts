import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountHolderSummaryChangesComponent } from './account-holder-summary-changes.component';
import {
  FormatUkDatePipe,
  GdsDateShortPipe,
  IndividualPipe,
  IndividualFullNamePipe,
  OrganisationPipe,
} from '@shared/pipes';
import { ActivatedRoute } from '@angular/router';
import { AccountHolderType } from '@shared/model/account';
import { RouterTestingModule } from '@angular/router/testing';
import { SummaryListComponent } from '@shared/summary-list';

const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));

describe('AccountHolderSummaryChangesComponent', () => {
  let component: AccountHolderSummaryChangesComponent;
  let fixture: ComponentFixture<AccountHolderSummaryChangesComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          AccountHolderSummaryChangesComponent,
          IndividualPipe,
          IndividualFullNamePipe,
          OrganisationPipe,
          GdsDateShortPipe,
          FormatUkDatePipe,
          SummaryListComponent,
        ],
        providers: [
          { provide: ActivatedRoute, useValue: activatedRouteSpy },
          IndividualPipe,
          OrganisationPipe,
          IndividualFullNamePipe,
        ],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountHolderSummaryChangesComponent);
    component = fixture.componentInstance;
    component.countries = [];
    component.currentValues = {
      accountHolder: {
        address: {
          buildingAndStreet: '',
          buildingAndStreet2: '',
          buildingAndStreet3: '',
          country: '',
          postCode: '',
          townOrCity: '',
          stateOrProvince: '',
        },
        details: {
          registrationNumber: '',
          countryOfBirth: 'Nowhere',
          birthDate: {
            day: '01',
            month: '02',
            year: '1980',
          },
          name: 'Georgie',
          noRegistrationNumJustification: '',
        },
        id: 0,
        type: AccountHolderType.ORGANISATION,
      },
    };
    component.changedValues = {
      accountHolderDiff: {
        address: {
          buildingAndStreet: '',
          buildingAndStreet2: '',
          buildingAndStreet3: '',
          country: '',
          postCode: '',
          townOrCity: '',
          stateOrProvince: '',
        },
        details: {
          registrationNumber: '',
          countryOfBirth: 'Nowhere',
          birthDate: {
            day: '01',
            month: '02',
            year: '1980',
          },
          name: 'Georgie',
          noRegistrationNumJustification: '',
        },
        emailAddress: { emailAddress: '', emailAddressConfirmation: '' },
        phoneNumber: {
          countryCode1: '',
          countryCode2: '',
          phoneNumber1: '',
          phoneNumber2: '',
        },
      },
    };

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
