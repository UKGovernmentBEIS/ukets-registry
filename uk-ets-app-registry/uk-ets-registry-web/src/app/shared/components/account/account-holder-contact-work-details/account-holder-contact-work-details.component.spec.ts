import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import {
  aTestAccountHolderStateWithIndividual,
  aTestSharedState,
} from '@account-opening/account-holder/account-holder.test.helper';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { StoreModule } from '@ngrx/store';
import { AccountHolderContactWorkDetailsComponent } from './account-holder-contact-work-details.component';
import { SharedModule } from '@registry-web/shared/shared.module';

const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));
const formBuilder = new FormBuilder();
describe('AccountHolderContactWorkDetailsComponent', () => {
  const TEST_CONTACT_TYP = 'primary';
  const MOCK_CURRENT_URL = 'the current url';
  const ACCOUNT_HOLDER_CONTACT = {
    id: 1,
    new: true,
    details: {
      firstName: 'First name',
      lastName: 'Last name',
      aka: '',
      birthDate: {
        day: '',
        month: '',
        year: '',
      },
      isOverEighteen: true,
    },
    positionInCompany: '',
    address: {
      buildingAndStreet: 'address 1',
      buildingAndStreet2: 'address 2',
      buildingAndStreet3: 'address 3',
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
  let fixture: ComponentFixture<AccountHolderContactWorkDetailsComponent>;
  let component: AccountHolderContactWorkDetailsComponent;
  const initialState = {
    accountOpening: aTestAccountHolderStateWithIndividual,
    shared: aTestSharedState,
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [
          ConnectFormDirective,
          FormGroupDirective,
          AccountHolderContactWorkDetailsComponent,
        ],
        providers: [
          { provide: Router, useValue: routerSpy },
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: {
                paramMap: {
                  get: () => TEST_CONTACT_TYP,
                },
                _routerState: {
                  url: MOCK_CURRENT_URL,
                },
              },
            },
          },
          { provide: FormBuilder, useValue: formBuilder },
        ],
        imports: [
          StoreModule.forRoot(initialState),
          ReactiveFormsModule,
          SharedModule,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountHolderContactWorkDetailsComponent);
    component = fixture.debugElement.componentInstance;
    component.isAHUpdateWizard = false;
    component.countryCodes = [
      {
        region: 'GREECE',
        code: 'GR',
      },
    ];
    component.formGroup = formBuilder.group(component.initForm());
    component._accountHolderContact = ACCOUNT_HOLDER_CONTACT;
    fixture.componentInstance.ngOnInit();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should be equal to GREECE', () => {
    expect(component.countryCodes[0].region).toEqual('GREECE');
  });

  it('should be invalid at first', () => {
    expect(component.formGroup.get('address').valid).toBeFalsy();
  });
});
