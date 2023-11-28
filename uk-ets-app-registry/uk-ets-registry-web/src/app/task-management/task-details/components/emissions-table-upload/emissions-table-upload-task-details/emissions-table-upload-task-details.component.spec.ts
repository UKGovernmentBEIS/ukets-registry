import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { EmissionsTableUploadTaskDetailsComponent } from './emissions-table-upload-task-details.component';

describe('EmissionsTableUploadTaskDetailsComponent', () => {
  let component: EmissionsTableUploadTaskDetailsComponent;
  let fixture: ComponentFixture<EmissionsTableUploadTaskDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmissionsTableUploadTaskDetailsComponent],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmissionsTableUploadTaskDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
