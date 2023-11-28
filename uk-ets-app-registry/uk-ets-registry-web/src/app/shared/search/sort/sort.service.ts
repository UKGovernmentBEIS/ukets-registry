import { Injectable } from '@angular/core';
import { SortParameters } from './SortParameters';
import { Observable, Subject } from 'rxjs';

@Injectable()
export class SortService {
  private sortParametersSubject = new Subject<SortParameters>();

  sortByColumn(event: SortParameters) {
    this.sortParametersSubject.next(event);
  }

  getSortParameters(): Observable<SortParameters> {
    return this.sortParametersSubject.asObservable();
  }

  getColumnSorted(): Observable<SortParameters> {
    return this.sortParametersSubject.asObservable();
  }
}
