import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockStore } from '@ngrx/store/testing';

import {
  CheckRequestAndSubmitContainerComponent,
  CheckRequestAndSubmitComponent,
} from '@emissions-table/components/check-request-and-submit';
import { CancelRequestLinkComponent } from '@shared/components/account/cancel-request-link';
import { ReactiveFormsModule } from '@angular/forms';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls';

describe.skip('CheckRequestAndSubmitContainerComponent', () => {
  let component: CheckRequestAndSubmitContainerComponent;
  let fixture: ComponentFixture<CheckRequestAndSubmitContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        CheckRequestAndSubmitContainerComponent,
        CheckRequestAndSubmitComponent,
        CancelRequestLinkComponent,
        UkProtoFormTextComponent,
        DisableControlDirective,
      ],
      imports: [RouterTestingModule, ReactiveFormsModule],
      providers: [provideMockStore()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckRequestAndSubmitContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
