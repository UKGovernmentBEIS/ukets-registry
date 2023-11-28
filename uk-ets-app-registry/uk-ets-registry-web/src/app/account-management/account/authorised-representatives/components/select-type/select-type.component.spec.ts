import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectTypeComponent } from './select-type.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { APP_BASE_HREF } from '@angular/common';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { Configuration } from '@shared/configuration/configuration.interface';
import {
  ArSubmittedUpdateRequest,
  AuthorisedRepresentative,
} from '@shared/model/account';

describe('SelectTypeComponent', () => {
  let component: SelectTypeComponent;
  let fixture: ComponentFixture<SelectTypeComponent>;
  let store: MockStore<any>;
  let configuration$: Observable<Array<Configuration>>;
  let mockConfigurationSelector: any;
  const initialStateConfiguration = [
    {
      'server.port': '8080',
    },
    {
      'business.property.account.max.number.of.authorised.representatives': '8',
    },
    {
      'business.property.account.min.number.of.authorised.representatives': '2',
    },
    { 'application.url': 'http://localhost:4200' },
  ];
  let authorisedRepresentatives$: Observable<Array<AuthorisedRepresentative>>;
  let pendingARRequests$: Observable<Array<ArSubmittedUpdateRequest>>;
  let mockAuthorizeRepresentativesSelector: any;
  let mockPendingARRequestsSelector: any;
  const initialStateAuthorizeRepresentatives = [
    {
      urid: 'UK405681794859',
      firstName: '',
      lastName: '',
      right: null,
      state: null,
      user: null,
      contact: null,
    },
    {
      urid: 'UK405681794860',
      firstName: '',
      lastName: '',
      right: null,
      state: null,
      user: null,
      contact: null,
    },
  ];

  const initialStatePendingARRequests = [];

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [SelectTypeComponent, UkRadioInputComponent],
        providers: [
          provideMockStore(),
          { provide: APP_BASE_HREF, useValue: '/' },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectTypeComponent);
    fixture.componentInstance.authorisedReps =
      initialStateAuthorizeRepresentatives;
    fixture.componentInstance.configuration = initialStateConfiguration;
    fixture.componentInstance.pendingARRequests = initialStatePendingARRequests;
    component = fixture.componentInstance;
    store = TestBed.inject(Store) as MockStore<any>;

    mockConfigurationSelector = store.overrideSelector(
      'configuration',
      initialStateConfiguration
    );
    mockAuthorizeRepresentativesSelector = store.overrideSelector(
      'authorizeRepresentatives',
      initialStateAuthorizeRepresentatives
    );
    mockPendingARRequestsSelector = store.overrideSelector(
      'pendingARRequests',
      initialStatePendingARRequests
    );
    fixture.detectChanges();
  });

  it('should check the configuration selector ', (done) => {
    configuration$ = store.select('configuration');
    configuration$.subscribe((result) => {
      expect(result[1]).toEqual(initialStateConfiguration[1]);
      done(); // ensure the assertions are executed
    });
  });

  it('should check the authorize representatives selector ', (done) => {
    authorisedRepresentatives$ = store.select('authorizeRepresentatives');
    authorisedRepresentatives$.subscribe((result) => {
      expect(result[1]).toEqual(initialStateAuthorizeRepresentatives[1]);
      done(); // ensure the assertions are executed
    });
  });

  it('should check the pending AR Requests selector ', (done) => {
    pendingARRequests$ = store.select('pendingARRequests');
    pendingARRequests$.subscribe((result) => {
      expect(result[1]).toEqual(initialStatePendingARRequests[1]);
      done(); // ensure the assertions are executed
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be equal to 8', () => {
    expect(
      fixture.componentInstance.configuration[1][
        'business.property.account.max.number.of.authorised.representatives'
      ]
    ).toEqual('8');
  });

  it('should be equal to 2', () => {
    expect(
      fixture.componentInstance.configuration[2][
        'business.property.account.min.number.of.authorised.representatives'
      ]
    ).toEqual('2');
  });
});
