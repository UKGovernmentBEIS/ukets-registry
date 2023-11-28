import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectRepresentativeTableComponent } from '@authorised-representatives/components';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  AccessRightsPipe,
  AuthorisedRepresentativeUpdateTypePipe,
} from '@shared/pipes';
import { AuthRepContactComponent } from '@shared/components/account/authorised-representatives';
import { PhoneNumberComponent } from '@shared/components/phone-number/phone-number.component';
import { ThreeLineAddressComponent } from '@shared/components/three-line-address/three-line-address.component';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import { ArDisplayNamePipe } from '@registry-web/shared/pipes/ar-display-name.pipe';

describe('SelectRepresentativeTableComponent', () => {
  let component: SelectRepresentativeTableComponent;
  let fixture: ComponentFixture<SelectRepresentativeTableComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [
          SelectRepresentativeTableComponent,
          AuthRepContactComponent,
          PhoneNumberComponent,
          ThreeLineAddressComponent,
          AccessRightsPipe,
          GovukTagComponent,
          AuthorisedRepresentativeUpdateTypePipe,
          ArDisplayNamePipe,
        ],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectRepresentativeTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
