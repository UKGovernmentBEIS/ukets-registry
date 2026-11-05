import { concatDateTime } from './shared.util';

describe('shared utils', () => {
  test('concatDateTime should combine date and pm time', () => {
    const result = concatDateTime('24 Sep 2026', '1:30pm');

    expect(new Date(result).toISOString()).toBe('2026-09-24T13:30:00.000Z');
  });

  test('concatDateTime should combine date and PM (capital) time', () => {
    const result = concatDateTime('24 Sep 2026', '1:30PM');

    expect(new Date(result).toISOString()).toBe('2026-09-24T13:30:00.000Z');
  });

  test('concatDateTimeshould combine date and am time', () => {
    const result = concatDateTime('24 Sep 2026', '1:30am');

    expect(new Date(result).toISOString()).toBe('2026-09-24T01:30:00.000Z');
  });

  test('concatDateTime should combine date and AM (capital) time', () => {
    const result = concatDateTime('24 Sep 2026', '1:30AM');

    expect(new Date(result).toISOString()).toBe('2026-09-24T01:30:00.000Z');
  });

  test('concatDateTimeshould handle noon', () => {
    const result = concatDateTime('24 Sep 2026', '12:00pm');

    expect(new Date(result).toISOString()).toBe('2026-09-24T12:00:00.000Z');
  });

  test('concatDateTime should handle midnight', () => {
    const result = concatDateTime('24 Sep 2026', '12:00am');

    expect(new Date(result).toISOString()).toBe('2026-09-24T00:00:00.000Z');
  });

  test.each([['1:30pm'], ['1:30PM'], ['1:30Pm'], ['1:30pM']])(
    'concatDateTime should parse %s',
    (time) => {
      expect(new Date(concatDateTime('24 Sep 2026', time)).toISOString()).toBe(
        '2026-09-24T13:30:00.000Z'
      );
    }
  );

  test('concatDateTime should handle null time', () => {
    expect(() => concatDateTime('24 Sep 2026', null)).toThrow(
      'Invalid date or time: 24 Sep 2026 null'
    );
  });

  test('concatDateTime should handle undefined time', () => {
    expect(() => concatDateTime('24 Sep 2026', undefined)).toThrow(
      'Invalid date or time: 24 Sep 2026 undefined'
    );
  });
});
