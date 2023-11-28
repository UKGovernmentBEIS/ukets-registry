import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectAccessRightsComponent } from './select-access-rights.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { AuthorisedRepresentativeUpdateTypePipe } from '@shared/pipes';

describe('SelectAccessRightsComponent', () => {
  let component: SelectAccessRightsComponent;
  let fixture: ComponentFixture<SelectAccessRightsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule],
        declarations: [
          SelectAccessRightsComponent,
          UkRadioInputComponent,
          AuthorisedRepresentativeUpdateTypePipe,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectAccessRightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
