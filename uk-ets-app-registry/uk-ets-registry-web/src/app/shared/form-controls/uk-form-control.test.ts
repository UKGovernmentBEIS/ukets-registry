import { UkFormControlComponent } from './uk-form-control.component';
import { FormGroup } from '@angular/forms';

describe('UkFormControl', () => {
  test(`When a UkFormControl object created with id, initialState and FormGroup
        then this formControl is added into the Form controls`, () => {
    const control = new UkFormControlComponent();
    control.form = new FormGroup({});
    control.id = 'testField';
    control.formState = 'initialValue';
    // eslint-disable-next-line @angular-eslint/no-lifecycle-call
    control.ngOnInit();
    expect(control.form.value).toStrictEqual({
      testField: control.formState,
    });
  });
});
