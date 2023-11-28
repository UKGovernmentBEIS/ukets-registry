import { GovukTimePipe } from '@shared/pipes/govuk-time.pipe';

describe('GovukTimePipe', () => {
  const pipe = new GovukTimePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform time compliant to GDS style guide', () => {
    expect(pipe.transform('1:30am')).toEqual('1:30am');
    expect(pipe.transform('12:00am')).toEqual('midnight');
    expect(pipe.transform('12:00pm')).toEqual('midday');
    expect(pipe.transform('')).toEqual('');
    expect(pipe.transform(null)).toEqual('');
    expect(pipe.transform(undefined)).toEqual('');
  });
});
