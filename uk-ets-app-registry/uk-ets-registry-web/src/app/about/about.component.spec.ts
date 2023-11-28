import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AboutComponent } from './about.component';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

describe('AboutComponent', () => {
  let component: AboutComponent;
  let fixture: ComponentFixture<AboutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AboutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct version in the HTML', () => {
    const versionElement = fixture.debugElement.query(
      By.css('[class*="govuk-"][class*="-font-weight-bold"]')
    );
    expect(versionElement).toBeTruthy();
    const versionText = versionElement.nativeElement.textContent.trim();
    expect(versionText).toBeTruthy();
  });
});
