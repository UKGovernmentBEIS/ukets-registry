import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { ConnectFormDirective } from 'src/app/shared/connect-form.directive';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { AccountOpeningState } from '../account-opening.model';
import { IsBillablePipe } from '@shared/pipes';
import { AccountDetailsFormComponent } from '@shared/components/account/account-details/account-details-form.component';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';

const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));
const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));
const formBuilder = new FormBuilder();

describe('AccountDetailsComponent', () => {
  let fixture: ComponentFixture<AccountDetailsFormComponent>;
  let component: AccountDetailsFormComponent;
  let store: MockStore<AccountOpeningState>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [ReactiveFormsModule],
        declarations: [
          IsBillablePipe,
          ConnectFormDirective,
          FormGroupDirective,
          AccountDetailsFormComponent,
          ScreenReaderPageAnnounceDirective,
        ],
        providers: [
          { provide: Router, useValue: routerSpy },
          { provide: ActivatedRoute, useValue: activatedRouteSpy },
          { provide: FormBuilder, useValue: formBuilder },
          provideMockStore({}),
        ],
      }).compileComponents();
      fixture = TestBed.createComponent(AccountDetailsFormComponent);
      component = fixture.debugElement.componentInstance;
      store = TestBed.inject(Store) as MockStore<any>;
      store.addReducer();
    })
  );

  test('the component is created', () => {
    expect(component).toBeTruthy();
  });
});
