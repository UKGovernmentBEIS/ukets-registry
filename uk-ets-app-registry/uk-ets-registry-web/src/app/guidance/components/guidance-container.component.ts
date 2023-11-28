import { Component, OnInit } from '@angular/core';
import { canGoBack } from '@shared/shared.action';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-guidance',
  templateUrl: './guidance-container.component.html',
})
export class GuidanceContainerComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: null,
      })
    );
  }
}
