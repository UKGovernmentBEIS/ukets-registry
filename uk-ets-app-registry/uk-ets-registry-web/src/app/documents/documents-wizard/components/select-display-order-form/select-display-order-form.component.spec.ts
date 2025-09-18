import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { SelectDisplayOrderFormComponent } from './select-display-order-form.component';
import { SharedModule } from '@registry-web/shared/shared.module';

describe('SelectDisplayOrderFormComponent', () => {
  let component: SelectDisplayOrderFormComponent;
  let fixture: ComponentFixture<SelectDisplayOrderFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SelectDisplayOrderFormComponent,
        ReactiveFormsModule,
        SharedModule,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectDisplayOrderFormComponent);
    component = fixture.componentInstance;
    const storedOrder = 2;
    const orderOptions = [1, 2, 3];
    component.storedOrder = storedOrder;
    component.orderOptions = orderOptions;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit handleSubmit event on form submission', () => {
    spyOn(component.handleSubmit, 'emit');
    const storedOrder = 2;
    const orderOptions = [1, 2, 3];
    component.storedOrder = storedOrder;
    component.orderOptions = orderOptions;
    component.ngOnInit();
    component.onContinue();
    expect(component.handleSubmit.emit).toHaveBeenCalledWith(storedOrder);
  });

  it('should set the default order if storedOrder is not provided', () => {
    const orderOptions = [1, 2, 3];
    component.orderOptions = orderOptions;
    component.storedOrder = null;
    component.ngOnInit();
    expect(component.formGroup.get('order').value).toEqual(3);
  });
});
