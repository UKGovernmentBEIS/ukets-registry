import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { SelectCategoryFormComponent } from './select-category-form.component';
import { SharedModule } from '@registry-web/shared/shared.module';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import { RegistryDocumentCategory } from '@registry-web/documents/models/document.model';
import { of } from 'rxjs';

describe('SelectCategoryFormComponent', () => {
  let component: SelectCategoryFormComponent;
  let fixture: ComponentFixture<SelectCategoryFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SharedModule, SelectCategoryFormComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectCategoryFormComponent);
    component = fixture.componentInstance;
    component.storedUpdateType = DocumentUpdateType.ADD_DOCUMENT_CATEGORY;
    component.categories = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit selectedCategory when onContinue is called with valid form', () => {
    spyOn(component.selectedCategory, 'emit');
    component.storedUpdateType = DocumentUpdateType.ADD_DOCUMENT_CATEGORY;
    component.categories = [
      {
        id: 1,
        name: 'Category 1',
        order: 1,
        createdOn: new Date(),
        documents: [],
      },
      {
        id: 2,
        name: 'Category 2',
        order: 2,
        createdOn: new Date(),
        documents: [],
      },
    ];
    fixture.detectChanges();
    component.ngOnInit();
    component.selectCategoryFormGroup.get('selectCategory').setValue(1);
    component.onContinue();
    expect(component.selectedCategory.emit).toHaveBeenCalledWith(1);
  });

  it('should emit errorDetails when onContinue is called with invalid form', () => {
    spyOn(component.errorDetails, 'emit');
    component.storedUpdateType = DocumentUpdateType.DELETE_DOCUMENT_CATEGORY;
    component.categories = [
      {
        id: 1,
        name: 'Category 1',
        order: 1,
        createdOn: new Date(),
        documents: [
          {
            id: 1,
            name: 'Document 1',
            order: 1,
            title: 'title',
            createdOn: new Date(),
          },
        ],
      },
      {
        id: 2,
        name: 'Category 2',
        order: 2,
        createdOn: new Date(),
        documents: [],
      },
    ];
    fixture.detectChanges();
    component.ngOnInit();
    component.selectCategoryFormGroup.get('selectCategory').setValue(1);
    component.onContinue();
    expect(component.errorDetails.emit).toHaveBeenCalled();
  });

  it('should return true from showErrors when form is invalid and touched', () => {
    component.storedUpdateType = DocumentUpdateType.ADD_DOCUMENT_CATEGORY;
    component.categories = [
      {
        id: 1,
        name: 'Category 1',
        order: 1,
        createdOn: new Date(),
        documents: [],
      },
      {
        id: 2,
        name: 'Category 2',
        order: 2,
        createdOn: new Date(),
        documents: [],
      },
    ];
    fixture.detectChanges();
    component.ngOnInit();
    component.selectCategoryFormGroup.get('selectCategory').setValue(null);
    component.selectCategoryFormGroup.markAllAsTouched();
    expect(component.showErrors()).toBe(true);
  });
});
