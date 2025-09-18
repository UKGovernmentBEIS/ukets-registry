import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { ExclusionReasonComponent } from './exclusion-reason.component';
import { ReactiveFormsModule } from '@angular/forms';
import { VerifiedEmissions } from '@registry-web/account-shared/model';
import { By } from '@angular/platform-browser';

describe('ExclusionReasonComponent', () => {
  let component: ExclusionReasonComponent;
  let fixture: ComponentFixture<ExclusionReasonComponent>;

  const mockEmissionEntries: VerifiedEmissions[] | any[] = [
    { year: 2022, reportableEmissions: '5' },
    { year: 2023, reportableEmissions: 'Excluded' },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ExclusionReasonComponent, ReactiveFormsModule],
    });
    fixture = TestBed.createComponent(ExclusionReasonComponent);
    component = fixture.componentInstance;
    component.year = 2024;
    component.emissionEntries = mockEmissionEntries;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should set hasEmissions to false if no emissions exist for the selected year', () => {
    component.emissionEntries = [];
    fixture.detectChanges();
    expect(component.hasEmissions).toBe(false);
  });

  it('should call submitReason when the form is submitted', fakeAsync(() => {
    spyOn(component.submitReason, 'emit');
    const submitButton = fixture.debugElement.query(
      By.css('button[type="submit"]')
    ).nativeElement;

    component.formGroup.controls['exclusionReason'].setValue(
      'Testing exclusion reason'
    );

    submitButton.click();
    tick();
    fixture.detectChanges();

    expect(component.submitReason.emit).toHaveBeenCalledWith(
      'Testing exclusion reason'
    );
  }));
});
