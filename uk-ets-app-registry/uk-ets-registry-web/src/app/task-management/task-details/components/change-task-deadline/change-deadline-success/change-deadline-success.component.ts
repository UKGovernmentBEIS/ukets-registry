import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { navigateToTaskDetails } from '../../../actions/task-details-navigation.actions';
import {
  canGoBack,
  clearGoBackRoute,
} from '@registry-web/shared/shared.action';

@Component({
  selector: 'app-change-deadline-success',
  templateUrl: './change-deadline-success.component.html',
})
export class ChangeDeadlineSuccessComponent {
  constructor(private store: Store) {
    this.store.dispatch(clearGoBackRoute());
  }

  onBackToTask() {
    this.store.dispatch(navigateToTaskDetails({}));
  }
}
