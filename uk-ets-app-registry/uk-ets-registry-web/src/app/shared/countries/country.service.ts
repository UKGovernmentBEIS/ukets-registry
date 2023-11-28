import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PhoneNumberUtil } from 'google-libphonenumber';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IUkOfficialCountry } from './country.interface';

@Injectable({
  providedIn: 'root',
})
export class CountryService {
  ukCountriesUrl = '../../assets/uk_countries.json';

  constructor(private http: HttpClient) {}

  getCountries(): Observable<IUkOfficialCountry[]> {
    return this.http.get(this.ukCountriesUrl).pipe(
      map((response) => {
        return Array.from(
          new Map<string, IUkOfficialCountry>(Object.entries(response)).values()
        )
          .filter((country) => !country.item[0]['end-date'])
          .sort((a, b) => (a.item[0].name > b.item[0].name ? 1 : -1))
          .map((country) => {
            country.callingCode = PhoneNumberUtil.getInstance().getCountryCodeForRegion(
              country.key === 'UK' ? 'GB' : country.key
            );
            return country;
          });
      })
    );
  }
}
