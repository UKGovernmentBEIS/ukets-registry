import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { DeclarationComponent } from './declaration.component';

describe('DeclarationComponent', () => {
  let component: DeclarationComponent;
  let fixture: ComponentFixture<DeclarationComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(DeclarationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update confirm control value on change event', () => {
    const confirmControl = component.formGroup.get('confirm');
    const newConfirmedValue = true;
    component.onChange({ target: { checked: newConfirmedValue } });
    expect(confirmControl.value).toEqual(newConfirmedValue);
  });

  it('should emit errorDetails on invalid submit', () => {
    const errorDetailsSpy = spyOn(component.errorDetails, 'emit');
    component.onSubmit();
    expect(errorDetailsSpy).toHaveBeenCalled();
  });
});
