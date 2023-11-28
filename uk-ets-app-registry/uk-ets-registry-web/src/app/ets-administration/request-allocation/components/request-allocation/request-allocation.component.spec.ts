import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { RequestAllocationComponent } from './request-allocation.component';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

describe('RequestAllocationComponent', () => {
  let component: RequestAllocationComponent;
  let fixture: ComponentFixture<RequestAllocationComponent>;

  const allocationYears: number[] = [2021, 2022];

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [RequestAllocationComponent, UkRadioInputComponent],
        imports: [ReactiveFormsModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestAllocationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render title in h1 tag', () => {
    component.allocationYears = allocationYears;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain(
      'Choose the allocation details'
    );
  });

  test('should have as caption `Request allocation of UK allowances`', () => {
    component.allocationYears = allocationYears;
    fixture.detectChanges();
    const caption = fixture.debugElement.query(By.css('.govuk-caption-xl'));
    expect(caption.nativeElement.textContent).toContain(
      'Request allocation of UK allowances'
    );
  });
});
