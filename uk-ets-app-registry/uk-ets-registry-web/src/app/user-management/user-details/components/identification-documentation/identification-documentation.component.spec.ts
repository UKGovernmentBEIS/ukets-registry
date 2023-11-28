import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { IdentificationDocumentationComponent } from './identification-documentation.component';
import { GdsDateTimeShortPipe, ProtectPipe } from '@shared/pipes';
import { AuthApiService } from '../../../../auth/auth-api.service';
import { MockAuthApiService } from '../../../../../testing/mock-auth-api-service';
import { StoreModule } from '@ngrx/store';
import * as fromAuth from '../../../../auth/auth.reducer';
import { IdentificationDocumentationListComponent } from '@shared/components/identification-documentation-list';
import { RequestDocPipe } from '@shared/pipes/request-doc.pipe';
import { RouterTestingModule } from '@angular/router/testing';

describe('IdentificationDocumentationComponent', () => {
  let component: IdentificationDocumentationComponent;
  let fixture: ComponentFixture<IdentificationDocumentationComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          StoreModule.forRoot({
            auth: fromAuth.reducer,
          }),
          RouterTestingModule,
        ],
        declarations: [
          IdentificationDocumentationComponent,
          IdentificationDocumentationListComponent,
          ProtectPipe,
          GdsDateTimeShortPipe,
          RequestDocPipe,
        ],
        providers: [{ provide: AuthApiService, useValue: MockAuthApiService }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(IdentificationDocumentationComponent);
    component = fixture.componentInstance;
    component.user = {
      username: 'user_details_test',
      email: 'ukets_user@gov.uk',
      firstName: 'James',
      lastName: 'Bond',
      userRoles: [],
      eligibleForSpecificActions: false,
      attributes: {
        urid: ['UK230169410292'],
        alsoKnownAs: ['also known as Value'],
        buildingAndStreet: ['Ethnikis Antistasis 67'],
        buildingAndStreetOptional: [''],
        buildingAndStreetOptional2: [''],
        country: ['GR'],
        countryOfBirth: ['GR'],
        postCode: ['15231'],
        townOrCity: ['Athens'],
        stateOrProvince: ['Attica'],
        birthDate: ['3/31/1990'],
        state: ['ATTICA'],
        workBuildingAndStreet: ['Kifisias 8'],
        workBuildingAndStreetOptional: [''],
        workBuildingAndStreetOptional2: [''],
        workCountry: ['GR'],
        workCountryCode: [''],
        workEmailAddress: ['ukets_user_work@gov.uk'],
        workEmailAddressConfirmation: [''],
        workPhoneNumber: ['2222422'],
        workPostCode: [''],
        workTownOrCity: ['Athens'],
        workStateOrProvince: ['Attica'],
      },
    };
    component.documents = [
      {
        id: 1,
        name: 'File 1',
        type: 'Proof of identity',
        uploadedDate: new Date(),
      },
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate', () => {
    component = new IdentificationDocumentationComponent();
    expect(component).toBeDefined();
  });

  it('should be equal to File 1', () => {
    expect(component.documents.length).toEqual(1);
    expect(component.documents[0].name).toEqual('File 1');
  });
});
