import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AccountHolderSelectionComponent } from '@account-opening/account-holder/account-holder-selection';
import { OrganisationPipe, IndividualPipe, ProtectPipe } from '@shared/pipes';
import { CUSTOM_ELEMENTS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import {
  AccountHolderSelectionType,
  AccountHolderType,
} from '@shared/model/account';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { AuthModel } from '../../../auth/auth.model';
import { selectLoggedInUser } from '../../../auth/auth.selector';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { MockAuthApiService } from '../../../../testing/mock-auth-api-service';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import {
  FormatTypeAheadAccountHolderResultPipe,
  FormatAccountHolderResultPipe,
} from '@account-shared/pipes';

describe('AccountHolderSelectionComponent', () => {
  let component: AccountHolderSelectionComponent;
  let fixture: ComponentFixture<AccountHolderSelectionComponent>;
  let store: MockStore<any>;
  let loggedinUser$: Observable<AuthModel>;
  const formBuilder: FormBuilder = new FormBuilder();

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [
        AccountHolderSelectionComponent,
        OrganisationPipe,
        IndividualPipe,
        IndividualPipe,
        ProtectPipe,
        ScreenReaderPageAnnounceDirective,
        FormatTypeAheadAccountHolderResultPipe,
        FormatAccountHolderResultPipe,
      ],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [
        provideMockStore(),
        { provide: FormBuilder, useValue: formBuilder },
        { provide: AuthApiService, useValue: MockAuthApiService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountHolderSelectionComponent);
    component = fixture.componentInstance;
    component.accountHolder = {
      id: 1000000,
      type: AccountHolderType.INDIVIDUAL,
      details: {
        name: 'Test individual',
        firstName: 'Test',
        lastName: 'individual',
        birthDate: null,
        countryOfBirth: null,
      },
      address: {},
    };
    component.accountHolderList = [
      {
        id: 1000000,
        type: AccountHolderType.INDIVIDUAL,
        details: {
          name: 'Test individual',
          firstName: 'Test',
          lastName: 'individual',
          birthDate: null,
          countryOfBirth: null,
        },
        address: {},
      },
      {
        id: 1000001,
        type: AccountHolderType.INDIVIDUAL,
        details: {
          name: 'Test individual',
          firstName: 'Test',
          lastName: 'individual',
          birthDate: null,
          countryOfBirth: null,
        },
        address: {},
      },
    ];
    component.accountHolderType = AccountHolderType.INDIVIDUAL;
    component.accountHolderSelectionType = AccountHolderSelectionType.NEW;

    store = TestBed.inject(Store) as MockStore<any>;
    store.overrideSelector(selectLoggedInUser, {
      authenticated: true,
      showLoading: true,
      id: '100001',
      sessionUuid: 'b132b104-179e-45ba-a934-d090b6610312',
      urid: '100001',
      username: 'test',
      roles: [],
      firstName: 'firstName',
      lastName: 'lastName',
      knownAs: 'knownAs',
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate', () => {
    const accountHolderSelectionComponent: AccountHolderSelectionComponent = new AccountHolderSelectionComponent(
      formBuilder
    );
    expect(accountHolderSelectionComponent).toBeDefined();
  });

  it('should instantiate', () => {
    const pipeComponent: OrganisationPipe = new OrganisationPipe();
    expect(pipeComponent).toBeDefined();
  });

  it('should instantiate', () => {
    const pipeComponent: IndividualPipe = new IndividualPipe();
    expect(pipeComponent).toBeDefined();
  });

  it('should be equal to 1000001', () => {
    expect(fixture.componentInstance.accountHolderList[1].id).toEqual(1000001);
  });

  it('should be equal to INDIVIDUAL', () => {
    expect(fixture.componentInstance.accountHolderType).toEqual(
      AccountHolderType.INDIVIDUAL
    );
  });

  it('should check the LoggedInUser selector ', (done) => {
    loggedinUser$ = store.select(selectLoggedInUser);
    loggedinUser$.subscribe((result) => {
      expect(result.id).toEqual('100001');
      done(); // ensure the assertions are executed
    });
  });
});
