import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { SignRequestFormComponent } from '@signing/components';
import { ReactiveFormsModule } from '@angular/forms';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-text/uk-proto-form-text.component';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';

describe('SignRequestFormComponent', () => {
  let component: SignRequestFormComponent;
  let fixture: ComponentFixture<SignRequestFormComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule],
        declarations: [
          SignRequestFormComponent,
          UkProtoFormTextComponent,
          DisableControlDirective,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SignRequestFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render the correct texts in the labels', () => {
    // fixture.detectChanges() is not needed here because text is static there is no binding
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toMatch('Sign proposal');
    expect(compiled.querySelector('p').textContent).toMatch(
      'By signing this proposal you confirm that the information above is correct.'
    );
    expect(compiled.querySelector('label').textContent).toMatch(
      'Enter the 6-digit code shown in your authenticator app'
    );
  });

  test('otoCode is omitted in the parent component when user enters an otpCode value in the input', () => {
    let otpCodeOutput = ''; // expected output omitted
    // Subscribe to the output as does the parent component does.
    component.otpCode.subscribe((value: string) => (otpCodeOutput = value));
    const otpCodeInput: HTMLInputElement = fixture.nativeElement.querySelector(
      'input'
    );
    // simulate user entering an otpcode in the input box
    const userOtpCode = '007007';
    otpCodeInput.value = userOtpCode;

    // Dispatch a DOM event so that Angular learns of input value change.
    // In older browsers, such as IE, you might need a CustomEvent instead. See
    // https://developer.mozilla.org/en-US/docs/Web/API/CustomEvent/CustomEvent#Polyfill
    otpCodeInput.dispatchEvent(new Event('input'));

    // fixture.detectChanges(); again not needed because it does not update the DOM
    expect(otpCodeOutput).toBe(userOtpCode);
  });
});
