import { Pipe, PipeTransform } from '@angular/core';
import { selectAllCountries } from '../shared.selector';
import { Store } from '@ngrx/store';
import { Observable, map, of } from 'rxjs';
import { CountryNamePipe } from './country-name.pipe';

@Pipe({
  name: 'countryNameAsync',
  pure: false,
})
export class CountryNameAsyncPipe implements PipeTransform {
  constructor(
    private store: Store,
    private countryNamePipe: CountryNamePipe
  ) {}

  transform(countryCode: string): Observable<string> {
    return countryCode
      ? this.store
          .select(selectAllCountries)
          .pipe(
            map((countries) =>
              this.countryNamePipe.transform(countryCode, countries)
            )
          )
      : of(null);
  }
}
