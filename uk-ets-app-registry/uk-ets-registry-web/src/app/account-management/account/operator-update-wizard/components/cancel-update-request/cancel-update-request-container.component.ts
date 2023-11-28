import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import { OperatorUpdateActions } from '@operator-update/actions';
import { selectCurrentOperatorInfo } from '@operator-update/reducers';
import { Observable } from 'rxjs';
import { Operator, operatorTypeMap } from '@shared/model/account';

@Component({
  selector: 'app-cancel-update-request-container',
  template: `
    <app-cancel-update-request
      [updateRequestText]="
        operatorTypeMap[(currentOperatorInfo$ | async)?.type] + ' update'
      "
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelUpdateRequestContainerComponent implements OnInit {
  goBackRoute: string;
  currentOperatorInfo$: Observable<Operator>;
  operatorTypeMap = operatorTypeMap;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.goBackRoute = params.goBackRoute;
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.goBackRoute,
        extras: { skipLocationChange: true },
      })
    );
    this.currentOperatorInfo$ = this.store.select(selectCurrentOperatorInfo);
  }

  onCancel() {
    this.store.dispatch(OperatorUpdateActions.cancelOperatorUpdateRequest());
  }
}
