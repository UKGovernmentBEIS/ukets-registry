import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import { selectCookiesAccepted } from '@shared/shared.selector';

@Component({
  selector: 'app-email-info-container',
  template: `
    <app-email-info
      [cookiesAccepted]="cookiesAccepted$ | async"
      (navigationEmitter)="navigateToEmailAddress($event)"
    />
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailInfoContainerComponent implements OnInit {
  cookiesAccepted$: Observable<boolean>;

  constructor(private _router: Router, private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.cookiesAccepted$ = this.store.select(selectCookiesAccepted);
  }

  navigateToEmailAddress(path) {
    this._router.navigate([path], {
      skipLocationChange: true,
    });
  }
}
