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
import { OperatorType, Regulator } from '@shared/model/account';
import { By } from '@angular/platform-browser';
import { MaritimeInputComponent } from '@shared/components/account/operator/maritime-input/maritime-input.component';
import { ExistingEmitterIdAsyncValidator } from '@registry-web/shared/validation/existing-emitter-id-async-validator';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));
const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));
const formBuilder = new FormBuilder();
const existingEmitterIdAsyncValidator =
  describe('MaritimeInputComponent', () => {
    let fixture: ComponentFixture<MaritimeInputComponent>;
    let component: MaritimeInputComponent;
    // let httpMock: HttpTestingController;

    const initialState = {
      accountOpening: operatorTestHelper.aTestOperatorState,
      shared: operatorTestHelper.aTestSharedState,
    };

    beforeEach(waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [
          ConnectFormDirective,
          FormGroupDirective,
          MaritimeInputComponent,
          ScreenReaderPageAnnounceDirective,
        ],
        providers: [
          { provide: Router, useValue: routerSpy },
          { provide: ActivatedRoute, useValue: activatedRouteSpy },
          { provide: FormBuilder, useValue: formBuilder },
          {
            provide: UK_ETS_REGISTRY_API_BASE_URL,
            useValue: 'https://apiBaseUrl',
          },
          ExistingEmitterIdAsyncValidator,
        ],
        imports: [
          StoreModule.forRoot(initialState),
          ReactiveFormsModule,
          HttpClientTestingModule,
        ],
      }).compileComponents();
      fixture = TestBed.createComponent(MaritimeInputComponent);
      component = fixture.debugElement.componentInstance;
    }));

    test('the component is created', () => {
      expect(component).toBeTruthy();
    });

    test('First year of verified emission submission is required', () => {
      component.maritimeOperator = {
        type: OperatorType.MARITIME_OPERATOR,
        identifier: 1000009,
        imo: 'imo123',
        monitoringPlan: {
          id: '12345DD',
        },
        emitterId: '5653357886HT5',
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
        component.genericValidator.processMessages(component.formGroup)
          .firstYear
      ).toBe('Enter the first year of verified emission submission');
    });

    test('Year cannot be less than 2026', () => {
      component.maritimeOperator = {
        type: OperatorType.MARITIME_OPERATOR,
        identifier: 1000010,
        imo: 'imo123',
        monitoringPlan: {
          id: '12345DD',
        },
        emitterId: '5653357886HT5',
        regulator: Regulator.DAERA,
        changedRegulator: Regulator.DAERA,
        firstYear: '2020',
        lastYear: '2021',
      };
      component.emissionStartYear = 2026;
      fixture.detectChanges();

      const continueButton = fixture.debugElement.query(By.css('button'));
      continueButton.nativeElement.click();
      fixture.detectChanges();

      expect(component.getAllFormGroupValidationErrors()).toBeDefined();
      expect(
        component.genericValidator.processMessages(component.formGroup)
          .firstYear
      ).toBe('Year cannot be less than 2026');
    });

    test('Year cannot be greater than 2100', () => {
      component.maritimeOperator = {
        type: OperatorType.MARITIME_OPERATOR,
        identifier: 1000010,
        imo: 'imo123',
        monitoringPlan: {
          id: '12345DD',
        },
        emitterId: '5653357886HT5',
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
        component.genericValidator.processMessages(component.formGroup)
          .firstYear
      ).toBe('Year cannot be greater than 2100');
    });

    test('Invalid year number', () => {
      component.maritimeOperator = {
        type: OperatorType.MARITIME_OPERATOR,
        identifier: 1000010,
        imo: 'imo123',
        monitoringPlan: {
          id: '12345DD',
        },
        emitterId: '5653357886HT5',
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
        component.genericValidator.processMessages(component.formGroup)
          .firstYear
      ).toBe('Invalid year number');
    });

    test('Monitoring plan ID must be set', () => {
      component.maritimeOperator = {
        type: OperatorType.MARITIME_OPERATOR,
        identifier: 1000010,
        imo: 'imo123',
        monitoringPlan: {
          id: '',
        },
        emitterId: '5653357886HT5',
        regulator: Regulator.DAERA,
        changedRegulator: Regulator.DAERA,
        firstYear: '2026',
        lastYear: '',
      };
      fixture.detectChanges();

      const continueButton = fixture.debugElement.query(By.css('button'));
      continueButton.nativeElement.click();
      fixture.detectChanges();

      expect(component.getAllFormGroupValidationErrors()).toBeDefined();
      expect(
        component.genericValidator.processMessages(component.formGroup).id
      ).toBe('Enter the Monitoring plan ID');
    });

    test('Company IMO number must be set', () => {
      component.maritimeOperator = {
        type: OperatorType.MARITIME_OPERATOR,
        identifier: 1000010,
        imo: '',
        monitoringPlan: {
          id: '12345DD',
        },
        emitterId: '5653357886HT5',
        regulator: Regulator.DAERA,
        changedRegulator: Regulator.DAERA,
        firstYear: '2026',
        lastYear: '2026',
      };
      fixture.detectChanges();

      const continueButton = fixture.debugElement.query(By.css('button'));
      continueButton.nativeElement.click();
      fixture.detectChanges();

      expect(component.getAllFormGroupValidationErrors()).toBeDefined();
      expect(
        component.genericValidator.processMessages(component.formGroup).imo
      ).toBe('Enter the Company IMO number');
    });

    test('Emitter ID must be set', () => {
      component.maritimeOperator = {
        type: OperatorType.MARITIME_OPERATOR,
        identifier: 1000010,
        imo: 'imo123',
        monitoringPlan: {
          id: '12345DD',
        },
        emitterId: '',
        regulator: Regulator.DAERA,
        changedRegulator: Regulator.DAERA,
        firstYear: '2026',
        lastYear: '2026',
      };
      fixture.detectChanges();

      const continueButton = fixture.debugElement.query(By.css('button'));
      continueButton.nativeElement.click();
      fixture.detectChanges();

      expect(component.getAllFormGroupValidationErrors()).toBeDefined();
      expect(
        component.genericValidator.processMessages(component.formGroup)
          .emitterId
      ).toBe('Enter the Emitter ID');
    });
  });
