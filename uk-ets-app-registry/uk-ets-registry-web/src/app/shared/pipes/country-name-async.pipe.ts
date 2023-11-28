import { Pipe, PipeTransform } from '@angular/core';
import { selectAllCountries } from '../shared.selector';
import { Store } from '@ngrx/store';
import { Observable, map, of } from 'rxjs';
import { CountryNamePipe } from './country-name.pipe';

@Pipe({
  name: 'countryNameAsync',
  pure: true,
})
export class CountryNameAsyncPipe implements PipeTransform {
  constructor(private store: Store, private countryNamePipe: CountryNamePipe) {}

  private getCountryName$(countryCode): Observable<string> {
    return this.store
      .select(selectAllCountries)
      .pipe(
        map((countries) =>
          this.countryNamePipe.transform(countryCode, countries)
        )
      );
  }

  transform(countryCode: string): Observable<string> {
    if (!countryCode) {
      return of(null);
    }
    return this.getCountryName$(countryCode);
  }
}
