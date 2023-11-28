import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AccountSummaryComponent } from './account-summary.component';
import { RouterLinkDirectiveStub } from '@shared/test/router-link-directive-stub';

describe('AccountSummaryComponent', () => {
  let component: AccountSummaryComponent;
  let fixture: ComponentFixture<AccountSummaryComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AccountSummaryComponent, RouterLinkDirectiveStub],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
