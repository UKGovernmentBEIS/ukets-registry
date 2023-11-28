import { Pipe, PipeTransform } from '@angular/core';
import { IUkOfficialCountry } from '@shared/countries/country.interface';

@Pipe({
  name: 'countryName',
  pure: true,
})
export class CountryNamePipe implements PipeTransform {
  transform(countryCode: string, countries: IUkOfficialCountry[]): string {
    if (!countryCode || !countries) {
      return null;
    }
    const countryObj = countries.filter((f) => f.key === countryCode);
    return countryObj[0].item[0].name;
  }
}
