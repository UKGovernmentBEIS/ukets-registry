import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { CheckRequestAndSubmitComponent } from '@emissions-table/components/check-request-and-submit';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls';
import { FileBase } from '@shared/model/file';

describe('CheckRequestAndSubmitComponent', () => {
  let component: CheckRequestAndSubmitComponent;
  let fixture: ComponentFixture<CheckRequestAndSubmitComponent>;
  const fileHeader: FileBase = {
    id: 89678,
    fileName:
      'UK_Emissions_28062021_SEPA_83BBCD0F874C2E436A5E6DA772AA2822.xlsx',
    fileSize: '8272623',
  };
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule],
      declarations: [
        CheckRequestAndSubmitComponent,
        UkProtoFormTextComponent,
        DisableControlDirective,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckRequestAndSubmitComponent);
    component = fixture.componentInstance;
    component.fileHeader = fileHeader;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
