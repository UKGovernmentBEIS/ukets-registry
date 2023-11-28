import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { EmissionsTableUploadComponent } from '@emissions-table/components/emissions-table-upload/emissions-table-upload.component';
import { UkSelectFileComponent } from '@shared/form-controls';

describe('EmissionsTableUploadComponent', () => {
  let component: EmissionsTableUploadComponent;
  let fixture: ComponentFixture<EmissionsTableUploadComponent>;
  const initialStateConfig = [
    {
      'registry.file.emissions.table.type':
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    },
    {
      'registry.file.max.size': '2048',
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule],
      declarations: [UkSelectFileComponent, EmissionsTableUploadComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmissionsTableUploadComponent);
    component = fixture.componentInstance;
    component.configuration = initialStateConfig;
    component.isInProgress = false;
    component.fileProgress = 0;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
