import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmUserStatusActionComponent } from './confirm-user-status-action.component';
import { RouterLinkDirectiveStub } from '@shared/test/router-link-directive-stub';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { By } from '@angular/platform-browser';
import { UserStatusPipe } from '@user-management/user-details/user-status/pipes/user-status.pipe';
import { UkProtoFormCommentAreaComponent } from '@shared/form-controls/uk-proto-form-controls';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';

describe('ConfirmUserStatusActionComponent', () => {
  let component: ConfirmUserStatusActionComponent;
  let fixture: ComponentFixture<ConfirmUserStatusActionComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          ConfirmUserStatusActionComponent,
          UkProtoFormCommentAreaComponent,
          UserStatusPipe,
          RouterLinkDirectiveStub,
          DisableControlDirective,
        ],
        imports: [ReactiveFormsModule, RouterModule.forRoot([])],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmUserStatusActionComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render title in h1 tag', () => {
    component.currentUserStatus = 'REGISTERED';
    component.userStatusAction = {
      label: 'Validate user',
      value: 'VALIDATE',
      newStatus: 'VALIDATED',
    };
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain(
      'Check your update and confirm'
    );
  });

  test('should render warning text in govuk-warning-text__text', () => {
    component.currentUserStatus = 'ENROLLED';
    component.userStatusAction = {
      label: 'Validate user',
      value: 'VALIDATE',
      newStatus: 'VALIDATED',
      message:
        'A print letter with registry activation code task will be created',
    };
    fixture.detectChanges();
    const warningText = fixture.debugElement.query(
      By.css('.govuk-warning-text__text')
    );
    expect(warningText.nativeElement.textContent).toContain(
      'A print letter with registry activation code task will be created'
    );
  });

  test('should have as caption `Change the user status`', () => {
    component.currentUserStatus = 'REGISTERED';
    component.userStatusAction = {
      label: 'Validate user',
      value: 'VALIDATE',
      newStatus: 'VALIDATED',
    };
    fixture.detectChanges();
    const caption = fixture.debugElement.query(By.css('.govuk-caption-xl'));
    expect(caption.nativeElement.textContent).toContain(
      'Change the user status'
    );
  });

  test('the current user status is rendered correctly', () => {
    component.currentUserStatus = 'REGISTERED';
    component.userStatusAction = {
      label: 'Validate user',
      value: 'VALIDATE',
      newStatus: 'VALIDATED',
    };
    fixture.detectChanges();
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[0];
    expect(key.nativeElement.textContent).toContain('Current user status');
    const value = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__value')
    )[0];
    expect(value.nativeElement.textContent).toContain('Registered');
  });

  test('the user status action is rendered correctly', () => {
    component.currentUserStatus = 'REGISTERED';
    component.userStatusAction = {
      label: 'Validate user',
      value: 'VALIDATE',
      newStatus: 'VALIDATED',
    };
    fixture.detectChanges();
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[1];
    expect(key.nativeElement.textContent).toContain('Action');
    const value = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__value')
    )[1];
    expect(value.nativeElement.textContent).toContain('Validate user');
  });

  test('the new user status is rendered correctly', () => {
    component.currentUserStatus = 'REGISTERED';
    component.userStatusAction = {
      label: 'Validate user',
      value: 'VALIDATE',
      newStatus: 'VALIDATED',
    };
    fixture.detectChanges();
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[2];
    expect(key.nativeElement.textContent).toContain('New user status');
    const value = fixture.debugElement.queryAll(
      By.css('.panel-change-notification')
    )[0];
    expect(value.nativeElement.textContent).toContain('Validated');
  });

  test('the comment textarea is rendered correctly', () => {
    component.currentUserStatus = 'ACTIVE';
    component.userStatusAction = {
      label: 'Suspent user',
      value: 'SUSPEND',
      newStatus: 'SUSPENDED',
    };
    fixture.detectChanges();
    const key = fixture.debugElement.query(By.css('.govuk-label'));
    expect(key.nativeElement.textContent).toContain(
      ' Explain why you decided to suspend this user '
    );
    const value = fixture.debugElement.queryAll(By.css('textarea'));
    expect(value).toBeTruthy();
  });

  test('comment is required', () => {
    component.currentUserStatus = 'ACTIVE';
    component.userStatusAction = {
      label: 'Suspent user',
      value: 'SUSPEND',
      newStatus: 'SUSPENDED',
    };
    fixture.detectChanges();

    const contButton = fixture.debugElement.query(By.css('button'));
    contButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    expect(
      component.genericValidator.processMessages(component.formGroup).comment
    ).toBe('You must enter a comment');

    //error message
    const errorMessage = fixture.debugElement.query(
      By.css('.govuk-error-message')
    ).nativeElement.textContent;
    expect(errorMessage.trim()).toBe('Error: You must enter a comment');
  });

  test('comment for user status change is set correctly', () => {
    component.currentUserStatus = 'ACTIVE';
    component.userStatusAction = {
      label: 'Suspent user',
      value: 'SUSPEND',
      newStatus: 'SUSPENDED',
    };
    fixture.detectChanges();

    const textarea = fixture.debugElement.query(By.css('textarea'));
    textarea.nativeElement.value = 'User is no longer eligible for transfers';
    textarea.nativeElement.dispatchEvent(new Event('input'));

    fixture.detectChanges();

    const comment = fixture.componentInstance.formGroup.get('comment').value;

    expect(comment).toBe(textarea.nativeElement.value);
  });
});
