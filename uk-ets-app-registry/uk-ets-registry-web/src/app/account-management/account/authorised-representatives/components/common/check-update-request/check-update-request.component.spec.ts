import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckUpdateRequestComponent } from './check-update-request.component';
import {
  ArUpdateAccessRightsComponent,
  ArUpdateTypeComponent,
  ArUpdateUserComponent,
  AuthRepContactComponent,
  AuthRepTableComponent,
} from '@shared/components/account/authorised-representatives';
import {
  AccessRightsPipe,
  AuthorisedRepresentativeUpdateTypePipe,
  ProtectPipe,
} from '@shared/pipes';
import { PhoneNumberComponent } from '@shared/components/phone-number/phone-number.component';
import { ThreeLineAddressComponent } from '@shared/components/three-line-address/three-line-address.component';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { AuthApiService } from '../../../../../../auth/auth-api.service';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import { MockAuthApiService } from '../../../../../../../testing/mock-auth-api-service';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ArDisplayNamePipe } from '@registry-web/shared/pipes/ar-display-name.pipe';

const formBuilder = new FormBuilder();

describe('CheckUpdateRequestComponent', () => {
  let component: CheckUpdateRequestComponent;
  let fixture: ComponentFixture<CheckUpdateRequestComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterModule.forRoot([]), ReactiveFormsModule],
        declarations: [
          CheckUpdateRequestComponent,
          ArUpdateTypeComponent,
          ArUpdateAccessRightsComponent,
          ArUpdateUserComponent,
          AuthorisedRepresentativeUpdateTypePipe,
          AccessRightsPipe,
          AuthRepTableComponent,
          AuthRepContactComponent,
          PhoneNumberComponent,
          ThreeLineAddressComponent,
          ProtectPipe,
          GovukTagComponent,
          FormGroupDirective,
          ArDisplayNamePipe,
        ],
        providers: [
          { provide: APP_BASE_HREF, useValue: '/' },
          { provide: AuthApiService, useValue: MockAuthApiService },
          { provide: FormBuilder, useValue: formBuilder },
        ],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckUpdateRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
