import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SelectEntityFormComponent } from './select-entity-form.component';
import { NoteType } from '@registry-web/shared/model';

describe('SelectEntityFormComponent', () => {
  let component: SelectEntityFormComponent;
  let fixture: ComponentFixture<SelectEntityFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      declarations: [],
    }).compileComponents();

    fixture = TestBed.createComponent(SelectEntityFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit handleSubmit when form is submitted', () => {
    spyOn(component.handleSubmit, 'emit');
    const selectedType = NoteType.ACCOUNT;

    component.storedType = selectedType;
    component.formGroup.controls['noteType'].setValue(selectedType);
    component.formGroup.controls['noteType'].updateValueAndValidity();
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      const submitButton = fixture.nativeElement.querySelector('#continue');
      submitButton.click();

      expect(component.handleSubmit.emit).toHaveBeenCalledWith(selectedType);
    });
  });

  it('should emit handleCancel when cancel link is clicked', () => {
    spyOn(component.handleCancel, 'emit');
    const selectedType = NoteType.ACCOUNT;
    component.formGroup.controls['noteType'].setValue(selectedType);
    component.formGroup.controls['noteType'].updateValueAndValidity();
    fixture.detectChanges();

    component.storedType = selectedType;
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      const cancelButton = fixture.nativeElement.querySelector('.govuk-link');
      cancelButton.click();

      expect(component.handleCancel.emit).toHaveBeenCalledWith(selectedType);
    });
  });
});
