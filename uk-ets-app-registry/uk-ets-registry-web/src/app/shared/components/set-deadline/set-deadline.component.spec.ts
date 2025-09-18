import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  UkProtoFormCommentAreaComponent,
  UkProtoFormSelectComponent,
} from '@shared/form-controls/uk-proto-form-controls';
import { SetDeadlineComponent } from './set-deadline.component';

const formBuilder = new FormBuilder();

describe('SetDeadlineComponent', () => {
  let component: SetDeadlineComponent;
  let fixture: ComponentFixture<SetDeadlineComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule],
        declarations: [SetDeadlineComponent],
        providers: [{ provide: FormBuilder, useValue: formBuilder }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SetDeadlineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit deadline when form is submitted', () => {
    const setDeadlineSpy = spyOn(component.setDeadline, 'emit');
    const expectedDeadline = new Date('2023-12-31');

    component.formGroup.setValue({
      deadline: expectedDeadline,
    });

    component.onSubmit();

    expect(setDeadlineSpy).toHaveBeenCalledWith(expectedDeadline);
  });

  it('should have the correct form validators', () => {
    component.initialDeadline = '2023-12-01';
    component.ngOnInit();

    const deadlineControl = component.formGroup.get('deadline');

    deadlineControl.setValue(null);
    expect(deadlineControl.hasError('required')).toBeTruthy();

    deadlineControl.setValue('2023-12-01');
    expect(deadlineControl.hasError('notInitial')).toBeTruthy();

    deadlineControl.setValue('2023-12-02');
    expect(deadlineControl.hasError('notInitial')).toBeFalsy();
  });
});
