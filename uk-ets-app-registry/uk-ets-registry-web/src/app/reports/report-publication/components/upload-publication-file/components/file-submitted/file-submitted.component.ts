import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { navigateTo } from '@report-publication/actions';
import { clearGoBackRoute } from '@shared/shared.action';

@Component({
  selector: 'app-file-submitted',
  templateUrl: './file-submitted.component.html',
})
export class FileSubmittedComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(clearGoBackRoute());
  }

  navigateBackToSectionDetails() {
    this.store.dispatch(navigateTo({}));
  }
}
