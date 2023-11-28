import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { RemoveAccountComponent } from './remove-account.component';
import { APP_BASE_HREF } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TrustedAccountStatus } from '@shared/model/account';

describe('RemoveAccountComponent', () => {
  let component: RemoveAccountComponent;
  let fixture: ComponentFixture<RemoveAccountComponent>;

  const trustedAccountActive = {
    id: 123456,
    accountFullIdentifier: 'UK-23421-234-3242',
    underSameAccountHolder: false,
    description: 'This is the description',
    name: null,
    status: TrustedAccountStatus.ACTIVE,
    activationDate: '23 Apr 2020',
    activationTime: '02:56pm',
  };

  const trustedAccountPendingActivation = {
    id: 123457,
    accountFullIdentifier: 'UK-23421-345-3242',
    underSameAccountHolder: false,
    description: 'This is the description',
    name: null,
    status: TrustedAccountStatus.PENDING_ACTIVATION,
    activationDate: '23 Apr 2020',
    activationTime: '02:56pm',
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterModule.forRoot([])],
        declarations: [RemoveAccountComponent],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(RemoveAccountComponent);
    component = fixture.componentInstance;
    component.eligibleTrustedAccounts = [
      trustedAccountActive,
      trustedAccountPendingActivation,
    ];
    component.selectedAccountsForRemoval = [trustedAccountPendingActivation];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have checked pre-selected trusted accounts', () => {
    const compiled = fixture.debugElement.nativeElement;
    let elem = compiled.querySelector('#trusted-account-1');
    expect(elem.checked).toBeTruthy();
    elem = compiled.querySelector('#trusted-account-0');
    expect(elem.checked).toBeFalsy();
  });

  it('should add/remove trusted account to/from accounts to be removed', () => {
    component.toggle(true, trustedAccountActive);
    expect(component.selectedAccountsForRemoval.length === 1);
    expect(
      component.selectedAccountsForRemoval.filter(
        (ta) =>
          ta.accountFullIdentifier ===
          trustedAccountActive.accountFullIdentifier
      ).length === 1
    );
    component.toggle(false, trustedAccountActive);
    expect(component.selectedAccountsForRemoval.length === 0);
    expect(
      component.selectedAccountsForRemoval.filter(
        (ta) =>
          ta.accountFullIdentifier ===
          trustedAccountActive.accountFullIdentifier
      ).length === 0
    );
  });
});
