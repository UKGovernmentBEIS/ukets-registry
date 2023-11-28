import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideMockStore } from '@ngrx/store/testing';

import {
  EmissionsTableUploadComponent,
  EmissionsTableUploadContainerComponent,
} from '@emissions-table/components/emissions-table-upload';
import { UkSelectFileComponent } from '@shared/form-controls';

describe.skip('EmissionsTableUploadContainerComponent', () => {
  let component: EmissionsTableUploadContainerComponent;
  let fixture: ComponentFixture<EmissionsTableUploadContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [
        EmissionsTableUploadContainerComponent,
        EmissionsTableUploadComponent,
        UkSelectFileComponent,
      ],
      providers: [provideMockStore()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmissionsTableUploadContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
