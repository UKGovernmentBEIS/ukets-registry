import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PasswordRevealDirective } from '@shared/directives/password-reveal.directive';
import { By } from '@angular/platform-browser';

@Component({
  selector: 'app-test-comp',
  template: `
    <input
      appPasswordReveal
      id="password"
      class="govuk-input govuk-!-width-three-quarters"
      type="password"
    />
  `,
})
class TestComponent {}

describe('PasswordRevealDirective', () => {
  let fixture: ComponentFixture<TestComponent>;
  let passwordField: HTMLInputElement;
  let passwordRevealButton: HTMLButtonElement;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      declarations: [PasswordRevealDirective, TestComponent],
    }).createComponent(TestComponent);

    fixture.detectChanges();

    passwordField = fixture.debugElement.query(
      By.directive(PasswordRevealDirective)
    ).nativeElement;
    // for some reason going through the debugElement does not find the dynamically added button.
    // I even changed the directive to use renderer2 as suggested here but still not working:
    //https://github.com/angular/angular/issues/32082
    passwordRevealButton = <HTMLButtonElement>(
      document.getElementsByClassName('govuk-button')[0]
    );
  });

  it('should initially have password type and button text Show', () => {
    expect(passwordField.type).toEqual('password');
    expect(passwordRevealButton.textContent).toEqual('Show');
    expect(passwordRevealButton.getAttribute('tabIndex')).toEqual('-1');
  });

  it('should  have text type and button text Hide when clicked', () => {
    passwordRevealButton.click();

    expect(passwordField.type).toEqual('text');
    expect(passwordRevealButton.textContent).toEqual('Hide');
  });
});
