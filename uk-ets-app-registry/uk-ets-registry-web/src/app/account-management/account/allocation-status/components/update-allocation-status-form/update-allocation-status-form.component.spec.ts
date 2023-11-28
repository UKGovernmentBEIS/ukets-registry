import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateAllocationStatusFormComponent } from './update-allocation-status-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { UkProtoFormCommentAreaComponent } from '@shared/form-controls/uk-proto-form-controls';

describe('UpdateAllocationStatusFormComponent', () => {
  let component: UpdateAllocationStatusFormComponent;
  let fixture: ComponentFixture<UpdateAllocationStatusFormComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, RouterTestingModule],
        declarations: [
          UkProtoFormCommentAreaComponent,
          UpdateAllocationStatusFormComponent,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateAllocationStatusFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
