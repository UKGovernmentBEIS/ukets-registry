import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountHolderContactUpdateDetailsContainerComponent } from '@task-details/components';
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
import { AccountHolderMultipleOwnershipComponent } from '@task-details/components';
import { RouterTestingModule } from '@angular/router/testing';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import { SummaryListComponent } from '@shared/summary-list';
import { of } from 'rxjs';
import { provideMockStore } from '@ngrx/store/testing';
import { selectAllCountries } from '@registry-web/shared/shared.selector';

const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));

describe('AccountHolderContactUpdateDetailsContainerComponent', () => {
  let component: AccountHolderContactUpdateDetailsContainerComponent;
  let fixture: ComponentFixture<AccountHolderContactUpdateDetailsContainerComponent>;
  let store: Store;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({}), RouterTestingModule],
      declarations: [
        AccountHolderContactUpdateDetailsContainerComponent,
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
        { provide: IndividualPipe },
        { provide: OrganisationPipe },
        { provide: IndividualFullNamePipe },
        provideMockStore({
          selectors: [{ selector: selectAllCountries, value: [] }],
        }),
      ],
    });

    await TestBed.compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(
      AccountHolderContactUpdateDetailsContainerComponent
    );

    component = fixture.componentInstance;
    component.taskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS,
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
      accountHolderContact: {
        positionInCompany: '',
        id: 1234,
        new: false,
        details: {
          aka: '',
          lastName: 'Kook',
          firstName: 'Gio',
          birthDate: {
            day: '01',
            month: '02',
            year: '1980',
          },
        },
        address: {
          buildingAndStreet: '',
          buildingAndStreet2: '',
          buildingAndStreet3: '',
          country: '',
          postCode: '',
          townOrCity: '',
          stateOrProvince: '',
        },
        emailAddress: { emailAddress: '', emailAddressConfirmation: '' },
        phoneNumber: {
          countryCode1: '',
          countryCode2: '',
          phoneNumber1: '',
          phoneNumber2: '',
        },
      },

      accountHolderContactDiff: {
        positionInCompany: '',
        id: 1234,
        new: false,
        details: {
          aka: '',
          lastName: 'Kook',
          firstName: 'Gio',
          birthDate: {
            day: '01',
            month: '02',
            year: '1980',
          },
        },
        address: {
          buildingAndStreet: '',
          buildingAndStreet2: '',
          buildingAndStreet3: '',
          country: '',
          postCode: '',
          townOrCity: '',
          stateOrProvince: '',
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
