import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateAllocationStatusWizardComponent } from './update-allocation-status-wizard.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { CancelRequestLinkComponent } from '@shared/components/account/cancel-request-link';
import { provideMockStore } from '@ngrx/store/testing';

describe('UpdateAllocationStatusWizardComponent', () => {
  let component: UpdateAllocationStatusWizardComponent;
  let fixture: ComponentFixture<UpdateAllocationStatusWizardComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, RouterTestingModule],
        providers: [provideMockStore()],
        declarations: [
          CancelRequestLinkComponent,
          UpdateAllocationStatusWizardComponent,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateAllocationStatusWizardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
