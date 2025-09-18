import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AuthorisedRepresentativesComponent } from './authorised-representatives.component';
import { StoreModule } from '@ngrx/store';
import * as fromAuth from '@registry-web/auth/auth.reducer';
import { RouterModule } from '@angular/router';
import {
  AccessRightsPipe,
  AuthorisedRepresentativeUpdateTypePipe,
  ProtectPipe,
} from '@shared/pipes';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { MockAuthApiService } from '../../../../../testing/mock-auth-api-service';
import { APP_BASE_HREF } from '@angular/common';
import {
  AuthRepContactComponent,
  AuthRepTableComponent,
} from '@shared/components/account/authorised-representatives';
import { GovukTagComponent } from '@shared/govuk-components';
import { User } from '@shared/user';
import { ARAccessRights } from '@shared/model/account';
import { PhoneNumberComponent } from '@shared/components/phone-number/phone-number.component';
import { ThreeLineAddressComponent } from '@shared/components/three-line-address/three-line-address.component';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { ArDisplayNamePipe } from '@registry-web/shared/pipes/ar-display-name.pipe';

describe('AuthorisedRepresentativesComponent', () => {
  let component: AuthorisedRepresentativesComponent;
  let fixture: ComponentFixture<AuthorisedRepresentativesComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({
          auth: fromAuth.reducer,
        }),
        RouterModule.forRoot([]),
      ],
      declarations: [
        AuthorisedRepresentativesComponent,
        AuthRepTableComponent,
        ProtectPipe,
        GovukTagComponent,
        AccessRightsPipe,
        AuthRepContactComponent,
        PhoneNumberComponent,
        ThreeLineAddressComponent,
        ArDisplayNamePipe,
      ],
      providers: [
        { provide: AuthApiService, useValue: MockAuthApiService },
        {
          provide: AuthorisedRepresentativeUpdateTypePipe,
          useValue: AuthorisedRepresentativesUpdateType.ADD,
        },
        { provide: APP_BASE_HREF, useValue: '/' },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthorisedRepresentativesComponent);
    component = fixture.componentInstance;
    component.authorisedReps = [
      {
        urid: 'UK123456',
        firstName: 'James',
        lastName: 'Bond',
        right: ARAccessRights.INITIATE_AND_APPROVE,
        state: 'ACTIVE',
        user: {
          emailAddress: '',
          emailAddressConfirmation: '',
          userId: '',
          username: '',
          password: '',
          firstName: '',
          lastName: '',
          alsoKnownAs: '',
          buildingAndStreet: '',
          buildingAndStreetOptional: '',
          buildingAndStreetOptional2: '',
          postCode: '',
          townOrCity: '',
          stateOrProvince: '',
          country: '',
          birthDate: { day: null, month: null, year: null },
          countryOfBirth: '',
          workMobileCountryCode: '',
          workMobilePhoneNumber: '',
          workAlternativeCountryCode: '',
          workAlternativePhoneNumber: '',
          noMobilePhoneNumberReason: '',
          workBuildingAndStreet: '',
          workBuildingAndStreetOptional: '',
          workBuildingAndStreetOptional2: '',
          workTownOrCity: '',
          workStateOrProvince: '',
          workPostCode: '',
          workCountry: '',
          urid: '',
          state: 'REGISTERED',
          status: 'REGISTERED',
          memorablePhrase: '',
        } as User,
        contact: {
          city: '',
          country: '',
          emailAddress: '',
          line1: '',
          line2: '',
          line3: '',
          postCode: '',
          mobileCountryCode: '',
          mobilePhoneNumber: '',
          alternativeCountryCode: '',
          alternativePhoneNumber: '',
          noMobilePhoneNumberReason: '',
        },
      },
    ];
    component.pendingRequests = [];
    component.accountId = '';
    component.canRequestUpdate = false;
    component.isAdmin = false;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
