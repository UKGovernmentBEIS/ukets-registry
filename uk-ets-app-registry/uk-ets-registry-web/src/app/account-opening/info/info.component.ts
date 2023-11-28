import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack, clearErrors } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectEtrAddress } from '@registry-web/shared/shared.selector';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
})
export class InfoComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  helpdeskEmail$: Observable<string>;

  ngOnInit() {
    this.helpdeskEmail$ = this.store.select(selectEtrAddress);
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.store.dispatch(clearErrors());
  }

  onContinue() {
    this._router.navigate(['account-type'], {
      skipLocationChange: true,
      relativeTo: this.route,
    });
  }
}
