import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchTasksUserCriteriaComponent } from './search-tasks-user-criteria.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ACCOUNT_TYPE_OPTIONS } from '@task-management/task-list/task-search/search-tasks-form/search-tasks-form.model';
import {
  UkProtoFormDatePickerComponent,
  UkProtoFormSelectComponent,
  UkProtoFormTextComponent,
} from '@shared/form-controls/uk-proto-form-controls';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import { ProtectPipe } from '@registry-web/shared/pipes';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { MockAuthApiService } from 'src/testing/mock-auth-api-service';
import { MockProtectPipe } from 'src/testing/mock-protect.pipe';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TaskSearchCriteria } from '@registry-web/task-management/model';
import { DatePipe } from '@angular/common';

describe('SearchTasksUserCriteriaComponent', () => {
  let component: SearchTasksUserCriteriaComponent;
  let fixture: ComponentFixture<SearchTasksUserCriteriaComponent>;

  const availableFields = [
    'accountNumber',
    'accountHolder',
    'taskStatus',
    'claimantName',
    'taskType',
    'requestId',
    'transactionId',
    'taskOutcome',
    'initiatorName',
    'accountType',
    'claimedOnFrom',
    'claimedOnTo',
    'createdOnFrom',
    'createdOnTo',
    'completedOnFrom',
    'completedOnTo',
  ];

  const initialCriteria = {
    accountNumber: null,
    accountHolder: null,
    taskStatus: 'OPEN',
    claimantName: null,
    taskType: null,
    requestId: null,
    claimedOnFrom: null,
    claimedOnTo: null,
    createdOnFrom: null,
    createdOnTo: null,
    completedOnFrom: null,
    completedOnTo: null,
    transactionId: null,
    taskOutcome: null,
    initiatorName: null,
    accountType: null,
  };

  const criteria = {
    accountNumber: 'accountNumber',
    accountHolder: 'accountHolder',
    taskStatus: 'taskStatus',
    claimantName: 'claimantName',
    taskType: 'taskType',
    requestId: 'requestId',
    claimedOnFrom: '2019-11-12',
    claimedOnTo: '2019-12-12',
    createdOnFrom: '2019-13-12',
    createdOnTo: '2019-14-12',
    completedOnFrom: '2019-15-12',
    completedOnTo: '2019-16-12',
    transactionId: 'transactionId',
    taskOutcome: 'taskOutcome',
    initiatorName: 'initiatorName',
    accountType: 'accountType',
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, NgbModule, NgbModule],
        declarations: [
          UkProtoFormTextComponent,
          UkProtoFormDatePickerComponent,
          UkProtoFormSelectComponent,
          SearchTasksUserCriteriaComponent,
          DisableControlDirective,
          MockProtectPipe,
        ],
        providers: [
          DatePipe,
          { provide: AuthApiService, useValue: MockAuthApiService },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchTasksUserCriteriaComponent);
    component = fixture.componentInstance;
    component.accountTypeOptions = ACCOUNT_TYPE_OPTIONS;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test(`the search form model contains the fields ${availableFields}`, () => {
    availableFields.forEach((controlId) =>
      expect(component.form.controls[controlId]).toBeTruthy()
    );
  });

  test(`The form value should implement the TaskSearchCriteria interface`, () => {
    expect(component.form.value).toStrictEqual(initialCriteria);
  });

  test(`The form value changes accordingly to its controls changes`, () => {
    availableFields.forEach((fieldName) => {
      component.form.controls[fieldName].setValue(criteria[fieldName]);
    });
    expect(component.form.value).toStrictEqual(criteria);
  });

  test(`the search form emits its value on sumbit`, () => {
    let emmitedCriteria;
    component.search.subscribe((value) => {
      emmitedCriteria = value;
    });
    component.onSearch();
    expect(emmitedCriteria).toBeTruthy();
  });

  test('the search form submits on search button click', () => {
    let emmitedCriteria;
    component.search.subscribe((value) => {
      emmitedCriteria = value;
    });
    fixture.debugElement
      .query(By.css('button.submit-form'))
      .nativeElement.click();
    expect(emmitedCriteria).toBeTruthy();
  });

  test('The search form clears its value on submit', () => {
    let emmitedCriteria;
    component.search.subscribe((value) => {
      emmitedCriteria = value;
    });
    availableFields.forEach((fieldName) => {
      component.form.controls[fieldName].setValue(criteria[fieldName]);
    });
    availableFields.forEach((fieldName) => {
      expect(component.form.controls[fieldName].value).toBeTruthy();
    });
    component.onClear();
    availableFields.forEach((fieldName) => {
      expect(component.form.controls[fieldName].value).toBeFalsy();
    });
    expect(emmitedCriteria).toBeTruthy();
  });

  test('The search form clears its value on click of clear button', () => {
    let emmitedCriteria;
    component.search.subscribe((value) => {
      emmitedCriteria = value;
    });
    fixture.debugElement
      .query(By.css('button.clear-form'))
      .nativeElement.click();
    availableFields.forEach((fieldName) => {
      expect(component.form.controls[fieldName].value).toBeFalsy();
    });
    expect(emmitedCriteria).toBeTruthy();
  });

  test('The search form resets to its default value on click of reset button', () => {
    let emmitedCriteria: TaskSearchCriteria;
    component.search.subscribe((value) => {
      emmitedCriteria = value;
    });
    fixture.debugElement
      .query(By.css('button.reset-form'))
      .nativeElement.click();
    availableFields.forEach((fieldName) => {
      if (fieldName == 'taskStatus') {
        expect(component.form.controls[fieldName].value).toEqual('OPEN');
      } else {
        expect(component.form.controls[fieldName].value).toBeFalsy();
      }
    });
    expect(emmitedCriteria).toBeTruthy();
  });
});
