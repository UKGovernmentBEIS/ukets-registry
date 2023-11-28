import {
  ComponentFixture,
  inject,
  TestBed,
  waitForAsync,
} from '@angular/core/testing';
import { AllocationTableUploadComponent } from '@allocation-table/components';
import {
  FormBuilder,
  FormControlDirective,
  FormControlName,
  FormGroupDirective,
  FormsModule,
  NgControl,
  ReactiveFormsModule,
} from '@angular/forms';
import { UkSelectFileComponent } from '@shared/form-controls/uk-select-file';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Store } from '@ngrx/store';
import { RouterModule } from '@angular/router';

const formBuilder = new FormBuilder();

describe('AllocationTableUploadComponent', () => {
  let component: AllocationTableUploadComponent;
  let fixture: ComponentFixture<AllocationTableUploadComponent>;
  let store: MockStore<any>;
  let mockConfigurationSelector: any;
  const initialStateConfiguration = [
    {
      'registry.file.allocation.table.type':
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    },
    {
      'registry.file.max.size': '2048',
    },
  ];

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, RouterModule],
        declarations: [
          AllocationTableUploadComponent,
          FormGroupDirective,
          UkSelectFileComponent,
          FormControlDirective,
          FormControlName,
        ],
        providers: [
          provideMockStore(),
          { provide: FormBuilder, useValue: formBuilder },
          NgControl,
        ],
      }).compileComponents();
    })
  );

  beforeEach(inject([FormBuilder], (fb: FormBuilder) => {
    fixture = TestBed.createComponent(AllocationTableUploadComponent);
    fixture.componentInstance.configuration = initialStateConfiguration;
    component = fixture.componentInstance;
    store = TestBed.inject(Store) as MockStore<any>;
    mockConfigurationSelector = store.overrideSelector(
      'configuration',
      initialStateConfiguration
    );
    component.formGroup = fb.group({
      uploadFile: [''],
    });
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
