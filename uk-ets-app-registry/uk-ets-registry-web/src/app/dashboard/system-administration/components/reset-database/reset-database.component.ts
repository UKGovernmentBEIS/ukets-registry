import { Component, OnInit } from '@angular/core';
import { ResetDatabaseResult } from '../../model';
import { Observable } from 'rxjs';
import { selectResetDatabaseResult } from '../../store/reducers';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-reset-database',
  templateUrl: './reset-database.component.html',
  styles: []
})
export class ResetDatabaseComponent implements OnInit {
  resetDatabaseResult$: Observable<ResetDatabaseResult>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.resetDatabaseResult$ = this.store.select(selectResetDatabaseResult);
  }
}
