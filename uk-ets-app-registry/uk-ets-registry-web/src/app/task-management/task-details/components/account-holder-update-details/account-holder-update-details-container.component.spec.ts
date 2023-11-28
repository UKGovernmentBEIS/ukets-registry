import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountHolderUpdateDetailsContainerComponent } from './account-holder-update-details-container.component';
import { Store, StoreModule } from '@ngrx/store';
import { AccountHolderSummaryChangesComponent } from '@shared/components/account/account-holder-summary-changes';
import {
  FormatUkDatePipe,
  GdsDateShortPipe,
  IndividualFullNamePipe,
  IndividualPipe,
  OrganisationPipe,
} from '@shared/pipes';
import { ActivatedRoute } from '@angular/router';
import { RequestType, TaskOutcome } from '@task-management/model';
import { AccountHolderType } from '@shared/model/account';
import { AccountHolderMultipleOwnershipComponent } from '../account-holder-multiple-ownership/account-holder-multiple-ownership.component';
import { RouterTestingModule } from '@angular/router/testing';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import { SummaryListComponent } from '@shared/summary-list';
import { provideMockStore } from '@ngrx/store/testing';
import { selectAllCountries } from '@registry-web/shared/shared.selector';

const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));

describe('AccountHolderUpdateDetailsContainerComponent', () => {
  let component: AccountHolderUpdateDetailsContainerComponent;
  let fixture: ComponentFixture<AccountHolderUpdateDetailsContainerComponent>;
  let store: Store;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({}), RouterTestingModule],
      declarations: [
        AccountHolderUpdateDetailsContainerComponent,
        AccountHolderMultipleOwnershipComponent,
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
        provideMockStore({
          selectors: [{ selector: selectAllCountries, value: [] }],
        }),
      ],
    });

    await TestBed.compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(
      AccountHolderUpdateDetailsContainerComponent
    );

    component = fixture.componentInstance;
    component.taskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS,
      accountDetails: {
        name: '',
        accountType: null,
        accountNumber: '',
        accountHolderName: '',
        accountHolderId: '12345',
        complianceStatus: 'NOT_APPLICABLE',
        accountStatus: 'OPEN',
        address: {
          buildingAndStreet: '',
          buildingAndStreet2: '',
          buildingAndStreet3: '',
          townOrCity: '',
          stateOrProvince: '',
          country: '',
          postCode: '',
        },
      },
      accountHolder: {
        id: 1234,
        address: '',
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
        type: AccountHolderType.ORGANISATION,
      },
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
      accountHolderOwnership: [],
    };
    store = TestBed.inject(Store);

    spyOn(store, 'dispatch').and.callThrough();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
