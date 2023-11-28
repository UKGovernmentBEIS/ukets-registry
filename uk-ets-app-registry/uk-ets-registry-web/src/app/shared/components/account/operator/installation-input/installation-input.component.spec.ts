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
import { InstallationInputComponent } from '@shared/components/account/operator/installation-input/installation-input.component';

const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));
const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));
const formBuilder = new FormBuilder();

describe('InstallationInputComponent', () => {
  let fixture: ComponentFixture<InstallationInputComponent>;
  let component: InstallationInputComponent;
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
          InstallationInputComponent,
          ScreenReaderPageAnnounceDirective,
        ],
        providers: [
          { provide: Router, useValue: routerSpy },
          { provide: ActivatedRoute, useValue: activatedRouteSpy },
          { provide: FormBuilder, useValue: formBuilder },
        ],
        imports: [StoreModule.forRoot(initialState), ReactiveFormsModule],
      }).compileComponents();
      fixture = TestBed.createComponent(InstallationInputComponent);
      component = fixture.debugElement.componentInstance;
    })
  );

  test('the component is created', () => {
    expect(component).toBeTruthy();
  });
});
