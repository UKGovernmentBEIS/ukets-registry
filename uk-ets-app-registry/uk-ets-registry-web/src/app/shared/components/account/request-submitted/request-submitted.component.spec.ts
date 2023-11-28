import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestSubmittedComponent } from './request-submitted.component';
import { APP_BASE_HREF } from '@angular/common';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

describe('RequestSubmittedComponent', () => {
  let component: RequestSubmittedComponent;
  let fixture: ComponentFixture<RequestSubmittedComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [RequestSubmittedComponent],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestSubmittedComponent);
    component = fixture.componentInstance;
    component.submittedIdentifier = '1000000';
    component.accountId = '10000014';
    component.urid = 'UK588332110438';
    component.notificationId = '78878';
    component.isAdmin = true;
    component.toBeLoggedOut = true;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  //See JIRA UKETS-5110
  test('should render request success message as: The registry administrator will review your request.', () => {
    fixture.detectChanges();
    const key = fixture.debugElement.queryAll(By.css('.govuk-body'))[0];
    expect(key.nativeElement.textContent).toContain(
      'The registry administrator will review your request'
    );
  });

  //See JIRA UKETS-5110
  test('should render request success message (only for admins) as: The registry administrator will review your request.', () => {
    component.showOnlyAdminMessage = true;
    fixture.detectChanges();
    const key = fixture.debugElement.queryAll(By.css('.govuk-body'))[0];
    expect(key.nativeElement.textContent).toContain(
      'The registry administrator will review your request'
    );
  });

  it('should call navigateToEmitter on link click', () => {
    component.toBeLoggedOut = true;
    fixture.detectChanges();
    const output = spyOn(component.navigateToEmitter, 'emit');
    const link = fixture.debugElement.query(By.css('#back'));

    link.triggerEventHandler('click', { button: 0 });

    expect(output).toHaveBeenCalledTimes(1);
  });

  it('should NOT call navigateToEmitter on link click', () => {
    component.toBeLoggedOut = false;
    fixture.detectChanges();
    const output = spyOn(component.navigateToEmitter, 'emit');
    const link = fixture.debugElement.query(By.css('#back'));

    link.triggerEventHandler('click', { button: 0 });

    expect(output).toHaveBeenCalledTimes(0);
  });
});
