import { Pipe, PipeTransform } from '@angular/core';
import { AuthApiService } from 'src/app/auth/auth-api.service';
import { Store } from '@ngrx/store';
import { isAuthenticated } from 'src/app/auth/auth.selector';
import { Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

@Pipe({
  name: 'requestDoc'
})
export class RequestDocPipe implements PipeTransform {
  constructor(private authApiService: AuthApiService, private store: Store) {}

  transform(urn: string, isEligible: boolean): Observable<boolean> {
    return this.store.select(isAuthenticated).pipe(
      flatMap((loggedIn: boolean) => {
        if (loggedIn) {
          if (urn && isEligible) {
            return this.authApiService.hasScope(urn);
          }
          return of(false);
        } else {
          return of(false);
        }
      })
    );
  }
}
