import { ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Store } from '@ngrx/store';
import { AuthModel } from '../../../auth/auth.model';
import { Observable } from 'rxjs';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { SelectionComponent } from '@account-opening/authorised-representative/selection/selection.component';
import {
  AccountAccessState,
  AccountHolderType,
  ARAccessRights,
  ARSelectionType,
} from '@shared/model/account';
import { UkSelectAuthorisedRepresentativeComponent } from '@shared/form-controls/uk-select-authorised-representative';
import { User } from '@shared/user';
import { selectLoggedInUser } from '../../../auth/auth.selector';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { ArDisplayNamePipe } from '@registry-web/shared/pipes/ar-display-name.pipe';

describe('ARsSelectionComponent', () => {
  let component: SelectionComponent;
  let fixture: ComponentFixture<SelectionComponent>;
  let store: MockStore<any>;
  let loggedinUser$: Observable<AuthModel>;
  const formBuilder: FormBuilder = new FormBuilder();

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [
        SelectionComponent,
        UkSelectAuthorisedRepresentativeComponent,
        ScreenReaderPageAnnounceDirective,
        ArDisplayNamePipe,
      ],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [
        provideMockStore(),
        { provide: FormBuilder, useValue: formBuilder },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectionComponent);
    component = fixture.componentInstance;
    component.fetchedAuthorisedRepresentatives = [
      {
        urid: 'UK1234567',
        firstName: 'Test Name',
        lastName: 'Test Last Name',
        user: aTestUser('Test Name', 'Test Last Name'),
        right: ARAccessRights.INITIATE_AND_APPROVE,
        state: 'ACTIVE',
        contact: null,
      },
      {
        urid: 'UK1234568',
        firstName: 'Test Name 2',
        lastName: 'Test Last Name 2',
        user: aTestUser('Test Name 2', 'Test Last Name 2'),
        right: ARAccessRights.INITIATE_AND_APPROVE,
        state: 'ACTIVE',
        contact: null,
      },
    ];

    component.accountHolder = {
      id: 10001,
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

    component.authorisedRepresentatives = [];
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
      knownAs: 'KnownAs',
    });
    component.formGroup = formBuilder.group(component.getFormModel());
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate', () => {
    component = new SelectionComponent(formBuilder);
    expect(component).toBeDefined();
  });

  it('should be equal to UK1234567', () => {
    expect(
      fixture.componentInstance.fetchedAuthorisedRepresentatives[0].urid
    ).toEqual('UK1234567');
  });

  it('should be invalid at first', () => {
    expect(component.formGroup.get('arSelection').valid).toBeFalsy();
  });

  it('should set value to form idToSearch', fakeAsync(() => {
    component.formGroup.setValue({
      arSelection: {
        selectionTypeRadio: ARSelectionType.BY_ID,
        listSelect: '',
        userIdInput: 'UK1234567',
      },
    });
    expect(component.formGroup.get('arSelection').value.userIdInput).toEqual(
      'UK1234567'
    );
  }));

  it('should set value to form selectedARFromList', fakeAsync(() => {
    component.formGroup.setValue({
      arSelection: {
        selectionTypeRadio: ARSelectionType.FROM_LIST,
        listSelect: 'UK1234567',
        userIdInput: '',
      },
    });
    expect(component.formGroup.get('arSelection').value.listSelect).toEqual(
      'UK1234567'
    );
  }));

  it('should check the LoggedInUser selector ', (done) => {
    loggedinUser$ = store.select(selectLoggedInUser);
    loggedinUser$.subscribe((result) => {
      expect(result.id).toEqual('100001');
      done(); // ensure the assertions are executed
    });
  });

  test('should not be empty in order to proceed to the next page.', async () => {
    component.formGroup.setValue({
      arSelection: {
        selectionTypeRadio: ARSelectionType.FROM_LIST,
        listSelect: 'UK1234567',
        userIdInput: '',
      },
    });
    const output = spyOn(component.output, 'emit');
    component.onContinue();
    expect(output).toHaveBeenCalled();
    expect(output).toHaveBeenCalledWith('UK1234567');
  });

  test('should call onError when the drop down value is empty.', async () => {
    component.formGroup.setValue({
      arSelection: {
        selectionTypeRadio: ARSelectionType.FROM_LIST,
        listSelect: '',
        userIdInput: '',
      },
    });
    const onError = spyOn(component, 'onError');
    component.onError();
    expect(onError).toHaveBeenCalled();
  });

  test('should call onError when the search value is empty.', async () => {
    component.formGroup.setValue({
      arSelection: {
        selectionTypeRadio: ARSelectionType.BY_ID,
        listSelect: '',
        userIdInput: '',
      },
    });
    const onError = spyOn(component, 'onError');
    component.onError();
    expect(onError).toHaveBeenCalled();
  });

  function aTestUser(firstName: string, lastName: string): User {
    const user = new User();
    user.firstName = firstName;
    user.lastName = lastName;
    return user;
  }
});
