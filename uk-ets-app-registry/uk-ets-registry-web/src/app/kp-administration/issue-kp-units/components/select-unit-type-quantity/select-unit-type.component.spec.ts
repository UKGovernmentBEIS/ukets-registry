import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectUnitTypeComponent } from './select-unit-type.component';
import { RemainingQuantityPipe } from '../../pipes/remaining-quantity.pipe';
import { UnitTypeAndActivityPipe } from '../../pipes/registry-level-info-view.pipe';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';

const formBuilder = new FormBuilder();

describe('SelectUnitTypeComponentComponent', () => {
  let component: SelectUnitTypeComponent;
  let fixture: ComponentFixture<SelectUnitTypeComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          SelectUnitTypeComponent,
          UnitTypeAndActivityPipe,
          RemainingQuantityPipe,
          FormGroupDirective,
        ],
        imports: [ReactiveFormsModule],
        providers: [
          UnitTypeAndActivityPipe,
          { provide: FormBuilder, useValue: formBuilder },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectUnitTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
