import { Component, OnInit } from '@angular/core';
import { canGoBack } from '@shared/shared.action';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-introduction',
  templateUrl: './introduction.component.html',
})
export class IntroductionComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/guidance`,
      })
    );
  }
}
