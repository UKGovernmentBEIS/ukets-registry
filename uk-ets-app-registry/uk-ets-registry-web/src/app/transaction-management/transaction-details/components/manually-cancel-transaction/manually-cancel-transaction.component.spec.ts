import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ManuallyCancelTransactionComponent } from './manually-cancel-transaction.component';
import { UkProtoFormCommentAreaComponent } from '@shared/form-controls/uk-proto-form-controls';

import { ReactiveFormsModule } from '@angular/forms';

describe('CancelTransactionComponent', () => {
  let component: ManuallyCancelTransactionComponent;
  let fixture: ComponentFixture<ManuallyCancelTransactionComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule],
        declarations: [
          ManuallyCancelTransactionComponent,
          UkProtoFormCommentAreaComponent,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ManuallyCancelTransactionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
