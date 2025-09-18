import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { SelectDocumentFormComponent } from './select-document-form.component';
import { SharedModule } from '@registry-web/shared/shared.module';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import { RegistryDocumentCategory } from '@registry-web/documents/models/document.model';

describe('SelectDocumentFormComponent', () => {
  let component: SelectDocumentFormComponent;
  let fixture: ComponentFixture<SelectDocumentFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectDocumentFormComponent, ReactiveFormsModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectDocumentFormComponent);
    component = fixture.componentInstance;
    component.categories = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit selectedDocument event on form submission with valid form', () => {
    spyOn(component.selectedDocument, 'emit');
    const categories: RegistryDocumentCategory[] = [
      {
        id: 1,
        name: 'Category 1',
        createdOn: new Date(),
        order: 1,
        documents: [
          {
            id: 1,
            name: 'Document 1',
            title: 'title',
            order: 1,
            createdOn: new Date(),
          },
          {
            id: 2,
            name: 'Document 2',
            title: 'title',
            order: 2,
            createdOn: new Date(),
          },
        ],
      },
      {
        id: 2,
        name: 'Category 2',
        createdOn: new Date(),
        order: 2,
        documents: [
          {
            id: 3,
            name: 'Document 3',
            title: 'title',
            order: 1,
            createdOn: new Date(),
          },
          {
            id: 4,
            name: 'Document 4',
            title: 'title',
            order: 2,
            createdOn: new Date(),
          },
        ],
      },
    ];
    component.categories = categories;
    component.storedCategoryId = 1;
    component.storedDocumentId = 2;
    component.storedUpdateType = DocumentUpdateType.ADD_DOCUMENT;
    component.ngOnInit();
    component.onContinue();
    expect(component.selectedDocument.emit).toHaveBeenCalledWith({
      id: 2,
      categoryId: 1,
      name: 'Document 2',
      order: 2,
      title: 'title',
    });
  });

  it('should emit errorDetails event when form is invalid', () => {
    spyOn(component.errorDetails, 'emit');
    const categories: RegistryDocumentCategory[] = [
      {
        id: 1,
        name: 'Category 1',
        createdOn: new Date(),
        order: 1,
        documents: [
          {
            id: 1,
            name: 'Document 1',
            title: 'title',
            order: 1,
            createdOn: new Date(),
          },
          {
            id: 2,
            name: 'Document 2',
            title: 'title',
            order: 2,
            createdOn: new Date(),
          },
        ],
      },
      {
        id: 2,
        name: 'Category 2',
        createdOn: new Date(),
        order: 2,
        documents: [
          {
            id: 3,
            name: 'Document 3',
            title: 'title',
            order: 1,
            createdOn: new Date(),
          },
          {
            id: 4,
            name: 'Document 4',
            title: 'title',
            order: 2,
            createdOn: new Date(),
          },
        ],
      },
    ];
    component.categories = categories;
    component.storedCategoryId = 1;
    component.storedDocumentId = null;
    component.storedUpdateType = DocumentUpdateType.ADD_DOCUMENT;
    component.ngOnInit();
    component.onContinue();
    expect(component.errorDetails.emit).toHaveBeenCalled();
  });
});
