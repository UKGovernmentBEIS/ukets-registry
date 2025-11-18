import { Pipe, PipeTransform } from '@angular/core';
import { IUkOfficialCountry } from '@shared/countries/country.interface';

@Pipe({
  name: 'countryName',
  pure: true,
})
export class CountryNamePipe implements PipeTransform {
  transform(countryCode: string, countries: IUkOfficialCountry[]): string {
    return countryCode && countries
      ? countries.find((country) => country.key === countryCode)?.item[0]?.name
      : null;
  }
}
