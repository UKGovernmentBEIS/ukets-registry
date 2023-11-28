import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';

@Component({
  selector: 'app-ar',
  templateUrl: './ar.component.html',
})
export class ArComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/guidance`,
      })
    );
  }
}
