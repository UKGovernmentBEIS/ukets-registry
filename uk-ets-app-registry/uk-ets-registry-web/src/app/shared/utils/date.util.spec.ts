import { dateInvalidAt } from './date.util';

const INVALID_DAY = 2,
  INVALID_MONTH = 1,
  YEAR = 0,
  VALID_DATE = null;

describe('DateUtil dateInvalidAt', () => {
  it('should return null for valid dates', () => {
    expect(dateInvalidAt(28, 2, 2001)).toBe(VALID_DATE);
    expect(dateInvalidAt(1, 1, 1970)).toBe(VALID_DATE);
  });

  it('should check if month or day are out of bounds', () => {
    expect(dateInvalidAt(32, 1, 1970)).toBe(INVALID_DAY);
    expect(dateInvalidAt(1, 16, 1970)).toBe(INVALID_MONTH);
    expect(dateInvalidAt(0, 1, 1970)).toBe(INVALID_DAY);
    expect(dateInvalidAt(1, 0, 1970)).toBe(INVALID_MONTH);
  });

  it('should check if month has 31 days', () => {
    expect(dateInvalidAt(31, 1, 1970)).toBe(VALID_DATE);
    expect(dateInvalidAt(31, 2, 1970)).toBe(INVALID_DAY);
    expect(dateInvalidAt(31, 3, 1970)).toBe(VALID_DATE);
    expect(dateInvalidAt(31, 4, 1970)).toBe(INVALID_DAY);
    expect(dateInvalidAt(31, 5, 1970)).toBe(VALID_DATE);
    expect(dateInvalidAt(31, 6, 1970)).toBe(INVALID_DAY);
    expect(dateInvalidAt(31, 7, 1970)).toBe(VALID_DATE);
    expect(dateInvalidAt(31, 8, 1970)).toBe(VALID_DATE);
    expect(dateInvalidAt(31, 9, 1970)).toBe(INVALID_DAY);
    expect(dateInvalidAt(31, 10, 1970)).toBe(VALID_DATE);
    expect(dateInvalidAt(31, 11, 1970)).toBe(INVALID_DAY);
    expect(dateInvalidAt(31, 12, 1970)).toBe(VALID_DATE);
  });

  it('should check leap years if 29th of February', () => {
    expect(dateInvalidAt(29, 2, 2001)).toBe(INVALID_DAY);
    expect(dateInvalidAt(29, 2, 2000)).toBe(VALID_DATE);
  });
});
