import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { StoreModule } from '@ngrx/store';
import * as registrationTestHelper from 'src/app/registration/registration.test.helper';
import * as fixtureTestHelper from 'src/testing/helpers/from-fixture.test.helper';
import { CheckAnswersAndSubmitComponent } from './check-answers-and-submit.component';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';

describe('CheckAnswersAndSubmitComponent', () => {
  let fixture: ComponentFixture<CheckAnswersAndSubmitComponent>;
  let component: CheckAnswersAndSubmitComponent;
  const initialState = {
    registration: registrationTestHelper.aTestRegistrationState,
    shared: registrationTestHelper.aTestSharedState,
  };

  function testField(id: string, userProperty: string) {
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      expect(fixtureTestHelper.getText(fixture, '#' + id).trim()).toEqual(
        expect.stringMatching(
          '^\\s*' +
            registrationTestHelper.escapeRegExp(
              registrationTestHelper.aTestUser()[userProperty]
            ) +
            '\\s*'
        )
      );
    });
  }

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [
          CheckAnswersAndSubmitComponent,
          ScreenReaderPageAnnounceDirective,
        ],
        imports: [StoreModule.forRoot(initialState)],
      }).compileComponents();
      fixture = TestBed.createComponent(CheckAnswersAndSubmitComponent);
      component = fixture.debugElement.componentInstance;
    })
  );

  test('the component is created', () => {
    expect(component).toBeTruthy();
  });

  it('should display the first name', () => {
    testField('first-name', 'firstName');
  });

  it('should display the last name', () => {
    testField('last-name', 'lastName');
  });

  it('should display the also known as', () => {
    testField('also-known-as', 'alsoKnownAs');
  });

  it('should display the personal address', () => {
    fixture.detectChanges();
    expect(fixtureTestHelper.getText(fixture, '#personal-address')).toEqual(
      expect.stringMatching(registrationTestHelper.personalAddressRegExp())
    );
  });

  it('should display the town or city', () => {
    testField('town-or-city', 'townOrCity');
  });

  it('should display the state or province', () => {
    testField('state-or-province', 'stateOrProvince');
  });

  it('should display the country', () => {
    testField('country', 'country');
  });

  it('should display the postcode', () => {
    testField('postcode', 'postCode');
  });

  it('should display the country of birth', () => {
    fixture.detectChanges();
    expect(fixtureTestHelper.getText(fixture, '#country-of-birth')).toEqual(
      expect.stringMatching(
        '\\s*' +
          registrationTestHelper.escapeRegExp(
            registrationTestHelper.countryNameFromCode(
              registrationTestHelper.personalCountry()
            )
          ) +
          '\\s*'
      )
    );
  });

  it('should display the date of birth', () => {
    fixture.detectChanges();
    const birthDate = registrationTestHelper.aTestUser().birthDate;
    const pattern = `\\s*${birthDate.day}/${birthDate.month}/${birthDate.year}\\s*`;
    expect(fixtureTestHelper.getText(fixture, '#date-of-birth')).toEqual(
      expect.stringMatching(pattern)
    );
  });

  it('should display the phone number', () => {
    fixture.detectChanges();
    expect(fixtureTestHelper.getText(fixture, '#phone-number')).toEqual(
      registrationTestHelper.phoneNumber()
    );
  });

  it('should display the work address', () => {
    fixture.detectChanges();
    expect(fixtureTestHelper.getText(fixture, '#work-address')).toEqual(
      expect.stringMatching(registrationTestHelper.workAddressRegExp())
    );
  });

  it('should display the work town or city', () => {
    testField('work-town-or-city', 'workTownOrCity');
  });

  it('should display the work state or province', () => {
    testField('work-state-or-province', 'workStateOrProvince');
  });

  it('should display the work country', () => {
    testField('work-country', 'workCountry');
  });

  it('should display the work postcode', () => {
    testField('work-postcode', 'workPostCode');
  });
});
