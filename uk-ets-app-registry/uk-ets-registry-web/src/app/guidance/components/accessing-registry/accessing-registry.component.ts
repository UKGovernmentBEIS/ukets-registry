import { Component, OnInit } from '@angular/core';
import { canGoBack } from '@shared/shared.action';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-guidance-accessing-registry',
  templateUrl: './accessing-registry.component.html',
})
export class AccessingRegistryComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/guidance`,
      })
    );
  }
}
