import { FormControl } from '@angular/forms';
import { UkRegistryValidators } from './uk-registry.validators';

describe('yearShouldBeAfter', () => {
  const control = new FormControl('input', [
    UkRegistryValidators.yearShouldBeAfter(2021),
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

describe('notPermittedValue', () => {
  it('should be VALID for string value if the value does not match', () => {
    const control = new FormControl('2022', [
      UkRegistryValidators.notPermittedValue('2021'),
    ]);
    expect(control.status).toBe('VALID');
  });

  it('should be INVALID for string value if the value matches', () => {
    const control = new FormControl('2021', [
      UkRegistryValidators.notPermittedValue('2021'),
    ]);
    expect(control.status).toBe('INVALID');
    expect(control.errors?.['notPermittedValue']).toBe(true);
  });

  it('should be VALID for { [key:string]: string } object if the objects do not match', () => {
    const control1 = new FormControl({ a: '2023', b: '2024' }, [
      UkRegistryValidators.notPermittedValue({ a: '2021', b: '2022' }),
    ]);
    const control2 = new FormControl(null, [
      UkRegistryValidators.notPermittedValue({ a: '2021', b: '2022' }),
    ]);
    expect(control1.status).toBe('VALID');
    expect(control2.status).toBe('VALID');
  });

  it('should be INVALID for { [key:string]: string } object if the objects match', () => {
    const control1 = new FormControl({ a: '2021', b: '2022' }, [
      UkRegistryValidators.notPermittedValue({ a: '2021', b: '2022' }),
    ]);
    const control2 = new FormControl({ b: '2022', a: '2021' }, [
      UkRegistryValidators.notPermittedValue({ a: '2021', b: '2022' }),
    ]);
    expect(control1.status).toBe('INVALID');
    expect(control1.errors?.['notPermittedValue']).toBe(true);
    expect(control2.status).toBe('INVALID');
    expect(control2.errors?.['notPermittedValue']).toBe(true);
  });
});
