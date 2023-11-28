import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { UkProtoFormTextareaComponent } from '@shared/form-controls/uk-proto-form-controls';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { MemorablePhraseComponent } from '@shared/components/user/memorable-phrase/memorable-phrase.component';

const formBuilder = new FormBuilder();

describe('MemorablePhraseComponent', () => {
  let component: MemorablePhraseComponent;
  let fixture: ComponentFixture<MemorablePhraseComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [
          ConnectFormDirective,
          MemorablePhraseComponent,
          UkProtoFormTextareaComponent,
          ScreenReaderPageAnnounceDirective,
        ],
        providers: [
          { provide: APP_BASE_HREF, useValue: '/' },
          { provide: FormBuilder, useValue: formBuilder },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(MemorablePhraseComponent);
    component = fixture.componentInstance;
    component.user = {
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
      birthDate: { day: '', month: '', year: '' },
      countryOfBirth: '',
      workCountryCode: '',
      workPhoneNumber: '',
      workEmailAddress: '',
      workEmailAddressConfirmation: '',
      workBuildingAndStreet: '',
      workBuildingAndStreetOptional: '',
      workBuildingAndStreetOptional2: '',
      workTownOrCity: '',
      workStateOrProvince: '',
      workPostCode: '',
      workCountry: '',
      urid: '',
      state: '',
      status: 'ENROLLED',
      memorablePhrase: 'Memorable Phrase test',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
