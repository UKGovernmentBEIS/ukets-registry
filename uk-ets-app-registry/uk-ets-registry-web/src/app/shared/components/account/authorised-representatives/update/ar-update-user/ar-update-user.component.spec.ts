import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ArUpdateUserComponent } from './ar-update-user.component';
import {
  AuthRepContactComponent,
  AuthRepTableComponent,
} from '@shared/components/account/authorised-representatives';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { MockAuthApiService } from '../../../../../../../testing/mock-auth-api-service';
import { AuthApiService } from '../../../../../../auth/auth-api.service';
import { AccessRightsPipe, ProtectPipe } from '@shared/pipes';
import { PhoneNumberComponent } from '@shared/components/phone-number/phone-number.component';
import { ThreeLineAddressComponent } from '@shared/components/three-line-address/three-line-address.component';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import { ArDisplayNamePipe } from '@registry-web/shared/pipes/ar-display-name.pipe';

describe('ArUpdateUserComponent', () => {
  let component: ArUpdateUserComponent;
  let fixture: ComponentFixture<ArUpdateUserComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterModule.forRoot([])],
        declarations: [
          ArUpdateUserComponent,
          AuthRepTableComponent,
          AuthRepContactComponent,
          PhoneNumberComponent,
          GovukTagComponent,
          ThreeLineAddressComponent,
          ProtectPipe,
          AccessRightsPipe,
          ArDisplayNamePipe,
        ],
        providers: [
          { provide: APP_BASE_HREF, useValue: '/' },
          { provide: AuthApiService, useValue: MockAuthApiService },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ArUpdateUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
