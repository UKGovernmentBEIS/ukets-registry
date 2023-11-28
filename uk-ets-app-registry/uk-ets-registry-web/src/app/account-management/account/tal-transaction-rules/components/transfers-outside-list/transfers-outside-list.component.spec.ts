import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { TransfersOutsideListComponent } from '@tal-transaction-rules/components';

describe('TransfersOutsideListComponent', () => {
  let component: TransfersOutsideListComponent;
  let fixture: ComponentFixture<TransfersOutsideListComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [TransfersOutsideListComponent, UkRadioInputComponent],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TransfersOutsideListComponent);
    component = fixture.componentInstance;
    component.currentRules = {
      rule1: true,
      rule2: false,
      rule3: true,
    };
    component.updatedRules = {
      rule1: true,
      rule2: false,
      rule3: true,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
