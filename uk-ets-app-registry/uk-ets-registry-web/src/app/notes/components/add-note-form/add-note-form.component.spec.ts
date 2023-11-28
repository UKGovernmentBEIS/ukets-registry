import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SharedModule } from '@registry-web/shared/shared.module';
import { AddNoteFormComponent } from './add-note-form.component';
import { ReactiveFormsModule } from '@angular/forms';

describe('AddNoteFormComponent', () => {
  let component: AddNoteFormComponent;
  let fixture: ComponentFixture<AddNoteFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SharedModule],
      declarations: [AddNoteFormComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddNoteFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit handleSubmit when form is submitted', () => {
    spyOn(component.handleSubmit, 'emit');
    const noteDescription = 'Test note description';

    component.storedNote = noteDescription;
    component.formGroup.controls['noteDescription'].setValue(noteDescription);
    component.formGroup.controls['noteDescription'].updateValueAndValidity();
    fixture.detectChanges();

    const submitButton = fixture.nativeElement.querySelector('#continue');
    submitButton.click();

    expect(component.handleSubmit.emit).toHaveBeenCalledWith(noteDescription);
  });

  it('should emit handleCancel when cancel link is clicked', () => {
    spyOn(component.handleCancel, 'emit');
    const noteDescription = 'Test note description';

    component.storedNote = noteDescription;
    component.formGroup.controls['noteDescription'].setValue(noteDescription);
    component.formGroup.controls['noteDescription'].updateValueAndValidity();
    fixture.detectChanges();

    const cancelButton = fixture.nativeElement.querySelector('.govuk-link');
    cancelButton.click();

    expect(component.handleCancel.emit).toHaveBeenCalledWith(noteDescription);
  });
});
