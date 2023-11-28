import { waitForAsync, TestBed, ComponentFixture } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ToggleButtonComponent } from '@shared/search/toggle-button/toggle-button.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { SearchMessagesFormComponent } from '@kp-administration/itl-messages/components';
import { MessageSearchCriteria } from '@kp-administration/itl-messages/model';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-text/uk-proto-form-text.component';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import { UkProtoFormDatePickerComponent } from '@shared/form-controls/uk-proto-form-controls';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DatePipe } from '@angular/common';

describe('SearchMessagesFormComponent', () => {
  let component: SearchMessagesFormComponent;
  let fixture: ComponentFixture<SearchMessagesFormComponent>;

  const availableFields = ['messageId', 'messageDateFrom', 'messageDateTo'];

  const initialCriteria: MessageSearchCriteria = {
    messageId: null,
    messageDateFrom: null,
    messageDateTo: null,
  };

  const criteria: MessageSearchCriteria = {
    messageId: 5,
    messageDateFrom: '2020-11-06',
    messageDateTo: '2020-11-06',
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, NgbModule],
        declarations: [
          SearchMessagesFormComponent,
          ToggleButtonComponent,
          UkProtoFormDatePickerComponent,
          UkProtoFormTextComponent,
          DisableControlDirective,
        ],
        providers: [DatePipe],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchMessagesFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  test('the component is created', () => {
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

  test('The search form clears its value on click of clear button', () => {
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

    fixture.debugElement
      .query(By.css('button.clear-form'))
      .nativeElement.click();
    availableFields.forEach((fieldName) => {
      expect(component.form.controls[fieldName].value).toBeFalsy();
    });
  });
});
