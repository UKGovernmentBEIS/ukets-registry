import { APP_BASE_HREF } from '@angular/common';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { MessageFormComponent } from '@kp-administration/itl-messages/components';
import { UkProtoFormCommentAreaComponent } from '@shared/form-controls/uk-proto-form-controls';

describe('MessageFormComponent', () => {
  let component: MessageFormComponent;
  let fixture: ComponentFixture<MessageFormComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [MessageFormComponent, UkProtoFormCommentAreaComponent],
        imports: [ReactiveFormsModule, RouterModule.forRoot([])],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // test('content is set correctly', () => {
  //   const contentInput = fixture.debugElement.query(By.css('textarea'));
  //   contentInput.nativeElement.value = 'A message to ITL.';
  //   contentInput.nativeElement.dispatchEvent(new Event('input'));

  //   fixture.detectChanges();
  //   const content = fixture.componentInstance.formGroup.get('content').value;
  //   expect(content).toBe(contentInput.nativeElement.value);
  // });

  test('content is required', () => {
    fixture.detectChanges();
    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    expect(
      component.genericValidator.processMessages(component.formGroup).content
    ).toBe('Message content is required.');

    // error message
    const errorMessage = fixture.debugElement.query(
      By.css('.govuk-error-message')
    ).nativeElement.textContent;
    expect(errorMessage.trim()).toBe('Error: Message content is required.');
  });
});
