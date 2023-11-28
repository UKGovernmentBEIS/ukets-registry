import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { SecondApprovalNecessaryComponent } from '@tal-transaction-rules/components';

describe('SecondApprovalNecessaryComponent', () => {
  let component: SecondApprovalNecessaryComponent;
  let fixture: ComponentFixture<SecondApprovalNecessaryComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [SecondApprovalNecessaryComponent, UkRadioInputComponent],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SecondApprovalNecessaryComponent);
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
