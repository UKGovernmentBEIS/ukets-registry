import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { TaskDetails } from '@task-management/model';
import { Observable } from 'rxjs';
import { bacsPaymentCompleteOrCancelled } from '@task-details/actions/task-details.actions';

@Component({
  selector: 'app-payment-bacs-cancel-container',
  template: `
    <app-payment-bacs-cancel
      (cancelRequest)="onCancel()"
    ></app-payment-bacs-cancel>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: ``,
})
export class PaymentBacsCancelContainerComponent implements OnInit {
  goBackRoute: string;
  task$: Observable<TaskDetails>;

  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/task-details/${this.route.snapshot.paramMap.get(
          'requestId'
        )}/payment-bacs-details`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      bacsPaymentCompleteOrCancelled({ status: 'CANCELLED' })
    );
  }
}
