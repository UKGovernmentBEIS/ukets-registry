import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { DocumentsWizardPath } from '@registry-web/documents/models/documents-wizard-path.model';
import { canGoBack } from '@registry-web/shared/shared.action';
import { SharedModule } from '@registry-web/shared/shared.module';
import { Observable } from 'rxjs';
import {
  navigateToCancelDocumentUpdateRequest,
  setDocumentOrder,
} from '../../store/documents-wizard.actions';
import { SelectDisplayOrderFormComponent } from '../select-display-order-form/select-display-order-form.component';
import {
  selectDocumentOrderOptions,
  selectDocumentOrder,
} from '../../store/documents-wizard.selector';

@Component({
  standalone: true,
  selector: 'app-choose-display-order-container',
  imports: [
    CommonModule,
    RouterModule,
    SharedModule,
    SelectDisplayOrderFormComponent,
  ],
  template: `<app-select-display-order-form
      [storedOrder]="storedOrder$ | async"
      [orderOptions]="orderOptions$ | async"
      (handleSubmit)="onContinue($event)"
    ></app-select-display-order-form>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
})
export class ChooseDisplayOrderContainerComponent implements OnInit {
  constructor(private store: Store, private route: ActivatedRoute) {}

  orderOptions$: Observable<number[]>;
  storedOrder$: Observable<number>;

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.UPLOAD_DOCUMENT}`,
        extras: { skipLocationChange: true },
      })
    );

    this.orderOptions$ = this.store.select(selectDocumentOrderOptions);
    this.storedOrder$ = this.store.select(selectDocumentOrder);
  }

  onContinue(selectedOrder: number) {
    this.store.dispatch(setDocumentOrder({ selectedOrder }));
  }

  onCancel() {
    this.store.dispatch(
      navigateToCancelDocumentUpdateRequest({
        route: this.route.snapshot['_routerState'].url,
      })
    );
  }
}
