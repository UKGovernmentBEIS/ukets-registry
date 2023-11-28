import { FormControl } from '@angular/forms';
import { UkRegistryValidators } from './uk-registry.validators';

describe('yearShouldBeAfter', () => {
  const control = new FormControl('input', [
    UkRegistryValidators.yearShouldBeAfter(2021)
  ]);

  it('should be VALID if year is after 2021', () => {
    control.setValue('2022');
    expect(control.status).toBe('VALID');
  });

  it('should be VALID if year is 2021', () => {
    control.setValue('2021');
    expect(control.status).toBe('VALID');
  });

  it('should be INVALID if year is before 2021', () => {
    control.setValue('2019');
    expect(control.status).toBe('INVALID');
  });
});
