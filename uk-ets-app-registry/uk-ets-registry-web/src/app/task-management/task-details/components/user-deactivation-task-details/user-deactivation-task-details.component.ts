import { Component, Input, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { IUkOfficialCountry } from '@registry-web/shared/countries/country.interface';
import { selectAllCountries } from '@registry-web/shared/shared.selector';
import { UserDeactivationTaskDetails } from '@registry-web/task-management/model';
import { ViewMode } from '@registry-web/user-management/user-details/model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-user-deactivation-task-details',
  templateUrl: './user-deactivation-task-details.component.html',
})
export class UserDeactivationTaskDetailsComponent implements OnInit {
  @Input()
  userDeactivationTaskDetails: UserDeactivationTaskDetails;
  countries$: Observable<IUkOfficialCountry[]>;

  readonly viewMode = ViewMode;
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.countries$ = this.store.select(selectAllCountries);
  }
}
