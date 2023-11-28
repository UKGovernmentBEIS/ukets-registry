import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { CheckAllocationRequestComponent } from './check-allocation-request.component';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { SharedModule } from '@registry-web/shared/shared.module';

describe('CheckAllocationRequestComponent', () => {
  let component: CheckAllocationRequestComponent;
  let fixture: ComponentFixture<CheckAllocationRequestComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [CheckAllocationRequestComponent],
        imports: [ReactiveFormsModule, RouterTestingModule, SharedModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckAllocationRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render title with class govuk-heading-l', () => {
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    const title = fixture.debugElement.query(By.css('.govuk-heading-l'));
    expect(title.nativeElement.textContent).toContain('Check this request');
  });
});
