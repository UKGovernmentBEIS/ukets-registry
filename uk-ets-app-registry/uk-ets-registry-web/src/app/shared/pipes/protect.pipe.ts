import { Pipe, PipeTransform } from '@angular/core';
import { AuthApiService } from 'src/app/auth/auth-api.service';
import { Store } from '@ngrx/store';
import { isAuthenticated } from 'src/app/auth/auth.selector';
import { Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

@Pipe({
  name: 'protect',
})
export class ProtectPipe implements PipeTransform {
  constructor(private authApiService: AuthApiService, private store: Store) {}

  transform(value: string, clientId?: string): Observable<boolean> {
    return this.store.select(isAuthenticated).pipe(
      mergeMap((loggedIn: boolean) => {
        if (loggedIn) {
          if (value) {
            return this.authApiService.hasScope(value, false, clientId);
          } else {
            return of(true);
          }
        } else {
          return of(false);
        }
      })
    );
  }
}
