import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Store } from '@ngrx/store';
import { AccessRightsComponent } from './access-rights.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { RulesForAuthorisedRepresentativeComponent } from '@shared/components/account/rules-for-authorised-representative';
import { ProtectPipe } from '@shared/pipes';
import { AuthApiService } from '../../../auth/auth-api.service';
import { MockAuthApiService } from '../../../../testing/mock-auth-api-service';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { AuthorisedRepresentativeDetailsPipe } from '@registry-web/shared/pipes/authorised-representative-details.pipe';
describe('AccessRightsComponent', () => {
  let component: AccessRightsComponent;
  let fixture: ComponentFixture<AccessRightsComponent>;
  const formBuilder: FormBuilder = new FormBuilder();
  let store: MockStore<any>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        AccessRightsComponent,
        RulesForAuthorisedRepresentativeComponent,
        ProtectPipe,
        ScreenReaderPageAnnounceDirective,
        AuthorisedRepresentativeDetailsPipe,
      ],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [
        provideMockStore(),
        { provide: FormBuilder, useValue: formBuilder },
        { provide: AuthApiService, useValue: MockAuthApiService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccessRightsComponent);
    component = fixture.componentInstance;
    fixture.componentInstance._authorisedRepresentative = {
      urid: '',
      firstName: '',
      lastName: '',
      right: null,
      state: null,
      user: null,
      contact: null,
    };
    store = TestBed.inject(Store) as MockStore<any>;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate', () => {
    const comp: AccessRightsComponent = new AccessRightsComponent(formBuilder);
    expect(comp).toBeDefined();
  });
});
