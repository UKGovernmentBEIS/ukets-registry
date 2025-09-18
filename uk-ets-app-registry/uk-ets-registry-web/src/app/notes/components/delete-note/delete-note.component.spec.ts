import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DeleteNoteComponent } from './delete-note.component';

describe('DeleteNoteComponent', () => {
  let component: DeleteNoteComponent;
  let fixture: ComponentFixture<DeleteNoteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteNoteComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteNoteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit handleDelete when delete button is clicked', () => {
    spyOn(component.handleDelete, 'emit');
    const deleteButton = fixture.nativeElement.querySelector(
      '.govuk-button--warning'
    );
    deleteButton.click();
    expect(component.handleDelete.emit).toHaveBeenCalled();
  });
});
