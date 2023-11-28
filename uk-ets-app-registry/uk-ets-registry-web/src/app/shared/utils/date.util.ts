export function dateInvalidAt(
  day: number,
  month: number,
  year: number
): number {
  const DAY = 2,
    MONTH = 1,
    YEAR = 0;

  if (checkInvalidYear(year)) return YEAR;
  if (checkInvalidMonth(month)) return MONTH;
  if (checkInvalidDay(day, month, year)) return DAY;

  return null;
}

function checkInvalidYear(year: number) {
  if (Number.isNaN(year)) return true;
  // if (year < 1800 || year > 2200) return true;
  return false;
}

function checkInvalidMonth(month: number) {
  if (Number.isNaN(month)) return true;
  if (month < 1 || month > 12) return true;
  return false;
}

function checkInvalidDay(day: number, month: number, year: number): boolean {
  if (Number.isNaN(day)) return true;
  const longMonths = [1, 3, 5, 7, 8, 10, 12];

  if (day < 1 || day > 31) return true;

  if (day == 31 && longMonths.indexOf(month) === -1) return true;

  if (month == 2 && day > 29) return true;

  if (month == 2 && day > 28 && !isLeapYear(year)) return true;

  return false;
}

function isLeapYear(year: number): boolean {
  return (year % 4 === 0 && year % 100 !== 0) || year % 400 === 0;
}
