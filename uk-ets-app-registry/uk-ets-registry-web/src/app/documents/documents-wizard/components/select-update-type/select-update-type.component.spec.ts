import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { SelectUpdateTypeComponent } from './select-update-type.component';
import { SharedModule } from '@registry-web/shared/shared.module';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import { RegistryDocumentCategory } from '@registry-web/documents/models/document.model';

describe('SelectUpdateTypeComponent', () => {
  let component: SelectUpdateTypeComponent;
  let fixture: ComponentFixture<SelectUpdateTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [],
      imports: [SelectUpdateTypeComponent, ReactiveFormsModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectUpdateTypeComponent);
    component = fixture.componentInstance;
    component.storedUpdateType = DocumentUpdateType.ADD_DOCUMENT_CATEGORY;
    component.categories = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit selectedUpdateType event on form submission with valid form', () => {
    spyOn(component.selectedUpdateType, 'emit');
    component.storedUpdateType = DocumentUpdateType.ADD_DOCUMENT_CATEGORY;
    component.categories = [];
    component.ngOnInit();
    component.onContinue();
    expect(component.selectedUpdateType.emit).toHaveBeenCalledWith(
      DocumentUpdateType.ADD_DOCUMENT_CATEGORY
    );
  });

  it('should emit errorDetails event when form is invalid', () => {
    spyOn(component.errorDetails, 'emit');
    component.storedUpdateType = null;
    component.categories = [];
    component.ngOnInit();
    component.onContinue();
    expect(component.errorDetails.emit).toHaveBeenCalled();
  });
});
