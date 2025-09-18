import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { RouterTestingModule } from '@angular/router/testing';
import { AccountSummaryComponent } from '@shared/components/transactions/account-summary/account-summary.component';
import { RequestType, TaskOutcome } from '@task-management/model';
import { ARAccessRights } from '@shared/model/account';
import {
  ArUpdateAccessRightsComponent,
  ArUpdateUserComponent,
  AuthRepContactComponent,
  AuthRepTableComponent,
} from '@shared/components/account/authorised-representatives';
import { AccessRightsPipe } from '@shared/pipes';
import { AuthorisedRepresentativesUpdateTaskDetailsComponent } from '@task-details/components/authorise-representative-update';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { PhoneNumberComponent } from '@shared/components/phone-number/phone-number.component';
import { ThreeLineAddressComponent } from '@shared/components/three-line-address/three-line-address.component';
import { MockProtectPipe } from '../../../../../testing/mock-protect.pipe';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import { User } from '@shared/user';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import { ArDisplayNamePipe } from '@registry-web/shared/pipes/ar-display-name.pipe';

describe('AuthoriseRepresentativesUpdateTaskDetailsComponent', () => {
  let component: AuthorisedRepresentativesUpdateTaskDetailsComponent;
  let fixture: ComponentFixture<AuthorisedRepresentativesUpdateTaskDetailsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [CommonModule, RouterTestingModule],
      declarations: [
        AuthorisedRepresentativesUpdateTaskDetailsComponent,
        AuthRepContactComponent,
        ArUpdateUserComponent,
        PhoneNumberComponent,
        ThreeLineAddressComponent,
        AuthRepTableComponent,
        ArUpdateAccessRightsComponent,
        AccountSummaryComponent,
        GovukTagComponent,
        AccessRightsPipe,
        MockProtectPipe,
        ArDisplayNamePipe,
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(
      AuthorisedRepresentativesUpdateTaskDetailsComponent
    );
    component = fixture.componentInstance;
    component.taskNotYetApproved = TaskOutcome.SUBMITTED_NOT_YET_APPROVED;
    component.authoriseRepresentativeTaskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
      accountInfo: {
        identifier: 10001,
        fullIdentifier: 'GB-100-1002-1-84',
        accountName: ' Party Holding 1',
        accountHolderName: 'Name 1',
      },
      newUser: {
        urid: '',
        firstName: '',
        lastName: '',
        right: ARAccessRights.INITIATE_AND_APPROVE,
        state: 'ACTIVE',
        user: new User(),
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
      currentUser: {
        urid: '',
        firstName: '',
        lastName: '',
        right: ARAccessRights.INITIATE_AND_APPROVE,
        state: 'ACTIVE',
        user: new User(),
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
      arUpdateType: AuthorisedRepresentativesUpdateType.ADD,
      arUpdateAccessRight: ARAccessRights.INITIATE,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
