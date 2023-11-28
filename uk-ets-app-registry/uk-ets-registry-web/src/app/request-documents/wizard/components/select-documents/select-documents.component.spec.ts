import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { SelectDocumentsComponent } from './select-documents.component';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-text/uk-proto-form-text.component';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import { RouterTestingModule } from '@angular/router/testing';

const formBuilder = new FormBuilder();

describe('SelectDocumentsComponent', () => {
  let component: SelectDocumentsComponent;
  let fixture: ComponentFixture<SelectDocumentsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterTestingModule],
        declarations: [
          SelectDocumentsComponent,
          UkProtoFormTextComponent,
          DisableControlDirective,
        ],
        providers: [{ provide: FormBuilder, useValue: formBuilder }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectDocumentsComponent);
    component = fixture.componentInstance;
    component.documentNames = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the correct title', () => {
    expect(component.getTitleText()).toEqual('Request user documents');
    component.accountHolderName = 'Test account holder';
    expect(component.getTitleText()).toEqual(
      'Request account holder documents'
    );
  });

  it('should create three document name text fields, if input is empty', () => {
    expect(component.getAllFormControls().length).toEqual(3);
  });
});
