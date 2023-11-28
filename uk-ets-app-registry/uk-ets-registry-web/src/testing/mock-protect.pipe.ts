import { Pipe, PipeTransform } from '@angular/core';
import { Observable, of } from 'rxjs';

@Pipe({ name: 'protect' })
export class MockProtectPipe implements PipeTransform {
  transform(value: string): Observable<boolean> {
    return of(true);
  }
}
