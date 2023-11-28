import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountTransferRequestSubmittedComponent } from './account-transfer-request-submitted.component';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';

describe('AccountTransferRequestSubmittedComponent', () => {
  let component: AccountTransferRequestSubmittedComponent;
  let fixture: ComponentFixture<AccountTransferRequestSubmittedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AccountTransferRequestSubmittedComponent],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountTransferRequestSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  //See JIRA UKETS-5110
  test('should render request success message as: The registry administrator will review your request.', () => {
    component.submittedIdentifier = 'UK678544';
    component.accountId = '78878';
    fixture.detectChanges();
    const key = fixture.debugElement.queryAll(By.css('.govuk-body'))[0];
    expect(key.nativeElement.textContent).toContain(
      'The registry administrator will review your request'
    );
  });
});
