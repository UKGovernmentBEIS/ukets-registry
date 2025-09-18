import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { AllocationJobStatusFiltersComponent } from './allocation-job-status-filters.component';
import { Store } from '@ngrx/store';

describe('AllocationJobStatusFiltersComponent', () => {
  let component: AllocationJobStatusFiltersComponent;
  let fixture: ComponentFixture<AllocationJobStatusFiltersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [],
      imports: [ReactiveFormsModule, AllocationJobStatusFiltersComponent],
      providers: [{ provide: Store, useValue: {} }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AllocationJobStatusFiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should emit toggleCriteria event when toggleCriteriaClick is called', () => {
    const emitSpy = jest.spyOn(component.toggleCriteria, 'emit');
    component.toggleCriteriaClick();
    expect(emitSpy).toHaveBeenCalled();
  });

  it('should emit submitClick event when doSubmit is called', () => {
    const submitClickSpy = jest.spyOn(component.submitClick, 'emit');
    const searchEmitSpy = jest.spyOn(component.search, 'emit');
    component.formGroup.setValue({
      requestIdentifier: 'Test',
      id: '123',
      status: 'In Progress',
      executionDateFrom: '',
      executionDateTo: '',
    });
    component.doSubmit();
    expect(submitClickSpy).toHaveBeenCalled();
    expect(searchEmitSpy).toHaveBeenCalledWith(component.formGroup.value);
  });

  it('should reset formGroup when onClear is called', () => {
    component.formGroup.setValue({
      requestIdentifier: 'Test',
      id: '123',
      status: 'In Progress',
      executionDateFrom: '',
      executionDateTo: '',
    });
    component.onClear();
    expect(component.formGroup.value).toEqual({
      requestIdentifier: null,
      id: null,
      status: null,
      executionDateFrom: null,
      executionDateTo: null,
    });
  });
});
