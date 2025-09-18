import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { DocumentCategoryFormComponent } from './document-category-form.component';
import { SharedModule } from '@registry-web/shared/shared.module';

describe('DocumentCategoryFormComponent', () => {
  let component: DocumentCategoryFormComponent;
  let fixture: ComponentFixture<DocumentCategoryFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        DocumentCategoryFormComponent,
        ReactiveFormsModule,
        SharedModule,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentCategoryFormComponent);
    component = fixture.componentInstance;
    component.storedDocumentCategory = {
      id: 1,
      name: 'category',
      order: 1,
      createdOn: new Date(),
      documents: [
        {
          id: 1,
          name: 'docname',
          title: 'title',
          order: 1,
          createdOn: new Date(),
        },
      ],
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit handleSubmit event on form submission', () => {
    spyOn(component.handleSubmit, 'emit');
    const formValue = {
      name: 'category 2',
      order: 1,
    };
    component.ngOnInit();
    component.formGroup.patchValue(formValue);
    component.onContinue();
    expect(component.handleSubmit.emit).toHaveBeenCalledWith(formValue);
  });
});
