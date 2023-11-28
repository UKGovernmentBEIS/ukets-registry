import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { StoreModule } from '@ngrx/store';
import * as accountHolderTestHelper from 'src/app/account-opening/account-holder/account-holder.test.helper';
import { Router, ActivatedRoute } from '@angular/router';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { ConnectFormDirective } from 'src/app/shared/connect-form.directive';
import { AccountHolderIndividualDetailsComponent } from '@shared/components/account/account-holder-individual-details';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';

const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));
const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));
const formBuilder = new FormBuilder();

describe('AccountHolderIndividualDetailsComponent', () => {
  let fixture: ComponentFixture<AccountHolderIndividualDetailsComponent>;
  let component: AccountHolderIndividualDetailsComponent;
  const initialState = {
    accountOpening:
      accountHolderTestHelper.aTestAccountHolderStateWithIndividual,
    shared: accountHolderTestHelper.aTestSharedState,
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [
          ConnectFormDirective,
          FormGroupDirective,
          AccountHolderIndividualDetailsComponent,
          ScreenReaderPageAnnounceDirective,
        ],
        providers: [
          { provide: Router, useValue: routerSpy },
          { provide: ActivatedRoute, useValue: activatedRouteSpy },
          { provide: FormBuilder, useValue: formBuilder },
        ],
        imports: [StoreModule.forRoot(initialState), ReactiveFormsModule],
      }).compileComponents();
      fixture = TestBed.createComponent(
        AccountHolderIndividualDetailsComponent
      );
      component = fixture.debugElement.componentInstance;
    })
  );

  test('the component is created', () => {
    expect(component).toBeTruthy();
  });
});
