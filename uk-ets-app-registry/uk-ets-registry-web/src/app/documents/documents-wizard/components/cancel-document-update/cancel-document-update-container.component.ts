import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import { SharedModule } from '@registry-web/shared/shared.module';
import { cancelDocumentUpdateRequestConfirm } from '../../store/documents-wizard.actions';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  imports: [SharedModule, CommonModule, RouterModule],
  selector: 'app-cancel-document-update-container',
  template: ` <div
      appScreenReaderPageAnnounce
      [pageTitle]="'Cancel request'"
    ></div>

    <app-cancel-update-request
      [notification]="
        'Are you sure you want to cancel the update of the documents and return back to the documents page?'
      "
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelDocumentUpdateContainerComponent implements OnInit {
  goBackRoute: string;

  constructor(private store: Store, private route: ActivatedRoute) {}

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
  }

  onCancel() {
    this.store.dispatch(cancelDocumentUpdateRequestConfirm());
  }
}
