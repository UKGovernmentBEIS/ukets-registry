import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArInAccountsComponent } from './ar-in-accounts.component';
import { provideMockStore } from '@ngrx/store/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AccessRightsPipe } from '@shared/pipes';
import { ARAccessRights } from '@shared/model/account';
import { AuthApiService } from '../../../../auth/auth-api.service';
import { MockAuthApiService } from '../../../../../testing/mock-auth-api-service';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import { MockProtectPipe } from '../../../../../testing/mock-protect.pipe';

describe('ArInAccountsComponent', () => {
  let component: ArInAccountsComponent;
  let fixture: ComponentFixture<ArInAccountsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        ArInAccountsComponent,
        AccessRightsPipe,
        MockProtectPipe,
        GovukTagComponent,
      ],
      imports: [RouterTestingModule],
      providers: [
        provideMockStore(),
        { provide: AuthApiService, useValue: MockAuthApiService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ArInAccountsComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.arInAccount = [
      {
        accountName: 'Account Name',
        accountIdentifier: 1002,
        accountHolderName: 'Account Holder Name',
        right: ARAccessRights.INITIATE_AND_APPROVE,
        state: 'ACTIVE',
        accountFullIdentifier: '',
      },
    ];

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call go to account with specific identifier', () => {
    const identifier =
      fixture.componentInstance.arInAccount[0].accountIdentifier;
    const anchor = fixture.debugElement.nativeElement.querySelector('a');
    expect(anchor.getAttribute('href')).toEqual('/account/' + identifier);
  });

  it('should be equal to account identifier 1002', () => {
    expect(fixture.componentInstance.arInAccount[0].accountIdentifier).toEqual(
      1002
    );
  });
});
