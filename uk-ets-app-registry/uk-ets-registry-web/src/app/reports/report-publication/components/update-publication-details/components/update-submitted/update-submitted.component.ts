import { Component, OnInit } from '@angular/core';
import { navigateTo } from '@report-publication/actions';
import { Store } from '@ngrx/store';
import { clearGoBackRoute } from '@shared/shared.action';

@Component({
  selector: 'app-update-submitted',
  templateUrl: './update-submitted.component.html',
})
export class UpdateSubmittedComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(clearGoBackRoute());
  }

  navigateBackToSectionDetails() {
    this.store.dispatch(navigateTo({}));
  }
}
