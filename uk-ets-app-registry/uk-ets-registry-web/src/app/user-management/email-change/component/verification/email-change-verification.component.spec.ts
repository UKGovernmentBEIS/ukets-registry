import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { EmailChangeVerificationComponent } from './email-change-verification.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('EmailChangeVerificationComponent', () => {
  let component: EmailChangeVerificationComponent;
  let fixture: ComponentFixture<EmailChangeVerificationComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [EmailChangeVerificationComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailChangeVerificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
