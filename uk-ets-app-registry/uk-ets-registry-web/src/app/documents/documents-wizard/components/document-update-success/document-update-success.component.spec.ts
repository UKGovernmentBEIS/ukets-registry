import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DocumentUpdateSuccessComponent } from './document-update-success.component';
import { SharedModule } from '@registry-web/shared/shared.module';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { of } from 'rxjs';

describe('DocumentUpdateSuccessComponent', () => {
  let component: DocumentUpdateSuccessComponent;
  let fixture: ComponentFixture<DocumentUpdateSuccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentUpdateSuccessComponent, SharedModule, RouterModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of({}),
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentUpdateSuccessComponent);
    component = fixture.componentInstance;
    component.storedUpdateType = DocumentUpdateType.ADD_DOCUMENT;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set correct text based on storedUpdateType', () => {
    component.storedUpdateType = DocumentUpdateType.ADD_DOCUMENT_CATEGORY;
    component.ngOnInit();
    expect(component.text).toEqual(
      'The category will appear in the documents page.'
    );

    component.storedUpdateType = DocumentUpdateType.UPDATE_DOCUMENT_CATEGORY;
    component.ngOnInit();
    expect(component.text).toEqual(
      'The category will appear updated according to the new changes.'
    );

    component.storedUpdateType = DocumentUpdateType.DELETE_DOCUMENT_CATEGORY;
    component.ngOnInit();
    expect(component.text).toEqual(
      'The category will not be available in the documents page.'
    );

    component.storedUpdateType = DocumentUpdateType.ADD_DOCUMENT;
    component.ngOnInit();
    expect(component.text).toEqual(
      'The document will be available in the list of documents.'
    );

    component.storedUpdateType = DocumentUpdateType.UPDATE_DOCUMENT;
    component.ngOnInit();
    expect(component.text).toEqual(
      'The updated document will be available in the list of documents.'
    );

    component.storedUpdateType = DocumentUpdateType.DELETE_DOCUMENT;
    component.ngOnInit();
    expect(component.text).toEqual(
      'The document will not be available in the list of documents.'
    );
  });
});
