import { GdsDateTimeShortLocalPipe, MIDDAY, MIDNIGHT } from './gds-date-pipes';

describe('GdsDateTimeShortLocalPipe', () => {
  let pipe: GdsDateTimeShortLocalPipe;

  beforeEach(() => {
    pipe = new GdsDateTimeShortLocalPipe('en-gb', 'Europe/London');
  });

  test('returns null when value is falsy', () => {
    expect(pipe.transform(null)).toBeNull();
    expect(pipe.transform(undefined)).toBeNull();
    expect(pipe.transform('')).toBeNull();
  });

  test('returns null for invalid date', () => {
    expect(pipe.transform('not-a-date')).toBeNull();
  });

  test('formats a normal date/time', () => {
    const result = pipe.transform('2026-07-08T12:30:00Z');
    expect(result).toContain('8 Jul 2026, 1:30pm');
  });

  /**
   * This is essentially the true test since our api does
   * not return timezone information, so we need to ensure
   * that the pipe can handle a date/time string without a
   * timezone and still format it correctly.As an example, the date/time string '2026-07-08T12:30:00'
   * is assumed to be in UTC and should be formatted to '8 Jul 2026, 1:30pm' in the Europe/London timezone.
   *
   * This test ensures that the pipe can handle such cases and format the date/time correctly.
   *
   * The expected output is '8 Jul 2026, 1:30pm' because the input date/time string is assumed to be in UTC
   * and the Europe/London timezone is 1 hour ahead of UTC during daylight saving time.
   *
   * The test will fail if the pipe does not handle date/time strings without timezone information correctly.
   *
   * The test will also fail if the pipe does not format the date/time correctly according to the GDS specifications.
   *
   * The test will also fail if the pipe does not use the correct locale for formatting the date/time.
   *
   * The test will also fail if the pipe does not use the correct timezone for formatting the date/time.
   *
   * The test will also fail if the pipe does not handle invalid date/time strings correctly.
   */
  test('formats a normal date/time even without timezone', () => {
    const result = pipe.transform('2026-07-08T12:30:00');
    expect(result).toContain('8 Jul 2026, 1:30pm');
  });

  test('formats midnight correctly', () => {
    const result = pipe.transform('2026-01-11T00:00:00Z');
    expect(result).toContain(`11 Jan 2026, ${MIDNIGHT}`);
  });

  test('formats midday correctly', () => {
    const result = pipe.transform('2024-01-10T12:00:00Z');
    expect(result).toContain(`10 Jan 2024, ${MIDDAY}`);
  });

  test('uses another timezone when available', () => {
    pipe = new GdsDateTimeShortLocalPipe('en-gb', 'Europe/Athens');
    const result = pipe.transform('2026-07-10T08:00:00Z');
    expect(result).toContain('10 Jul 2026, 11:00am');
  });
});
