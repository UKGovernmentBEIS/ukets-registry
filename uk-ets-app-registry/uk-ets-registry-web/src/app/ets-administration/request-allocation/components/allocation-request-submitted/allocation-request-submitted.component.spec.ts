import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { AllocationRequestSubmittedComponent } from './allocation-request-submitted.component';
import { By } from 'protractor';

describe('AllocationRequestSubmittedComponent', () => {
  let component: AllocationRequestSubmittedComponent;
  let fixture: ComponentFixture<AllocationRequestSubmittedComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AllocationRequestSubmittedComponent],
        imports: [ReactiveFormsModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AllocationRequestSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render title in p', () => {
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('p').textContent).toContain(
      'Your request will be reviewed by the Registry Administrator'
    );
  });
});
