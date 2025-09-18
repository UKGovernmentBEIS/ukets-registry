import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { UserDetailsUpdateTaskDetailsComponent } from './user-details-update-task-details.component';
import { RequestType } from '@task-management/model';
import { RouterModule } from '@angular/router';
import { PersonalDetailsSummaryChangesComponent } from '@shared/components/user/personal-details-summary-changes';
import { WorkDetailsSummaryChangesComponent } from '@shared/components/user/work-details-summary-changes';
import { APP_BASE_HREF } from '@angular/common';
import { CountryNamePipe, FormatUkDatePipe } from '@shared/pipes';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import { IUser } from '@shared/user';
import { SummaryListComponent } from '@shared/summary-list';
import { provideMockStore } from '@ngrx/store/testing';
import { MemorablePhraseSummaryChangesComponent } from '@shared/components/user/memorable-phrase-summary-changes';

describe('UserDetailsUpdateTaskDetailsComponent', () => {
  let component: UserDetailsUpdateTaskDetailsComponent;
  let fixture: ComponentFixture<UserDetailsUpdateTaskDetailsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      declarations: [
        UserDetailsUpdateTaskDetailsComponent,
        PersonalDetailsSummaryChangesComponent,
        WorkDetailsSummaryChangesComponent,
        MemorablePhraseSummaryChangesComponent,
        SummaryListComponent,
      ],
      providers: [
        { provide: APP_BASE_HREF, useValue: '/' },
        FormatUkDatePipe,
        CountryNamePipe,
        provideMockStore(),
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDetailsUpdateTaskDetailsComponent);
    component = fixture.componentInstance;
    component.userDetailsUpdateTaskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.USER_DETAILS_UPDATE_REQUEST,
      current: {
        firstName: 'currentName',
        lastName: 'currentLastName',
        countryOfBirth: 'GR',
        birthDate: {
          day: '30',
          month: '12',
          year: '1960',
        },
        workEmailAddress: 'test@test.com',
        workBuildingAndStreet: 'currentWorkAddress',
        workTownOrCity: 'currentWorkCity',
        workStateOrProvince: 'currentWorkStateOrProvince',
        workPostCode: 'currentWorkPostCode',
        workCountry: 'UK',
      } as IUser,
      changed: {
        firstName: 'changedName',
        lastName: 'changedLastName',
        countryOfBirth: 'GR',
        birthDate: {
          day: '1',
          month: '1',
          year: '1950',
        },
        workEmailAddress: 'test@test.com',
        workBuildingAndStreet: 'changedWorkAddress',
        workTownOrCity: 'changedWorkCity',
        workStateOrProvince: 'changedWorkStateOrProvince',
        workPostCode: '',
        workCountry: 'GR',
        memorablePhrase: 'a phrase',
      } as IUser,
      userDetails: [
        {
          username: '',
          email: '',
          firstName: '',
          lastName: '',
          eligibleForSpecificActions: false,
          userRoles: [],
          attributes: {
            urid: [''],
            alsoKnownAs: [''],
            buildingAndStreet: [''],
            buildingAndStreetOptional: [''],
            buildingAndStreetOptional2: [''],
            country: [''],
            countryOfBirth: [''],
            postCode: [''],
            townOrCity: [''],
            stateOrProvince: [''],
            birthDate: [''],
            state: ['ENROLLED'],
            workBuildingAndStreet: [''],
            workBuildingAndStreetOptional: [''],
            workBuildingAndStreetOptional2: [''],
            workCountry: [''],
            workMobileCountryCode: [''],
            workMobilePhoneNumber: [''],
            workAlternativeCountryCode: [''],
            workAlternativePhoneNumber: [''],
            noMobilePhoneNumberReason: [''],
            workPostCode: [''],
            workTownOrCity: [''],
            workStateOrProvince: [''],
            lastLoginDate: [''],
            memorablePhrase: '',
          },
        },
      ],
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
