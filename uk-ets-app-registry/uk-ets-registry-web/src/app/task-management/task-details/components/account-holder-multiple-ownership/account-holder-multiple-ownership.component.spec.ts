import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountHolderMultipleOwnershipComponent } from './account-holder-multiple-ownership.component';

describe('AccountHolderMultipleOwnershipComponent', () => {
  let component: AccountHolderMultipleOwnershipComponent;
  let fixture: ComponentFixture<AccountHolderMultipleOwnershipComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AccountHolderMultipleOwnershipComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountHolderMultipleOwnershipComponent);
    component = fixture.componentInstance;
    component.accountHolderOwnership = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
