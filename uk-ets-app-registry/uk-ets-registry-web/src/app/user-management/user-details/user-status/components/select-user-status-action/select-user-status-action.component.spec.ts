import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectUserStatusActionComponent } from './select-user-status-action.component';
import { UserStatusActionOption } from '@user-management/model';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { RouterLinkDirectiveStub } from '@shared/test/router-link-directive-stub';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { By } from '@angular/platform-browser';

describe('SelectUserStatusActionComponent', () => {
  let component: SelectUserStatusActionComponent;
  let fixture: ComponentFixture<SelectUserStatusActionComponent>;

  const userStatusActions: UserStatusActionOption[] = [
    {
      label: 'Validate user',
      value: 'VALIDATE',
      enabled: true,
      newStatus: 'VALIDATED',
    },
  ];

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          SelectUserStatusActionComponent,
          UkRadioInputComponent,
          RouterLinkDirectiveStub,
        ],
        imports: [ReactiveFormsModule, RouterModule.forRoot([])],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectUserStatusActionComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render title in h1 tag', () => {
    component.allowedUserStatusActions = userStatusActions;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain('Select Action');
  });

  test('should have as caption `Change the user status`', () => {
    component.allowedUserStatusActions = userStatusActions;
    fixture.detectChanges();
    const caption = fixture.debugElement.query(By.css('.govuk-caption-xl'));
    expect(caption.nativeElement.textContent).toContain(
      'Change the user status'
    );
  });

  test('the account status action types are rendered correctly', () => {
    component.allowedUserStatusActions = userStatusActions;
    fixture.detectChanges();

    userStatusActions.forEach((action, index) => {
      const labelName = fixture.debugElement.nativeElement.querySelector(
        `label[for=userStatusAction${index}]`
      ).textContent;
      expect(action.label).toBe(labelName.trim());
    });
  });

  test('selection of user status action is required', () => {
    component.allowedUserStatusActions = userStatusActions;
    fixture.detectChanges();

    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    expect(
      component.genericValidator.processMessages(component.formGroup)
        .userStatusAction
    ).toBe('You must select an action');

    // error message
    const errorMessage = fixture.debugElement.query(
      By.css('.govuk-error-message')
    ).nativeElement.textContent;
    expect(errorMessage.trim()).toBe('Error:You must select an action');
  });

  test('user status action is set correctly', () => {
    component.allowedUserStatusActions = userStatusActions;
    fixture.detectChanges();

    userStatusActions.forEach((type, index) => {
      const firstSelection = fixture.debugElement.query(
        By.css(`#userStatusAction${index}`)
      );

      firstSelection.triggerEventHandler('change', {
        target: firstSelection.nativeElement,
      });

      const continueButton = fixture.debugElement.query(By.css('button'));
      continueButton.nativeElement.click();
      fixture.detectChanges();

      const userStatusAction = fixture.componentInstance.formGroup.get(
        'userStatusAction'
      ).value;
      expect(component.getAllFormGroupValidationErrors()).toBeNull();
      expect(userStatusAction).toBe(userStatusActions[index].value);
    });
  });
});
