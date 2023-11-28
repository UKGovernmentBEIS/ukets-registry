import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { StoreModule } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { ConnectFormDirective } from 'src/app/shared/connect-form.directive';
import { AccountHolderIndividualContactDetailsComponent } from '@shared/components/account/account-holder-individual-contact-details';
import {
  aTestAccountHolderStateWithIndividual,
  aTestSharedState,
} from '@account-opening/account-holder/account-holder.test.helper';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';

const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));
const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));
const formBuilder = new FormBuilder();

describe('AccountHolderIndividualContactDetailsComponent', () => {
  let fixture: ComponentFixture<AccountHolderIndividualContactDetailsComponent>;
  let component: AccountHolderIndividualContactDetailsComponent;
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
          AccountHolderIndividualContactDetailsComponent,
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
        AccountHolderIndividualContactDetailsComponent
      );
      component = fixture.debugElement.componentInstance;
    })
  );

  test('the component is created', () => {
    expect(component).toBeTruthy();
  });
});
