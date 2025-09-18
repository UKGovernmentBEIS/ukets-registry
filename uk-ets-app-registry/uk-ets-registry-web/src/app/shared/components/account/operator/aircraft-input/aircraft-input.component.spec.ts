import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import * as operatorTestHelper from '@account-opening/operator/operator.test.helper';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { ActivatedRoute, Router } from '@angular/router';
import { StoreModule } from '@ngrx/store';
import { AircraftInputComponent } from '@shared/components/account/operator/aircraft-input/aircraft-input.component';
import { OperatorType, Regulator } from '@shared/model/account';
import { By } from '@angular/platform-browser';
import { ExistingEmitterIdAsyncValidator } from '@registry-web/shared/validation/existing-emitter-id-async-validator';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClientTestingModule } from '@angular/common/http/testing';

const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));
const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));
const formBuilder = new FormBuilder();

describe('AircraftInputComponent', () => {
  let fixture: ComponentFixture<AircraftInputComponent>;
  let component: AircraftInputComponent;
  const initialState = {
    accountOpening: operatorTestHelper.aTestOperatorState,
    shared: operatorTestHelper.aTestSharedState,
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [
          ConnectFormDirective,
          FormGroupDirective,
          AircraftInputComponent,
          ScreenReaderPageAnnounceDirective,
        ],
        providers: [
          { provide: Router, useValue: routerSpy },
          { provide: ActivatedRoute, useValue: activatedRouteSpy },
          { provide: FormBuilder, useValue: formBuilder },
          { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: 'https://apiBaseUrl' },
          ExistingEmitterIdAsyncValidator,
        ],
        imports: [StoreModule.forRoot(initialState), ReactiveFormsModule, HttpClientTestingModule],
      }).compileComponents();
      fixture = TestBed.createComponent(AircraftInputComponent);
      component = fixture.debugElement.componentInstance;
    })
  );

  test('the component is created', () => {
    expect(component).toBeTruthy();
  });

  test('First year of verified emission submission is required', () => {
    component.aircraftOperator = {
      type: OperatorType.AIRCRAFT_OPERATOR,
      identifier: 1000009,
      monitoringPlan: {
        id: '12345DD',
      },
      emitterId:'35675656767HT',
      regulator: Regulator.DAERA,
      changedRegulator: Regulator.DAERA,
      firstYear: null,
      lastYear: null,
    };
    fixture.detectChanges();

    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    expect(
      component.genericValidator.processMessages(component.formGroup).firstYear
    ).toBe('Enter the first year of verified emission submission');
  });

  test('Year cannot be less than 2021', () => {
    component.aircraftOperator = {
      type: OperatorType.AIRCRAFT_OPERATOR,
      identifier: 1000010,
      monitoringPlan: {
        id: '12345DD',
      },
      emitterId:'35675656767HT',
      regulator: Regulator.DAERA,
      changedRegulator: Regulator.DAERA,
      firstYear: '2020',
      lastYear: '2021',
    };
    fixture.detectChanges();

    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    expect(
      component.genericValidator.processMessages(component.formGroup).firstYear
    ).toBe('Year cannot be less than 2021');
  });

  test('Year cannot be greater than 2100', () => {
    component.aircraftOperator = {
      type: OperatorType.AIRCRAFT_OPERATOR,
      identifier: 1000010,
      monitoringPlan: {
        id: '12345DD',
      },
      emitterId:'35675656767HT',
      regulator: Regulator.DAERA,
      changedRegulator: Regulator.DAERA,
      firstYear: '2101',
      lastYear: '2021',
    };
    fixture.detectChanges();

    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    expect(
      component.genericValidator.processMessages(component.formGroup).firstYear
    ).toBe('Year cannot be greater than 2100');
  });

  test('Invalid year number', () => {
    component.aircraftOperator = {
      type: OperatorType.AIRCRAFT_OPERATOR,
      identifier: 1000010,
      monitoringPlan: {
        id: '12345DD',
      },
      emitterId:'35675656767HT',
      regulator: Regulator.DAERA,
      changedRegulator: Regulator.DAERA,
      firstYear: '21xy',
      lastYear: '2021',
    };
    fixture.detectChanges();

    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    expect(
      component.genericValidator.processMessages(component.formGroup).firstYear
    ).toBe('Invalid year number');
  });
});
